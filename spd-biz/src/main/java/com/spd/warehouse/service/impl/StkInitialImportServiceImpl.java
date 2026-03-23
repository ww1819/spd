package com.spd.warehouse.service.impl;

import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.warehouse.domain.*;
import com.spd.warehouse.domain.dto.InitialImportExcelRow;
import com.spd.warehouse.mapper.*;
import com.spd.warehouse.service.IStkInitialImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import com.spd.warehouse.util.InitialImportDateParser;

/**
 * 期初库存导入 Service 实现
 *
 * @author spd
 */
@Service
public class StkInitialImportServiceImpl implements IStkInitialImportService {

    @Autowired
    private StkInitialImportMapper stkInitialImportMapper;
    @Autowired
    private StkInitialImportEntryMapper stkInitialImportEntryMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;
    @Autowired
    private FdFactoryMapper fdFactoryMapper;
    @Autowired
    private FdSupplierMapper fdSupplierMapper;
    @Autowired
    private StkBatchMapper stkBatchMapper;
    @Autowired
    private StkInventoryMapper stkInventoryMapper;
    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;
    @Autowired
    private FdUnitMapper fdUnitMapper;
    @Autowired
    private FdWarehouseCategoryMapper fdWarehouseCategoryMapper;
    @Autowired
    private FdFinanceCategoryMapper fdFinanceCategoryMapper;

    private static final String BATCH_SOURCE_QC = "QC";

