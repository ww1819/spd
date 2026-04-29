package com.spd.warehouse.service.impl;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.exception.DocRefQtyValidationException;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.domain.WhWarehouseApplyEntry;
import com.spd.department.service.IWhWarehouseApplyService;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.BasApplyMapper;
import com.spd.department.mapper.WhWarehouseApplyMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
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

import com.spd.warehouse.constants.HcDocBillRefType;
import com.spd.warehouse.domain.HcDocBillRef;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.vo.InboundEntryRefChannelQtyVo;
import com.spd.warehouse.domain.vo.StkOutBillExportFlatRow;
import com.spd.warehouse.utils.InventoryMaterialSnapshotHelper;
import com.spd.warehouse.mapper.HcDocBillRefMapper;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IHcDocBillRefService;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ITenantScopeService;
import com.spd.common.utils.uuid.UUID7;
import com.spd.gz.domain.GzBillEntryChangeLog;
import com.spd.warehouse.mapper.StkBillEntryChangeLogMapper;

/**
 * 出入库Service业务层处理
 *
 * @author spd
 * @date 2023-12-17
 */
@Service
public class StkIoBillServiceImpl implements IStkIoBillService
{
    /** 衡水三院等租户：出库审核通过后自动执行收货确认，确认人同审核人 */
    private static final String TENANT_ID_HENGSHUI_THIRD_AUTO_RECEIPT = "hengsui-third-001";

    /** 服务器 interface 接口 URL（scminterface） */
    @Value("${spd.interface.url:http://localhost:8088}")
    private String interfaceUrl;

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
    private IWhWarehouseApplyService whWarehouseApplyService;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private ISbCustomerService sbCustomerService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private IHcDocBillRefService hcDocBillRefService;

    @Autowired
    private HcDocBillRefMapper hcDocBillRefMapper;

    @Autowired
    private WhWarehouseApplyMapper whWarehouseApplyMapper;

    @Autowired
    private StkBillEntryChangeLogMapper stkBillEntryChangeLogMapper;

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
        if (stkIoBillEntryList == null) {
            stkIoBill.setStkIoBillEntryList(new ArrayList<>());
            stkIoBill.setMaterialList(new ArrayList<>());
            return stkIoBill;
        }
        String billTenantId = stkIoBill.getTenantId();
        List<FdMaterial> materialList = new ArrayList<>();
        for (StkIoBillEntry entry : stkIoBillEntryList) {
            FdMaterial fromJoin = entry.getMaterial();
            Long materialId = entry.getMaterialId();
            FdMaterial loaded = null;
            if (materialId != null) {
                if (StringUtils.isNotEmpty(billTenantId)) {
                    loaded = fdMaterialMapper.selectFdMaterialByIdAndTenant(materialId, billTenantId);
                }
                if (loaded == null) {
                    loaded = fdMaterialMapper.selectFdMaterialById(materialId);
                }
            }
            if (loaded != null) {
                entry.setMaterial(loaded);
            } else {
                entry.setMaterial(fromJoin);
            }
            materialList.add(entry.getMaterial());
        }
        stkIoBill.setMaterialList(materialList);
        fillSrcEntryRefConsumption(stkIoBill);
        if (stkIoBill.getBillType() != null && stkIoBill.getBillType().intValue() == 401) {
            fillTkSourceEntryReturnChannelConsumption(stkIoBill);
        }
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
        if (stkIoBill.getBillType() != null && stkIoBill.getBillType() == 101) {
            normalizeInboundSupplierFields(stkIoBill);
        }
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertStkIoBillEntry(stkIoBill);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
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
        // 仅当请求中带了明细列表且非空时，才同步明细（增量更新）；否则保留原明细，只更新主表
        List<StkIoBillEntry> entryList = stkIoBill.getStkIoBillEntryList();
        if (entryList != null && !entryList.isEmpty()) {
            if (stkIoBill.getBillType() != null && stkIoBill.getBillType() == 101) {
                normalizeInboundSupplierFields(stkIoBill);
            }
            syncStkIoBillEntry(stkIoBill);
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
        if (stkIoBill.getBillType() != null && stkIoBill.getBillType() == 201
            && StringUtils.isNotEmpty(stkIoBill.getTenantId())) {
            whWarehouseApplyService.releaseWhApplyCkRefsForOutboundBill(id, stkIoBill.getTenantId());
        }
        stkIoBill.setDelFlag(1);
        stkIoBill.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoBill.setUpdateTime(new Date());
        stkIoBill.setDeleteBy(SecurityUtils.getUserIdStr());
        stkIoBill.setDeleteTime(new Date());

        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        String deleteBy = SecurityUtils.getUserIdStr();
        Date deleteTime = new Date();
        if (stkIoBillEntryList != null) {
            for (StkIoBillEntry entry : stkIoBillEntryList) {
                entry.setDelFlag(1);
                entry.setParenId(id);
                entry.setDeleteBy(deleteBy);
                entry.setDeleteTime(deleteTime);
                stkIoBillMapper.updatestkIobillEntry(entry);
            }
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

        Integer auditBillType = stkIoBill.getBillType();
        if (auditBillType != null) {
            if (auditBillType == 201 || auditBillType == 301) {
                assertWarehouseStockEntriesMatchBillHeader(stkIoBill, auditBillType == 301);
            } else if (auditBillType == 401) {
                assertTkDepInventoryEntriesMatchBillHeader(stkIoBill);
            }
        }
        assertReferencedQtyWithinLimits(stkIoBill, stkIoBill.getId());

        normalizeInboundSupplierFields(stkIoBill);

        //更新库存   
        updateInventory(stkIoBill,stkIoBillEntryList);

        stkIoBill.setBillStatus(2);//已审核状态
        stkIoBill.setAuditDate(new Date());
        stkIoBill.setAuditBy(auditBy);
        int res = stkIoBillMapper.updateStkIoBill(stkIoBill);
        if (res > 0 && auditBillType != null && auditBillType == 201) {
            String tid = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId();
            if (TENANT_ID_HENGSHUI_THIRD_AUTO_RECEIPT.equals(tid)) {
                tryApplyOutboundReceiptConfirmation(stkIoBill, auditBy);
            }
        }
        return res;
    }


    /**
     * 明细 suppler_id（字符串）解析为供应商主键
     */
    private Long parseSupplerIdString(String s) {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        try {
            return Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 入库行供应商：明细 suppler_id 优先，其次主表 suppler_id，再次耗材档案默认供应商
     */
    private Long resolveInboundLineSupplierId(StkIoBill bill, StkIoBillEntry entry) {
        if (entry != null) {
            Long fromEntry = parseSupplerIdString(entry.getSupplerId());
            if (fromEntry != null) {
                return fromEntry;
            }
        }
        if (bill != null && bill.getSupplerId() != null) {
            return bill.getSupplerId();
        }
        if (entry != null && entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null && m.getSupplierId() != null) {
                return m.getSupplierId();
            }
        }
        return null;
    }

    /**
     * 出库/退货/调拨流水：以仓库库存行为准，其次明细、主表
     */
    private Long resolveStockFlowSupplierId(StkIoBill bill, StkIoBillEntry entry, StkInventory inv) {
        if (inv != null && inv.getSupplierId() != null) {
            return inv.getSupplierId();
        }
        if (entry != null) {
            Long fromEntry = parseSupplerIdString(entry.getSupplerId());
            if (fromEntry != null) {
                return fromEntry;
            }
        }
        if (bill != null && bill.getSupplerId() != null) {
            return bill.getSupplerId();
        }
        return null;
    }

    /**
     * 生产厂家：优先取仓库库存行冗余字段，其次批次表 stk_batch.factory_id
     */
    private Long resolveFactoryId(StkInventory inv) {
        if (inv == null) {
            return null;
        }
        if (inv.getFactoryId() != null) {
            return inv.getFactoryId();
        }
        if (inv.getBatchId() != null) {
            StkBatch b = stkBatchMapper.selectStkBatchById(inv.getBatchId());
            if (b != null && b.getFactoryId() != null) {
                return b.getFactoryId();
            }
        }
        return null;
    }

    /**
     * 入库保存/审核前：补全明细与主表供应商，保证库存、批次、仓库流水一致
     */
    private void normalizeInboundSupplierFields(StkIoBill bill) {
        if (bill == null || bill.getBillType() == null || bill.getBillType() != 101) {
            return;
        }
        List<StkIoBillEntry> list = bill.getStkIoBillEntryList();
        if (list == null) {
            return;
        }
        for (StkIoBillEntry e : list) {
            Long line = resolveInboundLineSupplierId(bill, e);
            if (line != null && StringUtils.isEmpty(e.getSupplerId())) {
                e.setSupplerId(String.valueOf(line));
            }
        }
        if (bill.getSupplerId() == null) {
            Long unified = null;
            for (StkIoBillEntry e : list) {
                Long line = resolveInboundLineSupplierId(bill, e);
                if (line == null) {
                    continue;
                }
                if (unified == null) {
                    unified = line;
                } else if (!unified.equals(line)) {
                    unified = null;
                    break;
                }
            }
            if (unified != null) {
                bill.setSupplerId(unified);
            }
        }
    }

    /** 出库/退库制单：从仓库库存回填明细 suppler_id，便于对账与流水兜底 */
    private void fillOutboundEntrySupplerIdFromWarehouse(StkIoBill bill, StkIoBillEntry entry) {
        if (entry == null || StringUtils.isEmpty(entry.getBatchNo()) || StringUtils.isNotEmpty(entry.getSupplerId())) {
            return;
        }
        Long whId = bill != null ? bill.getWarehouseId() : null;
        StkInventory inv = null;
        if (whId != null) {
            inv = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(entry.getBatchNo(), whId);
        }
        if (inv == null) {
            inv = stkInventoryMapper.selectStkInventoryOne(entry.getBatchNo());
        }
        if (inv != null && inv.getSupplierId() != null) {
            entry.setSupplerId(String.valueOf(inv.getSupplierId()));
        }
    }

    /**
     * 从耗材档案填充明细行上的名称/规格/型号/厂家快照（历史追溯；仅在对应字段为空时写入）
     */
    private void fillEntryMaterialSnapshot(StkIoBillEntry entry, String tenantId) {
        if (entry == null || entry.getMaterialId() == null) {
            return;
        }
        FdMaterial m = null;
        if (StringUtils.isNotEmpty(tenantId)) {
            m = fdMaterialMapper.selectFdMaterialByIdAndTenant(entry.getMaterialId(), tenantId);
        }
        if (m == null) {
            m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
        }
        if (m == null) {
            return;
        }
        if (StringUtils.isEmpty(entry.getMaterialName())) {
            entry.setMaterialName(m.getName());
        }
        if (StringUtils.isEmpty(entry.getMaterialSpeci())) {
            entry.setMaterialSpeci(m.getSpeci());
        }
        if (StringUtils.isEmpty(entry.getMaterialModel())) {
            entry.setMaterialModel(m.getModel());
        }
        if (entry.getMaterialFactoryId() == null && m.getFactoryId() != null) {
            entry.setMaterialFactoryId(m.getFactoryId());
        }
    }

    private void updateInventory(StkIoBill stkIoBill,List<StkIoBillEntry> stkIoBillEntryList){

        Integer billType = stkIoBill.getBillType();
        StkInventory stkInventory = null;
        for(StkIoBillEntry entry : stkIoBillEntryList){
            Long lineSupplerId = resolveInboundLineSupplierId(stkIoBill, entry);

            if(entry.getQty() != null && BigDecimal.ZERO.compareTo(entry.getQty()) != 0){
                if(billType == 101){//入库
                    // 批次表：若该批次号不存在则插入一条批次记录，用于追溯；并关联 batch_id 到库存
                    StkBatch stkBatch = null;
                    if (StringUtils.isNotEmpty(entry.getBatchNo())) {
                        stkBatch = stkBatchMapper.selectByBatchNo(entry.getBatchNo());
                        if (stkBatch == null) {
                            stkBatch = buildStkBatchForInbound(entry, stkIoBill, lineSupplerId);
                            stkBatchMapper.insertStkBatch(stkBatch);
                        }
                    }

                    stkInventory = new StkInventory();
                    stkInventory.setBatchNo(entry.getBatchNo());
                    if (stkBatch != null) {
                        stkInventory.setBatchId(stkBatch.getId());
                        stkInventory.setFactoryId(stkBatch.getFactoryId());
                    }
                    stkInventory.setMaterialNo(entry.getBatchNumber());
                    stkInventory.setMaterialId(entry.getMaterialId());
                    stkInventory.setWarehouseId(stkIoBill.getWarehouseId());
                    stkInventory.setQty(entry.getQty());
                    // 优先使用 unitPrice，如果为空则使用 price
                    BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                    stkInventory.setUnitPrice(unitPrice);
                    BigDecimal rkAmt = unitPrice != null ? entry.getQty().multiply(unitPrice) : BigDecimal.ZERO;
                    stkInventory.setAmt(rkAmt);
                    stkInventory.setMaterialDate(new Date());
                    stkInventory.setWarehouseDate(new Date());
                    stkInventory.setSupplierId(lineSupplerId);
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
                    InventoryMaterialSnapshotHelper.fillWarehouseRow(stkInventory, entry, fdMaterialMapper, stkIoBill.getTenantId());
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
                    rkFlow.setAmt(rkAmt);
                    rkFlow.setBeginTime(entry.getBeginTime());
                    rkFlow.setEndTime(entry.getEndTime());
                    rkFlow.setMainBarcode(entry.getMainBarcode());
                    rkFlow.setSubBarcode(entry.getSubBarcode());
                    rkFlow.setSupplierId(lineSupplerId);
                    rkFlow.setFactoryId(stkInventory.getFactoryId());
                    rkFlow.setLx("RK");
                    rkFlow.setBatchId(stkInventory.getBatchId());
                    rkFlow.setOriginBusinessType("入库结算");
                    rkFlow.setKcNo(stkInventory.getId());
                    rkFlow.setFlowTime(new Date());
                    rkFlow.setDelFlag(0);
                    rkFlow.setCreateTime(new Date());
                    rkFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(rkFlow.getTenantId())) rkFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(rkFlow);
                    if (entry.getId() != null) {
                        stkIoBillMapper.updateStkIoBillEntryInboundWhRef(entry.getId(), stkInventory.getId());
                    }
                }else if(billType == 201){//出库
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    if (StringUtils.isEmpty(batchNo)) {
                        throw new ServiceException("出库批次号不能为空");
                    }
                    if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ServiceException(String.format("出库数量必须大于0，批次号：%s", batchNo));
                    }
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

                    validateInventory(batchNo, warehouseId, stkIoBillEntryList);

                    BigDecimal unitPrice = inventory.getUnitPrice();//单价

                    String updateBy = SecurityUtils.getUserIdStr();
                    int decRows = stkInventoryMapper.decreaseStkInventoryQty(inventory.getId(), qty, updateBy);
                    if (decRows == 0) {
                        StkInventory latest = stkInventoryMapper.selectStkInventoryById(inventory.getId());
                        BigDecimal cur = latest != null && latest.getQty() != null ? latest.getQty() : BigDecimal.ZERO;
                        throw new ServiceException(String.format(
                                "实际库存不足，出库审核已拒绝。批次：%s，本行出库：%s，当前库存：%s（可能与他单并发或本单多行合计超库存，请刷新后重试）",
                                batchNo, qty, cur));
                    }
                    inventory = stkInventoryMapper.selectStkInventoryById(inventory.getId());
                    if (inventory == null) {
                        throw new ServiceException(String.format("出库扣减后重新加载库存失败，批次：%s", batchNo));
                    }

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
                    ckFlow.setSupplierId(resolveStockFlowSupplierId(stkIoBill, entry, inventory));
                    ckFlow.setFactoryId(resolveFactoryId(inventory));
                    ckFlow.setLx("CK");
                    ckFlow.setBatchId(inventory.getBatchId());
                    ckFlow.setOriginBusinessType("出库结算");
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
                    BigDecimal depUnitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : inventory.getUnitPrice();
                    stkDepInventory.setUnitPrice(depUnitPrice);
                    stkDepInventory.setAmt(depUnitPrice != null ? entry.getQty().multiply(depUnitPrice) : BigDecimal.ZERO);
                    stkDepInventory.setBatchNo(entry.getBatchNo());
                    stkDepInventory.setBatchId(inventory.getBatchId());
                    stkDepInventory.setMaterialDate(inventory.getMaterialDate());
                    stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
                    stkDepInventory.setWarehouseId(stkIoBill.getWarehouseId());
                    stkDepInventory.setBeginDate(entry.getBeginTime() != null ? entry.getBeginTime() : inventory.getBeginTime());
                    stkDepInventory.setEndDate(entry.getEndTime() != null ? entry.getEndTime() : inventory.getEndTime());
                    Long ckLineSup = resolveStockFlowSupplierId(stkIoBill, entry, inventory);
                    stkDepInventory.setSupplierId(ckLineSup != null ? String.valueOf(ckLineSup) : null);
                    stkDepInventory.setFactoryId(resolveFactoryId(inventory));
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
                    InventoryMaterialSnapshotHelper.fillDepRow(stkDepInventory, entry, inventory, fdMaterialMapper, stkIoBill.getTenantId());
                    // 科室库存 kc_no = 来源仓库库存 id（与出库仓库流水 CK 的 kc_no 一致）
                    if (inventory.getId() != null) {
                        stkDepInventory.setKcNo(inventory.getId());
                    }
                    stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
                    if (entry.getId() != null) {
                        stkIoBillMapper.updateStkIoBillEntryOutboundAuditRefs(entry.getId(), inventory.getId(), stkDepInventory.getId());
                    }
                }else if(billType == 301){//退货
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    if (StringUtils.isEmpty(batchNo)) {
                        throw new ServiceException("退货批次号不能为空");
                    }
                    if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ServiceException(String.format("退货数量必须大于0，批次号：%s", batchNo));
                    }
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
                    thFlow.setSupplierId(resolveStockFlowSupplierId(stkIoBill, entry, inventory));
                    thFlow.setFactoryId(resolveFactoryId(inventory));
                    thFlow.setMainBarcode(inventory.getMainBarcode());
                    thFlow.setSubBarcode(inventory.getSubBarcode());
                    thFlow.setLx("TH");
                    thFlow.setBatchId(inventory.getBatchId());
                    thFlow.setOriginBusinessType("退货结算");
                    thFlow.setKcNo(inventory.getId());
                    thFlow.setFlowTime(new Date());
                    thFlow.setDelFlag(0);
                    thFlow.setCreateTime(new Date());
                    thFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(thFlow.getTenantId())) thFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(thFlow);
                }else if(billType == 401){//退库（仅允许对已收货确认的科室库存退库；优先按明细 kc_no=科室库存 id 锁定行）
                    BigDecimal qty = entry.getQty();//退库数量
                    if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ServiceException("退库数量必须大于0");
                    }

                    Long returnWarehouseId401 = entry.getWarehouseId();
                    if (returnWarehouseId401 == null) {
                        throw new ServiceException("退库目标仓库ID不能为空");
                    }

                    StkDepInventory stkDepInventory;
                    String batchNo = entry.getBatchNo();
                    Long depInvKey401 = entry.resolveDepInventoryKeyForDepOps();
                    if (depInvKey401 != null) {
                        stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryById(depInvKey401);
                        if (stkDepInventory == null) {
                            throw new ServiceException(String.format("退库-科室库存不存在或无权访问，id=%s", depInvKey401));
                        }
                        if (!returnWarehouseId401.equals(stkDepInventory.getWarehouseId())) {
                            throw new ServiceException(String.format("退库-科室库存id：%s 与单据仓库不一致", depInvKey401));
                        }
                        if (stkDepInventory.getReceiptConfirmStatus() == null || stkDepInventory.getReceiptConfirmStatus() != 1) {
                            throw new ServiceException(String.format("退库-科室库存id：%s 未收货确认，不能退库", depInvKey401));
                        }
                        if (StringUtils.isNotEmpty(batchNo) && !batchNo.equals(stkDepInventory.getBatchNo())) {
                            throw new ServiceException("退库明细批次号与科室库存不一致，请刷新后重试");
                        }
                        if (StringUtils.isEmpty(batchNo)) {
                            batchNo = stkDepInventory.getBatchNo();
                            entry.setBatchNo(batchNo);
                        }
                    } else {
                        if (StringUtils.isEmpty(batchNo)) {
                            throw new ServiceException("退库批次号不能为空（或请指定科室库存id）");
                        }
                        stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo, returnWarehouseId401);
                        if(stkDepInventory == null){
                            throw new ServiceException(String.format("退库-批次号：%s，未收货确认或不存在，不能退库!", batchNo));
                        }
                        if(stkDepInventory.getReceiptConfirmStatus() == null || stkDepInventory.getReceiptConfirmStatus() != 1){
                            throw new ServiceException(String.format("退库-批次号：%s，科室库存未收货确认，不能退库!", batchNo));
                        }
                    }

