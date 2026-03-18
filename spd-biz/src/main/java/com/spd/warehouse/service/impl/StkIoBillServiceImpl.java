package com.spd.warehouse.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.BasApplyMapper;
import com.spd.department.mapper.HcKsFlowMapper;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.HcCkFlowMapper;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;

import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.vo.StkOutBillExportFlatRow;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;

/**
 * 出入库Service业务层处理
 *
 * @author spd
 * @date 2023-12-17
 */
@Service
public class StkIoBillServiceImpl implements IStkIoBillService
{
    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;

    @Autowired
    private HcKsFlowMapper hcKsFlowMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private StkBatchMapper stkBatchMapper;

    @Autowired
    private FdSupplierMapper fdSupplierMapper;

    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;

    @Autowired
    private FdFactoryMapper fdFactoryMapper;

    @Autowired
    private FdWarehouseCategoryMapper fdWarehouseCategoryMapper;

    @Autowired
    private FdFinanceCategoryMapper fdFinanceCategoryMapper;

    @Autowired
    private FdUnitMapper fdUnitMapper;

    @Autowired
    private BasApplyMapper basApplyMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private ISbCustomerService sbCustomerService;

    /**
     * 查询出入库
     *
     * @param id 出入库主键
     * @return 出入库
     */
    @Override
    public StkIoBill selectStkIoBillById(Long id)
    {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
        if (stkIoBill == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(stkIoBill.getTenantId());
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(StkIoBillEntry entry : stkIoBillEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            entry.setMaterial(fdMaterial);
            materialList.add(fdMaterial);
        }
        stkIoBill.setMaterialList(materialList);
        return stkIoBill;
    }

    /**
     * 查询出入库列表
     *
     * @param stkIoBill 出入库
     * @return 出入库
     */
    @Override
    public List<StkIoBill> selectStkIoBillList(StkIoBill stkIoBill)
    {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectStkIoBillList(stkIoBill);
    }

    /**
     * 新增入库
     *
     * @param stkIoBill 入库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertStkIoBill(StkIoBill stkIoBill)
    {
        if (StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        // 从仓库带出结算方式到单据及明细（入库/出库/消耗）
        if (stkIoBill.getWarehouseId() != null) {
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(stkIoBill.getWarehouseId()));
            if (wh != null && StringUtils.isNotEmpty(wh.getSettlementType())) {
                stkIoBill.setSettlementType(wh.getSettlementType());
                List<StkIoBillEntry> entries = stkIoBill.getStkIoBillEntryList();
                if (entries != null) {
                    for (StkIoBillEntry e : entries) {
                        e.setSettlementType(wh.getSettlementType());
                    }
                }
            }
        }
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        // 根据billType生成不同的单据号
        if (stkIoBill.getBillNo() == null || stkIoBill.getBillNo().isEmpty()) {
            if (stkIoBill.getBillType() != null && stkIoBill.getBillType() == 501) {
                // 调拨单：单号里的日期 = 当前时间，SimpleDateFormat 转成 yyyyMMdd
                Date date = new Date();
                String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
                stkIoBill.setBillNo(getJSNumber("DB", dateStr));
            } else {
                // 默认入库类型，使用RK前缀
                stkIoBill.setBillNo(getNumber());
            }
        }
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertStkIoBillEntry(stkIoBill);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getNumber() {
        String str = "RK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }


    /**
     * 修改出入库
     *
     * @param stkIoBill 出入库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateStkIoBill(StkIoBill stkIoBill)
    {
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        // 仅当请求中带了明细列表且非空时，才逻辑删除旧明细并重新插入；否则保留原明细，只更新主表
        List<StkIoBillEntry> entryList = stkIoBill.getStkIoBillEntryList();
        if (entryList != null && !entryList.isEmpty()) {
            stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId(), SecurityUtils.getUserIdStr(), new Date());
            updateStkIoBillEntry(stkIoBill);
        }
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

//    /**
//     * 批量删除出入库
//     *
//     * @param ids 需要删除的出入库主键
//     * @return 结果
//     */
//    @Transactional
//    @Override
//    public int deleteStkIoBillByIds(Long[] ids)
//    {
//        stkIoBillMapper.deleteStkIoBillEntryByParenIds(ids);
//        return stkIoBillMapper.deleteStkIoBillByIds(ids);
//    }

    /**
     * 删除出入库信息
     *
     * @param id 出入库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoBillById(Long id)
    {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
        if(stkIoBill == null){
            throw new ServiceException(String.format("业务：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(stkIoBill.getTenantId());
        stkIoBill.setDelFlag(1);
        stkIoBill.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoBill.setUpdateTime(new Date());
        stkIoBill.setDeleteBy(SecurityUtils.getUserIdStr());
        stkIoBill.setDeleteTime(new Date());

        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        String deleteBy = SecurityUtils.getUserIdStr();
        Date deleteTime = new Date();
        for(StkIoBillEntry entry : stkIoBillEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            entry.setDeleteBy(deleteBy);
            entry.setDeleteTime(deleteTime);
            stkIoBillMapper.updatestkIobillEntry(entry);
        }

        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    /**
     * 审核入库信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int auditStkIoBill(String id, String auditBy) {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(Long.parseLong(id));
        if(stkIoBill == null){
            throw new ServiceException(String.format("入库业务ID：%s，不存在!", id));
        }
        if (stkIoBill.getBillStatus() != null && stkIoBill.getBillStatus() == 2) {
            throw new ServiceException("该单据已审核，不能重复审核");
        }
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();

        //更新库存   
        updateInventory(stkIoBill,stkIoBillEntryList);

        stkIoBill.setBillStatus(2);//已审核状态
        stkIoBill.setAuditDate(new Date());
        stkIoBill.setAuditBy(auditBy);
        int res = stkIoBillMapper.updateStkIoBill(stkIoBill);
        return res;
    }


    private void updateInventory(StkIoBill stkIoBill,List<StkIoBillEntry> stkIoBillEntryList){

        Integer billType = stkIoBill.getBillType();
        Long supplerId = stkIoBill.getSupplerId();
        StkInventory stkInventory = null;
        for(StkIoBillEntry entry : stkIoBillEntryList){

            if(entry.getQty() != null && BigDecimal.ZERO.compareTo(entry.getQty()) != 0){
                if(billType == 101){//入库
                    // 批次表：若该批次号不存在则插入一条批次记录，用于追溯；并关联 batch_id 到库存
                    StkBatch stkBatch = null;
                    if (StringUtils.isNotEmpty(entry.getBatchNo())) {
                        stkBatch = stkBatchMapper.selectByBatchNo(entry.getBatchNo());
                        if (stkBatch == null) {
                            stkBatch = buildStkBatchForInbound(entry, stkIoBill, supplerId);
                            stkBatchMapper.insertStkBatch(stkBatch);
                        }
                    }

                    stkInventory = new StkInventory();
                    stkInventory.setBatchNo(entry.getBatchNo());
                    if (stkBatch != null) {
                        stkInventory.setBatchId(stkBatch.getId());
                    }
                    stkInventory.setMaterialNo(entry.getBatchNumber());
                    stkInventory.setMaterialId(entry.getMaterialId());
                    stkInventory.setWarehouseId(stkIoBill.getWarehouseId());
                    stkInventory.setQty(entry.getQty());
                    // 优先使用 unitPrice，如果为空则使用 price
                    BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                    stkInventory.setUnitPrice(unitPrice);
                    stkInventory.setAmt(entry.getAmt());
                    stkInventory.setMaterialDate(new Date());
                    stkInventory.setWarehouseDate(new Date());
                    stkInventory.setSupplierId(supplerId);
                    stkInventory.setMainBarcode(entry.getMainBarcode());
                    stkInventory.setSubBarcode(entry.getSubBarcode());
                    stkInventory.setBeginTime(entry.getBeginTime());
                    stkInventory.setEndTime(entry.getEndTime());
                    stkInventory.setReceiptOrderNo(stkIoBill.getBillNo());
                    stkInventory.setCreateTime(new Date());
                    stkInventory.setCreateBy(SecurityUtils.getUserIdStr());
                    stkInventory.setBatchNumber(entry.getBatchNumber());
                    if (StringUtils.isEmpty(stkInventory.getTenantId())) {
                        stkInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    }
                    if (StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                        stkInventory.setSettlementType(stkIoBill.getSettlementType());
                    }
                    stkInventoryMapper.insertStkInventory(stkInventory);
                    // 插仓库流水（lx=RK），反写仓库库存id到流水和入库单明细
                    HcCkFlow rkFlow = new HcCkFlow();
                    rkFlow.setBillId(stkIoBill.getId());
                    rkFlow.setEntryId(entry.getId());
                    rkFlow.setWarehouseId(stkIoBill.getWarehouseId());
                    rkFlow.setMaterialId(entry.getMaterialId());
                    rkFlow.setBatchNo(entry.getBatchNo());
                    rkFlow.setBatchNumber(entry.getBatchNumber());
                    rkFlow.setQty(entry.getQty());
                    rkFlow.setUnitPrice(stkInventory.getUnitPrice());
                    rkFlow.setAmt(entry.getAmt());
                    rkFlow.setBeginTime(entry.getBeginTime());
                    rkFlow.setEndTime(entry.getEndTime());
                    rkFlow.setMainBarcode(entry.getMainBarcode());
                    rkFlow.setSubBarcode(entry.getSubBarcode());
                    rkFlow.setSupplierId(supplerId);
                    rkFlow.setLx("RK");
                    rkFlow.setKcNo(stkInventory.getId());
                    rkFlow.setFlowTime(new Date());
                    rkFlow.setDelFlag(0);
                    rkFlow.setCreateTime(new Date());
                    rkFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(rkFlow.getTenantId())) rkFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(rkFlow);
                    if (entry.getId() != null) {
                        stkIoBillMapper.updateStkIoBillEntryKcNo(entry.getId(), stkInventory.getId());
                    }
                }else if(billType == 201){//出库
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    Long warehouseId = stkIoBill.getWarehouseId();
                    // 优先使用批次号和仓库ID精确查询，如果查不到再使用仅批次号查询
                    StkInventory inventory = null;
                    if(warehouseId != null){
                        inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, warehouseId);
                    }
                    if(inventory == null){
                        inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);
                    }

                    if(inventory == null){
                        throw new ServiceException(String.format("出库-批次号：%s，不存在!", batchNo));
                    }

                    validateInventory(batchNo,stkIoBillEntryList);

                    BigDecimal inventoryQty = inventory.getQty();//库存数量
                    BigDecimal unitPrice = inventory.getUnitPrice();//单价

                    //出库数量不能大于库存数量
                    if(qty.compareTo(inventoryQty) > 0){
                        throw new ServiceException(String.format("实际库存不足！出库数量：%s，实际库存：%s", qty,inventoryQty));
                    }
                    BigDecimal subQty = inventoryQty.subtract(qty);
                    // 扣减仓库库存
                    inventory.setQty(subQty);
                    if(unitPrice != null && subQty != null){
                        inventory.setAmt(subQty.multiply(unitPrice));
                    } else {
                        inventory.setAmt(BigDecimal.ZERO);
                    }
                    inventory.setUpdateTime(new Date());
                    inventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkInventoryMapper.updateStkInventory(inventory);

                    // 插仓库流水（lx=CK，kc_no=仓库库存id）
                    HcCkFlow ckFlow = new HcCkFlow();
                    ckFlow.setBillId(stkIoBill.getId());
                    ckFlow.setEntryId(entry.getId());
                    ckFlow.setWarehouseId(stkIoBill.getWarehouseId());
                    ckFlow.setMaterialId(entry.getMaterialId());
                    ckFlow.setBatchNo(entry.getBatchNo());
                    ckFlow.setBatchNumber(entry.getBatchNumber());
                    ckFlow.setQty(entry.getQty());
                    ckFlow.setUnitPrice(entry.getUnitPrice());
                    ckFlow.setAmt(entry.getAmt());
                    ckFlow.setBeginTime(entry.getBeginTime());
                    ckFlow.setEndTime(entry.getEndTime());
                    ckFlow.setMainBarcode(inventory.getMainBarcode());
                    ckFlow.setSubBarcode(inventory.getSubBarcode());
                    ckFlow.setSupplierId(inventory.getSupplierId());
                    ckFlow.setLx("CK");
                    ckFlow.setKcNo(inventory.getId());
                    ckFlow.setFlowTime(new Date());
                    ckFlow.setDelFlag(0);
                    ckFlow.setCreateTime(new Date());
                    ckFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(ckFlow.getTenantId())) ckFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(ckFlow);
                    // 出库审核即插入科室库存（未确认），记录单据主表id、明细id、单据号、单据类型，便于收货确认时精确定位
                    StkDepInventory stkDepInventory = new StkDepInventory();
                    stkDepInventory.setMaterialId(entry.getMaterialId());
                    stkDepInventory.setMaterialNo(inventory.getMaterialNo());
                    stkDepInventory.setDepartmentId(stkIoBill.getDepartmentId());
                    stkDepInventory.setQty(entry.getQty());
                    stkDepInventory.setUnitPrice(entry.getUnitPrice());
                    stkDepInventory.setAmt(entry.getAmt());
                    stkDepInventory.setBatchNo(entry.getBatchNo());
                    stkDepInventory.setMaterialDate(inventory.getMaterialDate());
                    stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
                    stkDepInventory.setBeginDate(inventory.getBeginTime());
                    stkDepInventory.setEndDate(inventory.getEndTime());
                    stkDepInventory.setSupplierId(inventory.getSupplierId() != null ? inventory.getSupplierId().toString() : null);
                    stkDepInventory.setMainBarcode(inventory.getMainBarcode());
                    stkDepInventory.setSubBarcode(inventory.getSubBarcode());
                    stkDepInventory.setOutOrderNo(stkIoBill.getBillNo());
                    stkDepInventory.setBatchNumber(entry.getBatchNumber());
                    stkDepInventory.setReceiptConfirmStatus(0);
                    stkDepInventory.setBillId(stkIoBill.getId());
                    stkDepInventory.setBillEntryId(entry.getId());
                    stkDepInventory.setBillNo(stkIoBill.getBillNo());
                    stkDepInventory.setBillType(201);
                    stkDepInventory.setRemark("本库存由科室出库业务生成");
                    if (StringUtils.isEmpty(stkDepInventory.getTenantId())) {
                        stkDepInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    }
                    if (StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                        stkDepInventory.setSettlementType(stkIoBill.getSettlementType());
                    }
                    stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
                    if (entry.getId() != null) {
                        stkIoBillMapper.updateStkIoBillEntryKcNo(entry.getId(), stkDepInventory.getId());
                    }
                }else if(billType == 301){//退货
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);

                    if(inventory == null){
                        throw new ServiceException(String.format("退货-批次号：%s，不存在!", batchNo));
                    }
                    BigDecimal inventoryQty = inventory.getQty();//实际库存数量
                    BigDecimal unitPrice = inventory.getUnitPrice();

                    //退货数量不能大于库存数量
                    if(qty.compareTo(inventoryQty) > 0){
                        throw new ServiceException(String.format("实际库存不足！退货数量：%s，实际库存：%s", qty,inventoryQty));
                    }else{
                        BigDecimal subQty = inventoryQty.subtract(qty);
                        if (unitPrice == null || subQty == null) {
                            throw new ServiceException("单价或库存数量为空");
                        }
                        inventory.setQty(subQty);
                        BigDecimal amt = subQty.multiply(unitPrice);
                        inventory.setAmt(amt);
                        inventory.setUpdateTime(new Date());
                        inventory.setUpdateBy(SecurityUtils.getUserIdStr());
                        //更新库存明细表
                        stkInventoryMapper.updateStkInventory(inventory);
                    }
                    // 插仓库流水（lx=TH，kc_no=仓库库存id）
                    HcCkFlow thFlow = new HcCkFlow();
                    thFlow.setBillId(stkIoBill.getId());
                    thFlow.setEntryId(entry.getId());
                    thFlow.setWarehouseId(stkIoBill.getWarehouseId() != null ? stkIoBill.getWarehouseId() : inventory.getWarehouseId());
                    thFlow.setMaterialId(entry.getMaterialId());
                    thFlow.setBatchNo(entry.getBatchNo());
                    thFlow.setBatchNumber(entry.getBatchNumber());
                    thFlow.setQty(entry.getQty());
                    thFlow.setUnitPrice(inventory.getUnitPrice());
                    thFlow.setAmt(entry.getAmt());
                    thFlow.setBeginTime(entry.getBeginTime());
                    thFlow.setEndTime(entry.getEndTime());
                    thFlow.setSupplierId(inventory.getSupplierId());
                    thFlow.setLx("TH");
                    thFlow.setKcNo(inventory.getId());
                    thFlow.setFlowTime(new Date());
                    thFlow.setDelFlag(0);
                    thFlow.setCreateTime(new Date());
                    thFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(thFlow.getTenantId())) thFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(thFlow);
                }else if(billType == 401){//退库（仅允许对已收货确认的科室库存退库）
                    String batchNo = entry.getBatchNo();//退库批次号
                    BigDecimal qty = entry.getQty();//退库数量

                    StkDepInventory stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo);
                    if(stkDepInventory == null){
                        throw new ServiceException(String.format("退库-批次号：%s，未收货确认或不存在，不能退库!", batchNo));
                    }
                    if(stkDepInventory.getReceiptConfirmStatus() == null || stkDepInventory.getReceiptConfirmStatus() != 1){
                        throw new ServiceException(String.format("退库-批次号：%s，科室库存未收货确认，不能退库!", batchNo));
                    }

                    BigDecimal stkDepInventoryQty = stkDepInventory.getQty();//科室库存实际数量
                    if(qty.compareTo(stkDepInventoryQty) > 0){
                        throw new ServiceException(String.format("科室库存不足！退库数量：%s，实际库存：%s", qty,stkDepInventoryQty));
                    }
                    stkDepInventory.setQty(stkDepInventoryQty.subtract(qty));
                    stkDepInventory.setUpdateTime(new Date());
                    stkDepInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);

                    //更新库存数量
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);

                    if(inventory == null){
                        throw new ServiceException(String.format("退库-批次号：%s，不存在!", batchNo));
                    }
                    BigDecimal inventoryQty = inventory.getQty();
                    inventoryQty = inventoryQty.add(qty);

                    inventory.setQty(inventoryQty);
                    inventory.setAmt(inventoryQty.multiply(inventory.getUnitPrice()));
                    inventory.setUpdateTime(new Date());
                    inventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkInventoryMapper.updateStkInventory(inventory);
                    // 插仓库流水（lx=TK，kc_no=仓库库存id）
                    HcCkFlow tkFlow = new HcCkFlow();
                    tkFlow.setBillId(stkIoBill.getId());
                    tkFlow.setEntryId(entry.getId());
                    tkFlow.setWarehouseId(stkIoBill.getWarehouseId() != null ? stkIoBill.getWarehouseId() : inventory.getWarehouseId());
                    tkFlow.setMaterialId(entry.getMaterialId());
                    tkFlow.setBatchNo(entry.getBatchNo());
                    tkFlow.setBatchNumber(entry.getBatchNumber());
                    tkFlow.setQty(entry.getQty());
                    tkFlow.setUnitPrice(inventory.getUnitPrice());
                    tkFlow.setAmt(entry.getAmt());
                    tkFlow.setBeginTime(entry.getBeginTime());
                    tkFlow.setEndTime(entry.getEndTime());
                    tkFlow.setSupplierId(inventory.getSupplierId());
                    tkFlow.setLx("TK");
                    tkFlow.setKcNo(inventory.getId());
                    tkFlow.setFlowTime(new Date());
                    tkFlow.setDelFlag(0);
                    tkFlow.setCreateTime(new Date());
                    tkFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(tkFlow.getTenantId())) tkFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(tkFlow);
                } else if (billType == 501) {// 调拨：转出仓库扣减+流水ZC，转入仓库增加+流水ZR
                    Long outWarehouseId = stkIoBill.getWarehouseId();  // 转出仓库
                    Long inWarehouseId = stkIoBill.getDepartmentId(); // 调拨单中 department_id 存调入仓库id
                    if (outWarehouseId == null) {
                        throw new ServiceException("调拨单转出仓库不能为空");
                    }
                    if (inWarehouseId == null) {
                        throw new ServiceException("调拨单调入仓库不能为空");
                    }
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();

                    // 1) 转出仓库：优先按明细 kc_no（库存id）查库存，否则按批次号+转出仓库查；扣减并插流水 ZC
                    StkInventory outInventory = null;
                    if (entry.getKcNo() != null) {
                        outInventory = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
                    }
                    if (outInventory == null) {
                        outInventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, outWarehouseId);
                    }
                    if (outInventory == null) {
                        outInventory = stkInventoryMapper.selectStkInventoryOne(batchNo);
                    }
                    if (outInventory == null) {
                        throw new ServiceException(String.format("调拨-批次号：%s，在转出仓库不存在!", batchNo));
                    }
                    if (!outWarehouseId.equals(outInventory.getWarehouseId())) {
                        throw new ServiceException(String.format("调拨-批次号：%s，不在转出仓库!", batchNo));
                    }
                    validateInventory(batchNo, stkIoBillEntryList);
                    BigDecimal outQty = outInventory.getQty();
                    BigDecimal unitPrice = outInventory.getUnitPrice();
                    if (qty.compareTo(outQty) > 0) {
                        throw new ServiceException(String.format("调拨转出库存不足！数量：%s，实际库存：%s", qty, outQty));
                    }
                    BigDecimal outSubQty = outQty.subtract(qty);
                    outInventory.setQty(outSubQty);
                    outInventory.setAmt(unitPrice != null && outSubQty != null ? outSubQty.multiply(unitPrice) : BigDecimal.ZERO);
                    outInventory.setUpdateTime(new Date());
                    outInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkInventoryMapper.updateStkInventory(outInventory);

                    HcCkFlow zcFlow = new HcCkFlow();
                    zcFlow.setBillId(stkIoBill.getId());
                    zcFlow.setEntryId(entry.getId());
                    zcFlow.setWarehouseId(outWarehouseId);
                    zcFlow.setMaterialId(entry.getMaterialId());
                    zcFlow.setBatchNo(entry.getBatchNo());
                    zcFlow.setBatchNumber(entry.getBatchNumber());
                    zcFlow.setQty(entry.getQty());
                    zcFlow.setUnitPrice(entry.getUnitPrice());
                    zcFlow.setAmt(entry.getAmt());
                    zcFlow.setBeginTime(entry.getBeginTime());
                    zcFlow.setEndTime(entry.getEndTime());
                    zcFlow.setSupplierId(outInventory.getSupplierId());
                    zcFlow.setLx("ZC");
                    zcFlow.setKcNo(outInventory.getId());
                    zcFlow.setFlowTime(new Date());
                    zcFlow.setDelFlag(0);
                    zcFlow.setCreateTime(new Date());
                    zcFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(zcFlow.getTenantId())) zcFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(zcFlow);

                    // 2) 转入仓库：增加库存，插流水 ZR
                    StkInventory inInventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, inWarehouseId);
                    if (inInventory == null) {
                        StkBatch stkBatch = null;
                        if (StringUtils.isNotEmpty(batchNo)) {
                            stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
                            if (stkBatch == null) {
                                stkBatch = buildStkBatchForInbound(entry, stkIoBill, supplerId);
                                stkBatchMapper.insertStkBatch(stkBatch);
                            }
                        }
                        inInventory = new StkInventory();
                        inInventory.setBatchNo(batchNo);
                        if (stkBatch != null) {
                            inInventory.setBatchId(stkBatch.getId());
                        }
                        inInventory.setMaterialNo(entry.getBatchNumber());
                        inInventory.setMaterialId(entry.getMaterialId());
                        inInventory.setWarehouseId(inWarehouseId);
                        inInventory.setQty(qty);
                        BigDecimal inUnitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                        inInventory.setUnitPrice(inUnitPrice);
                        inInventory.setAmt(entry.getAmt());
                        inInventory.setMaterialDate(new Date());
                        inInventory.setWarehouseDate(new Date());
                        inInventory.setSupplierId(supplerId);
                        inInventory.setBeginTime(entry.getBeginTime());
                        inInventory.setEndTime(entry.getEndTime());
                        inInventory.setReceiptOrderNo(stkIoBill.getBillNo());
                        inInventory.setCreateTime(new Date());
                        inInventory.setCreateBy(SecurityUtils.getUserIdStr());
                        if (StringUtils.isEmpty(inInventory.getTenantId())) {
                            inInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                        }
                        stkInventoryMapper.insertStkInventory(inInventory);

                        HcCkFlow zrFlow = new HcCkFlow();
                        zrFlow.setBillId(stkIoBill.getId());
                        zrFlow.setEntryId(entry.getId());
                        zrFlow.setWarehouseId(inWarehouseId);
                        zrFlow.setMaterialId(entry.getMaterialId());
                        zrFlow.setBatchNo(entry.getBatchNo());
                        zrFlow.setBatchNumber(entry.getBatchNumber());
                        zrFlow.setQty(entry.getQty());
                        zrFlow.setUnitPrice(inInventory.getUnitPrice());
                        zrFlow.setAmt(entry.getAmt());
                        zrFlow.setBeginTime(entry.getBeginTime());
                        zrFlow.setEndTime(entry.getEndTime());
                        zrFlow.setSupplierId(supplerId);
                        zrFlow.setLx("ZR");
                        zrFlow.setKcNo(inInventory.getId());
                        zrFlow.setFlowTime(new Date());
                        zrFlow.setDelFlag(0);
                        zrFlow.setCreateTime(new Date());
                        zrFlow.setCreateBy(SecurityUtils.getUserIdStr());
                        if (StringUtils.isEmpty(zrFlow.getTenantId())) zrFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                        hcCkFlowMapper.insertHcCkFlow(zrFlow);
                    } else {
                        BigDecimal inQty = inInventory.getQty().add(qty);
                        inInventory.setQty(inQty);
                        BigDecimal inUp = inInventory.getUnitPrice();
                        inInventory.setAmt(inUp != null && inQty != null ? inQty.multiply(inUp) : BigDecimal.ZERO);
                        inInventory.setUpdateTime(new Date());
                        inInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                        stkInventoryMapper.updateStkInventory(inInventory);

                        HcCkFlow zrFlow = new HcCkFlow();
                        zrFlow.setBillId(stkIoBill.getId());
                        zrFlow.setEntryId(entry.getId());
                        zrFlow.setWarehouseId(inWarehouseId);
                        zrFlow.setMaterialId(entry.getMaterialId());
                        zrFlow.setBatchNo(entry.getBatchNo());
                        zrFlow.setBatchNumber(entry.getBatchNumber());
                        zrFlow.setQty(entry.getQty());
                        zrFlow.setUnitPrice(inInventory.getUnitPrice());
                        zrFlow.setAmt(entry.getAmt());
                        zrFlow.setBeginTime(entry.getBeginTime());
                        zrFlow.setEndTime(entry.getEndTime());
                        zrFlow.setSupplierId(inInventory.getSupplierId());
                        zrFlow.setLx("ZR");
                        zrFlow.setKcNo(inInventory.getId());
                        zrFlow.setFlowTime(new Date());
                        zrFlow.setDelFlag(0);
                        zrFlow.setCreateTime(new Date());
                        zrFlow.setCreateBy(SecurityUtils.getUserIdStr());
                        if (StringUtils.isEmpty(zrFlow.getTenantId())) zrFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                        hcCkFlowMapper.insertHcCkFlow(zrFlow);
                    }
                }
            }
        }
    }

    /**
     * 入库审核时组装批次记录（采购入库）
     */
    private StkBatch buildStkBatchForInbound(StkIoBillEntry entry, StkIoBill stkIoBill, Long supplerId) {
        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setBatchNumber(entry.getBatchNumber());
        b.setMainBarcode(entry.getMainBarcode());
        b.setSubBarcode(entry.getSubBarcode());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        b.setUnitPrice(unitPrice);
        b.setBillId(stkIoBill.getId());
        b.setBillNo(stkIoBill.getBillNo());
        b.setEntryId(entry.getId());
        b.setBatchSource("1"); // 采购入库
        Date now = new Date();
        String username = SecurityUtils.getUserIdStr();
        b.setAuditTime(now);
        b.setAuditBy(username);
        b.setCreateTime(now);
        b.setCreateBy(username);
        b.setDelFlag(0);

        if (entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null) {
                b.setMaterialCode(m.getCode());
                b.setMaterialName(m.getName());
                b.setSpeci(m.getSpeci());
                b.setModel(m.getModel());
                b.setRegisterNo(m.getRegisterNo());
                b.setPermitNo(m.getPermitNo());
                b.setUnitId(m.getUnitId());
                if (m.getUnitId() != null) {
                    FdUnit u = fdUnitMapper.selectFdUnitByUnitId(m.getUnitId());
                    if (u != null) {
                        b.setUnitName(u.getUnitName());
                    }
                }
                if (m.getFactoryId() != null) {
                    FdFactory f = fdFactoryMapper.selectFdFactoryByFactoryId(m.getFactoryId());
                    if (f != null) {
                        b.setFactoryId(f.getFactoryId());
                        b.setFactoryCode(f.getFactoryCode());
                        b.setFactoryName(f.getFactoryName());
                    }
                }
                if (m.getStoreroomId() != null) {
                    FdWarehouseCategory wc = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(m.getStoreroomId());
                    if (wc != null) {
                        b.setStoreroomId(wc.getWarehouseCategoryId());
                        b.setStoreroomCode(wc.getWarehouseCategoryCode());
                        b.setStoreroomName(wc.getWarehouseCategoryName());
                    }
                }
                if (m.getFinanceCategoryId() != null) {
                    FdFinanceCategory fc = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(m.getFinanceCategoryId());
                    if (fc != null) {
                        b.setFinanceCategoryId(fc.getFinanceCategoryId());
                        b.setFinanceCategoryCode(fc.getFinanceCategoryCode());
                        b.setFinanceCategoryName(fc.getFinanceCategoryName());
                    }
                }
            }
        }
        if (supplerId != null) {
            FdSupplier sup = fdSupplierMapper.selectFdSupplierById(supplerId);
            if (sup != null) {
                b.setSupplierId(sup.getId());
                b.setSupplierCode(sup.getCode());
                b.setSupplierName(sup.getName());
            }
        }
        if (stkIoBill.getWarehouseId() != null) {
            b.setWarehouseId(stkIoBill.getWarehouseId());
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(stkIoBill.getWarehouseId()));
            if (wh != null) {
                b.setWarehouseCode(wh.getCode());
                b.setWarehouseName(wh.getName());
            }
        }
        return b;
    }

    /**
     * 更新科室库存
     * @param inventory 库存明细
     * @param stkIoBill 出入库库存表
     * @param entry 出入库库存明细表
     */
    private void updateDepInventory(StkInventory inventory,StkIoBill stkIoBill,StkIoBillEntry entry){

        String batchNo = entry.getBatchNo();
        StkDepInventory stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo);

        if(stkDepInventory == null){
            //更新科室库存明细表
            stkDepInventory = new StkDepInventory();
            stkDepInventory.setMaterialId(entry.getMaterialId());
            stkDepInventory.setMaterialNo(entry.getBatchNumber());
            stkDepInventory.setDepartmentId(stkIoBill.getDepartmentId());
            stkDepInventory.setQty(entry.getQty());
            stkDepInventory.setUnitPrice(entry.getUnitPrice());
            stkDepInventory.setAmt(entry.getAmt());
            stkDepInventory.setBatchNo(entry.getBatchNo());
            stkDepInventory.setMaterialNo(inventory.getMaterialNo());
            stkDepInventory.setMaterialDate(inventory.getMaterialDate());
            stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
            stkDepInventory.setBeginDate(inventory.getBeginTime());
            stkDepInventory.setEndDate(inventory.getEndTime());
            stkDepInventory.setSupplierId(inventory.getSupplierId().toString());
            stkDepInventory.setBatchNumber(entry.getBatchNumber());
            if (StringUtils.isEmpty(stkDepInventory.getTenantId()) && stkIoBill != null) {
                stkDepInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
            }
            if (stkIoBill != null && StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                stkDepInventory.setSettlementType(stkIoBill.getSettlementType());
            }
            stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
        }else{
            BigDecimal oldQty = stkDepInventory.getQty();
            BigDecimal qty = entry.getQty();

            stkDepInventory.setQty(oldQty.add(qty));//数量
            stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
        }
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }

    /**
     * 新增出入库明细信息
     *
     * @param stkIoBill 出入库对象
     */
    public void insertStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId();
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBatchNo(getBatchNumber());
                stkIoBillEntry.setDelFlag(0);
                if (StringUtils.isNotEmpty(tenantId)) stkIoBillEntry.setTenantId(tenantId);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 更新出入库明细信息
     *
     * @param stkIoBill 出入库对象
     */
    public void updateStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId();
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                if(StringUtils.isEmpty(stkIoBillEntry.getBatchNo())){
                    stkIoBillEntry.setBatchNo(getBatchNumber());
                }
                if (StringUtils.isNotEmpty(tenantId)) stkIoBillEntry.setTenantId(tenantId);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 新增出库
     *
     * @param stkIoBill 出库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertOutStkIoBill(StkIoBill stkIoBill)
    {
        if (StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        stkIoBill.setBillNo(getBillNumber("CK"));
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertOutStkIoBillEntry(stkIoBill);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getBillNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectOutMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    @Transactional
    @Override
    public int updateOutStkIoBill(StkIoBill stkIoBill) {
        // 如果退货日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId(), SecurityUtils.getUserIdStr(), new Date());
        insertOutStkIoBillEntry(stkIoBill);
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    @Transactional
    @Override
    public int insertTkStkIoBill(StkIoBill stkIoBill) {
        if (StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        stkIoBill.setBillNo(getTKNumber("TK"));
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertTKStkIoBillEntry(stkIoBill);
        return rows;
    }

    public String getTKNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectTKMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    public String getTHNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectTHMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    public String getJSNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectJSMaxBillNo(date);
        return FillRuleUtil.getNumber(str, maxNum, date);
    }

    /** 按指定日期生成单号（调拨单用制单日期，与前端“今天”一致） */
    public String getJSNumber(String str, String date) {
        String maxNum = stkIoBillMapper.selectJSMaxBillNo(date);
        return FillRuleUtil.getNumber(str, maxNum, date);
    }

    @Transactional
    @Override
    public int insertTHStkIoBill(StkIoBill stkIoBill) {
        if (StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        // 如果退货日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setBillNo(getTHNumber("TH"));
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertOutStkIoBillEntry(stkIoBill);
        return rows;
    }

    @Transactional
    @Override
    public int updateTKStkIoBill(StkIoBill stkIoBill) {
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId(), SecurityUtils.getUserIdStr(), new Date());
        insertTKStkIoBillEntry(stkIoBill);
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectRTHStkIoBillList(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectRTHStkIoBillList(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectCTKStkIoBillList(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectCTKStkIoBillList(stkIoBill);
    }
    @Override
    public List<Map<String, Object>> selectOutboundSummaryByDepartment() {
        return stkIoBillMapper.selectOutboundSummaryByDepartment();
    }

    public List<Map<String, Object>> selectRTHStkIoBillSummaryList(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectRTHStkIoBillSummaryList(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectCTKStkIoBillListSummary(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectCTKStkIoBillListSummary(stkIoBill);
    }

    @Override
    public TotalInfo selectCTKStkIoBillListTotal(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectCTKStkIoBillListTotal(stkIoBill);
    }

    @Override
    public TotalInfo selectCTKStkIoBillListSummaryTotal(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectCTKStkIoBillListSummaryTotal(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectHistoryInventory(String previousDateString) {
        return stkIoBillMapper.selectHistoryInventory(previousDateString);
    }

    /**
     * 查询进销存明细列表
     * @param stkIoBill
     * @return
     */
    @Override
    public List<Map<String, Object>> selectListPurInventory(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectListPurInventory(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectMonthInitDataList(String beginDate,String endDate,String toStatDate,String toEndDate) {
        return stkIoBillMapper.selectMonthInitDataList(beginDate,endDate,toStatDate,toEndDate);
    }

    @Override
    public List<StkIoBill> getMonthHandleDataList(String beginDate, String endDate) {
        return stkIoBillMapper.getMonthHandleDataList(beginDate,endDate);
    }

    /**
     * 新增退库明细信息
     *
     * @param stkIoBill 出库对象
     */
    public void insertTKStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                validateTKInventory(stkIoBillEntry.getBatchNo(),stkIoBillEntryList);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setDelFlag(0);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 校验科室商品库存
     * @param stkIoBillEntryList
     */
    private void validateTKInventory(String oldBatchNo,List<StkIoBillEntry> stkIoBillEntryList){
        for(StkIoBillEntry entry : stkIoBillEntryList){
            String batchNo = entry.getBatchNo();
            BigDecimal qty = entry.getQty();//退库数量

            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = stkDepInventoryMapper.selectTKStkInvntoryByBatchNo(batchNo);

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("科室库存不足！退库数量：%s，实际库存：%s", qty,inventoryQty));
            }

        }
    }

    /**
     * 新增出库明细信息
     *
     * @param stkIoBill 出库对象
     */
    public void insertOutStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                validateInventory(stkIoBillEntry.getBatchNo(),stkIoBillEntryList);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setDelFlag(0);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 校验商品库存
     * @param stkIoBillEntryList
     */
    private void validateInventory(String oldBatchNo,List<StkIoBillEntry> stkIoBillEntryList){
        for(StkIoBillEntry stkIoBillEntry : stkIoBillEntryList){
            String batchNo = stkIoBillEntry.getBatchNo();
            BigDecimal qty = stkIoBillEntry.getQty();//出库数量

            if(oldBatchNo == null || batchNo == null || !oldBatchNo.equals(batchNo)){
                continue;
            }
            if (qty == null) {
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = stkInventoryMapper.selectStkInvntoryByBatchNo(batchNo);
            if (inventoryQty == null) {
                continue;
            }

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("实际库存不足！出库数量：%s，实际库存：%s", qty,inventoryQty));
            }

        }
    }

    @Override
    public StkIoBill createCkEntriesByDApply(String dApplyId) {
        BasApply basApply = this.basApplyMapper.selectBasApplyById(Long.valueOf(dApplyId));
        if (basApply == null) {
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", dApplyId));
        }
        if (basApply.getApplyBillStatus() != 2) {
            throw new ServiceException(String.format("科室申领ID：%s，未审核，不能生成出库单!", dApplyId));
        }
        StkIoBill stkIoBill = new StkIoBill();
        stkIoBill.setDApplyId(dApplyId);
        stkIoBill.setDepartmentId(basApply.getDepartmentId());
        stkIoBill.setWarehouseId(basApply.getWarehouseId());
        stkIoBill.setBillType(201);
        // 设置引用单号为科室申请单号
        stkIoBill.setRefBillNo(basApply.getApplyBillNo());
        List<BasApplyEntry> list = basApply.getBasApplyEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("科室申领ID：%s，明细不存在!", dApplyId));
        }
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (BasApplyEntry basApplyEntry : list) {
            StkIoBillEntry stkIoBillEntry = new StkIoBillEntry();
            stkIoBillEntry.setMaterialId(basApplyEntry.getMaterialId());
            stkIoBillEntry.setQty(basApplyEntry.getQty());
            stkIoBillEntry.setUnitPrice(basApplyEntry.getUnitPrice());
            stkIoBillEntry.setAmt(basApplyEntry.getAmt());
            // 加载完整的material对象
            if (basApplyEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(basApplyEntry.getMaterialId());
                stkIoBillEntry.setMaterial(material);
            }
            entryList.add(stkIoBillEntry);
        }
        stkIoBill.setStkIoBillEntryList(entryList);
        stkIoBill.setDepartmentId(basApply.getDepartmentId());
        return stkIoBill;
    }

    @Override
    public StkIoBill createCkEntriesByRkApply(String rkApplyId) {
        StkIoBill rkBill = this.stkIoBillMapper.selectStkIoBillById(Long.valueOf(rkApplyId));
        if (rkBill == null) {
            throw new ServiceException(String.format("入库单ID：%s，不存在!", rkApplyId));
        }
        if (rkBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("入库单ID：%s，未审核，不能生成出库单!", rkApplyId));
        }

        List<StkIoBillEntry> list = rkBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("入库单ID：%s，明细不存在!", rkApplyId));
        }

        StkIoBill ckBill = new StkIoBill();
        ckBill.setWarehouseId(rkBill.getWarehouseId());
        ckBill.setBillType(201);
        // 设置引用单号为入库单号
        ckBill.setRefBillNo(rkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (StkIoBillEntry rkEntry : list) {
            StkIoBillEntry ckEntry = new StkIoBillEntry();
            ckEntry.setMaterialId(rkEntry.getMaterialId());
            ckEntry.setQty(rkEntry.getQty());
            ckEntry.setUnitPrice(rkEntry.getUnitPrice());
            ckEntry.setAmt(rkEntry.getAmt());
            ckEntry.setBatchNo(rkEntry.getBatchNo());
            ckEntry.setBatchNumber(rkEntry.getBatchNumber());
            ckEntry.setBeginTime(rkEntry.getBeginTime());
            ckEntry.setEndTime(rkEntry.getEndTime());
            // 加载完整的material对象
            if (rkEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(rkEntry.getMaterialId());
                ckEntry.setMaterial(material);
            }
            entryList.add(ckEntry);
        }
        ckBill.setStkIoBillEntryList(entryList);
        return ckBill;
    }

    @Override
    public StkIoBill createRkEntriesByDingdan(String dingdanId) {
        PurchaseOrder purchaseOrder = this.purchaseOrderMapper.selectPurchaseOrderById(Long.valueOf(dingdanId));
        if (purchaseOrder == null) {
            throw new ServiceException(String.format("采购订单ID：%s，不存在!", dingdanId));
        }
        if (!"2".equals(purchaseOrder.getOrderStatus())) {
            throw new ServiceException(String.format("采购订单ID：%s，未审核，不能生成入库单!", dingdanId));
        }
        // 手动查询采购订单明细数据
        List<PurchaseOrderEntry> list = purchaseOrderMapper.selectPurchaseOrderEntryByParentId(Long.valueOf(dingdanId));
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("采购订单ID：%s，明细不存在!", dingdanId));
        }
        StkIoBill stkIoBill = new StkIoBill();
        stkIoBill.setWarehouseId(purchaseOrder.getWarehouseId());
        stkIoBill.setSupplerId(purchaseOrder.getSupplierId());
        stkIoBill.setBillType(101);
        // 设置引用单号为采购订单号
        stkIoBill.setRefBillNo(purchaseOrder.getOrderNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (PurchaseOrderEntry purchaseOrderEntry : list) {
            StkIoBillEntry stkIoBillEntry = new StkIoBillEntry();
            stkIoBillEntry.setMaterialId(purchaseOrderEntry.getMaterialId());
            stkIoBillEntry.setQty(purchaseOrderEntry.getOrderQty());
            stkIoBillEntry.setUnitPrice(purchaseOrderEntry.getUnitPrice());
            // 计算金额，添加null检查避免空指针异常
            if (purchaseOrderEntry.getOrderQty() != null && purchaseOrderEntry.getUnitPrice() != null) {
                stkIoBillEntry.setAmt(purchaseOrderEntry.getOrderQty().multiply(purchaseOrderEntry.getUnitPrice()));
            } else {
                stkIoBillEntry.setAmt(BigDecimal.ZERO);
            }
            // 加载耗材详细信息，前端表格需要显示耗材的名称、规格等信息
            if (purchaseOrderEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(purchaseOrderEntry.getMaterialId());
                stkIoBillEntry.setMaterial(material);
            }
            entryList.add(stkIoBillEntry);
        }
        stkIoBill.setStkIoBillEntryList(entryList);
        return stkIoBill;
    }

    @Override
    public StkIoBill createThEntriesByRkApply(String rkApplyId) {
        StkIoBill rkBill = this.stkIoBillMapper.selectStkIoBillById(Long.valueOf(rkApplyId));
        if (rkBill == null) {
            throw new ServiceException(String.format("入库单ID：%s，不存在!", rkApplyId));
        }
        if (rkBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("入库单ID：%s，未审核，不能生成出库单!", rkApplyId));
        }

        List<StkIoBillEntry> list = rkBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("入库单ID：%s，明细不存在!", rkApplyId));
        }
        StkIoBill thBill = new StkIoBill();
        thBill.setWarehouseId(rkBill.getWarehouseId());
        thBill.setSupplerId(rkBill.getSupplerId());
        thBill.setBillType(301);
        // 设置引用单号为入库单号
        thBill.setRefBillNo(rkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (StkIoBillEntry stkIoBillEntry : list) {
            StkIoBillEntry thEntry = new StkIoBillEntry();
            thEntry.setMaterialId(stkIoBillEntry.getMaterialId());
            thEntry.setQty(stkIoBillEntry.getQty());
            thEntry.setUnitPrice(stkIoBillEntry.getUnitPrice());
            thEntry.setAmt(stkIoBillEntry.getAmt());
            thEntry.setBatchNo(stkIoBillEntry.getBatchNo());
            thEntry.setBatchNumber(stkIoBillEntry.getBatchNumber());
            thEntry.setBeginTime(stkIoBillEntry.getBeginTime());
            thEntry.setEndTime(stkIoBillEntry.getEndTime());
            // 加载耗材详细信息，前端表格需要显示耗材的名称、规格等信息
            if (stkIoBillEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(stkIoBillEntry.getMaterialId());
                thEntry.setMaterial(material);
            }
            entryList.add(thEntry);
        }
        thBill.setStkIoBillEntryList(entryList);

        return thBill;
    }

    @Override
    public StkIoBill createTkEntriesByCkApply(String ckApplyId) {
        StkIoBill ckBill = this.stkIoBillMapper.selectStkIoBillById(Long.valueOf(ckApplyId));
        if (ckBill == null) {
            throw new ServiceException(String.format("出库单ID：%s，不存在!", ckApplyId));
        }
        if (ckBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("出库单ID：%s，未审核，不能生成出库单!", ckApplyId));
        }

        List<StkIoBillEntry> list = ckBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("出库单ID：%s，明细不存在!", ckApplyId));
        }

        StkIoBill tkBill = new StkIoBill();
        tkBill.setWarehouseId(ckBill.getWarehouseId());
        tkBill.setDepartmentId(ckBill.getDepartmentId());
        tkBill.setBillType(401);
        // 设置引用单号为出库单号
        tkBill.setRefBillNo(ckBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (StkIoBillEntry stkIoBillEntry : list) {
            StkIoBillEntry tkEntry = new StkIoBillEntry();
            tkEntry.setMaterialId(stkIoBillEntry.getMaterialId());
            tkEntry.setQty(stkIoBillEntry.getQty());
            tkEntry.setUnitPrice(stkIoBillEntry.getUnitPrice());
            tkEntry.setAmt(stkIoBillEntry.getAmt());
            tkEntry.setBatchNo(stkIoBillEntry.getBatchNo());
            tkEntry.setBatchNumber(stkIoBillEntry.getBatchNumber());
            tkEntry.setBeginTime(stkIoBillEntry.getBeginTime());
            tkEntry.setEndTime(stkIoBillEntry.getEndTime());
            // 加载完整的material对象
            if (stkIoBillEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(stkIoBillEntry.getMaterialId());
                tkEntry.setMaterial(material);
            }
            entryList.add(tkEntry);
        }
        tkBill.setStkIoBillEntryList(entryList);
        return tkBill;
    }


    @Override
    public StkIoBill createThEntriesByTkApply(String tkApplyId) {
        StkIoBill tkBill = this.stkIoBillMapper.selectStkIoBillById(Long.valueOf(tkApplyId));
        if (tkBill == null) {
            throw new ServiceException(String.format("入库单ID：%s，不存在!", tkApplyId));
        }
        if (tkBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("入库单ID：%s，未审核，不能生成出库单!", tkApplyId));
        }

        List<StkIoBillEntry> list = tkBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("入库单ID：%s，明细不存在!", tkApplyId));
        }
        StkIoBill thBill = new StkIoBill();
        thBill.setWarehouseId(tkBill.getWarehouseId());
        thBill.setSupplerId(tkBill.getSupplerId());
        thBill.setBillType(301);
        // 设置引用单号为科室退库单号
        thBill.setRefBillNo(tkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (StkIoBillEntry stkIoBillEntry : list) {
            StkIoBillEntry thEntry = new StkIoBillEntry();
            thEntry.setMaterialId(stkIoBillEntry.getMaterialId());
            thEntry.setQty(stkIoBillEntry.getQty());
            thEntry.setUnitPrice(stkIoBillEntry.getUnitPrice());
            thEntry.setAmt(stkIoBillEntry.getAmt());
            thEntry.setBatchNo(stkIoBillEntry.getBatchNo());
            thEntry.setBatchNumber(stkIoBillEntry.getBatchNumber());
            thEntry.setBeginTime(stkIoBillEntry.getBeginTime());
            thEntry.setEndTime(stkIoBillEntry.getEndTime());
            // 加载耗材详细信息，前端表格需要显示耗材的名称、规格等信息
            if (stkIoBillEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(stkIoBillEntry.getMaterialId());
                thEntry.setMaterial(material);
            }
            entryList.add(thEntry);
        }
        thBill.setStkIoBillEntryList(entryList);

        return thBill;
    }

    /**
     * 查询结算明细：根据供应商、日期范围、仓库结算类型查询出库明细
     */
    @Override
    public List<StkIoBillEntry> selectSettlementDetails(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectSettlementDetails(stkIoBill);
    }

    /**
     * 批量确认收货
     * @param ids 出库单ID列表（逗号分隔）
     * @param confirmBy 确认人
     * @return 结果
     */
    @Override
    @Transactional
    public int confirmReceipt(String ids, String confirmBy) {
        if (StringUtils.isEmpty(ids)) {
            throw new ServiceException("请选择要确认的出库单");
        }
        String[] idArray = ids.split(",");
        int successCount = 0;
        for (String idStr : idArray) {
            Long id = Long.parseLong(idStr.trim());
            StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
            if (stkIoBill == null) {
                continue;
            }
            // 只确认已审核的出库单
            if (stkIoBill.getBillStatus() != 2) {
                continue;
            }
            // 只确认出库单（billType=201）
            if (stkIoBill.getBillType() == null || stkIoBill.getBillType() != 201) {
                continue;
            }
            // 只确认未确认收货的出库单
            if (stkIoBill.getReceiptConfirmStatus() != null && stkIoBill.getReceiptConfirmStatus() == 1) {
                continue;
            }
            
            // 收货确认：科室库存在出库审核时已插入(未确认)，此处仅将对应科室库存更新为已确认并插科室流水；按单据主表id+明细id校验避免改错记录
            List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
            if (stkIoBillEntryList != null && !stkIoBillEntryList.isEmpty()) {
                for (StkIoBillEntry entry : stkIoBillEntryList) {
                    Long depId;
                    StkDepInventory stkDepInventory = null;
                    if (entry.getKcNo() != null) {
                        stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryById(entry.getKcNo());
                        if (stkDepInventory != null) {
                            // 校验科室库存记录是否确属当前出库单及当前明细，避免收货确认改错明细
                            if (!stkIoBill.getId().equals(stkDepInventory.getBillId()) || !entry.getId().equals(stkDepInventory.getBillEntryId())) {
                                throw new ServiceException(String.format("收货确认数据异常：科室库存id=%s 与出库单id=%s、明细id=%s 不一致（bill_id=%s, bill_entry_id=%s），请勿继续操作。",
                                        stkDepInventory.getId(), stkIoBill.getId(), entry.getId(),
                                        stkDepInventory.getBillId(), stkDepInventory.getBillEntryId()));
                            }
                            if (!Objects.equals(entry.getMaterialId(), stkDepInventory.getMaterialId())
                                    || (entry.getQty() != null && stkDepInventory.getQty() != null && entry.getQty().compareTo(stkDepInventory.getQty()) != 0)
                                    || (entry.getQty() == null ^ (stkDepInventory.getQty() == null))
                                    || !Objects.equals(entry.getBatchNo(), stkDepInventory.getBatchNo())) {
                                throw new ServiceException(String.format("收货确认数据异常：科室库存id=%s 与出库单明细耗材/数量/批次号不一致，请勿继续操作。", stkDepInventory.getId()));
                            }
                            if (stkDepInventory.getReceiptConfirmStatus() != null && stkDepInventory.getReceiptConfirmStatus() == 1) {
                                depId = stkDepInventory.getId();
                            } else {
                                stkDepInventory.setReceiptConfirmStatus(1);
                                stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
                                depId = stkDepInventory.getId();
                            }
                        } else {
                            throw new ServiceException(String.format("收货确认失败：出库单明细id=%s 对应的科室库存id=%s 不存在。", entry.getId(), entry.getKcNo()));
                        }
                    } else {
                        String batchNo = entry.getBatchNo();
                        Long warehouseId = stkIoBill.getWarehouseId();
                        StkInventory inventory = null;
                        if (warehouseId != null) {
                            inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, warehouseId);
                        }
                        if (inventory == null) {
                            inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);
                        }
                        if (inventory == null) {
                            continue;
                        }
                        stkDepInventory = new StkDepInventory();
                        stkDepInventory.setMaterialId(entry.getMaterialId());
                        stkDepInventory.setMaterialNo(inventory.getMaterialNo());
                        stkDepInventory.setDepartmentId(stkIoBill.getDepartmentId());
                        stkDepInventory.setQty(entry.getQty());
                        stkDepInventory.setUnitPrice(entry.getUnitPrice());
                        stkDepInventory.setAmt(entry.getAmt());
                        stkDepInventory.setBatchNo(entry.getBatchNo());
                        stkDepInventory.setMaterialDate(inventory.getMaterialDate());
                        stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
                        stkDepInventory.setBeginDate(inventory.getBeginTime());
                        stkDepInventory.setEndDate(inventory.getEndTime());
                        stkDepInventory.setSupplierId(inventory.getSupplierId() != null ? inventory.getSupplierId().toString() : null);
                        stkDepInventory.setOutOrderNo(stkIoBill.getBillNo());
                        stkDepInventory.setBatchNumber(entry.getBatchNumber());
                        stkDepInventory.setReceiptConfirmStatus(1);
                        stkDepInventory.setBillId(stkIoBill.getId());
                        stkDepInventory.setBillEntryId(entry.getId());
                        stkDepInventory.setBillNo(stkIoBill.getBillNo());
                        stkDepInventory.setBillType(201);
                        stkDepInventory.setRemark("本库存由科室出库业务生成");
                        if (StringUtils.isEmpty(stkDepInventory.getTenantId())) {
                            stkDepInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                        }
                        if (StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                            stkDepInventory.setSettlementType(stkIoBill.getSettlementType());
                        }
                        stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
                        depId = stkDepInventory.getId();
                        if (entry.getId() != null) {
                            stkIoBillMapper.updateStkIoBillEntryKcNo(entry.getId(), depId);
                        }
                        stkDepInventory.setKcNo(depId);
                        stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
                    }
                    StkInventory inventory = null;
                    String batchNo = entry.getBatchNo();
                    Long warehouseId = stkIoBill.getWarehouseId();
                    if (warehouseId != null) {
                        inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, warehouseId);
                    }
                    if (inventory == null) {
                        inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);
                    }
                    HcKsFlow ksFlow = new HcKsFlow();
                    ksFlow.setBillId(stkIoBill.getId());
                    ksFlow.setEntryId(entry.getId());
                    ksFlow.setDepartmentId(stkIoBill.getDepartmentId());
                    ksFlow.setMaterialId(entry.getMaterialId());
                    ksFlow.setBatchNo(entry.getBatchNo());
                    ksFlow.setBatchNumber(entry.getBatchNumber());
                    ksFlow.setQty(entry.getQty());
                    ksFlow.setUnitPrice(entry.getUnitPrice());
                    ksFlow.setAmt(entry.getAmt());
                    ksFlow.setBeginTime(entry.getBeginTime());
                    ksFlow.setEndTime(entry.getEndTime());
                    ksFlow.setSupplierId(inventory != null && inventory.getSupplierId() != null ? inventory.getSupplierId().toString() : null);
                    ksFlow.setKcNo(depId);
                    ksFlow.setLx("CK");
                    ksFlow.setFlowTime(new Date());
                    ksFlow.setDelFlag(0);
                    ksFlow.setCreateTime(new Date());
                    ksFlow.setCreateBy(confirmBy);
                    if (StringUtils.isEmpty(ksFlow.getTenantId())) ksFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcKsFlowMapper.insertHcKsFlow(ksFlow);
                }
            }
            
            stkIoBill.setReceiptConfirmStatus(1); // 已确认
            stkIoBill.setUpdateBy(confirmBy);
            stkIoBill.setUpdateTime(new Date());
            int res = stkIoBillMapper.updateStkIoBill(stkIoBill);
            if (res > 0) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public void exportOutWarehouseGroupedByBill(StkIoBill q, HttpServletResponse response) throws IOException
    {
        if (q == null)
        {
            q = new StkIoBill();
        }
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        List<Long> billIds = null;
        if (StringUtils.isNotEmpty(q.getExportBillIds()))
        {
            billIds = new ArrayList<>();
            for (String s : q.getExportBillIds().split(","))
            {
                if (s == null)
                {
                    continue;
                }
                s = s.trim();
                if (s.isEmpty())
                {
                    continue;
                }
                try
                {
                    billIds.add(Long.parseLong(s));
                }
                catch (NumberFormatException ignored)
                {
                }
            }
            if (billIds.isEmpty())
            {
                billIds = null;
            }
        }
        List<StkOutBillExportFlatRow> rows = stkIoBillMapper.selectOutBillGroupedExportRows(q, billIds);
        LinkedHashMap<Long, List<StkOutBillExportFlatRow>> byBill = new LinkedHashMap<>();
        for (StkOutBillExportFlatRow r : rows)
        {
            if (r.getBillId() == null)
            {
                continue;
            }
            byBill.computeIfAbsent(r.getBillId(), k -> new ArrayList<>()).add(r);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fnFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("拣货单导出");

        String hospitalName = resolveHospitalNameForExport(q);
        String mainTitleText = buildPickListMainTitle(q, hospitalName);

        Font fontBold = wb.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeightInPoints((short) 11);

        Font bigTitleFont = wb.createFont();
        bigTitleFont.setBold(true);
        bigTitleFont.setFontHeightInPoints((short) 16);

        CellStyle bigTitleStyle = wb.createCellStyle();
        bigTitleStyle.setFont(bigTitleFont);
        bigTitleStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        bigTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setThinBorderAround(bigTitleStyle);
        bigTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        bigTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bigTitleStyle.setWrapText(true);

        CellStyle billMergedInfoStyle = wb.createCellStyle();
        billMergedInfoStyle.setFont(fontBold);
        billMergedInfoStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        billMergedInfoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setThinBorderAround(billMergedInfoStyle);
        billMergedInfoStyle.setAlignment(HorizontalAlignment.LEFT);
        billMergedInfoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        billMergedInfoStyle.setWrapText(true);

        CellStyle detailHeadStyle = wb.createCellStyle();
        detailHeadStyle.setFont(fontBold);
        detailHeadStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        detailHeadStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setThinBorderAround(detailHeadStyle);
        detailHeadStyle.setAlignment(HorizontalAlignment.CENTER);
        detailHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dataTextStyle = wb.createCellStyle();
        setThinBorderAround(dataTextStyle);
        dataTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataTextStyle.setWrapText(true);

        CellStyle dataNumStyle = wb.createCellStyle();
        setThinBorderAround(dataNumStyle);
        dataNumStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataNumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.DataFormat df = wb.createDataFormat();
        dataNumStyle.setDataFormat(df.getFormat("#,##0.######"));

        CellStyle emptyMsgStyle = wb.createCellStyle();
        Font fMsg = wb.createFont();
        fMsg.setBold(true);
        fMsg.setFontHeightInPoints((short) 12);
        emptyMsgStyle.setFont(fMsg);
        emptyMsgStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        emptyMsgStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        emptyMsgStyle.setAlignment(HorizontalAlignment.CENTER);

        final int lastCol = 6;
        DataFormatter dataFormatter = new DataFormatter();

        int rowNum = 0;
        if (byBill.isEmpty())
        {
            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 14 * 256);
            sheet.setColumnWidth(2, 12 * 256);
            sheet.setColumnWidth(3, 8 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 14 * 256);
            sheet.setColumnWidth(6, 12 * 256);
            Row r0 = sheet.createRow(rowNum++);
            Cell c0 = r0.createCell(0);
            c0.setCellValue(mainTitleText);
            c0.setCellStyle(bigTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));
            r0.setHeightInPoints(estimateMergedRowHeightPoints(mainTitleText, sheet, 0, lastCol, 16));
            Row r1 = sheet.createRow(rowNum++);
            Cell c1a = r1.createCell(0);
            c1a.setCellValue("无符合条件的出库单或明细数据");
            c1a.setCellStyle(emptyMsgStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));
            r1.setHeightInPoints(24);
        }
        else
        {
            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 14 * 256);
            sheet.setColumnWidth(2, 12 * 256);
            sheet.setColumnWidth(3, 8 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 14 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            for (Map.Entry<Long, List<StkOutBillExportFlatRow>> e : byBill.entrySet())
            {
                List<StkOutBillExportFlatRow> detail = e.getValue();
                if (detail == null || detail.isEmpty())
                {
                    continue;
                }
                StkOutBillExportFlatRow first = detail.get(0);

                int bigTitleRow = rowNum;
                Row bigRow = sheet.createRow(rowNum++);
                Cell bt = bigRow.createCell(0);
                bt.setCellValue(mainTitleText);
                bt.setCellStyle(bigTitleStyle);
                sheet.addMergedRegion(new CellRangeAddress(bigTitleRow, bigTitleRow, 0, lastCol));
                bigRow.setHeightInPoints(estimateMergedRowHeightPoints(mainTitleText, sheet, 0, lastCol, 16));

                int infoRow = rowNum;
                Row info = sheet.createRow(rowNum++);
                String billNo = first.getBillNo() != null ? first.getBillNo() : "";
                String dept = first.getDepartmentName() != null ? first.getDepartmentName() : "";
                String infoText = "单据号：" + billNo + "        科室名称：" + dept;
                Cell ic = info.createCell(0);
                ic.setCellValue(infoText);
                ic.setCellStyle(billMergedInfoStyle);
                sheet.addMergedRegion(new CellRangeAddress(infoRow, infoRow, 0, lastCol));
                info.setHeightInPoints(estimateMergedRowHeightPoints(infoText, sheet, 0, lastCol, 11));

                Row head = sheet.createRow(rowNum++);
                head.setHeightInPoints(18);
                String[] cols = { "名称", "规格", "型号", "单位", "数量", "批号", "有效期" };
                for (int i = 0; i < cols.length; i++)
                {
                    Cell hc = head.createCell(i);
                    hc.setCellValue(cols[i]);
                    hc.setCellStyle(detailHeadStyle);
                }
                for (StkOutBillExportFlatRow r : detail)
                {
                    Row dr = sheet.createRow(rowNum++);
                    setCellStr(dr, 0, r.getMaterialName(), dataTextStyle);
                    setCellStr(dr, 1, r.getSpeci(), dataTextStyle);
                    setCellStr(dr, 2, r.getModel(), dataTextStyle);
                    setCellStr(dr, 3, r.getUnitName(), dataTextStyle);
                    Cell cq = dr.createCell(4);
                    if (r.getQty() != null)
                    {
                        cq.setCellValue(r.getQty().doubleValue());
                        cq.setCellStyle(dataNumStyle);
                    }
                    else
                    {
                        cq.setCellValue("");
                        cq.setCellStyle(dataTextStyle);
                    }
                    setCellStr(dr, 5, r.getBatchPh(), dataTextStyle);
                    setCellStr(dr, 6, r.getEndTime() != null ? sdf.format(r.getEndTime()) : "", dataTextStyle);
                    setDetailRowHeightAuto(dr, sheet, dataFormatter);
                }
                rowNum++;
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fn = URLEncoder.encode(mainTitleText + "_" + fnFmt.format(new Date()), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fn + ".xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    private String resolveHospitalNameForExport(StkIoBill q)
    {
        String tid = q != null ? q.getTenantId() : null;
        if (StringUtils.isEmpty(tid))
        {
            tid = SecurityUtils.getCustomerId();
        }
        if (StringUtils.isEmpty(tid))
        {
            return "";
        }
        try
        {
            SbCustomer c = sbCustomerService.selectSbCustomerById(tid);
            if (c != null && StringUtils.isNotEmpty(c.getCustomerName()))
            {
                return c.getCustomerName();
            }
        }
        catch (Exception ignored)
        {
        }
        return "";
    }

    private static String buildPickListMainTitle(StkIoBill q, String hospitalName)
    {
        Calendar cal = Calendar.getInstance();
        if (q != null)
        {
            if (q.getAuditEndDate() != null)
            {
                cal.setTime(q.getAuditEndDate());
            }
            else if (q.getEndDate() != null)
            {
                cal.setTime(q.getEndDate());
            }
            else if (q.getAuditBeginDate() != null)
            {
                cal.setTime(q.getAuditBeginDate());
            }
            else if (q.getBeginDate() != null)
            {
                cal.setTime(q.getBeginDate());
            }
        }
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        String h = StringUtils.isNotEmpty(hospitalName) ? hospitalName : "本院";
        return y + "年" + m + "月份" + h + "拣货单";
    }

    private static int textDisplayUnits(String s)
    {
        if (s == null || s.isEmpty())
        {
            return 0;
        }
        int u = 0;
        for (int i = 0; i < s.length(); i++)
        {
            char ch = s.charAt(i);
            u += ch > 127 ? 2 : 1;
        }
        return Math.max(u, 1);
    }

    private static double mergedWidthChars(Sheet sheet, int fromCol, int toCol)
    {
        double w = 0;
        for (int c = fromCol; c <= toCol; c++)
        {
            w += sheet.getColumnWidth(c) / 256.0;
        }
        return Math.max(w, 6);
    }

    private static float estimateMergedRowHeightPoints(String text, Sheet sheet, int c0, int c1, int fontPt)
    {
        if (text == null)
        {
            text = "";
        }
        double widthChars = mergedWidthChars(sheet, c0, c1);
        double lineCapacity = widthChars * 1.85;
        int lines = Math.max(1, (int) Math.ceil(textDisplayUnits(text) / lineCapacity));
        float lineH = Math.max(13f, fontPt * 1.3f);
        return Math.min(409f, lines * lineH + 8f);
    }

    private static void setDetailRowHeightAuto(Row row, Sheet sheet, DataFormatter df)
    {
        int maxLines = 1;
        for (int col = 0; col <= 6; col++)
        {
            Cell c = row.getCell(col);
            if (c == null)
            {
                continue;
            }
            String s = df.formatCellValue(c);
            int cw = sheet.getColumnWidth(col);
            double lineCap = Math.max(3.5, cw / 256.0) * 1.85;
            int lines = Math.max(1, (int) Math.ceil(textDisplayUnits(s) / lineCap));
            maxLines = Math.max(maxLines, lines);
        }
        row.setHeightInPoints(Math.min(409f, maxLines * 15f + 8f));
    }

    private static void setThinBorderAround(CellStyle style)
    {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    }

    private static void setCellStr(Row row, int col, String val, CellStyle style)
    {
        Cell c = row.createCell(col);
        c.setCellValue(val != null ? val : "");
        c.setCellStyle(style);
    }
}
