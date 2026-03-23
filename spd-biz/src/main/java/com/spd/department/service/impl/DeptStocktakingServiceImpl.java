package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.domain.StkInventory;
import com.spd.department.mapper.DeptStocktakingMapper;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import com.spd.department.service.IDeptStocktakingService;

/**
 * 科室盘点Service业务层处理
 *
 * @author spd
 * @date 2025-01-28
 */
@Service
public class DeptStocktakingServiceImpl implements IDeptStocktakingService
{
    @Autowired
    private DeptStocktakingMapper deptStocktakingMapper;

    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private StkBatchMapper stkBatchMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    /**
     * 查询科室盘点
     *
     * @param id 科室盘点主键
     * @return 科室盘点
     */
    @Override
    public StkIoStocktaking selectDeptStocktakingById(Long id)
    {
        return deptStocktakingMapper.selectDeptStocktakingById(id);
    }

    /**
     * 查询科室盘点列表
     * 只查询科室盘点（departmentId不为空，warehouseId为空或忽略）
     *
     * @param stkIoStocktaking 科室盘点
     * @return 科室盘点集合
     */
    @Override
    public List<StkIoStocktaking> selectDeptStocktakingList(StkIoStocktaking stkIoStocktaking)
    {
        // 确保只查询科室盘点，不查询仓库盘点
        stkIoStocktaking.setWarehouseId(null);
        return deptStocktakingMapper.selectDeptStocktakingList(stkIoStocktaking);
    }

    /**
     * 新增科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDeptStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setStockNo(getNumber());
        stkIoStocktaking.setCreateTime(DateUtils.getNowDate());
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        int rows = deptStocktakingMapper.insertDeptStocktaking(stkIoStocktaking);
        insertStkIoStocktakingEntry(stkIoStocktaking);
        return rows;
    }

    //流水号
    public String getNumber() {
        String str = "KSPD"; // 科室盘点前缀
        String date = FillRuleUtil.getDateNum();
        String maxNum = deptStocktakingMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDeptStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        Long parenId = stkIoStocktaking.getId();
        List<StkIoStocktakingEntry> entryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        List<Long> keepIds = new ArrayList<>();
        if (StringUtils.isNotNull(entryList)) {
            for (StkIoStocktakingEntry entry : entryList) {
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                // 补齐盘盈明细的可退库仓库：从产品档案默认所属仓库获取
                fillReturnWarehouseIdByMaterial(entry);
                if (entry.getId() != null) {
                    deptStocktakingMapper.updateDeptStocktakingEntry(entry);
                    keepIds.add(entry.getId());
                } else {
                    deptStocktakingMapper.insertDeptStocktakingEntrySingle(entry);
                }
            }
            deptStocktakingMapper.deleteDeptStocktakingEntryByParenIdExceptIds(parenId, keepIds);
        }
        return deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
    }

    /**
     * 批量删除科室盘点
     *
     * @param ids 需要删除的科室盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptStocktakingByIds(Long[] ids)
    {
        deptStocktakingMapper.deleteDeptStocktakingEntryByParenIds(ids);
        return deptStocktakingMapper.deleteDeptStocktakingByIds(ids);
    }

    /**
     * 删除科室盘点信息
     *
     * @param id 科室盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptStocktakingById(Long id)
    {
        deptStocktakingMapper.deleteDeptStocktakingEntryByParenId(id);
        return deptStocktakingMapper.deleteDeptStocktakingById(id);
    }

    /**
     * 审核科室盘点信息
     * 更新科室库存（stk_dep_inventory表）
     * @param id
     * @return
     */
    @Transactional
    @Override
    public int auditDeptStocktaking(String id) {
        StkIoStocktaking stkIoStocktaking = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }

        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();

