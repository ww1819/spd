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
import java.util.*;

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
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            InitialImportExcelRow row = rows.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rowIndex", i + 1);
            item.put("data", row);
            boolean hasThird = StringUtils.isNotEmpty(row.getThirdPartyMaterialId());
            boolean hasCode = StringUtils.isNotEmpty(row.getMaterialCode());
            if (!hasThird && !hasCode) {
                item.put("error", "第三方系统产品档案ID与耗材编码至少填一项");
            } else if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                item.put("error", "数量必须大于0");
            } else if (whId == null && (StringUtils.isEmpty(row.getWarehouseCode()))) {
                item.put("error", "请指定仓库或提供仓库编码");
            } else {
                item.put("error", null);
            }
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
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(warehouseId));
        if (wh == null) {
            throw new ServiceException("仓库不存在");
        }
        String username = SecurityUtils.getUsername();
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
        stkInitialImportMapper.insert(main);

        List<StkInitialImportEntry> entries = new ArrayList<>();
        int sortOrder = 0;
        for (InitialImportExcelRow row : rows) {
            if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            boolean hasThirdPartyMaterialId = StringUtils.isNotEmpty(row.getThirdPartyMaterialId());
            boolean hasMaterialCode = StringUtils.isNotEmpty(row.getMaterialCode());
            if (!hasThirdPartyMaterialId && !hasMaterialCode) {
                throw new ServiceException("第" + (sortOrder + 1) + "行：第三方系统产品档案ID与耗材编码至少填一项");
            }
            FdMaterial material = null;
            if (hasThirdPartyMaterialId) {
                material = fdMaterialMapper.selectFdMaterialByHisId(row.getThirdPartyMaterialId().trim());
                if (material == null) {
                    material = createNewMaterialFromRow(row, true);
                    fdMaterialMapper.insertFdMaterial(material);
                }
            } else {
                material = fdMaterialMapper.selectFdMaterialByCode(row.getMaterialCode().trim());
                if (material == null) {
                    material = createNewMaterialFromRow(row, false);
                    fdMaterialMapper.insertFdMaterial(material);
                }
            }
            Long factoryId = null;
            if (StringUtils.isNotEmpty(row.getFactoryName())) {
                FdFactory fq = new FdFactory();
                fq.setFactoryName(row.getFactoryName());
                List<FdFactory> fl = fdFactoryMapper.selectFdFactoryList(fq);
                if (fl != null && !fl.isEmpty()) {
                    factoryId = fl.get(0).getFactoryId();
                }
            }
            Long supplierId = null;
            if (StringUtils.isNotEmpty(row.getSupplierName())) {
                FdSupplier sq = new FdSupplier();
                sq.setName(row.getSupplierName());
                List<FdSupplier> sl = fdSupplierMapper.selectFdSupplierList(sq);
                if (sl != null && !sl.isEmpty()) {
                    supplierId = sl.get(0).getId();
                }
            }
            BigDecimal unitPrice = row.getUnitPrice() != null ? row.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal qty = row.getQty();
            BigDecimal amt = unitPrice.multiply(qty);

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
            entry.setBeginTime(row.getBeginTime());
            entry.setEndTime(row.getEndTime());
            entry.setFactoryId(factoryId);
            entry.setSupplierId(supplierId);
            entry.setSortOrder(++sortOrder);
            entry.setThirdPartyDetailId(StringUtils.isNotEmpty(row.getThirdPartyDetailId()) ? row.getThirdPartyDetailId().trim() : null);
            entry.setThirdPartyMaterialId(StringUtils.isNotEmpty(row.getThirdPartyMaterialId()) ? row.getThirdPartyMaterialId().trim() : null);
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
        String username = SecurityUtils.getUsername();
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
            inv.setBeginTime(entry.getBeginTime());
            inv.setEndTime(entry.getEndTime());
            inv.setReceiptOrderNo(main.getBillNo());
            inv.setCreateTime(auditTime);
            inv.setCreateBy(username);
            inv.setBatchNumber(entry.getBatchNumber());
            inv.setDelFlag(0);
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
            flow.setLx("QC");
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
        return stkInitialImportMapper.update(main);
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
        Date now = new Date();
        String username = SecurityUtils.getUsername();
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
        return b;
    }

    /** 根据导入行创建新产品档案：byHisId=true 时按第三方档案ID匹配（生成编码），否则按耗材编码（使用模板编码） */
    private FdMaterial createNewMaterialFromRow(InitialImportExcelRow row, boolean byHisId) {
        FdMaterial m = new FdMaterial();
        String name = StringUtils.isNotEmpty(row.getMaterialName()) ? row.getMaterialName().trim() : row.getMaterialCode();
        if (StringUtils.isEmpty(name)) {
            name = "期初导入-" + (row.getThirdPartyMaterialId() != null ? row.getThirdPartyMaterialId() : row.getMaterialCode());
        }
        m.setName(name);
        if (byHisId) {
            m.setHisId(row.getThirdPartyMaterialId().trim());
            m.setCode(generateUniqueMaterialCode());
        } else {
            m.setCode(row.getMaterialCode().trim());
        }
        m.setStoreroomId(resolveWarehouseCategoryId(row.getWarehouseCategory()));
        m.setFinanceCategoryId(resolveFinanceCategoryId(row.getFinanceCategory()));
        m.setDelFlag(0);
        m.setCreateBy(SecurityUtils.getUsername());
        m.setCreateTime(new Date());
        return m;
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