    @Override
    public AjaxResult preview(MultipartFile file, Long warehouseId) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择要导入的文件");
        }
        ExcelUtil<InitialImportExcelRow> util = new ExcelUtil<>(InitialImportExcelRow.class);
        List<InitialImportExcelRow> rows;
        try {
            rows = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("解析文件失败：" + e.getMessage());
        }
        if (rows == null || rows.isEmpty()) {
            return AjaxResult.error("文件中没有有效数据");
        }
        Long whId = warehouseId;
        if (whId == null && rows.size() > 0 && StringUtils.isNotEmpty(rows.get(0).getWarehouseCode())) {
            FdWarehouse q = new FdWarehouse();
            q.setCode(rows.get(0).getWarehouseCode());
            List<FdWarehouse> list = fdWarehouseMapper.selectFdWarehouseList(q);
            if (list != null && !list.isEmpty()) {
                whId = list.get(0).getId();
            }
        }
        String tenantPreview = resolveTenantIdForPreview(whId, rows);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            InitialImportExcelRow row = rows.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rowIndex", i + 1);
            item.put("data", row);
            // trimToNull：纯空格视为未填，此时走耗材编码匹配
            boolean hasThird = StringUtils.isNotEmpty(StringUtils.trimToNull(row.getThirdPartyMaterialId()));
            boolean hasCode = StringUtils.isNotEmpty(StringUtils.trimToNull(row.getMaterialCode()));
            String err = null;
            if (!hasThird && !hasCode) {
                err = "HIS系统产品档案id为空时请填写耗材编码；或至少填写其一";
            } else if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                err = "数量必须大于0";
            } else if (whId == null && (StringUtils.isEmpty(row.getWarehouseCode()))) {
                err = "请指定仓库或提供仓库编码";
            } else if (StringUtils.isNotEmpty(row.getHisFactoryId())) {
                if (StringUtils.isEmpty(tenantPreview)) {
                    err = "填写了HIS系统生产厂家id时请先指定仓库或保证仓库编码可解析，以便按租户校验";
                } else if (!factoryExistsByHisId(tenantPreview, row.getHisFactoryId().trim())) {
                    err = "HIS系统生产厂家id「" + row.getHisFactoryId().trim() + "」在系统中不存在";
                }
            }
            if (err == null && StringUtils.isNotEmpty(row.getHisSupplierId())) {
                if (StringUtils.isEmpty(tenantPreview)) {
                    err = "填写了HIS系统供应商id时请先指定仓库或保证仓库编码可解析，以便按租户校验";
                } else if (!supplierExistsByHisId(tenantPreview, row.getHisSupplierId().trim())) {
                    err = "HIS系统供应商id「" + row.getHisSupplierId().trim() + "」在系统中不存在";
                }
            }
            if (err == null && StringUtils.isEmpty(row.getHisSupplierId())
                && StringUtils.isNotEmpty(row.getSupplierName()) && !supplierExistsByName(row.getSupplierName().trim())) {
                err = "供应商「" + row.getSupplierName().trim() + "」在系统中不存在，请先在基础数据中创建该供应商";
            }
            if (err == null)
            {
                String e1 = InitialImportDateParser.validateOrError(row.getBeginDateRaw(), "生产日期");
                if (e1 != null)
                {
                    err = e1;
                }
            }
            if (err == null)
            {
                String e2 = InitialImportDateParser.validateOrError(row.getEndDateRaw(), "效期");
                if (e2 != null)
                {
                    err = e2;
                }
            }
            item.put("error", err);
            result.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", result);
        data.put("warehouseId", whId);
        return AjaxResult.success(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirmImport(Long warehouseId, List<InitialImportExcelRow> rows) {
        if (warehouseId == null) {
            throw new ServiceException("请选择所属仓库");
        }
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException("导入数据不能为空");
        }
        // 不按 Mapper 内 tenant 条件查，避免 Token/上下文租户与 fd_warehouse.tenant_id 瞬时一致性问题导致误报「仓库不存在」
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseByIdIgnoreTenant(String.valueOf(warehouseId));
        if (wh == null) {
            throw new ServiceException("仓库不存在");
        }
        assertInitialImportWarehouseTenant(wh);
        String username = SecurityUtils.getUserIdStr();
        Date now = new Date();

        StkInitialImport main = new StkInitialImport();
        main.setId(UUID7.generateUUID7());
        main.setBillNo(getQcBillNo());
        main.setWarehouseId(warehouseId);
        main.setImportOperator(username);
        main.setImportTime(now);
        main.setStockGenTime(null);
        main.setBillStatus(0);
        main.setCreateBy(username);
        main.setCreateTime(now);
        if (StringUtils.isEmpty(main.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            main.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(main.getTenantId()) && wh.getTenantId() != null) {
            main.setTenantId(wh.getTenantId());
        }
        stkInitialImportMapper.insert(main);

        String tenantId = resolveTenantIdForInitial(wh);
        List<StkInitialImportEntry> entries = new ArrayList<>();
        int sortOrder = 0;
        for (int idx = 0; idx < rows.size(); idx++) {
            InitialImportExcelRow row = rows.get(idx);
            int rowNum = idx + 1;
            if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            // HIS 非空（trim 后）则优先按 his_id 匹配；为空则按耗材编码匹配产品档案（租户内编码优先）
            boolean hasThirdPartyMaterialId = StringUtils.isNotEmpty(StringUtils.trimToNull(row.getThirdPartyMaterialId()));
            boolean hasMaterialCode = StringUtils.isNotEmpty(StringUtils.trimToNull(row.getMaterialCode()));
            if (!hasThirdPartyMaterialId && !hasMaterialCode) {
                throw new ServiceException("第" + rowNum + "行：HIS系统产品档案id为空时请填写耗材编码；或至少填写其一");
            }
            FdMaterial material = null;
            if (hasThirdPartyMaterialId) {
                String hisMid = StringUtils.trim(row.getThirdPartyMaterialId());
                if (StringUtils.isNotEmpty(tenantId)) {
                    material = fdMaterialMapper.selectFdMaterialByTenantAndHisId(tenantId, hisMid);
                }
                if (material == null) {
                    material = fdMaterialMapper.selectFdMaterialByHisId(hisMid);
                }
                if (material == null) {
                    material = createNewMaterialFromRow(row, true, tenantId);
                    fdMaterialMapper.insertFdMaterial(material);
                }
            } else {
                // HIS 为空：按耗材编码匹配 SPD 产品档案 id（material_id）
                String code = StringUtils.trim(row.getMaterialCode());
                if (StringUtils.isNotEmpty(tenantId)) {
                    material = fdMaterialMapper.selectFdMaterialByTenantAndCode(tenantId, code);
                }
                if (material == null) {
                    material = fdMaterialMapper.selectFdMaterialByCode(code);
                }
                if (material == null) {
                    material = createNewMaterialFromRow(row, false, tenantId);
                    fdMaterialMapper.insertFdMaterial(material);
                }
            }
            Long factoryId = null;
            if (StringUtils.isNotEmpty(row.getHisFactoryId())) {
                String hid = row.getHisFactoryId().trim();
                if (StringUtils.isEmpty(tenantId)) {
                    throw new ServiceException("第" + rowNum + "行：无法解析租户，无法按HIS系统生产厂家id匹配");
                }
                FdFactory ff = fdFactoryMapper.selectFdFactoryByTenantAndHisId(tenantId, hid);
                if (ff == null) {
                    throw new ServiceException("第" + rowNum + "行：HIS系统生产厂家id「" + hid + "」在系统中不存在");
                }
                factoryId = ff.getFactoryId();
            } else if (StringUtils.isNotEmpty(row.getFactoryName())) {
                FdFactory fq = new FdFactory();
                fq.setFactoryName(row.getFactoryName());
                List<FdFactory> fl = fdFactoryMapper.selectFdFactoryList(fq);
                if (fl != null && !fl.isEmpty()) {
                    factoryId = fl.get(0).getFactoryId();
                }
            }
            Long supplierId = null;
            if (StringUtils.isNotEmpty(row.getHisSupplierId())) {
                String hid = row.getHisSupplierId().trim();
                if (StringUtils.isEmpty(tenantId)) {
                    throw new ServiceException("第" + rowNum + "行：无法解析租户，无法按HIS系统供应商id匹配");
                }
                FdSupplier sup = fdSupplierMapper.selectFdSupplierByTenantAndHisId(tenantId, hid);
                if (sup == null) {
                    throw new ServiceException("第" + rowNum + "行：HIS系统供应商id「" + hid + "」在系统中不存在");
                }
                supplierId = sup.getId();
            } else if (StringUtils.isNotEmpty(row.getSupplierName())) {
                String supplierName = row.getSupplierName().trim();
                FdSupplier sq = new FdSupplier();
                sq.setName(supplierName);
                List<FdSupplier> sl = fdSupplierMapper.selectFdSupplierList(sq);
                if (sl != null) {
                    for (FdSupplier s : sl) {
                        if (supplierName.equals(s.getName())) {
                            supplierId = s.getId();
                            break;
                        }
                    }
                }
                if (supplierId == null) {
                    throw new ServiceException("第" + rowNum + "行：供应商「" + supplierName + "」在系统中不存在，请先在基础数据中创建该供应商后再导入");
                }
            }
            // 通过 HIS 匹配到的 SPD 生产厂家/供应商 ID：回写到产品档案（仅当档案中尚未维护时），便于后续业务使用
            syncMaterialFactorySupplierFromHisMatch(material, factoryId, supplierId, tenantId);

            BigDecimal unitPrice = scaleInitialMoney(row.getUnitPrice() != null ? row.getUnitPrice() : BigDecimal.ZERO);
            BigDecimal qty = scaleInitialMoney(row.getQty());
            BigDecimal amt = scaleInitialMoney(unitPrice.multiply(qty));

            Date beginDt = InitialImportDateParser.parseToSqlDate(row.getBeginDateRaw());
            Date endDt = InitialImportDateParser.parseToSqlDate(row.getEndDateRaw());
            if (StringUtils.isNotEmpty(StringUtils.trim(row.getBeginDateRaw())) && beginDt == null)
            {
                throw new ServiceException("第" + rowNum + "行：生产日期格式无效，请使用 YYYYMMDD 或 yyyy-MM-dd");
            }
            if (StringUtils.isNotEmpty(StringUtils.trim(row.getEndDateRaw())) && endDt == null)
            {
                throw new ServiceException("第" + rowNum + "行：效期格式无效，请使用 YYYYMMDD 或 yyyy-MM-dd");
            }

            StkInitialImportEntry entry = new StkInitialImportEntry();
            entry.setId(UUID7.generateUUID7());
            entry.setParenId(main.getId());
            entry.setMaterialId(material.getId());
            entry.setWarehouseId(warehouseId);
            entry.setUnitPrice(unitPrice);
            entry.setQty(qty);
            entry.setAmt(amt);
            entry.setBatchNo(FillRuleUtil.createBatchNo());
            entry.setBatchNumber(row.getBatchNumber());
            entry.setBeginTime(beginDt);
            entry.setEndTime(endDt);
            entry.setFactoryId(factoryId);
            entry.setSupplierId(supplierId);
            entry.setSortOrder(++sortOrder);
            entry.setHisId(StringUtils.isNotEmpty(row.getHisId()) ? row.getHisId().trim() : null);
            entry.setThirdPartyMaterialId(StringUtils.isNotEmpty(row.getThirdPartyMaterialId()) ? row.getThirdPartyMaterialId().trim() : null);
            entry.setMaterialCode(trimToNull(row.getMaterialCode()));
            entry.setSpeci(trimToNull(row.getSpeci()));
            entry.setModel(trimToNull(row.getModel()));
            entry.setRegisterNo(trimToNull(row.getRegisterNo()));
            entry.setMedicalNo(trimToNull(row.getMedicalNo()));
            entry.setMedicalName(trimToNull(row.getMedicalName()));
            entry.setMainBarcode(trimToNull(row.getMainBarcode()));
            entries.add(entry);
        }
        if (entries.isEmpty()) {
            throw new ServiceException("没有可导入的有效明细");
        }
        stkInitialImportEntryMapper.insertBatch(entries);
        return AjaxResult.success("期初单已生成，请到列表中审核。单号：" + main.getBillNo());
    }

    @Override
    public List<StkInitialImport> list(StkInitialImport query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId())) {
            // 与 Mapper 中 scopedTenantIdForSql 一致：用户 customerId → TenantContext → 请求头 X-Tenant-Id
            String tid = SecurityUtils.resolveEffectiveTenantId(null);
            if (StringUtils.isNotEmpty(tid)) {
                query.setTenantId(tid);
            }
        }
        return stkInitialImportMapper.selectList(query);
    }

    @Override
    public StkInitialImport getDetail(String id) {
        StkInitialImport main = stkInitialImportMapper.selectById(id);
        if (main != null) {
            main.setEntryList(stkInitialImportEntryMapper.selectByParenId(main.getId()));
        }
        return main;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int audit(String id) {
        StkInitialImport main = stkInitialImportMapper.selectById(id);
        if (main == null) {
            throw new ServiceException("期初单不存在");
        }
        if (main.getBillStatus() != null && main.getBillStatus() == 1) {
            throw new ServiceException("该单据已审核，不能重复审核");
        }
        List<StkInitialImportEntry> entries = stkInitialImportEntryMapper.selectByParenId(id);
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("期初单没有明细，无法审核");
        }
        String username = SecurityUtils.getUserIdStr();
        Date auditTime = new Date();

        for (StkInitialImportEntry entry : entries) {
            if (entry.getQty() == null || entry.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            String batchNo = entry.getBatchNo();
            if (StringUtils.isEmpty(batchNo)) {
                batchNo = FillRuleUtil.createBatchNo();
                entry.setBatchNo(batchNo);
            }
            StkBatch stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
            if (stkBatch == null) {
                stkBatch = buildStkBatchForInitial(main, entry);
                stkBatchMapper.insertStkBatch(stkBatch);
            }

            StkInventory inv = new StkInventory();
            inv.setBatchNo(batchNo);
            inv.setBatchId(stkBatch.getId());
            inv.setMaterialNo(entry.getBatchNumber());
            inv.setMaterialId(entry.getMaterialId());
            inv.setWarehouseId(main.getWarehouseId());
            inv.setQty(entry.getQty());
            inv.setUnitPrice(entry.getUnitPrice());
            inv.setAmt(entry.getAmt());
            inv.setMaterialDate(auditTime);
            inv.setWarehouseDate(auditTime);
            inv.setSupplierId(entry.getSupplierId());
            // 生产厂家：与批次一致；批次无则回退明细上的 factory_id
            Long factoryIdForStock = stkBatch.getFactoryId() != null ? stkBatch.getFactoryId() : entry.getFactoryId();
            inv.setFactoryId(factoryIdForStock);
            // 生产日期、效期（有效期）
            inv.setBeginTime(entry.getBeginTime());
            inv.setEndTime(entry.getEndTime());
            inv.setReceiptOrderNo(main.getBillNo());
            inv.setCreateTime(auditTime);
            inv.setCreateBy(username);
            inv.setBatchNumber(entry.getBatchNumber());
            inv.setDelFlag(0);
            // 租户：与期初主单/仓库一致，避免 stk_inventory.tenant_id 为空导致按租户查不到库存
            inv.setTenantId(resolveTenantIdForInitialStock(main));
            stkInventoryMapper.insertStkInventory(inv);

            HcCkFlow flow = new HcCkFlow();
            flow.setRefBillId(main.getId());
            flow.setRefEntryId(entry.getId());
            flow.setWarehouseId(main.getWarehouseId());
            flow.setMaterialId(entry.getMaterialId());
            flow.setBatchNo(batchNo);
            flow.setBatchNumber(entry.getBatchNumber());
            flow.setQty(entry.getQty());
            flow.setUnitPrice(entry.getUnitPrice());
            flow.setAmt(entry.getAmt());
            flow.setBeginTime(entry.getBeginTime());
            flow.setEndTime(entry.getEndTime());
            flow.setSupplierId(entry.getSupplierId());
            flow.setFactoryId(factoryIdForStock);
            flow.setLx("QC");
            flow.setBatchId(inv.getBatchId());
            flow.setOriginBusinessType("期初导入");
            flow.setKcNo(inv.getId());
            flow.setFlowTime(auditTime);
            flow.setDelFlag(0);
            flow.setCreateTime(auditTime);
            flow.setCreateBy(username);
            hcCkFlowMapper.insertHcCkFlow(flow);
        }

        main.setBillStatus(1);
        main.setStockGenTime(auditTime);
        main.setAuditBy(username);
        main.setAuditTime(auditTime);
        main.setUpdateBy(username);
        if (StringUtils.isEmpty(main.getTenantId())) {
            main.setTenantId(resolveTenantIdForInitialStock(main));
        }
        return stkInitialImportMapper.update(main);
    }

    /** 期初导入：单价/数量/金额统一保留 6 位小数，与明细表 decimal(18,6) 一致 */
    private static BigDecimal scaleInitialMoney(BigDecimal v)
    {
        if (v == null)
        {
            return null;
        }
        return v.setScale(6, RoundingMode.HALF_UP);
    }

    private String getQcBillNo() {
        String str = "QC";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkInitialImportMapper.selectMaxBillNo(str + date);
        return FillRuleUtil.getNumber(str, maxNum, date);
    }

    private StkBatch buildStkBatchForInitial(StkInitialImport main, StkInitialImportEntry entry) {
        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setBatchNumber(entry.getBatchNumber());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());
        b.setUnitPrice(entry.getUnitPrice());
        b.setRefBillId(main.getId());
        b.setBillNo(main.getBillNo());
        b.setRefEntryId(entry.getId());
        b.setBatchSource(BATCH_SOURCE_QC);
        b.setOriginBillType(null);
        b.setOriginFlowLx(BATCH_SOURCE_QC);
        b.setOriginBusinessType("期初导入");
        if (main.getWarehouseId() != null) {
            b.setOriginFromWarehouseId(main.getWarehouseId());
            b.setOriginToWarehouseId(main.getWarehouseId());
        }
        Date now = new Date();
        String username = SecurityUtils.getUserIdStr();
        b.setAuditTime(now);
        b.setAuditBy(username);
        b.setCreateTime(now);
        b.setCreateBy(username);
        b.setDelFlag(0);
        b.setWarehouseId(main.getWarehouseId());
        b.setSupplierId(entry.getSupplierId());
        b.setFactoryId(entry.getFactoryId());

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
                if (u != null) b.setUnitName(u.getUnitName());
            }
            if (m.getFactoryId() != null) {
                FdFactory f = fdFactoryMapper.selectFdFactoryByFactoryId(m.getFactoryId());
                if (f != null) {
                    b.setFactoryId(b.getFactoryId() != null ? b.getFactoryId() : f.getFactoryId());
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
        if (entry.getSupplierId() != null) {
            FdSupplier sup = fdSupplierMapper.selectFdSupplierById(entry.getSupplierId());
            if (sup != null) {
                b.setSupplierId(sup.getId());
                b.setSupplierCode(sup.getCode());
                b.setSupplierName(sup.getName());
            }
        }
        if (main.getWarehouseId() != null) {
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(main.getWarehouseId()));
            if (wh != null) {
                b.setWarehouseCode(wh.getCode());
                b.setWarehouseName(wh.getName());
            }
        }
        b.setTenantId(StringUtils.isNotEmpty(main.getTenantId()) ? main.getTenantId() : SecurityUtils.getCustomerId());
        return b;
    }

    /**
     * 根据导入行创建新产品档案：byHisId=true 时按 HIS/第三方档案ID（写入 his_id），否则按耗材编码（使用模板编码）。
     *
     * @param tenantId 租户ID，写入 fd_material.tenant_id
     */
    private FdMaterial createNewMaterialFromRow(InitialImportExcelRow row, boolean byHisId, String tenantId) {
        FdMaterial m = new FdMaterial();
        String name = StringUtils.isNotEmpty(row.getMaterialName()) ? row.getMaterialName().trim() : row.getMaterialCode();
        if (StringUtils.isEmpty(name)) {
            name = "期初导入-" + (row.getThirdPartyMaterialId() != null ? row.getThirdPartyMaterialId() : row.getMaterialCode());
        }
        m.setName(name);
        if (StringUtils.isNotEmpty(tenantId)) {
            m.setTenantId(tenantId);
        }
        if (byHisId) {
            m.setHisId(row.getThirdPartyMaterialId().trim());
            m.setCode(generateUniqueMaterialCode());
        } else {
            m.setCode(row.getMaterialCode().trim());
        }
        m.setSpeci(trimToNull(row.getSpeci()));
        m.setModel(trimToNull(row.getModel()));
        m.setRegisterNo(trimToNull(row.getRegisterNo()));
        m.setMedicalNo(trimToNull(row.getMedicalNo()));
        m.setMedicalName(trimToNull(row.getMedicalName()));
        m.setStoreroomId(resolveWarehouseCategoryId(row.getWarehouseCategory()));
        m.setFinanceCategoryId(resolveFinanceCategoryId(row.getFinanceCategory()));
        m.setDelFlag(0);
        m.setCreateBy(SecurityUtils.getUserIdStr());
        m.setCreateTime(new Date());
        return m;
    }

    /**
     * 当前用户带租户上下文时，期初导入所选仓库必须属于该租户（防止跨租户传 ID）。
     */
    private void assertInitialImportWarehouseTenant(FdWarehouse wh) {
        if (wh == null) {
            return;
        }
        String ctx = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isEmpty(ctx)) {
            return;
        }
        String wt = wh.getTenantId();
        if (StringUtils.isEmpty(wt)) {
            return;
        }
        if (!ctx.trim().equals(wt.trim())) {
            throw new ServiceException("无权操作该仓库（非本租户仓库）");
        }
    }

    /** 期初导入：优先仓库所属租户，其次当前登录/请求租户 */
    private String resolveTenantIdForInitial(FdWarehouse wh) {
        if (wh != null && StringUtils.isNotEmpty(wh.getTenantId())) {
            return wh.getTenantId();
        }
        return SecurityUtils.resolveEffectiveTenantId(null);
    }

    /**
     * 审核生成库存行时的租户：主表 tenant_id → 当前客户 → 仓库 tenant_id（与批次 buildStkBatchForInitial 逻辑一致）
     */
    private String resolveTenantIdForInitialStock(StkInitialImport main) {
        if (main == null) {
            return SecurityUtils.resolveEffectiveTenantId(null);
        }
        if (StringUtils.isNotEmpty(main.getTenantId())) {
            return main.getTenantId().trim();
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            return SecurityUtils.getCustomerId();
        }
        if (main.getWarehouseId() != null) {
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(main.getWarehouseId()));
            if (wh != null && StringUtils.isNotEmpty(wh.getTenantId())) {
                return wh.getTenantId();
            }
        }
        return SecurityUtils.resolveEffectiveTenantId(null);
    }

    /** 预览：用于按租户校验 HIS 生产厂家/供应商 id */
    private String resolveTenantIdForPreview(Long warehouseId, List<InitialImportExcelRow> rows) {
        if (warehouseId != null) {
            FdWarehouse w = fdWarehouseMapper.selectFdWarehouseByIdIgnoreTenant(String.valueOf(warehouseId));
            if (w != null && StringUtils.isNotEmpty(w.getTenantId())) {
                return w.getTenantId();
            }
        }
        if (rows != null && !rows.isEmpty() && StringUtils.isNotEmpty(rows.get(0).getWarehouseCode())) {
            FdWarehouse q = new FdWarehouse();
            q.setCode(rows.get(0).getWarehouseCode());
            List<FdWarehouse> list = fdWarehouseMapper.selectFdWarehouseList(q);
            if (list != null && !list.isEmpty() && StringUtils.isNotEmpty(list.get(0).getTenantId())) {
                return list.get(0).getTenantId();
            }
        }
        return SecurityUtils.resolveEffectiveTenantId(null);
    }

    private boolean factoryExistsByHisId(String tenantId, String hisId) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(hisId)) {
            return false;
        }
        return fdFactoryMapper.selectFdFactoryByTenantAndHisId(tenantId, hisId.trim()) != null;
    }

    private boolean supplierExistsByHisId(String tenantId, String hisId) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(hisId)) {
            return false;
        }
        return fdSupplierMapper.selectFdSupplierByTenantAndHisId(tenantId, hisId.trim()) != null;
    }

    /**
     * 将 HIS 匹配得到的 SPD 生产厂家 ID、供应商 ID 赋到产品档案（fd_material）上，仅补空不覆盖已有值。
     * 期初明细上的 material_id / factory_id / supplier_id 仍由下面 {@link StkInitialImportEntry} 落库。
     */
    private void syncMaterialFactorySupplierFromHisMatch(FdMaterial material, Long factoryId, Long supplierId, String tenantId) {
        if (material == null || material.getId() == null) {
            return;
        }
        boolean need = false;
        FdMaterial patch = new FdMaterial();
        patch.setId(material.getId());
        if (StringUtils.isNotEmpty(tenantId)) {
            patch.setTenantId(tenantId);
        } else if (StringUtils.isNotEmpty(material.getTenantId())) {
            patch.setTenantId(material.getTenantId());
        }
        if (factoryId != null && material.getFactoryId() == null) {
            patch.setFactoryId(factoryId);
            need = true;
            material.setFactoryId(factoryId);
        }
        if (supplierId != null && material.getSupplierId() == null) {
            patch.setSupplierId(supplierId);
            need = true;
            material.setSupplierId(supplierId);
        }
        if (need) {
            patch.setUpdateBy(SecurityUtils.getUserIdStr());
            patch.setUpdateTime(new Date());
            fdMaterialMapper.updateFdMaterial(patch);
        }
    }

    private boolean supplierExistsByName(String name) {
        if (StringUtils.isEmpty(name)) return true;
        FdSupplier q = new FdSupplier();
        q.setName(name);
        List<FdSupplier> list = fdSupplierMapper.selectFdSupplierList(q);
        if (list == null || list.isEmpty()) return false;
        for (FdSupplier s : list) {
            if (name.equals(s.getName())) return true;
        }
        return false;
    }

    private static String trimToNull(String s) {
        return s != null && !s.trim().isEmpty() ? s.trim() : null;
    }

    /** 生成不重复的产品档案编码 */
    private String generateUniqueMaterialCode() {
        String code;
        do {
            code = "QC" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        } while (fdMaterialMapper.selectFdMaterialByCode(code) != null);
        return code;
    }

    private Long resolveWarehouseCategoryId(String s) {
        if (StringUtils.isEmpty(s)) return null;
        s = s.trim();
        FdWarehouseCategory q = new FdWarehouseCategory();
        q.setWarehouseCategoryCode(s);
        List<FdWarehouseCategory> list = fdWarehouseCategoryMapper.selectFdWarehouseCategoryList(q);
        if (list != null && !list.isEmpty()) return list.get(0).getWarehouseCategoryId();
        q.setWarehouseCategoryCode(null);
        q.setWarehouseCategoryName(s);
        list = fdWarehouseCategoryMapper.selectFdWarehouseCategoryList(q);
        return (list != null && !list.isEmpty()) ? list.get(0).getWarehouseCategoryId() : null;
    }

    private Long resolveFinanceCategoryId(String s) {
        if (StringUtils.isEmpty(s)) return null;
        s = s.trim();
        FdFinanceCategory q = new FdFinanceCategory();
        q.setFinanceCategoryCode(s);
        List<FdFinanceCategory> list = fdFinanceCategoryMapper.selectFdFinanceCategoryList(q);
        if (list != null && !list.isEmpty()) return list.get(0).getFinanceCategoryId();
        q.setFinanceCategoryCode(null);
        q.setFinanceCategoryName(s);
        list = fdFinanceCategoryMapper.selectFdFinanceCategoryList(q);
        return (list != null && !list.isEmpty()) ? list.get(0).getFinanceCategoryId() : null;
    }
}