        //更新科室库存
        updateDepInventory(stkIoStocktaking, stkIoStocktakingEntryList);

        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);

        int res = deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
        return res;
    }

    /**
     * 驳回科室盘点信息
     * @param id 盘点ID
     * @param rejectReason 驳回原因
     * @return
     */
    @Transactional
    @Override
    public int rejectDeptStocktaking(String id, String rejectReason) {
        StkIoStocktaking stkIoStocktaking = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }
        if(stkIoStocktaking.getStockStatus() != 1){
            throw new ServiceException(String.format("科室盘点业务ID：%s，状态不是未审核，无法驳回!", id));
        }

        stkIoStocktaking.setStockStatus(3); // 驳回状态
        stkIoStocktaking.setRejectReason(rejectReason);
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateTime(new Date());

        int res = deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
        return res;
    }

    /**
     * 更新科室库存
     * @param stkIoStocktaking 科室盘点
     * @param stkIoStocktakingEntryList 盘点明细列表
     */
    private void updateDepInventory(StkIoStocktaking stkIoStocktaking, List<StkIoStocktakingEntry> stkIoStocktakingEntryList){
        Integer stockType = stkIoStocktaking.getStockType();

        for(StkIoStocktakingEntry entry : stkIoStocktakingEntryList){
            if(entry.getQty() != null && BigDecimal.ZERO.compareTo(entry.getQty()) != 0){

                if(stockType == 501){//期初
                    StkDepInventory stkDepInventory = new StkDepInventory();
                    stkDepInventory.setBatchNo(entry.getBatchNo());
                    stkDepInventory.setMaterialId(entry.getMaterialId());
                    stkDepInventory.setDepartmentId(stkIoStocktaking.getDepartmentId());
                    stkDepInventory.setWarehouseId(entry.getReturnWarehouseId());
                    stkDepInventory.setQty(entry.getQty());
                    // 优先使用 unitPrice，如果为空则使用 price
                    BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                    stkDepInventory.setUnitPrice(unitPrice);
                    stkDepInventory.setAmt(entry.getAmt());
                    stkDepInventory.setMaterialDate(new Date());
                    stkDepInventory.setWarehouseDate(new Date());
                    stkDepInventory.setMaterialNo(entry.getBatchNumber());
                    // batch_number 为产品生产批号；batch_no 为系统追溯批次号
                    stkDepInventory.setBatchNumber(entry.getBatchNumber());
                    stkDepInventory.setBeginDate(entry.getBeginTime());
                    stkDepInventory.setEndDate(entry.getEndTime());
                    // 科室盘点审核后视为已收货确认，避免退库时被 receipt_confirm_status=0 拦截
                    stkDepInventory.setReceiptConfirmStatus(1);
                    // 生成/补齐批次字典并回填科室库存 batch_id
                    FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                    if (material == null) {
                        throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                    }
                    StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                    if (stkBatch != null && stkBatch.getId() != null) {
                        stkDepInventory.setBatchId(stkBatch.getId());
                        ensureWarehouseQtyZeroInventory(entry, stkBatch);
                    }
                    stkDepInventory.setCreateTime(new Date());
                    stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());

                    stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
                }else if(stockType == 502){//盘点
                    String batchNo = entry.getBatchNo();
                    Long warehouseId = entry.getReturnWarehouseId();
                    BigDecimal stockQty = entry.getStockQty();//盘点数量
                    BigDecimal qty = entry.getQty();//库存数量

                    StkDepInventory depInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo, warehouseId);

                    if(depInventory == null){
                        throw new ServiceException(String.format("科室库存批次号：%s，不存在!", batchNo));
                    }

                    // 补齐批次字典关联，便于追溯展示
                    FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                    if (material == null) {
                        throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                    }
                    StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                    if (stkBatch != null && stkBatch.getId() != null && depInventory.getBatchId() == null) {
                        depInventory.setBatchId(stkBatch.getId());
                    }

                    boolean isProfit = stockQty.compareTo(qty) > 0;
                    if(stockQty.compareTo(qty) == 0){
                        depInventory.setReceiptConfirmStatus(1);
                        if (depInventory.getWarehouseId() == null) {
                            depInventory.setWarehouseId(warehouseId);
                        }
                        if (depInventory.getBatchNumber() == null) {
                            depInventory.setBatchNumber(entry.getBatchNumber());
                        }
                        if (depInventory.getBatchId() != null && depInventory.getBatchId().compareTo(0L) != 0) {
                            // 不强制额外写金额/数量，仅更新已确认/批次关联（如有）
                        }
                        stkDepInventoryMapper.updateStkDepInventory(depInventory);
                        continue;
                    }

                    BigDecimal totalQty = BigDecimal.ZERO;
                    BigDecimal totalAmt = BigDecimal.ZERO;

                    if(stockQty.compareTo(qty) > 0){
                        BigDecimal stkQty = stockQty.subtract(qty);//最终盘点数
                        //库存数量+最终盘点数
                        totalQty = totalQty.add(depInventory.getQty().add(stkQty));
                        // 优先使用 unitPrice，如果为空则使用 price
                        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                        totalAmt = totalAmt.add(depInventory.getQty().add(stkQty).multiply(unitPrice));
                    }else{
                        totalQty = totalQty.add(stockQty);//取盘点数
                        // 优先使用 unitPrice，如果为空则使用 price
                        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                        totalAmt = totalAmt.add(stockQty.multiply(unitPrice));
                    }
                    depInventory.setQty(totalQty);
                    depInventory.setAmt(totalAmt);
                    // 优先使用 unitPrice，如果为空则使用 price
                    BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                    depInventory.setUnitPrice(unitPrice);
                    depInventory.setWarehouseDate(new Date());
                    // 盘点审核视为已收货确认，确保后续可退库
                    depInventory.setReceiptConfirmStatus(1);
                    if (depInventory.getWarehouseId() == null) {
                        depInventory.setWarehouseId(warehouseId);
                    }
                    if (stkBatch != null && stkBatch.getId() != null) {
                        depInventory.setBatchId(stkBatch.getId());
                    }
                    if (depInventory.getBatchNumber() == null) {
                        depInventory.setBatchNumber(entry.getBatchNumber());
                    }
                    // 盘盈时补齐仓库 qty=0 库存记录+批次对象，供退库/追溯使用
                    if (isProfit) {
                        ensureWarehouseQtyZeroInventory(entry, stkBatch);
                    }
                    depInventory.setUpdateTime(new Date());
                    depInventory.setUpdateBy(SecurityUtils.getUserIdStr());

                    stkDepInventoryMapper.updateStkDepInventory(depInventory);
                }
            }
        }
    }

    /**
     * 新增盘点明细信息
     *
     * @param stkIoStocktaking 盘点对象
     */
    public void insertStkIoStocktakingEntry(StkIoStocktaking stkIoStocktaking)
    {
        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        Long id = stkIoStocktaking.getId();
        if (StringUtils.isNotNull(stkIoStocktakingEntryList))
        {
            List<StkIoStocktakingEntry> list = new ArrayList<StkIoStocktakingEntry>();
            for (StkIoStocktakingEntry stkIoStocktakingEntry : stkIoStocktakingEntryList)
            {
                stkIoStocktakingEntry.setParenId(id);
                if(StringUtils.isEmpty(stkIoStocktakingEntry.getBatchNo())){
                    stkIoStocktakingEntry.setBatchNo(getBatchNumber());
                }
                // 补齐盘盈明细的可退库仓库：从产品档案默认所属仓库获取
                fillReturnWarehouseIdByMaterial(stkIoStocktakingEntry);
                list.add(stkIoStocktakingEntry);
            }
            if (list.size() > 0)
            {
                deptStocktakingMapper.batchDeptStocktakingEntry(list);
            }
        }
    }

    /**
     * 补齐盘盈明细的可退库仓库ID：
     * 1) 从产品档案 fd_material.default_warehouse_id 取值
     * 2) 缺失则直接拦截，要求库房先维护产品档案
     */
    private void fillReturnWarehouseIdByMaterial(StkIoStocktakingEntry entry) {
        if (entry == null || entry.getMaterialId() == null) {
            return;
        }
        Long current = entry.getReturnWarehouseId();
        if (current != null) {
            return;
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
        if (material == null) {
            throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
        }
        Long defaultWarehouseId = material.getDefaultWarehouseId();
        if (defaultWarehouseId == null || defaultWarehouseId == 0) {
            throw new ServiceException("产品档案未维护默认所属仓库ID，请联系库房维护后再生成该产品档案的明细数据。");
        }
        entry.setReturnWarehouseId(defaultWarehouseId);
    }

    private StkBatch ensureStkBatchByStocktaking(StkIoStocktaking stkIoStocktaking, StkIoStocktakingEntry entry, FdMaterial material) {
        if (entry == null || StringUtils.isEmpty(entry.getBatchNo())) {
            return null;
        }
        Long warehouseId = entry.getReturnWarehouseId();
        StkBatch stkBatch = stkBatchMapper.selectByBatchNo(entry.getBatchNo());
        if (stkBatch != null) {
            // 补齐缺失批次字典关联
            if (stkBatch.getWarehouseId() == null && warehouseId != null) {
                stkBatch.setWarehouseId(warehouseId);
                // 这里不额外更新数据库，避免引入不必要的写操作
            }
            return stkBatch;
        }

        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        Long supplierId = entry.getSupplierId() != null ? entry.getSupplierId() : material.getSupplierId();

        String batchSource;
        String originBusinessType;
        String originFlowLx;
        Integer stockType = stkIoStocktaking.getStockType();
        if (stockType != null && stockType == 501) {
            batchSource = "科室期初盘盈";
            originBusinessType = "科室期初盘盈";
            originFlowLx = "PY";
        } else {
            boolean isProfit = entry.getProfitQty() != null && entry.getProfitQty().compareTo(BigDecimal.ZERO) > 0;
            if (isProfit) {
                batchSource = "科室盘盈";
                originBusinessType = "科室盘盈";
                originFlowLx = "PY";
            } else {
                batchSource = "科室盘亏";
                originBusinessType = "科室盘亏";
                originFlowLx = "PK";
            }
        }

        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setMaterialCode(material.getCode());
        b.setMaterialName(material.getName());
        b.setSpeci(material.getSpeci());
        b.setModel(material.getModel());
        b.setUnitId(material.getUnitId());
        if (material.getFdUnit() != null) {
            b.setUnitName(material.getFdUnit().getUnitName());
        }
        b.setUnitPrice(unitPrice);
        b.setBatchNumber(entry.getBatchNumber());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());

        b.setSupplierId(supplierId);
        b.setWarehouseId(warehouseId);
        b.setDepartmentId(stkIoStocktaking.getDepartmentId());

        b.setBatchSource(batchSource);
        b.setOriginBusinessType(originBusinessType);
        b.setOriginFlowLx(originFlowLx);

        b.setDelFlag(0);
        b.setCreateTime(new Date());
        b.setCreateBy(SecurityUtils.getUserIdStr());
        b.setUpdateTime(new Date());
        b.setUpdateBy(SecurityUtils.getUserIdStr());
        b.setTenantId(SecurityUtils.getCustomerId());

        stkBatchMapper.insertStkBatch(b);
        return stkBatchMapper.selectByBatchNo(entry.getBatchNo());
    }

    private void ensureWarehouseQtyZeroInventory(StkIoStocktakingEntry entry, StkBatch stkBatch) {
        if (entry == null || stkBatch == null || stkBatch.getId() == null) {
            return;
        }
        Long warehouseId = entry.getReturnWarehouseId();
        if (warehouseId == null) {
            return;
        }
        StkInventory inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(entry.getBatchNo(), warehouseId);
        if (inventory != null) {
            // 缺少批次对象表ID时补齐（不强制覆盖数量/金额）
            if (inventory.getBatchId() == null) {
                inventory.setBatchId(stkBatch.getId());
                stkInventoryMapper.updateStkInventory(inventory);
            }
            return;
        }

        StkInventory inv = new StkInventory();
        inv.setQty(BigDecimal.ZERO);
        inv.setMaterialId(entry.getMaterialId());
        inv.setWarehouseId(warehouseId);
        inv.setUnitPrice(stkBatch.getUnitPrice());
        inv.setAmt(BigDecimal.ZERO);
        inv.setBatchNo(entry.getBatchNo());
        inv.setBatchId(stkBatch.getId());
        inv.setMaterialNo(entry.getBatchNumber());
        inv.setMaterialDate(new Date());
        inv.setWarehouseDate(new Date());
        inv.setSupplierId(stkBatch.getSupplierId());
        inv.setBeginTime(entry.getBeginTime());
        inv.setEndTime(entry.getEndTime());
        inv.setBatchNumber(entry.getBatchNumber());
        if (StringUtils.isEmpty(inv.getTenantId())) {
            inv.setTenantId(SecurityUtils.getCustomerId());
        }
        stkInventoryMapper.insertStkInventory(inv);
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }
}