                    BigDecimal stkDepInventoryQty = stkDepInventory.getQty();//科室库存实际数量
                    if(qty.compareTo(stkDepInventoryQty) > 0){
                        throw new ServiceException(String.format("科室库存不足！退库数量：%s，实际库存：%s", qty,stkDepInventoryQty));
                    }
                    BigDecimal depNewQty = stkDepInventoryQty.subtract(qty);
                    BigDecimal depUnitPrice = stkDepInventory.getUnitPrice() != null
                            ? stkDepInventory.getUnitPrice()
                            : (entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice());
                    BigDecimal depNewAmt = depUnitPrice != null ? depNewQty.multiply(depUnitPrice) : BigDecimal.ZERO;
                    stkDepInventory.setQty(depNewQty);
                    stkDepInventory.setAmt(depNewAmt);
                    stkDepInventory.setUpdateTime(new Date());
                    stkDepInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);

                    // 更新仓库库存数量（必须按仓库精确锁定）
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, returnWarehouseId401);

                    // 仓库库存不存在时：根据批次字典创建一条 qty=0 的库存明细，用于追溯来源
                    if (inventory == null) {
                        StkBatch stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
                        if (stkBatch == null || stkBatch.getId() == null) {
                            throw new ServiceException(String.format("退库-批次字典不存在：%s", batchNo));
                        }

                        inventory = new StkInventory();
                        inventory.setBatchNo(entry.getBatchNo());
                        inventory.setBatchId(stkBatch.getId());
                        inventory.setMaterialNo(entry.getBatchNumber());
                        inventory.setMaterialId(entry.getMaterialId());
                        inventory.setWarehouseId(returnWarehouseId401);
                        inventory.setQty(BigDecimal.ZERO);

                        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                        inventory.setUnitPrice(unitPrice);
                        inventory.setAmt(BigDecimal.ZERO);

                        inventory.setMaterialDate(new Date());
                        inventory.setWarehouseDate(new Date());
                        inventory.setSupplierId(parseSupplerIdString(stkDepInventory.getSupplierId()));
                        inventory.setFactoryId(stkBatch.getFactoryId());
                        inventory.setMainBarcode(entry.getMainBarcode());
                        inventory.setSubBarcode(entry.getSubBarcode());
                        inventory.setBeginTime(entry.getBeginTime());
                        inventory.setEndTime(entry.getEndTime());
                        inventory.setReceiptOrderNo(stkIoBill.getBillNo());
                        inventory.setBatchNumber(entry.getBatchNumber());
                        inventory.setCreateTime(new Date());
                        inventory.setCreateBy(SecurityUtils.getUserIdStr());
                        if (StringUtils.isEmpty(inventory.getTenantId())) {
                            inventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                        }
                        if (StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                            inventory.setSettlementType(stkIoBill.getSettlementType());
                        }
                        stkInventoryMapper.insertStkInventory(inventory);
                    }

                    BigDecimal unitPrice = inventory.getUnitPrice() != null ? inventory.getUnitPrice() : BigDecimal.ZERO;
                    BigDecimal returnAmt = entry.getAmt() != null ? entry.getAmt() : qty.multiply(unitPrice);

                    BigDecimal inventoryQty = inventory.getQty();
                    inventoryQty = inventoryQty.add(qty);

                    inventory.setQty(inventoryQty);
                    inventory.setAmt(inventoryQty.multiply(unitPrice));
                    inventory.setUpdateTime(new Date());
                    inventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    stkInventoryMapper.updateStkInventory(inventory);
                    // 插仓库流水（lx=TK，kc_no=仓库库存id）
                    HcCkFlow tkFlow = new HcCkFlow();
                    tkFlow.setBillId(stkIoBill.getId());
                    tkFlow.setEntryId(entry.getId());
                    tkFlow.setWarehouseId(returnWarehouseId401);
                    tkFlow.setMaterialId(entry.getMaterialId());
                    tkFlow.setBatchNo(entry.getBatchNo());
                    tkFlow.setBatchNumber(entry.getBatchNumber());
                    tkFlow.setQty(entry.getQty());
                    tkFlow.setUnitPrice(unitPrice);
                    tkFlow.setAmt(returnAmt);
                    tkFlow.setBeginTime(entry.getBeginTime());
                    tkFlow.setEndTime(entry.getEndTime());
                    tkFlow.setSupplierId(resolveStockFlowSupplierId(stkIoBill, entry, inventory));
                    tkFlow.setFactoryId(resolveFactoryId(inventory));
                    tkFlow.setMainBarcode(inventory.getMainBarcode());
                    tkFlow.setSubBarcode(inventory.getSubBarcode());
                    tkFlow.setLx("TK");
                    tkFlow.setBatchId(inventory.getBatchId());
                    tkFlow.setOriginBusinessType("退库结算");
                    tkFlow.setKcNo(inventory.getId());
                    tkFlow.setFlowTime(new Date());
                    tkFlow.setDelFlag(0);
                    tkFlow.setCreateTime(new Date());
                    tkFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(tkFlow.getTenantId())) tkFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(tkFlow);

                    // 插科室流水（lx=TK，kc_no=科室库存id）
                    HcKsFlow ksTkFlow = new HcKsFlow();
                    ksTkFlow.setBillId(stkIoBill.getId());
                    ksTkFlow.setEntryId(entry.getId());
                    ksTkFlow.setDepartmentId(stkIoBill.getDepartmentId());
                    ksTkFlow.setWarehouseId(returnWarehouseId401);
                    ksTkFlow.setMaterialId(entry.getMaterialId());
                    ksTkFlow.setBatchNo(entry.getBatchNo());
                    ksTkFlow.setBatchNumber(entry.getBatchNumber());
                    ksTkFlow.setBatchId(inventory.getBatchId());
                    ksTkFlow.setQty(entry.getQty());
                    ksTkFlow.setUnitPrice(depUnitPrice);
                    ksTkFlow.setAmt(depUnitPrice != null ? entry.getQty().multiply(depUnitPrice) : returnAmt);
                    ksTkFlow.setBeginTime(entry.getBeginTime());
                    ksTkFlow.setEndTime(entry.getEndTime());
                    Long ksSupplierId = resolveStockFlowSupplierId(stkIoBill, entry, inventory);
                    ksTkFlow.setSupplierId(ksSupplierId != null ? String.valueOf(ksSupplierId) : null);
                    ksTkFlow.setFactoryId(resolveFactoryId(inventory));
                    ksTkFlow.setMainBarcode(inventory.getMainBarcode());
                    ksTkFlow.setSubBarcode(inventory.getSubBarcode());
                    ksTkFlow.setKcNo(stkDepInventory.getId());
                    ksTkFlow.setLx("TK");
                    ksTkFlow.setOriginBusinessType("退库结算");
                    ksTkFlow.setFlowTime(new Date());
                    ksTkFlow.setDelFlag(0);
                    ksTkFlow.setCreateTime(new Date());
                    ksTkFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(ksTkFlow.getTenantId())) ksTkFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcKsFlowMapper.insertHcKsFlow(ksTkFlow);
                } else if (billType == 501) {// 仓库调拨：审核时已生成 hc_ck_flow 转出(ZC)+转入(ZR)，勿重复补流水
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

                    // 1) 转出仓库：优先按明细 stk_inventory_id（兼容旧 kc_no）查库存，否则按批次号+转出仓库查；扣减并插流水 ZC
                    StkInventory outInventory = null;
                    Long whInvKey501 = entry.resolveStkInventoryKeyForWarehouseOps();
                    if (whInvKey501 != null) {
                        outInventory = stkInventoryMapper.selectStkInventoryById(whInvKey501);
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
                    validateInventory(batchNo, outWarehouseId, stkIoBillEntryList);
                    BigDecimal unitPrice = outInventory.getUnitPrice();
                    String updateBy501 = SecurityUtils.getUserIdStr();
                    int decOut = stkInventoryMapper.decreaseStkInventoryQty(outInventory.getId(), qty, updateBy501);
                    if (decOut == 0) {
                        StkInventory latestOut = stkInventoryMapper.selectStkInventoryById(outInventory.getId());
                        BigDecimal curOut = latestOut != null && latestOut.getQty() != null ? latestOut.getQty() : BigDecimal.ZERO;
                        throw new ServiceException(String.format(
                                "调拨转出库存不足，审核已拒绝。批次：%s，本行数量：%s，当前库存：%s（请刷新后重试）",
                                batchNo, qty, curOut));
                    }
                    outInventory = stkInventoryMapper.selectStkInventoryById(outInventory.getId());
                    if (outInventory == null) {
                        throw new ServiceException(String.format("调拨转出扣减后重新加载库存失败，批次：%s", batchNo));
                    }

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
                    zcFlow.setSupplierId(resolveStockFlowSupplierId(stkIoBill, entry, outInventory));
                    zcFlow.setFactoryId(resolveFactoryId(outInventory));
                    zcFlow.setMainBarcode(outInventory.getMainBarcode());
                    zcFlow.setSubBarcode(outInventory.getSubBarcode());
                    zcFlow.setLx("ZC");
                    zcFlow.setBatchId(outInventory.getBatchId());
                    zcFlow.setOriginBusinessType("调拨转出");
                    zcFlow.setKcNo(outInventory.getId());
                    zcFlow.setFlowTime(new Date());
                    zcFlow.setDelFlag(0);
                    zcFlow.setCreateTime(new Date());
                    zcFlow.setCreateBy(SecurityUtils.getUserIdStr());
                    if (StringUtils.isEmpty(zcFlow.getTenantId())) zcFlow.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
                    hcCkFlowMapper.insertHcCkFlow(zcFlow);

                    // 2) 转入仓库：增加库存，插流水 ZR（供应商与转出库存行一致）
                    Long transferSup = resolveStockFlowSupplierId(stkIoBill, entry, outInventory);
                    StkInventory inInventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(batchNo, inWarehouseId);
                    if (inInventory == null) {
                        StkBatch stkBatch = null;
                        if (StringUtils.isNotEmpty(batchNo)) {
                            stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
                            if (stkBatch == null) {
                                stkBatch = buildStkBatchForInbound(entry, stkIoBill, transferSup);
                                stkBatchMapper.insertStkBatch(stkBatch);
                            }
                        }
                        inInventory = new StkInventory();
                        inInventory.setBatchNo(batchNo);
                        if (stkBatch != null) {
                            inInventory.setBatchId(stkBatch.getId());
                            inInventory.setFactoryId(stkBatch.getFactoryId());
                        }
                        inInventory.setMaterialNo(entry.getBatchNumber());
                        inInventory.setMaterialId(entry.getMaterialId());
                        inInventory.setWarehouseId(inWarehouseId);
                        inInventory.setQty(qty);
                        BigDecimal inUnitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                        inInventory.setUnitPrice(inUnitPrice);
                        BigDecimal zrAmt = inUnitPrice != null ? qty.multiply(inUnitPrice) : BigDecimal.ZERO;
                        inInventory.setAmt(zrAmt);
                        inInventory.setMaterialDate(new Date());
                        inInventory.setWarehouseDate(new Date());
                        inInventory.setSupplierId(transferSup);
                        inInventory.setBeginTime(entry.getBeginTime());
                        inInventory.setEndTime(entry.getEndTime());
                        inInventory.setMainBarcode(entry.getMainBarcode());
                        inInventory.setSubBarcode(entry.getSubBarcode());
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
                        zrFlow.setAmt(zrAmt);
                        zrFlow.setBeginTime(entry.getBeginTime());
                        zrFlow.setEndTime(entry.getEndTime());
                        zrFlow.setSupplierId(transferSup);
                        zrFlow.setFactoryId(resolveFactoryId(inInventory));
                        zrFlow.setMainBarcode(inInventory.getMainBarcode());
                        zrFlow.setSubBarcode(inInventory.getSubBarcode());
                        zrFlow.setLx("ZR");
                        zrFlow.setBatchId(inInventory.getBatchId());
                        zrFlow.setOriginBusinessType("调拨转入");
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
                        BigDecimal zrAmt = inInventory.getUnitPrice() != null
                                ? entry.getQty().multiply(inInventory.getUnitPrice())
                                : BigDecimal.ZERO;
                        zrFlow.setAmt(zrAmt);
                        zrFlow.setBeginTime(entry.getBeginTime());
                        zrFlow.setEndTime(entry.getEndTime());
                        zrFlow.setSupplierId(inInventory.getSupplierId());
                        zrFlow.setFactoryId(resolveFactoryId(inInventory));
                        zrFlow.setMainBarcode(inInventory.getMainBarcode());
                        zrFlow.setSubBarcode(inInventory.getSubBarcode());
                        zrFlow.setLx("ZR");
                        zrFlow.setBatchId(inInventory.getBatchId());
                        zrFlow.setOriginBusinessType("调拨转入");
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
        // 批次来源：首次建批次时用于追溯展示。入库/调拨转入分别对应 RK/ZR。
        Integer billType = stkIoBill != null ? stkIoBill.getBillType() : null;
        String originFlowLx = null;
        String originBusinessType = null;
        Long originFromWh = null;
        Long originToWh = null;
        if (billType != null) {
            if (billType == 101) { // 入库
                originFlowLx = "RK";
                originBusinessType = "入库结算";
                originFromWh = stkIoBill.getWarehouseId();
                originToWh = stkIoBill.getWarehouseId();
            } else if (billType == 501) { // 调拨：转入端首次建批次 => ZR
                originFlowLx = "ZR";
                originBusinessType = "调拨转入";
                originFromWh = stkIoBill.getWarehouseId();     // 转出仓库
                originToWh = stkIoBill.getDepartmentId();     // 调入仓库（代码中department_id存调入仓库id）
            }
        }
        b.setBatchSource(originFlowLx);
        b.setOriginBillType(billType);
        b.setOriginFlowLx(originFlowLx);
        b.setOriginBusinessType(originBusinessType);
        b.setOriginFromWarehouseId(originFromWh);
        b.setOriginToWarehouseId(originToWh);
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
        b.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
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
        Long warehouseId = entry.getWarehouseId() != null ? entry.getWarehouseId() : inventory.getWarehouseId();
        StkDepInventory stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo, warehouseId);

        if(stkDepInventory == null){
            //更新科室库存明细表
            stkDepInventory = new StkDepInventory();
            stkDepInventory.setMaterialId(entry.getMaterialId());
            stkDepInventory.setMaterialNo(entry.getBatchNumber());
            stkDepInventory.setDepartmentId(stkIoBill.getDepartmentId());
            stkDepInventory.setQty(entry.getQty());
            BigDecimal depUnitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
            stkDepInventory.setUnitPrice(depUnitPrice);
            stkDepInventory.setAmt(depUnitPrice != null ? entry.getQty().multiply(depUnitPrice) : BigDecimal.ZERO);
            stkDepInventory.setBatchNo(entry.getBatchNo());
            stkDepInventory.setMaterialNo(inventory.getMaterialNo());
            stkDepInventory.setMaterialDate(inventory.getMaterialDate());
            stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
            stkDepInventory.setBeginDate(inventory.getBeginTime());
            stkDepInventory.setEndDate(inventory.getEndTime());
            Long depSup = resolveStockFlowSupplierId(stkIoBill, entry, inventory);
            stkDepInventory.setSupplierId(depSup != null ? String.valueOf(depSup) : null);
            stkDepInventory.setBatchNumber(entry.getBatchNumber());
            stkDepInventory.setWarehouseId(warehouseId);
            stkDepInventory.setMainBarcode(inventory.getMainBarcode());
            stkDepInventory.setSubBarcode(inventory.getSubBarcode());
            stkDepInventory.setFactoryId(resolveFactoryId(inventory));
            if (StringUtils.isEmpty(stkDepInventory.getTenantId()) && stkIoBill != null) {
                stkDepInventory.setTenantId(StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId());
            }
            if (stkIoBill != null && StringUtils.isNotEmpty(stkIoBill.getSettlementType())) {
                stkDepInventory.setSettlementType(stkIoBill.getSettlementType());
            }
            if (inventory.getId() != null) {
                stkDepInventory.setKcNo(inventory.getId());
            }
            stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
        }else{
            BigDecimal oldQty = stkDepInventory.getQty();
            BigDecimal qty = entry.getQty();

            stkDepInventory.setQty(oldQty.add(qty));//数量
            BigDecimal unitPrice = stkDepInventory.getUnitPrice() != null
                    ? stkDepInventory.getUnitPrice()
                    : (entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice());
            stkDepInventory.setAmt(unitPrice != null ? oldQty.add(qty).multiply(unitPrice) : BigDecimal.ZERO);
            if (stkDepInventory.getKcNo() == null && inventory.getId() != null) {
                stkDepInventory.setKcNo(inventory.getId());
            }
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
        normalizeInboundSupplierFields(stkIoBill);
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBillNo(stkIoBill.getBillNo());
                stkIoBillEntry.setBatchNo(getBatchNumber());
                stkIoBillEntry.setDelFlag(0);
                // 将表头仓库ID反写到明细，保证退库按仓库锁定
                stkIoBillEntry.setWarehouseId(stkIoBill.getWarehouseId());
                // tenant_id 在 mapper 批量写入时直接依赖 item.tenantId，必须严格兜底并确保不为空
                stkIoBillEntry.setTenantId(tenantId);
                fillEntryMaterialSnapshot(stkIoBillEntry, tenantId);
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
        normalizeInboundSupplierFields(stkIoBill);
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBillNo(stkIoBill.getBillNo());
                if (stkIoBillEntry.getDelFlag() == null) {
                    stkIoBillEntry.setDelFlag(0);
                }
                if(StringUtils.isEmpty(stkIoBillEntry.getBatchNo())){
                    stkIoBillEntry.setBatchNo(getBatchNumber());
                }
                // 将表头仓库ID反写到明细，保证退库按仓库锁定
                stkIoBillEntry.setWarehouseId(stkIoBill.getWarehouseId());
                // tenant_id 在 mapper 批量写入时直接依赖 item.tenantId，必须严格兜底并确保不为空
                stkIoBillEntry.setTenantId(tenantId);
                fillEntryMaterialSnapshot(stkIoBillEntry, tenantId);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    private void syncStkIoBillEntry(StkIoBill stkIoBill) {
        normalizeInboundSupplierFields(stkIoBill);
        List<StkIoBillEntry> incoming = stkIoBill.getStkIoBillEntryList();
        if (incoming == null || incoming.isEmpty()) {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        List<StkIoBillEntry> prepared = new ArrayList<>();
        for (StkIoBillEntry e : incoming) {
            if (e == null) {
                continue;
            }
            e.setParenId(stkIoBill.getId());
            e.setBillNo(stkIoBill.getBillNo());
            if (e.getDelFlag() == null) {
                e.setDelFlag(0);
            }
            if (StringUtils.isEmpty(e.getBatchNo())) {
                e.setBatchNo(getBatchNumber());
            }
            e.setWarehouseId(stkIoBill.getWarehouseId());
            e.setTenantId(tenantId);
            fillEntryMaterialSnapshot(e, tenantId);
            prepared.add(e);
        }
        applyIncrementalSync(stkIoBill.getId(), prepared, stkIoBill.getBillType(), tenantId);
    }

    private int syncOutStkIoBillEntry(StkIoBill stkIoBill) {
        List<StkIoBillEntry> incoming = stkIoBill.getStkIoBillEntryList();
        if (incoming == null || incoming.isEmpty()) {
            return 0;
        }
        int outBt = stkIoBill.getBillType() == null ? 0 : stkIoBill.getBillType();
        if (outBt == 201 || outBt == 301) {
            assertWarehouseStockEntriesMatchBillHeader(stkIoBill, outBt == 301);
        }
        String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        List<StkIoBillEntry> prepared = new ArrayList<>();
        Set<String> dedupKeys = new HashSet<>();
        int filteredCount = 0;
        for (StkIoBillEntry e : incoming) {
            if (e == null) {
                continue;
            }
            if (e.getStkInventoryId() == null && e.getDepInventoryId() == null && e.getKcNo() != null) {
                e.setStkInventoryId(e.getKcNo());
                e.setKcNo(null);
            }
            e.setWarehouseId(stkIoBill.getWarehouseId());
            String dedupKey = buildOutboundEntryDedupKey(e);
            if (StringUtils.isNotEmpty(dedupKey) && dedupKeys.contains(dedupKey)) {
                filteredCount++;
                continue;
            }
            validateInventory(e.getBatchNo(), stkIoBill.getWarehouseId(), incoming);
            e.setParenId(stkIoBill.getId());
            e.setBillNo(stkIoBill.getBillNo());
            e.setDelFlag(0);
            fillOutboundEntrySupplerIdFromWarehouse(stkIoBill, e);
            e.setTenantId(tenantId);
            fillEntryMaterialSnapshot(e, tenantId);
            prepared.add(e);
            if (StringUtils.isNotEmpty(dedupKey)) {
                dedupKeys.add(dedupKey);
            }
        }
        applyIncrementalSync(stkIoBill.getId(), prepared, stkIoBill.getBillType(), tenantId);
        return filteredCount;
    }

    private void syncTKStkIoBillEntry(StkIoBill stkIoBill) {
        List<StkIoBillEntry> incoming = stkIoBill.getStkIoBillEntryList();
        if (incoming == null || incoming.isEmpty()) {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        Long whId = stkIoBill.getWarehouseId();
        assertTkDepInventoryEntriesMatchBillHeader(stkIoBill);
        validateTKStkIoBillEntries(whId, stkIoBill.getDepartmentId(), incoming);
        List<StkIoBillEntry> prepared = new ArrayList<>();
        for (StkIoBillEntry e : incoming) {
            if (e == null) {
                continue;
            }
            if (e.getDepInventoryId() == null && e.getStkInventoryId() == null && e.getKcNo() != null) {
                e.setDepInventoryId(e.getKcNo());
                e.setKcNo(null);
            }
            e.setWarehouseId(whId);
            e.setParenId(stkIoBill.getId());
            e.setBillNo(stkIoBill.getBillNo());
            e.setDelFlag(0);
            fillOutboundEntrySupplerIdFromWarehouse(stkIoBill, e);
            e.setTenantId(tenantId);
            fillEntryMaterialSnapshot(e, tenantId);
            prepared.add(e);
        }
        applyIncrementalSync(stkIoBill.getId(), prepared, stkIoBill.getBillType(), tenantId);
    }

    private void applyIncrementalSync(Long parenId, List<StkIoBillEntry> preparedEntries, Integer billType, String tenantId) {
        List<Long> existingIds = stkIoBillMapper.selectActiveStkIoBillEntryIdsByParenId(parenId);
        Map<Long, StkIoBillEntry> oldEntryMap = stkIoBillMapper.selectActiveStkIoBillEntriesByParenId(parenId)
            .stream().collect(Collectors.toMap(StkIoBillEntry::getId, e -> e, (a, b) -> a));
        Map<Long, StkIoBillEntry> incomingById = new HashMap<>();
        List<StkIoBillEntry> newEntries = new ArrayList<>();
        for (StkIoBillEntry e : preparedEntries) {
            if (e.getId() != null) {
                incomingById.put(e.getId(), e);
            } else {
                newEntries.add(e);
            }
        }
        String operator = SecurityUtils.getUserIdStr();
        Date now = new Date();
        if (existingIds != null) {
            for (Long existingId : existingIds) {
                StkIoBillEntry incoming = incomingById.get(existingId);
                if (incoming != null) {
                    incoming.setUpdateBy(operator);
                    incoming.setUpdateTime(now);
                    incoming.setDeleteBy(null);
                    incoming.setDeleteTime(null);
                    incoming.setDelFlag(0);
                    stkIoBillMapper.updateStkIoBillEntryById(incoming);
                    StkIoBillEntry old = oldEntryMap.get(existingId);
                    if (old != null && isStkIoBillEntryChanged(old, incoming)) {
                        saveEntryChangeLog(resolveStkBillTypeForChangeLog(billType), parenId, "STK_IO_BILL_ENTRY", existingId,
                            "UPDATE", old, incoming, operator, tenantId);
                    }
                } else {
                    StkIoBillEntry deleted = new StkIoBillEntry();
                    deleted.setId(existingId);
                    deleted.setDelFlag(1);
                    deleted.setDeleteBy(operator);
                    deleted.setDeleteTime(now);
                    deleted.setUpdateBy(operator);
                    deleted.setUpdateTime(now);
                    stkIoBillMapper.updateStkIoBillEntryById(deleted);
                    saveEntryChangeLog(resolveStkBillTypeForChangeLog(billType), parenId, "STK_IO_BILL_ENTRY", existingId,
                        "DELETE", oldEntryMap.get(existingId), null, operator, tenantId);
                }
            }
        }
        if (!newEntries.isEmpty()) {
            stkIoBillMapper.batchStkIoBillEntry(newEntries);
            for (StkIoBillEntry inserted : newEntries) {
                saveEntryChangeLog(resolveStkBillTypeForChangeLog(billType), parenId, "STK_IO_BILL_ENTRY", null,
                    "INSERT", null, inserted, operator, tenantId);
            }
        }
    }

    private String resolveStkBillTypeForChangeLog(Integer billType) {
        if (billType == null) {
            return "STK_IO_BILL";
        }
        return "STK_IO_BILL_" + billType;
    }

    private void saveEntryChangeLog(String billType, Long billId, String entryType, Long entryId, String actionType,
                                    Object before, Object after, String operator, String tenantId) {
        GzBillEntryChangeLog rec = new GzBillEntryChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setBillType(billType);
        rec.setBillId(billId);
        rec.setEntryType(entryType);
        rec.setEntryId(entryId);
        rec.setActionType(actionType);
        rec.setBeforeJson(before == null ? null : JSON.toJSONString(before));
        rec.setAfterJson(after == null ? null : JSON.toJSONString(after));
        rec.setOperator(operator);
        rec.setChangeTime(DateUtils.getNowDate());
        rec.setTenantId(StringUtils.isNotEmpty(tenantId) ? tenantId : SecurityUtils.requiredScopedTenantIdForSql());
        stkBillEntryChangeLogMapper.insert(rec);
    }

    private boolean isStkIoBillEntryChanged(StkIoBillEntry oldRow, StkIoBillEntry newRow) {
        if (oldRow == null || newRow == null) {
            return false;
        }
        return !Objects.equals(oldRow.getMaterialId(), newRow.getMaterialId())
            || !Objects.equals(oldRow.getQty(), newRow.getQty())
            || !Objects.equals(oldRow.getPrice(), newRow.getPrice())
            || !Objects.equals(oldRow.getAmt(), newRow.getAmt())
            || !Objects.equals(oldRow.getBatchNo(), newRow.getBatchNo())
            || !Objects.equals(oldRow.getBatchNumber(), newRow.getBatchNumber())
            || !Objects.equals(oldRow.getBeginTime(), newRow.getBeginTime())
            || !Objects.equals(oldRow.getEndTime(), newRow.getEndTime())
            || !Objects.equals(oldRow.getMainBarcode(), newRow.getMainBarcode())
            || !Objects.equals(oldRow.getSubBarcode(), newRow.getSubBarcode())
            || !Objects.equals(oldRow.getSupplerId(), newRow.getSupplerId())
            || !Objects.equals(oldRow.getWarehouseId(), newRow.getWarehouseId())
            || !Objects.equals(oldRow.getBillNo(), newRow.getBillNo())
            || !Objects.equals(oldRow.getRemark(), newRow.getRemark());
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
        if (StringUtils.isEmpty(stkIoBill.getTenantId())) {
            stkIoBill.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        syncBillHeaderSupplerFromUniformEntries(stkIoBill);
        assertReferencedQtyWithinLimits(stkIoBill, null);
        stkIoBill.setBillNo(getBillNumber("CK"));
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        int filteredCount = insertOutStkIoBillEntry(stkIoBill);
        stkIoBill.setDedupFilteredCount(filteredCount);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
        if (StringUtils.isNotEmpty(stkIoBill.getWhWarehouseApplyId())) {
            whWarehouseApplyService.syncWhApplyCkRefsAfterOutboundSave(stkIoBill.getId());
        }
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
        if (stkIoBill.getId() != null && stkIoBill.getBillType() == null) {
            StkIoBill existing = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (existing != null) {
                stkIoBill.setBillType(existing.getBillType());
            }
        }
        Integer bt = stkIoBill.getBillType();
        if (bt != null && (bt == 201 || bt == 301)) {
            assertWarehouseStockEntriesMatchBillHeader(stkIoBill, bt == 301);
        }
        syncBillHeaderSupplerFromUniformEntries(stkIoBill);
        assertReferencedQtyWithinLimits(stkIoBill, stkIoBill.getId());
        // 如果退货日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        int filteredCount = syncOutStkIoBillEntry(stkIoBill);
        stkIoBill.setDedupFilteredCount(filteredCount);
        int u = stkIoBillMapper.updateStkIoBill(stkIoBill);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
        if (bt != null && bt == 201) {
            whWarehouseApplyService.syncWhApplyCkRefsAfterOutboundSave(stkIoBill.getId());
        }
        return u;
    }

    @Transactional
    @Override
    public int insertTkStkIoBill(StkIoBill stkIoBill) {
        if (StringUtils.isEmpty(stkIoBill.getTenantId())) {
            stkIoBill.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        syncBillHeaderSupplerFromUniformEntries(stkIoBill);
        assertReferencedQtyWithinLimits(stkIoBill, null);
        stkIoBill.setBillNo(getTKNumber("TK"));
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertTKStkIoBillEntry(stkIoBill);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
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
        if (StringUtils.isEmpty(stkIoBill.getTenantId())) {
            stkIoBill.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        assertReferencedQtyWithinLimits(stkIoBill, null);
        // 如果退货日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setBillNo(getTHNumber("TH"));
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        int filteredCount = insertOutStkIoBillEntry(stkIoBill);
        stkIoBill.setDedupFilteredCount(filteredCount);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
        return rows;
    }

    @Transactional
    @Override
    public int updateTKStkIoBill(StkIoBill stkIoBill) {
        assertTkDepInventoryEntriesMatchBillHeader(stkIoBill);
        syncBillHeaderSupplerFromUniformEntries(stkIoBill);
        assertReferencedQtyWithinLimits(stkIoBill, stkIoBill.getId());
        // 如果制单日期为空，自动设置为当前日期
        if (stkIoBill.getBillDate() == null) {
            stkIoBill.setBillDate(DateUtils.getNowDate());
        }
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        syncTKStkIoBillEntry(stkIoBill);
        int u = stkIoBillMapper.updateStkIoBill(stkIoBill);
        if (stkIoBill.getDocRefList() != null && !stkIoBill.getDocRefList().isEmpty()) {
            StkIoBill reloaded = stkIoBillMapper.selectStkIoBillById(stkIoBill.getId());
            if (reloaded != null) {
                hcDocBillRefService.saveRefsAfterStkBillInsert(stkIoBill, reloaded);
            }
        }
        return u;
    }

    @Override
    public List<Map<String, Object>> selectRTHStkIoBillList(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectRTHStkIoBillList(stkIoBill);
    }

    @Override
    public TotalInfo selectRTHStkIoBillListTotal(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectRTHStkIoBillListTotal(stkIoBill);
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
        if (stkIoBill != null && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
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
    public List<Map<String, Object>> selectPurchaseSummaryBySupplier(StkIoBill stkIoBill) {
        if (stkIoBill != null && StringUtils.isEmpty(stkIoBill.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoBill.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoBillMapper.selectPurchaseSummaryBySupplier(stkIoBill);
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
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            Long whId = stkIoBill.getWarehouseId();
            assertTkDepInventoryEntriesMatchBillHeader(stkIoBill);
            validateTKStkIoBillEntries(whId, stkIoBill.getDepartmentId(), stkIoBillEntryList);
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                if (stkIoBillEntry.getDepInventoryId() == null && stkIoBillEntry.getStkInventoryId() == null && stkIoBillEntry.getKcNo() != null) {
                    stkIoBillEntry.setDepInventoryId(stkIoBillEntry.getKcNo());
                    stkIoBillEntry.setKcNo(null);
                }
                // 退库锁定目标仓库：将表头仓库ID反写到明细
                stkIoBillEntry.setWarehouseId(whId);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBillNo(stkIoBill.getBillNo());
                stkIoBillEntry.setDelFlag(0);
                fillOutboundEntrySupplerIdFromWarehouse(stkIoBill, stkIoBillEntry);
                stkIoBillEntry.setTenantId(tenantId);
                fillEntryMaterialSnapshot(stkIoBillEntry, tenantId);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 出库/退货(201/301)：明细关联的仓库库存归属仓库须与表头一致；301 时尚须供应商一致。
     */
    private void assertWarehouseStockEntriesMatchBillHeader(StkIoBill bill, boolean checkSupplier) {
        Long headerWh = bill.getWarehouseId();
        Long headerSup = bill.getSupplerId();
        List<StkIoBillEntry> list = bill.getStkIoBillEntryList();
        if (list == null || list.isEmpty()) {
            return;
        }
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            StkIoBillEntry e = list.get(i);
            if (e == null) {
                continue;
            }
            int row = i + 1;
            StkInventory inv = null;
            Long whKey = e.resolveStkInventoryKeyForWarehouseOps();
            if (whKey != null) {
                inv = stkInventoryMapper.selectStkInventoryById(whKey);
                if (inv == null) {
                    errors.add("第" + row + "行：仓库库存明细不存在(stk_inventory_id/kc_no=" + whKey + ")");
                    continue;
                }
            } else if (StringUtils.isNotEmpty(e.getBatchNo()) && headerWh != null) {
                inv = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(e.getBatchNo().trim(), headerWh);
                if (inv == null) {
                    errors.add("第" + row + "行：批次「" + e.getBatchNo() + "」在单据仓库下无对应仓库库存");
                    continue;
                }
            } else {
                errors.add("第" + row + "行：缺少库存关联(stk_inventory_id/批次)，无法校验仓库库存归属");
                continue;
            }
            if (headerWh != null && !headerWh.equals(inv.getWarehouseId())) {
                errors.add("第" + row + "行：仓库库存归属仓库与单据仓库不一致");
            }
            if (checkSupplier && headerSup != null && inv.getSupplierId() != null && !headerSup.equals(inv.getSupplierId())) {
                errors.add("第" + row + "行：仓库库存供应商与单据供应商不一致");
            }
        }
        if (!errors.isEmpty()) {
            throw new ServiceException(String.join("；", errors));
        }
    }

    /**
     * 退库(401)：明细科室库存归属仓库、科室须与表头一致（含无 dep_inventory_id 的批次行在表头仓库+科室下须有科室库存）。
     */
    private void assertTkDepInventoryEntriesMatchBillHeader(StkIoBill bill) {
        Long headerWh = bill.getWarehouseId();
        Long headerDep = bill.getDepartmentId();
        List<StkIoBillEntry> list = bill.getStkIoBillEntryList();
        if (list == null || list.isEmpty()) {
            return;
        }
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            StkIoBillEntry e = list.get(i);
            if (e == null) {
                continue;
            }
            int row = i + 1;
            Long depKeyTk = e.resolveDepInventoryKeyForDepOps();
            if (depKeyTk != null) {
                StkDepInventory di = stkDepInventoryMapper.selectStkDepInventoryById(depKeyTk);
                if (di == null) {
                    errors.add("第" + row + "行：科室库存明细不存在(dep_inventory_id/kc_no=" + depKeyTk + ")");
                    continue;
                }
                if (headerWh != null && !headerWh.equals(di.getWarehouseId())) {
                    errors.add("第" + row + "行：科室库存归属仓库与单据仓库不一致");
                }
                if (headerDep != null && di.getDepartmentId() != null && !headerDep.equals(di.getDepartmentId())) {
                    errors.add("第" + row + "行：科室库存归属科室与单据科室不一致");
                }
            } else if (StringUtils.isNotEmpty(e.getBatchNo())) {
                if (headerWh == null) {
                    errors.add("第" + row + "行：单据缺少仓库，无法校验批次所属科室库存");
                    continue;
                }
                if (headerDep == null) {
                    errors.add("第" + row + "行：明细未关联科室库存ID且单据缺少科室，无法校验");
                    continue;
                }
                StkDepInventory probe = new StkDepInventory();
                probe.setBatchNo(e.getBatchNo().trim());
                probe.setWarehouseId(headerWh);
                probe.setDepartmentId(headerDep);
                List<StkDepInventory> matches = stkDepInventoryMapper.selectStkDepInventoryList(probe);
                if (matches == null || matches.isEmpty()) {
                    errors.add("第" + row + "行：批次「" + e.getBatchNo() + "」在单据仓库、科室下无对应科室库存");
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ServiceException(String.join("；", errors));
        }
    }

    /**
     * 退库保存前校验：有科室库存主键时按行汇总与该行库存比；否则按批次号汇总与批次总库存比（兼容旧数据）
     */
    private void validateTKStkIoBillEntries(Long warehouseId, Long departmentId, List<StkIoBillEntry> stkIoBillEntryList) {
        if (stkIoBillEntryList == null || warehouseId == null) {
            return;
        }
        Map<Long, BigDecimal> qtyByDepInvId = new HashMap<>();
        for (StkIoBillEntry e : stkIoBillEntryList) {
            if (e == null || e.getQty() == null) {
                continue;
            }
            Long depKey = e.resolveDepInventoryKeyForDepOps();
            if (depKey != null) {
                qtyByDepInvId.merge(depKey, e.getQty(), BigDecimal::add);
            }
        }
        for (Map.Entry<Long, BigDecimal> en : qtyByDepInvId.entrySet()) {
            StkDepInventory di = stkDepInventoryMapper.selectStkDepInventoryById(en.getKey());
            if (di == null) {
                throw new ServiceException(String.format("科室库存不存在或无权访问，id=%s", en.getKey()));
            }
            if (!warehouseId.equals(di.getWarehouseId())) {
                throw new ServiceException(String.format("科室库存id：%s 与退库仓库不一致", en.getKey()));
            }
            if (departmentId != null && di.getDepartmentId() != null && !departmentId.equals(di.getDepartmentId())) {
                throw new ServiceException(String.format("科室库存id：%s 与退库科室不一致", en.getKey()));
            }
            if (di.getReceiptConfirmStatus() == null || di.getReceiptConfirmStatus() != 1) {
                throw new ServiceException(String.format("科室库存id：%s 未收货确认，不能退库", en.getKey()));
            }
            BigDecimal invQty = di.getQty() != null ? di.getQty() : BigDecimal.ZERO;
            if (en.getValue().compareTo(invQty) > 0) {
                throw new ServiceException(String.format("科室库存不足！申请退库数量：%s，科室库存：%s（库存id=%s）", en.getValue(), invQty, en.getKey()));
            }
        }
        Map<String, BigDecimal> qtyByBatch = new HashMap<>();
        for (StkIoBillEntry e : stkIoBillEntryList) {
            if (e == null || e.getQty() == null || e.resolveDepInventoryKeyForDepOps() != null) {
                continue;
            }
            String bn = e.getBatchNo();
            if (StringUtils.isEmpty(bn)) {
                throw new ServiceException("退库明细缺少科室库存id时，批次号不能为空");
            }
            qtyByBatch.merge(bn.trim(), e.getQty(), BigDecimal::add);
        }
        for (Map.Entry<String, BigDecimal> en : qtyByBatch.entrySet()) {
            BigDecimal inventoryQty = stkDepInventoryMapper.selectTKStkInvntoryByBatchNo(en.getKey(), warehouseId);
            if (inventoryQty == null) {
                inventoryQty = BigDecimal.ZERO;
            }
            if (en.getValue().compareTo(inventoryQty) > 0) {
                throw new ServiceException(String.format("科室库存不足！退库数量：%s，批次「%s」实际库存：%s", en.getValue(), en.getKey(), inventoryQty));
            }
        }
    }

    /**
     * 新增出库明细信息
     *
     * @param stkIoBill 出库对象
     */
    public int insertOutStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        Integer outBt = stkIoBill.getBillType();
        int filteredCount = 0;
        if (outBt != null && (outBt == 201 || outBt == 301)) {
            assertWarehouseStockEntriesMatchBillHeader(stkIoBill, outBt == 301);
        }
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            String tenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            Set<String> dedupKeys = new HashSet<>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                if (stkIoBillEntry == null) {
                    continue;
                }
                if (stkIoBillEntry.getStkInventoryId() == null && stkIoBillEntry.getDepInventoryId() == null && stkIoBillEntry.getKcNo() != null) {
                    stkIoBillEntry.setStkInventoryId(stkIoBillEntry.getKcNo());
                    stkIoBillEntry.setKcNo(null);
                }
                // 将表头仓库ID反写到明细，保证后续按仓库校验/锁定准确
                stkIoBillEntry.setWarehouseId(stkIoBill.getWarehouseId());
                String dedupKey = buildOutboundEntryDedupKey(stkIoBillEntry);
                if (StringUtils.isNotEmpty(dedupKey) && dedupKeys.contains(dedupKey)) {
                    filteredCount++;
                    continue;
                }
                validateInventory(stkIoBillEntry.getBatchNo(), stkIoBill.getWarehouseId(), stkIoBillEntryList);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBillNo(stkIoBill.getBillNo());
                stkIoBillEntry.setDelFlag(0);
                fillOutboundEntrySupplerIdFromWarehouse(stkIoBill, stkIoBillEntry);
                stkIoBillEntry.setTenantId(tenantId);
                fillEntryMaterialSnapshot(stkIoBillEntry, tenantId);
                list.add(stkIoBillEntry);
                if (StringUtils.isNotEmpty(dedupKey)) {
                    dedupKeys.add(dedupKey);
                }
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
        return filteredCount;
    }

    /**
     * 引用单据防重键：优先来源明细ID（whApplyEntryId），其次库存来源键，最后批次+耗材键。
     */
    private String buildOutboundEntryDedupKey(StkIoBillEntry e) {
        if (e == null) {
            return null;
        }
        if (StringUtils.isNotEmpty(e.getWhApplyEntryId())) {
            return "WHAPPLY#" + e.getWhApplyEntryId().trim();
        }
        if (e.getStkInventoryId() != null) {
            return "STK_INV#" + e.getStkInventoryId();
        }
        if (e.getDepInventoryId() != null) {
            return "DEP_INV#" + e.getDepInventoryId();
        }
        if (e.getKcNo() != null) {
            return "KCNO#" + e.getKcNo();
        }
        if (e.getMaterialId() != null && StringUtils.isNotEmpty(e.getBatchNo())) {
            return "MAT_BATCH#" + e.getMaterialId() + "#" + e.getBatchNo().trim();
        }
        return null;
    }

    /**
     * Validate warehouse inventory (batchNo + warehouseId).
     * @param stkIoBillEntryList
     */
    private void validateInventory(String oldBatchNo, Long oldWarehouseId, List<StkIoBillEntry> stkIoBillEntryList){
        if (oldWarehouseId == null) {
            throw new ServiceException("单据仓库不能为空，无法校验库存");
        }
        if (oldBatchNo == null || stkIoBillEntryList == null) {
            return;
        }
        BigDecimal sumQty = BigDecimal.ZERO;
        for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList) {
            if (stkIoBillEntry == null) {
                continue;
            }
            String batchNo = stkIoBillEntry.getBatchNo();
            BigDecimal qty = stkIoBillEntry.getQty();
            if (batchNo == null || qty == null) {
                continue;
            }
            if (!oldBatchNo.equals(batchNo)) {
                continue;
            }
            Long entryWarehouseId = stkIoBillEntry.getWarehouseId();
            if (entryWarehouseId != null && !oldWarehouseId.equals(entryWarehouseId)) {
                continue;
            }
            sumQty = sumQty.add(qty);
        }
        if (sumQty.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        StkInventory inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(oldBatchNo, oldWarehouseId);
        if (inventory == null) {
            inventory = stkInventoryMapper.selectStkInventoryOne(oldBatchNo);
        }
        if (inventory == null) {
            throw new ServiceException(String.format("批次「%s」无对应仓库库存，无法出库", oldBatchNo));
        }
        BigDecimal inventoryQty = inventory.getQty() != null ? inventory.getQty() : BigDecimal.ZERO;
        if (inventoryQty.compareTo(sumQty) < 0) {
            throw new ServiceException(String.format(
                    "实际库存不足！本单该批次在仓库下的出库合计：%s，当前库存：%s，批次：%s",
                    sumQty, inventoryQty, oldBatchNo));
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
        stkIoBill.setBillStatus(1);
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
    public StkIoBill createCkEntriesByWhApply(String whWarehouseApplyId) {
        if (StringUtils.isEmpty(whWarehouseApplyId)) {
            throw new ServiceException("仓库申请单ID不能为空");
        }
        WhWarehouseApply wh = whWarehouseApplyService.selectWhWarehouseApplyById(whWarehouseApplyId);
        if (wh == null) {
            throw new ServiceException(String.format("仓库申请单ID：%s 不存在", whWarehouseApplyId));
        }
        if (wh.getBillStatus() == null || wh.getBillStatus() != 2) {
            throw new ServiceException("仓库申请单未生效，不能生成出库单");
        }
        if (Integer.valueOf(1).equals(wh.getVoidWholeFlag())) {
            throw new ServiceException("库房申请单已整单作废，不能生成出库单");
        }
        StkIoBill stkIoBill = new StkIoBill();
        stkIoBill.setWarehouseId(wh.getWarehouseId());
        stkIoBill.setDepartmentId(wh.getDepartmentId());
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(1);
        stkIoBill.setRefBillNo(wh.getApplyBillNo());
        stkIoBill.setDApplyId(wh.getBasApplyId());
        stkIoBill.setWhWarehouseApplyId(wh.getId());
        stkIoBill.setWhWarehouseApplyBillNo(wh.getApplyBillNo());
        List<WhWarehouseApplyEntry> list = wh.getEntryList();
        if (list == null || list.isEmpty()) {
            throw new ServiceException(String.format("仓库申请单ID：%s 无明细", whWarehouseApplyId));
        }
        List<StkIoBillEntry> entryList = new ArrayList<>();
        for (WhWarehouseApplyEntry we : list) {
            if (we == null) {
                continue;
            }
            BigDecimal pend = we.getPendingOutboundQty();
            if (pend == null || pend.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            StkIoBillEntry stkIoBillEntry = new StkIoBillEntry();
            stkIoBillEntry.setMaterialId(we.getMaterialId());
            stkIoBillEntry.setQty(pend);
            stkIoBillEntry.setUnitPrice(we.getUnitPrice());
            if (we.getUnitPrice() != null) {
                stkIoBillEntry.setAmt(we.getUnitPrice().multiply(pend).setScale(2, RoundingMode.HALF_UP));
            } else {
                stkIoBillEntry.setAmt(we.getAmt());
            }
            stkIoBillEntry.setBatchNo(we.getBatchNo());
            stkIoBillEntry.setBatchNumber(we.getBatchNumber());
            stkIoBillEntry.setBeginTime(we.getBeginTime());
            stkIoBillEntry.setEndTime(we.getEndTime());
            stkIoBillEntry.setWarehouseId(we.getWarehouseId());
            if (we.getStkInventoryId() != null) {
                stkIoBillEntry.setStkInventoryId(we.getStkInventoryId());
                stkIoBillEntry.setKcNo(null);
            }
            stkIoBillEntry.setWhApplyEntryId(we.getId());
            if (we.getSupplierId() != null) {
                stkIoBillEntry.setSupplerId(String.valueOf(we.getSupplierId()));
            }
            if (we.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(we.getMaterialId());
                stkIoBillEntry.setMaterial(material);
            }
            entryList.add(stkIoBillEntry);
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("无可出库数量（可能已全部作废或已下推出库单）");
        }
        stkIoBill.setStkIoBillEntryList(entryList);
        return stkIoBill;
    }

    @Override
    public StkIoBill createCkEntriesByRkApply(String rkApplyId) {
        StkIoBill rkBill = this.selectStkIoBillById(Long.valueOf(rkApplyId));
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

        String tenantId = StringUtils.isNotEmpty(rkBill.getTenantId()) ? rkBill.getTenantId() : SecurityUtils.getCustomerId();
        Map<String, BigDecimal> usedMap = hcDocBillRefService.sumRefQtyBySrcBillIdAndRefType(tenantId,
            String.valueOf(rkBill.getId()), HcDocBillRefType.RK_TO_CK);

        StkIoBill ckBill = new StkIoBill();
        ckBill.setWarehouseId(rkBill.getWarehouseId());
        ckBill.setDepartmentId(rkBill.getDepartmentId());
        ckBill.setBillType(201);
        ckBill.setRefBillNo(rkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        List<HcDocBillRef> docRefList = new ArrayList<>();
        int rkLine = 0;
        for (StkIoBillEntry rkEntry : list) {
            rkLine++;
            if (rkEntry == null || (rkEntry.getDelFlag() != null && rkEntry.getDelFlag() == 1)) {
                continue;
            }
            if (rkEntry.getId() == null) {
                continue;
            }
            BigDecimal lineQty = rkEntry.getQty() != null ? rkEntry.getQty() : BigDecimal.ZERO;
            BigDecimal used = usedMap.getOrDefault(String.valueOf(rkEntry.getId()), BigDecimal.ZERO);
            BigDecimal refable = lineQty.subtract(used);
            if (refable.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            StkIoBillEntry ckEntry = new StkIoBillEntry();
            ckEntry.setMaterialId(rkEntry.getMaterialId());
            ckEntry.setQty(refable);
            ckEntry.setUnitPrice(rkEntry.getUnitPrice());
            if (rkEntry.getUnitPrice() != null) {
                ckEntry.setAmt(rkEntry.getUnitPrice().multiply(refable).setScale(2, RoundingMode.HALF_UP));
            } else if (rkEntry.getAmt() != null && lineQty.compareTo(BigDecimal.ZERO) > 0) {
                ckEntry.setAmt(rkEntry.getAmt().multiply(refable).divide(lineQty, 2, RoundingMode.HALF_UP));
            } else {
                ckEntry.setAmt(BigDecimal.ZERO);
            }
            ckEntry.setBatchNo(rkEntry.getBatchNo());
            ckEntry.setBatchNumber(rkEntry.getBatchNumber());
            ckEntry.setBeginTime(rkEntry.getBeginTime());
            ckEntry.setEndTime(rkEntry.getEndTime());
            ckEntry.setSupplerId(rkEntry.getSupplerId());
            if (rkEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(rkEntry.getMaterialId());
                ckEntry.setMaterial(material);
            }
            copyInboundRefDisplayFromRkLine(rkEntry, ckEntry);
            entryList.add(ckEntry);
            docRefList.add(newSrcDocRef(HcDocBillRefType.RK_TO_CK, "101", rkBill, rkEntry, rkLine));
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("该入库单已全部被引用或无可再引用的数量");
        }
        ckBill.setStkIoBillEntryList(entryList);
        ckBill.setDocRefList(docRefList);
        syncBillHeaderSupplerFromUniformEntries(ckBill);
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
    public StkIoBill createRkEntriesByDeliveryNo(String deliveryNo) {
        if (StringUtils.isEmpty(deliveryNo)) {
            throw new ServiceException("配送单号不能为空");
        }
        String no = deliveryNo.trim();
        String xml = fetchDeliveryXml(no);
        List<Map<String, Object>> groupedRows = parseDeliveryXmlAndGroup(xml);
        if (groupedRows.isEmpty()) {
            throw new ServiceException("配送单无可用明细数据：" + no);
        }

        StkIoBill stkIoBill = new StkIoBill();
        stkIoBill.setBillType(101);
        stkIoBill.setRefBillNo(no);
        List<StkIoBillEntry> entryList = new ArrayList<>();
        List<String> missCodes = new ArrayList<>();
        for (Map<String, Object> row : groupedRows) {
            String code = String.valueOf(row.get("code"));
            if (StringUtils.isEmpty(code)) {
                continue;
            }
            FdMaterial material = fdMaterialMapper.selectFdMaterialByMainBarcode(code);
            if (material == null) {
                missCodes.add(code);
                continue;
            }
            BigDecimal qty = toBigDecimal(row.get("qty"));
            BigDecimal unitPrice = toBigDecimal(row.get("unitPrice"));
            if (unitPrice.compareTo(BigDecimal.ZERO) == 0 && material.getPrice() != null) {
                unitPrice = material.getPrice();
            }
            StkIoBillEntry e = new StkIoBillEntry();
            e.setMaterialId(material.getId());
            e.setMaterial(material);
            e.setQty(qty);
            e.setUnitPrice(unitPrice);
            e.setAmt(qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP));
            e.setBatchNumber(String.valueOf(row.getOrDefault("batchNumber", "")));
            e.setBeginTime(String.valueOf(row.getOrDefault("beginTime", "")));
            e.setEndTime(String.valueOf(row.getOrDefault("endTime", "")));
            entryList.add(e);
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("配送单明细未匹配到本地耗材档案，无法生成入库明细");
        }
        stkIoBill.setStkIoBillEntryList(entryList);
        if (!missCodes.isEmpty()) {
            String uniqueMiss = missCodes.stream().distinct().collect(Collectors.joining(","));
            stkIoBill.setRemark("以下编码未匹配本地耗材档案并已跳过：" + uniqueMiss);
        }
        return stkIoBill;
    }

    private String fetchDeliveryXml(String deliveryNo) {
        try {
            String base = interfaceUrl;
            if (!base.endsWith("/")) {
                base += "/";
            }
            String url = base + "api/scm/zs/deliveryData/download";
            String param = "deliveryNo=" + URLEncoder.encode(deliveryNo, "UTF-8");
            String xml = HttpUtils.sendGet(url, param, "UTF-8");
            if (StringUtils.isEmpty(xml) || !xml.contains("<LIST>")) {
                throw new ServiceException("未获取到有效配送单明细数据：" + deliveryNo);
            }
            return xml;
        } catch (Exception e) {
            throw new ServiceException("获取配送单明细失败：" + e.getMessage());
        }
    }

    private List<Map<String, Object>> parseDeliveryXmlAndGroup(String xml) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(false);
            Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            NodeList rows = doc.getElementsByTagName("LIST");
            Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                String code = xmlTagValue(row, "CODE");
                if (StringUtils.isEmpty(code)) {
                    continue;
                }
                BigDecimal qty = toBigDecimal(xmlTagValue(row, "SL"));
                BigDecimal unitPrice = toBigDecimal(xmlTagValue(row, "DJ"));
                String batchNumber = xmlTagValue(row, "PH");
                String endTime = xmlTagValue(row, "YXQ");
                String beginTime = xmlTagValue(row, "SCRQ");
                String key = code + "|" + unitPrice.toPlainString() + "|" + batchNumber + "|" + endTime + "|" + beginTime;
                Map<String, Object> agg = grouped.get(key);
                if (agg == null) {
                    agg = new HashMap<>();
                    agg.put("code", code);
                    agg.put("qty", BigDecimal.ZERO);
                    agg.put("unitPrice", unitPrice);
                    agg.put("batchNumber", batchNumber);
                    agg.put("endTime", endTime);
                    agg.put("beginTime", beginTime);
                    grouped.put(key, agg);
                }
                BigDecimal sum = toBigDecimal(agg.get("qty")).add(qty);
                agg.put("qty", sum);
            }
            result.addAll(grouped.values());
            return result;
        } catch (Exception e) {
            throw new ServiceException("解析配送单数据失败：" + e.getMessage());
        }
    }

    private static String xmlTagValue(Element row, String tagName) {
        NodeList nodes = row.getElementsByTagName(tagName);
        if (nodes == null || nodes.getLength() == 0 || nodes.item(0) == null) {
            return "";
        }
        String val = nodes.item(0).getTextContent();
        return val == null ? "" : val.trim();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        String txt = String.valueOf(value).trim();
        if (txt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(txt);
        } catch (Exception ignore) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public StkIoBill createThEntriesByRkApply(String rkApplyId) {
        StkIoBill rkBill = this.selectStkIoBillById(Long.valueOf(rkApplyId));
        if (rkBill == null) {
            throw new ServiceException(String.format("入库单ID：%s，不存在!", rkApplyId));
        }
        if (rkBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("入库单ID：%s，未审核，不能生成退货单!", rkApplyId));
        }

        List<StkIoBillEntry> list = rkBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("入库单ID：%s，明细不存在!", rkApplyId));
        }
        String tenantId = StringUtils.isNotEmpty(rkBill.getTenantId()) ? rkBill.getTenantId() : SecurityUtils.getCustomerId();
        Map<String, BigDecimal> usedMap = hcDocBillRefService.sumRefQtyBySrcBillIdAndRefType(tenantId,
            String.valueOf(rkBill.getId()), HcDocBillRefType.RK_TO_TH);

        StkIoBill thBill = new StkIoBill();
        thBill.setWarehouseId(rkBill.getWarehouseId());
        thBill.setDepartmentId(rkBill.getDepartmentId());
        thBill.setSupplerId(rkBill.getSupplerId());
        thBill.setBillType(301);
        thBill.setRefBillNo(rkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        List<HcDocBillRef> docRefList = new ArrayList<>();
        int rkLine = 0;
        for (StkIoBillEntry srcEntry : list) {
            rkLine++;
            if (srcEntry == null || (srcEntry.getDelFlag() != null && srcEntry.getDelFlag() == 1)) {
                continue;
            }
            if (srcEntry.getId() == null) {
                continue;
            }
            BigDecimal lineQty = srcEntry.getQty() != null ? srcEntry.getQty() : BigDecimal.ZERO;
            BigDecimal used = usedMap.getOrDefault(String.valueOf(srcEntry.getId()), BigDecimal.ZERO);
            BigDecimal refable = lineQty.subtract(used);
            if (refable.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            StkIoBillEntry thEntry = new StkIoBillEntry();
            thEntry.setMaterialId(srcEntry.getMaterialId());
            thEntry.setQty(refable);
            thEntry.setUnitPrice(srcEntry.getUnitPrice());
            if (srcEntry.getUnitPrice() != null) {
                thEntry.setAmt(srcEntry.getUnitPrice().multiply(refable).setScale(2, RoundingMode.HALF_UP));
            } else if (srcEntry.getAmt() != null && lineQty.compareTo(BigDecimal.ZERO) > 0) {
                thEntry.setAmt(srcEntry.getAmt().multiply(refable).divide(lineQty, 2, RoundingMode.HALF_UP));
            } else {
                thEntry.setAmt(BigDecimal.ZERO);
            }
            thEntry.setBatchNo(srcEntry.getBatchNo());
            thEntry.setBatchNumber(srcEntry.getBatchNumber());
            thEntry.setBeginTime(srcEntry.getBeginTime());
            thEntry.setEndTime(srcEntry.getEndTime());
            thEntry.setSupplerId(srcEntry.getSupplerId());
            if (srcEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(srcEntry.getMaterialId());
                thEntry.setMaterial(material);
            }
            copyInboundRefDisplayFromRkLine(srcEntry, thEntry);
            entryList.add(thEntry);
            docRefList.add(newSrcDocRef(HcDocBillRefType.RK_TO_TH, "101", rkBill, srcEntry, rkLine));
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("该入库单已全部被引用或无可再引用的数量");
        }
        thBill.setStkIoBillEntryList(entryList);
        thBill.setDocRefList(docRefList);
        syncBillHeaderSupplerFromUniformEntries(thBill);
        return thBill;
    }

    @Override
    public StkIoBill createTkEntriesByCkApply(String ckApplyId) {
        StkIoBill ckBill = this.selectStkIoBillById(Long.valueOf(ckApplyId));
        if (ckBill == null) {
            throw new ServiceException(String.format("出库单ID：%s，不存在!", ckApplyId));
        }
        if (ckBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("出库单ID：%s，未审核，不能生成退库单!", ckApplyId));
        }

        List<StkIoBillEntry> list = ckBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("出库单ID：%s，明细不存在!", ckApplyId));
        }

        String tenantId = StringUtils.isNotEmpty(ckBill.getTenantId()) ? ckBill.getTenantId() : SecurityUtils.getCustomerId();
        Map<String, BigDecimal> usedMap = hcDocBillRefService.sumRefQtyBySrcBillIdAndRefType(tenantId,
            String.valueOf(ckBill.getId()), HcDocBillRefType.CK_TO_TK);

        StkIoBill tkBill = new StkIoBill();
        tkBill.setWarehouseId(ckBill.getWarehouseId());
        tkBill.setDepartmentId(ckBill.getDepartmentId());
        tkBill.setBillType(401);
        tkBill.setRefBillNo(ckBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        List<HcDocBillRef> docRefList = new ArrayList<>();
        int ckLine = 0;
        for (StkIoBillEntry srcEntry : list) {
            ckLine++;
            if (srcEntry == null || (srcEntry.getDelFlag() != null && srcEntry.getDelFlag() == 1)) {
                continue;
            }
            if (srcEntry.getId() == null) {
                continue;
            }
            BigDecimal lineQty = srcEntry.getQty() != null ? srcEntry.getQty() : BigDecimal.ZERO;
            BigDecimal used = usedMap.getOrDefault(String.valueOf(srcEntry.getId()), BigDecimal.ZERO);
            BigDecimal refable = lineQty.subtract(used);
            if (refable.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            StkIoBillEntry tkEntry = new StkIoBillEntry();
            tkEntry.setMaterialId(srcEntry.getMaterialId());
            tkEntry.setQty(refable);
            tkEntry.setUnitPrice(srcEntry.getUnitPrice());
            if (srcEntry.getUnitPrice() != null) {
                tkEntry.setAmt(srcEntry.getUnitPrice().multiply(refable).setScale(2, RoundingMode.HALF_UP));
            } else if (srcEntry.getAmt() != null && lineQty.compareTo(BigDecimal.ZERO) > 0) {
                tkEntry.setAmt(srcEntry.getAmt().multiply(refable).divide(lineQty, 2, RoundingMode.HALF_UP));
            } else {
                tkEntry.setAmt(BigDecimal.ZERO);
            }
            tkEntry.setBatchNo(srcEntry.getBatchNo());
            tkEntry.setBatchNumber(srcEntry.getBatchNumber());
            tkEntry.setBeginTime(srcEntry.getBeginTime());
            tkEntry.setEndTime(srcEntry.getEndTime());
            tkEntry.setStkInventoryId(srcEntry.getStkInventoryId());
            tkEntry.setDepInventoryId(srcEntry.getDepInventoryId());
            tkEntry.setKcNo(srcEntry.getDepInventoryId() != null ? srcEntry.getDepInventoryId() : srcEntry.getKcNo());
            tkEntry.setSupplerId(srcEntry.getSupplerId());
            if (srcEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(srcEntry.getMaterialId());
                tkEntry.setMaterial(material);
            }
            entryList.add(tkEntry);
            docRefList.add(newSrcDocRef(HcDocBillRefType.CK_TO_TK, "201", ckBill, srcEntry, ckLine));
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("该出库单已全部被引用或无可再引用的数量");
        }
        tkBill.setStkIoBillEntryList(entryList);
        tkBill.setDocRefList(docRefList);
        syncBillHeaderSupplerFromUniformEntries(tkBill);
        return tkBill;
    }


    @Override
    public StkIoBill createThEntriesByTkApply(String tkApplyId) {
        StkIoBill tkBill = this.selectStkIoBillById(Long.valueOf(tkApplyId));
        if (tkBill == null) {
            throw new ServiceException(String.format("科室退库单ID：%s，不存在!", tkApplyId));
        }
        if (tkBill.getBillStatus() != 2) {
            throw new ServiceException(String.format("科室退库单ID：%s，未审核，不能生成退货单!", tkApplyId));
        }

        List<StkIoBillEntry> list = tkBill.getStkIoBillEntryList();
        if (list == null || list.size() == 0) {
            throw new ServiceException(String.format("科室退库单ID：%s，明细不存在!", tkApplyId));
        }
        String tenantId = StringUtils.isNotEmpty(tkBill.getTenantId()) ? tkBill.getTenantId() : SecurityUtils.getCustomerId();
        Map<String, BigDecimal> usedMap = hcDocBillRefService.sumRefQtyBySrcBillIdAndRefType(tenantId,
            String.valueOf(tkBill.getId()), HcDocBillRefType.TK_TO_TH);

        java.util.LinkedHashSet<Long> supSet = new java.util.LinkedHashSet<>();
        for (StkIoBillEntry x : list) {
            if (x == null || (x.getDelFlag() != null && x.getDelFlag() == 1)) {
                continue;
            }
            Long sid = parseSupplerIdString(x.getSupplerId());
            if (sid != null) {
                supSet.add(sid);
            }
        }
        if (supSet.size() > 1) {
            throw new ServiceException("科室退库单明细供应商不唯一，不能生成退货单");
        }

        StkIoBill thBill = new StkIoBill();
        thBill.setWarehouseId(tkBill.getWarehouseId());
        if (supSet.size() == 1) {
            thBill.setSupplerId(supSet.iterator().next());
        } else {
            thBill.setSupplerId(tkBill.getSupplerId());
        }
        thBill.setBillType(301);
        thBill.setRefBillNo(tkBill.getBillNo());
        List<StkIoBillEntry> entryList = new ArrayList<>();
        List<HcDocBillRef> docRefList = new ArrayList<>();
        int tkLine = 0;
        for (StkIoBillEntry srcEntry : list) {
            tkLine++;
            if (srcEntry == null || (srcEntry.getDelFlag() != null && srcEntry.getDelFlag() == 1)) {
                continue;
            }
            if (srcEntry.getId() == null) {
                continue;
            }
            BigDecimal lineQty = srcEntry.getQty() != null ? srcEntry.getQty() : BigDecimal.ZERO;
            BigDecimal used = usedMap.getOrDefault(String.valueOf(srcEntry.getId()), BigDecimal.ZERO);
            BigDecimal refable = lineQty.subtract(used);
            if (refable.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            StkIoBillEntry thEntry = new StkIoBillEntry();
            thEntry.setMaterialId(srcEntry.getMaterialId());
            thEntry.setQty(refable);
            thEntry.setUnitPrice(srcEntry.getUnitPrice());
            if (srcEntry.getUnitPrice() != null) {
                thEntry.setAmt(srcEntry.getUnitPrice().multiply(refable).setScale(2, RoundingMode.HALF_UP));
            } else if (srcEntry.getAmt() != null && lineQty.compareTo(BigDecimal.ZERO) > 0) {
                thEntry.setAmt(srcEntry.getAmt().multiply(refable).divide(lineQty, 2, RoundingMode.HALF_UP));
            } else {
                thEntry.setAmt(BigDecimal.ZERO);
            }
            thEntry.setBatchNo(srcEntry.getBatchNo());
            thEntry.setBatchNumber(srcEntry.getBatchNumber());
            thEntry.setBeginTime(srcEntry.getBeginTime());
            thEntry.setEndTime(srcEntry.getEndTime());
            thEntry.setSupplerId(srcEntry.getSupplerId());
            if (srcEntry.getMaterialId() != null) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(srcEntry.getMaterialId());
                thEntry.setMaterial(material);
            }
            copyTkReturnRefDisplayFromTkLine(srcEntry, thEntry);
            entryList.add(thEntry);
            docRefList.add(newSrcDocRef(HcDocBillRefType.TK_TO_TH, "401", tkBill, srcEntry, tkLine));
        }
        if (entryList.isEmpty()) {
            throw new ServiceException("该科室退库单已全部被引用或无可再引用的数量");
        }
        thBill.setStkIoBillEntryList(entryList);
        thBill.setDocRefList(docRefList);
        syncBillHeaderSupplerFromUniformEntries(thBill);
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

        // 操作人权限：必须拥有单据所属科室权限（管理员放开）
        Long operatorUserId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        // 确认人字段以当前登录人为准，避免前端/调用方伪造
        confirmBy = SecurityUtils.getUserIdStr();
        Set<Long> permittedDeptIdSet = null;
        List<Long> deptIds = tenantScopeService.resolveDepartmentScope(operatorUserId, customerId);
        // null 表示租户管理员/不限制；空集合表示无权限
        if (deptIds != null) {
            if (deptIds.isEmpty()) {
                throw new ServiceException("未配置科室权限，无法进行收货确认");
            }
            permittedDeptIdSet = new HashSet<>(deptIds);
        }

        String[] idArray = ids.split(",");
        int successCount = 0;
        for (String idStr : idArray) {
            Long id = Long.parseLong(idStr.trim());
            StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
            if (stkIoBill == null) {
                continue;
            }
            if (permittedDeptIdSet != null) {
                Long depId = stkIoBill.getDepartmentId();
                if (depId == null || !permittedDeptIdSet.contains(depId)) {
                    throw new ServiceException(String.format("无权确认该科室单据：出库单号=%s", stkIoBill.getBillNo()));
                }
            }
            if (tryApplyOutboundReceiptConfirmation(stkIoBill, confirmBy)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 出库单收货确认核心逻辑（与 confirmReceipt 单条处理一致）。
     * 审核通过后自动确认场景不校验科室数据权限，仍走同一业务校验与科室流水。
     *
     * @param confirmBy 确认人（科室流水 create_by；衡水三院自动确认时为审核人 auditBy）
     * @return 是否完成确认（未满足条件已确认则跳过返回 false）
     */
    private boolean tryApplyOutboundReceiptConfirmation(StkIoBill stkIoBill, String confirmBy) {
        if (stkIoBill == null) {
            return false;
        }
        if (stkIoBill.getBillStatus() == null || stkIoBill.getBillStatus() != 2) {
            return false;
        }
        if (stkIoBill.getBillType() == null || stkIoBill.getBillType() != 201) {
            return false;
        }
        if (stkIoBill.getReceiptConfirmStatus() != null && stkIoBill.getReceiptConfirmStatus() == 1) {
            return false;
        }

        // 收货确认：科室库存在出库审核时已插入(未确认)，此处仅将对应科室库存更新为已确认并插科室流水；绝不再次 insert 科室库存（避免双倍库存）
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        String billTenantId = StringUtils.isNotEmpty(stkIoBill.getTenantId()) ? stkIoBill.getTenantId() : SecurityUtils.getCustomerId();
        if (stkIoBillEntryList != null && !stkIoBillEntryList.isEmpty()) {
            for (StkIoBillEntry entry : stkIoBillEntryList) {
                if (entry == null) {
                    continue;
                }
                Long depId;
                StkDepInventory stkDepInventory = null;
                Long depLookupKey = entry.resolveDepInventoryKeyForDepOps();
                if (depLookupKey != null) {
                    stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryById(depLookupKey);
                    if (stkDepInventory != null
                            && (!Objects.equals(stkIoBill.getId(), stkDepInventory.getBillId())
                            || !Objects.equals(entry.getId(), stkDepInventory.getBillEntryId()))) {
                        stkDepInventory = null;
                    }
                }
                if (stkDepInventory == null && stkIoBill.getId() != null && entry.getId() != null) {
                    stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryByBillEntry(stkIoBill.getId(), entry.getId(), billTenantId);
                }
                if (stkDepInventory == null) {
                    throw new ServiceException(String.format(
                            "收货确认失败：出库单「%s」未找到明细对应的科室库存（出库审核应已生成、未确认）。批次：%s，请检查出库是否已审核或联系管理员。",
                            stkIoBill.getBillNo(), entry.getBatchNo() != null ? entry.getBatchNo() : "-"));
                }
                // 校验科室库存记录是否确属当前出库单及当前明细，避免收货确认改错明细
                if (!Objects.equals(stkIoBill.getId(), stkDepInventory.getBillId()) || !Objects.equals(entry.getId(), stkDepInventory.getBillEntryId())) {
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
                // 明细科室库存主键未反写时补写（kc_no 同步为兼容镜像）
                if (entry.getId() != null && (entry.getDepInventoryId() == null || !entry.getDepInventoryId().equals(depId))) {
                    stkIoBillMapper.updateStkIoBillEntryDepInventoryRef(entry.getId(), depId);
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
                // 历史数据科室库存未写 kc_no 时补写为来源仓库库存 id
                if (stkDepInventory.getKcNo() == null && inventory != null && inventory.getId() != null) {
                    stkDepInventory.setKcNo(inventory.getId());
                    stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
                }
                HcKsFlow ksFlow = new HcKsFlow();
                ksFlow.setBillId(stkIoBill.getId());
                ksFlow.setEntryId(entry.getId());
                ksFlow.setDepartmentId(stkIoBill.getDepartmentId());
                ksFlow.setWarehouseId(stkIoBill.getWarehouseId());
                ksFlow.setMaterialId(entry.getMaterialId());
                ksFlow.setBatchNo(entry.getBatchNo());
                ksFlow.setBatchNumber(entry.getBatchNumber());
                if (inventory != null) {
                    ksFlow.setBatchId(inventory.getBatchId());
                }
                ksFlow.setQty(entry.getQty());
                ksFlow.setUnitPrice(entry.getUnitPrice());
                ksFlow.setAmt(entry.getAmt());
                ksFlow.setBeginTime(entry.getBeginTime());
                ksFlow.setEndTime(entry.getEndTime());
                Long ksSupplierId = resolveStockFlowSupplierId(stkIoBill, entry, inventory);
                ksFlow.setSupplierId(ksSupplierId != null ? String.valueOf(ksSupplierId) : null);
                ksFlow.setFactoryId(resolveFactoryId(inventory));
                if (inventory != null) {
                    ksFlow.setMainBarcode(inventory.getMainBarcode());
                    ksFlow.setSubBarcode(inventory.getSubBarcode());
                }
                ksFlow.setKcNo(depId);
                ksFlow.setLx("CK");
                ksFlow.setOriginBusinessType("出库结算");
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
        return stkIoBillMapper.updateStkIoBill(stkIoBill) > 0;
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

        CellStyle subtotalTextStyle = wb.createCellStyle();
        subtotalTextStyle.cloneStyleFrom(dataTextStyle);
        subtotalTextStyle.setFont(fontBold);
        subtotalTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subtotalTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subtotalTextStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle subtotalNumStyle = wb.createCellStyle();
        subtotalNumStyle.cloneStyleFrom(dataNumStyle);
        subtotalNumStyle.setFont(fontBold);
        subtotalNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subtotalNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle emptyMsgStyle = wb.createCellStyle();
        Font fMsg = wb.createFont();
        fMsg.setBold(true);
        fMsg.setFontHeightInPoints((short) 12);
        emptyMsgStyle.setFont(fMsg);
        emptyMsgStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        emptyMsgStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        emptyMsgStyle.setAlignment(HorizontalAlignment.CENTER);

        final int lastCol = 8;
        DataFormatter dataFormatter = new DataFormatter();
        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalAmt = BigDecimal.ZERO;

        int rowNum = 0;
        if (byBill.isEmpty())
        {
            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 14 * 256);
            sheet.setColumnWidth(2, 12 * 256);
            sheet.setColumnWidth(3, 8 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 10 * 256);
            sheet.setColumnWidth(6, 12 * 256);
            sheet.setColumnWidth(7, 14 * 256);
            sheet.setColumnWidth(8, 12 * 256);
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
            sheet.setColumnWidth(5, 10 * 256);
            sheet.setColumnWidth(6, 12 * 256);
            sheet.setColumnWidth(7, 14 * 256);
            sheet.setColumnWidth(8, 12 * 256);

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
                String[] cols = { "名称", "规格", "型号", "单位", "数量", "单价", "金额", "批号", "有效期" };
                for (int i = 0; i < cols.length; i++)
                {
                    Cell hc = head.createCell(i);
                    hc.setCellValue(cols[i]);
                    hc.setCellStyle(detailHeadStyle);
                }
                BigDecimal billTotalQty = BigDecimal.ZERO;
                BigDecimal billTotalAmt = BigDecimal.ZERO;
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
                    Cell cup = dr.createCell(5);
                    if (r.getUnitPrice() != null)
                    {
                        cup.setCellValue(r.getUnitPrice().doubleValue());
                        cup.setCellStyle(dataNumStyle);
                    }
                    else
                    {
                        cup.setCellValue("");
                        cup.setCellStyle(dataTextStyle);
                    }
                    Cell ca = dr.createCell(6);
                    if (r.getAmt() != null)
                    {
                        ca.setCellValue(r.getAmt().doubleValue());
                        ca.setCellStyle(dataNumStyle);
                    }
                    else
                    {
                        ca.setCellValue("");
                        ca.setCellStyle(dataTextStyle);
                    }
                    setCellStr(dr, 7, r.getBatchPh(), dataTextStyle);
                    setCellStr(dr, 8, r.getEndTime() != null ? sdf.format(r.getEndTime()) : "", dataTextStyle);
                    setDetailRowHeightAuto(dr, sheet, dataFormatter);
                    if (r.getQty() != null)
                    {
                        billTotalQty = billTotalQty.add(r.getQty());
                    }
                    if (r.getAmt() != null)
                    {
                        billTotalAmt = billTotalAmt.add(r.getAmt());
                    }
                }

                Row subtotalRow = sheet.createRow(rowNum++);
                Cell subLabel = subtotalRow.createCell(0);
                subLabel.setCellValue("本单合计");
                subLabel.setCellStyle(subtotalTextStyle);
                sheet.addMergedRegion(new CellRangeAddress(subtotalRow.getRowNum(), subtotalRow.getRowNum(), 0, 3));
                for (int col = 1; col <= 3; col++)
                {
                    Cell filler = subtotalRow.createCell(col);
                    filler.setCellStyle(subtotalTextStyle);
                }
                Cell subQty = subtotalRow.createCell(4);
                subQty.setCellValue(billTotalQty.doubleValue());
                subQty.setCellStyle(subtotalNumStyle);
                Cell subUnitPrice = subtotalRow.createCell(5);
                subUnitPrice.setCellValue("");
                subUnitPrice.setCellStyle(subtotalTextStyle);
                Cell subAmt = subtotalRow.createCell(6);
                subAmt.setCellValue(billTotalAmt.doubleValue());
                subAmt.setCellStyle(subtotalNumStyle);
                Cell subBatch = subtotalRow.createCell(7);
                subBatch.setCellValue("");
                subBatch.setCellStyle(subtotalTextStyle);
                Cell subExpire = subtotalRow.createCell(8);
                subExpire.setCellValue("");
                subExpire.setCellStyle(subtotalTextStyle);
                subtotalRow.setHeightInPoints(20);

                grandTotalQty = grandTotalQty.add(billTotalQty);
                grandTotalAmt = grandTotalAmt.add(billTotalAmt);
                rowNum++;
            }

            Row grandRow = sheet.createRow(rowNum++);
            Cell grandLabel = grandRow.createCell(0);
            grandLabel.setCellValue("总合计");
            grandLabel.setCellStyle(subtotalTextStyle);
            sheet.addMergedRegion(new CellRangeAddress(grandRow.getRowNum(), grandRow.getRowNum(), 0, 3));
            for (int col = 1; col <= 3; col++)
            {
                Cell filler = grandRow.createCell(col);
                filler.setCellStyle(subtotalTextStyle);
            }
            Cell grandQtyCell = grandRow.createCell(4);
            grandQtyCell.setCellValue(grandTotalQty.doubleValue());
            grandQtyCell.setCellStyle(subtotalNumStyle);
            Cell grandUnitPrice = grandRow.createCell(5);
            grandUnitPrice.setCellValue("");
            grandUnitPrice.setCellStyle(subtotalTextStyle);
            Cell grandAmtCell = grandRow.createCell(6);
            grandAmtCell.setCellValue(grandTotalAmt.doubleValue());
            grandAmtCell.setCellStyle(subtotalNumStyle);
            Cell grandBatch = grandRow.createCell(7);
            grandBatch.setCellValue("");
            grandBatch.setCellStyle(subtotalTextStyle);
            Cell grandExpire = grandRow.createCell(8);
            grandExpire.setCellValue("");
            grandExpire.setCellStyle(subtotalTextStyle);
            grandRow.setHeightInPoints(22);
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
        for (int col = 0; col <= 8; col++)
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

    private static void setCellDecimal(Row row, int col, BigDecimal val, CellStyle style)
    {
        Cell c = row.createCell(col);
        if (val != null)
        {
            c.setCellValue(val.doubleValue());
        }
        c.setCellStyle(style);
    }

    @Override
    public void applyCtkDepartmentScopeToQuery(StkIoBill stkIoBill)
    {
        if (stkIoBill == null)
        {
            return;
        }
        if (stkIoBill.getParams() == null)
        {
            stkIoBill.setParams(new HashMap<>());
        }
        tenantScopeService.applyDepartmentScopeQueryParams(
            stkIoBill.getParams(), SecurityUtils.getUserId(), SecurityUtils.getCustomerId());
    }

    @Override
    public void exportCTKOverallDetailXlsx(StkIoBill q, HttpServletResponse response) throws IOException
    {
        if (q == null)
        {
            q = new StkIoBill();
        }
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        applyCtkDepartmentScopeToQuery(q);
        List<Map<String, Object>> mapList = stkIoBillMapper.selectCTKStkIoBillList(q);
        SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fnFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String beginStr = q.getBeginDate() != null ? dayFmt.format(q.getBeginDate()) : "";
        String endStr = "";
        if (q.getEndDate() != null)
        {
            endStr = dayFmt.format(q.getEndDate());
            if (endStr.length() > 10)
            {
                endStr = endStr.substring(0, 10);
            }
        }
        String title = "出退库明细_统计时间" + beginStr + "至" + endStr;
        String[] headers = new String[] {
            "耗材编码", "科室", "业务日期", "耗材名称", "规格", "生产厂家", "单位", "单价", "数量", "金额", "批号", "财务分类", "供应商", "仓库名称"
        };
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("出退库明细");
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle headStyle = wb.createCellStyle();
        Font hf = wb.createFont();
        hf.setBold(true);
        headStyle.setFont(hf);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setThinBorderAround(headStyle);
        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setThinBorderAround(dataStyle);
        CellStyle dataNumPriceAmtStyle = wb.createCellStyle();
        dataNumPriceAmtStyle.cloneStyleFrom(dataStyle);
        dataNumPriceAmtStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataNumPriceAmtStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.0000"));
        CellStyle dataNumQtyStyle = wb.createCellStyle();
        dataNumQtyStyle.cloneStyleFrom(dataStyle);
        dataNumQtyStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataNumQtyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        CellStyle totalLabelStyle = wb.createCellStyle();
        totalLabelStyle.cloneStyleFrom(headStyle);
        totalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle totalNumPriceAmtStyle = wb.createCellStyle();
        totalNumPriceAmtStyle.cloneStyleFrom(dataNumPriceAmtStyle);
        Font totalFont = wb.createFont();
        totalFont.setBold(true);
        totalNumPriceAmtStyle.setFont(totalFont);
        CellStyle totalNumQtyStyle = wb.createCellStyle();
        totalNumQtyStyle.cloneStyleFrom(dataNumQtyStyle);
        totalNumQtyStyle.setFont(totalFont);
        Row row0 = sheet.createRow(0);
        Cell c0 = row0.createCell(0);
        c0.setCellValue(title);
        c0.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
        Row row1 = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++)
        {
            setCellStr(row1, i, headers[i], headStyle);
        }
        int r = 2;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        for (Map<String, Object> map : mapList)
        {
            if (map == null)
            {
                continue;
            }
            Integer billType = parseCtkExportBillType(map.get("billType"));
            BigDecimal qty = toBd(map.get("materialQty"));
            BigDecimal amt = toBd(map.get("materialAmt"));
            BigDecimal up = toBd(map.get("unitPrice"));
            if (billType != null && billType == 401)
            {
                if (qty != null)
                {
                    qty = qty.abs().negate();
                }
                if (amt != null)
                {
                    amt = amt.abs().negate();
                }
                if (up != null)
                {
                    up = up.abs();
                }
            }
            if (up == null && amt != null && qty != null && qty.compareTo(BigDecimal.ZERO) != 0)
            {
                up = amt.abs().divide(qty.abs(), 4, RoundingMode.HALF_UP);
            }
            if (qty != null)
            {
                totalQty = totalQty.add(qty);
            }
            if (amt != null)
            {
                totalAmt = totalAmt.add(amt);
            }
            String bizDate = "";
            Object ad = map.get("auditDate");
            if (ad != null)
            {
                try
                {
                    if (ad instanceof Date)
                    {
                        bizDate = dayFmt.format((Date) ad);
                    }
                    else
                    {
                        String s = ad.toString();
                        bizDate = s.length() > 10 ? s.substring(0, 10) : s;
                    }
                }
                catch (Exception ignored)
                {
                    bizDate = "";
                }
            }
            String batchDisp = "/";
            Object bn = map.get("batchNumber");
            Object bno = map.get("batchNo");
            if (bn != null && StringUtils.isNotEmpty(String.valueOf(bn).trim()))
            {
                batchDisp = String.valueOf(bn).trim();
            }
            else if (bno != null && StringUtils.isNotEmpty(String.valueOf(bno).trim()))
            {
                batchDisp = String.valueOf(bno).trim();
            }
            Row dr = sheet.createRow(r++);
            setCellStr(dr, 0, StringUtils.nvl(map.get("materialCode"), "").toString(), dataStyle);
            setCellStr(dr, 1, StringUtils.nvl(map.get("departmentName"), "").toString(), dataStyle);
            setCellStr(dr, 2, bizDate, dataStyle);
            setCellStr(dr, 3, StringUtils.nvl(map.get("materialName"), "").toString(), dataStyle);
            setCellStr(dr, 4, StringUtils.nvl(map.get("materialSpeci"), "").toString(), dataStyle);
            setCellStr(dr, 5, StringUtils.nvl(map.get("factoryName"), "").toString(), dataStyle);
            setCellStr(dr, 6, StringUtils.nvl(map.get("unitName"), "").toString(), dataStyle);
            setCellDecimal(dr, 7, up, dataNumPriceAmtStyle);
            setCellDecimal(dr, 8, qty, dataNumQtyStyle);
            setCellDecimal(dr, 9, amt, dataNumPriceAmtStyle);
            setCellStr(dr, 10, batchDisp, dataStyle);
            setCellStr(dr, 11, StringUtils.nvl(map.get("financeCategoryName"), "").toString(), dataStyle);
            setCellStr(dr, 12, StringUtils.nvl(map.get("supplierName"), "").toString(), dataStyle);
            setCellStr(dr, 13, StringUtils.nvl(map.get("warehouseName"), "").toString(), dataStyle);
        }
        Row totalRow = sheet.createRow(r);
        setCellStr(totalRow, 0, "合计", totalLabelStyle);
        for (int i = 1; i < 8; i++)
        {
            setCellStr(totalRow, i, "", totalLabelStyle);
        }
        setCellDecimal(totalRow, 8, totalQty, totalNumQtyStyle);
        setCellDecimal(totalRow, 9, totalAmt, totalNumPriceAmtStyle);
        for (int i = 10; i < headers.length; i++)
        {
            setCellStr(totalRow, i, "", totalLabelStyle);
        }
        for (int i = 0; i < headers.length; i++)
        {
            sheet.setColumnWidth(i, 14 * 256);
        }
        sheet.setColumnWidth(3, 28 * 256);
        sheet.setColumnWidth(11, 18 * 256);
        sheet.setColumnWidth(12, 22 * 256);
        String fn = "出退库明细_统计时间" + beginStr + "至" + endStr + "_" + fnFmt.format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(fn, "UTF-8").replace("+", "%20"));
        wb.write(response.getOutputStream());
        wb.close();
    }

    private static Integer parseCtkExportBillType(Object billTypeObj)
    {
        if (billTypeObj == null)
        {
            return null;
        }
        if (billTypeObj instanceof Integer)
        {
            return (Integer) billTypeObj;
        }
        if (billTypeObj instanceof Number)
        {
            return ((Number) billTypeObj).intValue();
        }
        try
        {
            return Integer.parseInt(billTypeObj.toString());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static BigDecimal toBd(Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof BigDecimal)
        {
            return (BigDecimal) o;
        }
        if (o instanceof Number)
        {
            return BigDecimal.valueOf(((Number) o).doubleValue());
        }
        try
        {
            return new BigDecimal(o.toString());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /** 为源单明细填充已被引用量、可引用量（不落库）；入库单 101 拆分出库/退货两通道 */
    private void fillSrcEntryRefConsumption(StkIoBill bill) {
        if (bill == null || bill.getId() == null) {
            return;
        }
        String tid = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid)) {
            return;
        }
        List<StkIoBillEntry> list = bill.getStkIoBillEntryList();
        if (list == null) {
            return;
        }
        Integer bt = bill.getBillType();
        if (bt != null && bt.intValue() == 101) {
            Map<String, InboundEntryRefChannelQtyVo> ckMap =
                hcDocBillRefService.sumInboundOutboundChannelBySrcBillId(tid, String.valueOf(bill.getId()));
            Map<String, InboundEntryRefChannelQtyVo> thMap =
                hcDocBillRefService.sumInboundReturnChannelBySrcBillId(tid, String.valueOf(bill.getId()));
            Map<String, BigDecimal> legacy = hcDocBillRefService.sumRefQtyBySrcBillId(tid, String.valueOf(bill.getId()));
            for (StkIoBillEntry e : list) {
                if (e == null || e.getId() == null) {
                    continue;
                }
                if (e.getDelFlag() != null && e.getDelFlag() == 1) {
                    continue;
                }
                String ek = String.valueOf(e.getId());
                BigDecimal lineQty = e.getQty() != null ? e.getQty() : BigDecimal.ZERO;
                InboundEntryRefChannelQtyVo ck = ckMap.get(ek);
                BigDecimal ckA = ck != null && ck.getAuditedQty() != null ? ck.getAuditedQty() : BigDecimal.ZERO;
                BigDecimal ckP = ck != null && ck.getPendingQty() != null ? ck.getPendingQty() : BigDecimal.ZERO;
                InboundEntryRefChannelQtyVo th = thMap.get(ek);
                BigDecimal thA = th != null && th.getAuditedQty() != null ? th.getAuditedQty() : BigDecimal.ZERO;
                BigDecimal thP = th != null && th.getPendingQty() != null ? th.getPendingQty() : BigDecimal.ZERO;
                e.setSrcOutboundAuditedRefQty(ckA);
                e.setSrcOutboundPendingRefQty(ckP);
                e.setSrcOutboundRefableQty(maxZeroBd(lineQty.subtract(ckA).subtract(ckP)));
                e.setSrcReturnAuditedRefQty(thA);
                e.setSrcReturnPendingRefQty(thP);
                e.setSrcReturnRefableQty(maxZeroBd(lineQty.subtract(thA).subtract(thP)));
                Long invId = e.getStkInventoryId() != null ? e.getStkInventoryId() : e.getKcNo();
                if (invId != null) {
                    StkInventory inv = stkInventoryMapper.selectStkInventoryById(invId);
                    e.setLinkedStkQty(inv != null ? inv.getQty() : null);
                } else {
                    e.setLinkedStkQty(null);
                }
                BigDecimal u = legacy.getOrDefault(ek, BigDecimal.ZERO);
                e.setSrcRefedQty(u);
                e.setSrcRefableQty(maxZeroBd(lineQty.subtract(u)));
            }
            return;
        }
        Map<String, BigDecimal> used = hcDocBillRefService.sumRefQtyBySrcBillId(tid, String.valueOf(bill.getId()));
        for (StkIoBillEntry e : list) {
            if (e == null || e.getId() == null) {
                continue;
            }
            if (e.getDelFlag() != null && e.getDelFlag() == 1) {
                continue;
            }
            BigDecimal u = used.getOrDefault(String.valueOf(e.getId()), BigDecimal.ZERO);
            e.setSrcRefedQty(u);
            BigDecimal q = e.getQty() != null ? e.getQty() : BigDecimal.ZERO;
            e.setSrcRefableQty(maxZeroBd(q.subtract(u)));
        }
    }

    /** 科室退库单 401 作为源：按 TK_TO_TH 统计退货通道占用（不落库） */
    private void fillTkSourceEntryReturnChannelConsumption(StkIoBill tkBill) {
        if (tkBill == null || tkBill.getId() == null) {
            return;
        }
        String tid = StringUtils.isNotEmpty(tkBill.getTenantId()) ? tkBill.getTenantId() : SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid)) {
            return;
        }
        List<StkIoBillEntry> list = tkBill.getStkIoBillEntryList();
        if (list == null) {
            return;
        }
        Map<String, InboundEntryRefChannelQtyVo> thMap =
            hcDocBillRefService.sumTkReturnChannelBySrcBillId(tid, String.valueOf(tkBill.getId()));
        for (StkIoBillEntry e : list) {
            if (e == null || e.getId() == null) {
                continue;
            }
            if (e.getDelFlag() != null && e.getDelFlag() == 1) {
                continue;
            }
            String ek = String.valueOf(e.getId());
            BigDecimal lineQty = e.getQty() != null ? e.getQty() : BigDecimal.ZERO;
            InboundEntryRefChannelQtyVo th = thMap.get(ek);
            BigDecimal thA = th != null && th.getAuditedQty() != null ? th.getAuditedQty() : BigDecimal.ZERO;
            BigDecimal thP = th != null && th.getPendingQty() != null ? th.getPendingQty() : BigDecimal.ZERO;
            e.setSrcReturnAuditedRefQty(thA);
            e.setSrcReturnPendingRefQty(thP);
            e.setSrcReturnRefableQty(maxZeroBd(lineQty.subtract(thA).subtract(thP)));
            Long invId = e.getStkInventoryId();
            if (invId != null) {
                StkInventory inv = stkInventoryMapper.selectStkInventoryById(invId);
                e.setLinkedStkQty(inv != null ? inv.getQty() : null);
            } else {
                e.setLinkedStkQty(null);
            }
        }
    }

    private static BigDecimal maxZeroBd(BigDecimal b) {
        if (b == null || b.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return b;
    }

    private static void copyInboundRefDisplayFromRkLine(StkIoBillEntry rk, StkIoBillEntry target) {
        if (rk == null || target == null) {
            return;
        }
        target.setSrcOutboundAuditedRefQty(rk.getSrcOutboundAuditedRefQty());
        target.setSrcOutboundPendingRefQty(rk.getSrcOutboundPendingRefQty());
        target.setSrcOutboundRefableQty(rk.getSrcOutboundRefableQty());
        target.setSrcReturnAuditedRefQty(rk.getSrcReturnAuditedRefQty());
        target.setSrcReturnPendingRefQty(rk.getSrcReturnPendingRefQty());
        target.setSrcReturnRefableQty(rk.getSrcReturnRefableQty());
        target.setLinkedStkQty(rk.getLinkedStkQty());
    }

    private static void copyTkReturnRefDisplayFromTkLine(StkIoBillEntry tk, StkIoBillEntry target) {
        if (tk == null || target == null) {
            return;
        }
        target.setSrcReturnAuditedRefQty(tk.getSrcReturnAuditedRefQty());
        target.setSrcReturnPendingRefQty(tk.getSrcReturnPendingRefQty());
        target.setSrcReturnRefableQty(tk.getSrcReturnRefableQty());
        target.setLinkedStkQty(tk.getLinkedStkQty());
    }

    /**
     * 出库/退库保存前：若各明细解析出的供应商 id 一致，则写回主表 supplerId。
     */
    private void syncBillHeaderSupplerFromUniformEntries(StkIoBill bill) {
        if (bill == null) {
            return;
        }
        Integer bt = bill.getBillType();
        if (bt == null || (bt.intValue() != 201 && bt.intValue() != 401)) {
            return;
        }
        List<StkIoBillEntry> list = bill.getStkIoBillEntryList();
        if (list == null || list.isEmpty()) {
            return;
        }
        Long uniform = null;
        for (StkIoBillEntry e : list) {
            if (e == null || (e.getDelFlag() != null && e.getDelFlag() == 1)) {
                continue;
            }
            Long sid = parseSupplerIdString(e.getSupplerId());
            if (sid == null) {
                continue;
            }
            if (uniform == null) {
                uniform = sid;
            } else if (!uniform.equals(sid)) {
                return;
            }
        }
        if (uniform != null) {
            bill.setSupplerId(uniform);
        }
    }

    private HcDocBillRef newSrcDocRef(String refType, String srcBillKind, StkIoBill srcBill, StkIoBillEntry srcEntry,
        int srcLineOneBased) {
        HcDocBillRef r = new HcDocBillRef();
        r.setBizDomain("STK_IO_BILL");
        r.setRefType(refType);
        r.setSrcBillKind(srcBillKind);
        r.setSrcBillId(String.valueOf(srcBill.getId()));
        r.setSrcBillNo(srcBill.getBillNo());
        r.setSrcEntryId(String.valueOf(srcEntry.getId()));
        r.setSrcEntryLineNo(Integer.valueOf(srcLineOneBased));
        if (srcBill.getWarehouseId() != null) {
            r.setLockWarehouseId(String.valueOf(srcBill.getWarehouseId()));
        }
        if (srcBill.getSupplerId() != null) {
            r.setLockSupplierId(String.valueOf(srcBill.getSupplerId()));
        }
        if (srcBill.getDepartmentId() != null) {
            r.setLockDepartmentId(String.valueOf(srcBill.getDepartmentId()));
        }
        return r;
    }

    /**
     * 引用库房申请 / hc_doc_bill_ref 源单时，校验本单明细数量不超过可引用数量。
     *
     * @param excludeTgtBillId 修改或审核已存在单据时传入，用于从「已占用」中排除本单旧关联
     */
    private void assertReferencedQtyWithinLimits(StkIoBill bill, Long excludeTgtBillId)
    {
        if (bill == null)
        {
            return;
        }
        List<StkIoBillEntry> entries = bill.getStkIoBillEntryList();
        if (entries == null || entries.isEmpty())
        {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tenantId))
        {
            return;
        }
        String excludeTgtStr = excludeTgtBillId != null ? String.valueOf(excludeTgtBillId) : null;

        List<Map<String, Object>> errors = new ArrayList<>();

        if (StringUtils.isNotEmpty(bill.getWhWarehouseApplyId()))
        {
            Integer bt = bill.getBillType();
            if (bt != null && bt.intValue() == 201)
            {
                for (int i = 0; i < entries.size(); i++)
                {
                    StkIoBillEntry en = entries.get(i);
                    if (en == null || StringUtils.isEmpty(en.getWhApplyEntryId()))
                    {
                        continue;
                    }
                    WhWarehouseApplyEntry whEn = whWarehouseApplyMapper.selectWhWarehouseApplyEntryById(en.getWhApplyEntryId());
                    if (whEn == null)
                    {
                        continue;
                    }
                    BigDecimal lineQty = nz(whEn.getQty());
                    BigDecimal voidQty = nz(whEn.getLineVoidQty());
                    BigDecimal effective = lineQty.subtract(voidQty);
                    BigDecimal linkedOthers = nz(whWarehouseApplyMapper.sumLinkedQtyByWhApplyEntryIdExcludingCkBill(
                        en.getWhApplyEntryId(), excludeTgtStr));
                    BigDecimal maxAllowed = effective.subtract(linkedOthers);
                    if (maxAllowed.compareTo(BigDecimal.ZERO) < 0)
                    {
                        maxAllowed = BigDecimal.ZERO;
                    }
                    BigDecimal q = nz(en.getQty());
                    if (q.compareTo(maxAllowed) > 0)
                    {
                        errors.add(docRefErrRow(i + 1, en,
                            String.format("引用库房申请单：本行最多可出库 %s，当前 %s", fmtQty(maxAllowed), fmtQty(q)),
                            maxAllowed, q));
                    }
                }
            }
        }

        List<HcDocBillRef> refs = bill.getDocRefList();
        if ((refs == null || refs.isEmpty()) && bill.getId() != null)
        {
            refs = hcDocBillRefMapper.selectByTgtBillId(String.valueOf(bill.getId()));
        }
        if (refs != null && !refs.isEmpty())
        {
            Map<String, StkIoBillEntry> byTgtId = new HashMap<>();
            for (StkIoBillEntry en : entries)
            {
                if (en != null && en.getId() != null)
                {
                    byTgtId.put(String.valueOf(en.getId()), en);
                }
            }
            for (int i = 0; i < refs.size(); i++)
            {
                HcDocBillRef r = refs.get(i);
                if (r == null || StringUtils.isEmpty(r.getRefType()))
                {
                    continue;
                }
                if (StringUtils.isEmpty(r.getSrcBillId()) || StringUtils.isEmpty(r.getSrcEntryId()))
                {
                    continue;
                }
                StkIoBillEntry en = null;
                if (StringUtils.isNotEmpty(r.getTgtEntryId()))
                {
                    en = byTgtId.get(r.getTgtEntryId());
                }
                if (en == null && i < entries.size())
                {
                    en = entries.get(i);
                }
                if (en == null)
                {
                    continue;
                }
                int rowNo = i + 1;
                if (en.getId() != null)
                {
                    for (int k = 0; k < entries.size(); k++)
                    {
                        if (entries.get(k) != null && en.getId().equals(entries.get(k).getId()))
                        {
                            rowNo = k + 1;
                            break;
                        }
                    }
                }
                Long srcBillId = parseLongSafe(r.getSrcBillId());
                Long srcEntryId = parseLongSafe(r.getSrcEntryId());
                if (srcBillId == null || srcEntryId == null)
                {
                    continue;
                }
                StkIoBill srcBill = stkIoBillMapper.selectStkIoBillById(srcBillId);
                if (srcBill == null)
                {
                    errors.add(docRefErrRow(rowNo, en, "引用关联源单不存在或已删除", null, nz(en.getQty())));
                    continue;
                }
                SecurityUtils.ensureTenantAccess(srcBill.getTenantId());
                StkIoBillEntry srcEntry = findStkEntryById(srcBill.getStkIoBillEntryList(), srcEntryId);
                if (srcEntry == null)
                {
                    errors.add(docRefErrRow(rowNo, en, "引用关联源单明细不存在", null, nz(en.getQty())));
                    continue;
                }
                BigDecimal srcQty = nz(srcEntry.getQty());
                BigDecimal usedOthers = nz(hcDocBillRefMapper.sumRefQtyBySrcEntryExcludingTgtBill(tenantId,
                    String.valueOf(srcBillId), String.valueOf(srcEntryId), excludeTgtStr, r.getRefType()));
                BigDecimal maxAllowed = srcQty.subtract(usedOthers);
                if (maxAllowed.compareTo(BigDecimal.ZERO) < 0)
                {
                    maxAllowed = BigDecimal.ZERO;
                }
                BigDecimal q = nz(en.getQty());
                if (q.compareTo(maxAllowed) > 0)
                {
                    errors.add(docRefErrRow(rowNo, en,
                        String.format("引用源单明细可再下推数量最多为 %s（源单数量 %s，其它单据已占用 %s），当前 %s",
                            fmtQty(maxAllowed), fmtQty(srcQty), fmtQty(usedOthers), fmtQty(q)),
                        maxAllowed, q));
                }
            }
        }

        if (!errors.isEmpty())
        {
            throw new DocRefQtyValidationException("引用单据数量校验未通过，请查看明细", errors);
        }
    }

    private static StkIoBillEntry findStkEntryById(List<StkIoBillEntry> list, Long id)
    {
        if (list == null || id == null)
        {
            return null;
        }
        for (StkIoBillEntry e : list)
        {
            if (e != null && id.equals(e.getId()))
            {
                return e;
            }
        }
        return null;
    }

    private static Long parseLongSafe(String s)
    {
        if (StringUtils.isEmpty(s))
        {
            return null;
        }
        try
        {
            return Long.valueOf(s.trim());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static BigDecimal nz(BigDecimal b)
    {
        return b != null ? b : BigDecimal.ZERO;
    }

    private static String fmtQty(BigDecimal b)
    {
        if (b == null)
        {
            return "0";
        }
        return b.stripTrailingZeros().toPlainString();
    }

    private Map<String, Object> docRefErrRow(int lineNo, StkIoBillEntry en, String reason, BigDecimal maxRefQty,
        BigDecimal currentQty)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("lineNo", lineNo);
        m.put("materialName", resolveEntryMaterialName(en));
        m.put("reason", reason);
        if (maxRefQty != null)
        {
            m.put("maxRefQty", fmtQty(maxRefQty));
        }
        m.put("currentQty", fmtQty(currentQty));
        return m;
    }

    private String resolveEntryMaterialName(StkIoBillEntry en)
    {
        if (en == null)
        {
            return "";
        }
        if (en.getMaterial() != null && StringUtils.isNotEmpty(en.getMaterial().getName()))
        {
            return en.getMaterial().getName();
        }
        if (en.getMaterialId() != null)
        {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(en.getMaterialId());
            return m != null && m.getName() != null ? m.getName() : "";
        }
        return "";
    }
}
