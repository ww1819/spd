package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.domain.StkIoProfitLossEntry;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.mapper.HcCkFlowMapper;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkIoProfitLossMapper;
import com.spd.warehouse.mapper.StkIoStocktakingMapper;
import com.spd.warehouse.service.IStkIoProfitLossService;
import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.vo.StkProfitLossEntryVo;

/**
 * 盈亏单 Service 实现
 *
 * @author spd
 */
@Service
public class StkIoProfitLossServiceImpl implements IStkIoProfitLossService {

    private static final Logger log = LoggerFactory.getLogger(StkIoProfitLossServiceImpl.class);
    /** 盈亏业务域：仓库 */
    private static final String SCOPE_WH = "WH";
    /** 盈亏业务域：科室 */
    private static final String SCOPE_DEP = "DEP";
    // 批次来源 lx：盘盈 => PY
    private static final String BATCH_SOURCE_PROFIT = "PY";

    @Autowired
    private StkIoProfitLossMapper stkIoProfitLossMapper;
    @Autowired
    private StkIoStocktakingMapper stkIoStocktakingMapper;
    @Autowired
    private StkInventoryMapper stkInventoryMapper;
    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;
    @Autowired
    private StkBatchMapper stkBatchMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
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
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;

    @Override
    public StkIoProfitLoss selectStkIoProfitLossById(Long id) {
        StkIoProfitLoss bill = stkIoProfitLossMapper.selectStkIoProfitLossById(id);
        if (bill != null) {
            SecurityUtils.ensureTenantAccess(bill.getTenantId());
        }
        return bill;
    }

    @Override
    public List<StkIoProfitLoss> selectStkIoProfitLossList(StkIoProfitLoss stkIoProfitLoss) {
        if (stkIoProfitLoss != null && StringUtils.isEmpty(stkIoProfitLoss.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoProfitLoss.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoProfitLossMapper.selectStkIoProfitLossList(stkIoProfitLoss);
    }

    @Override
    public StkIoProfitLoss loadDraftByStocktakingId(Long stocktakingId) {
        StkIoStocktaking stocktaking = stkIoStocktakingMapper.selectStkIoStocktakingById(stocktakingId);
        if (stocktaking == null) {
            throw new ServiceException("盘点单不存在");
        }
        if (stocktaking.getStockStatus() == null || stocktaking.getStockStatus() != 2) {
            throw new ServiceException("仅已审核的盘点单可生成盈亏单");
        }
        boolean deptSt = isDeptStocktaking(stocktaking);
        if (deptSt && stocktaking.getAuditAdjustsInventory() != null && stocktaking.getAuditAdjustsInventory() == 1) {
            throw new ServiceException("该科室盘点单为「审核直接变更科室库存」，不可再生成科室盈亏单");
        }
        Integer exist = stkIoProfitLossMapper.countByStocktakingId(stocktakingId);
        if (exist != null && exist > 0) {
            throw new ServiceException("该盘点单已生成盈亏单，不可重复生成");
        }
        List<StkIoStocktakingEntry> withProfit = stkIoProfitLossMapper.selectStocktakingEntriesWithProfitLoss(stocktakingId);
        if (withProfit == null || withProfit.isEmpty()) {
            throw new ServiceException("该盘点单没有盈亏明细，无需生成盈亏单");
        }
        StkIoProfitLoss draft = new StkIoProfitLoss();
        draft.setUuidId(UUID7.generateUUID7());
        draft.setStocktakingId(stocktakingId);
        draft.setStocktakingUuid(stocktaking.getUuidId());
        draft.setStocktakingNo(stocktaking.getStockNo());
        draft.setBillStatus(1);
        draft.setDelFlag(0);

        String warehouseNameSnap = null;
        String departmentNameSnap = null;
        if (deptSt) {
            draft.setBizScope(SCOPE_DEP);
            draft.setDepartmentId(stocktaking.getDepartmentId());
            draft.setWarehouseId(null);
            if (stocktaking.getDepartmentId() != null) {
                FdDepartment d = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(stocktaking.getDepartmentId()));
                if (d != null) {
                    departmentNameSnap = d.getName();
                }
            }
            draft.setDepartmentNameSnap(departmentNameSnap);
        }
        else {
            draft.setBizScope(SCOPE_WH);
            draft.setWarehouseId(stocktaking.getWarehouseId());
            if (stocktaking.getWarehouseId() != null) {
                FdWarehouse whSnap = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(stocktaking.getWarehouseId()));
                if (whSnap != null) {
                    warehouseNameSnap = whSnap.getName();
                }
            }
            draft.setDepartmentNameSnap(null);
        }

        List<StkIoProfitLossEntry> entryList = new ArrayList<>();
        for (StkIoStocktakingEntry e : withProfit) {
            StkIoProfitLossEntry entry = new StkIoProfitLossEntry();
            entry.setEntryUuid(UUID7.generateUUID7());
            entry.setStocktakingEntryId(e.getId());
            entry.setStocktakingLineUuid(e.getEntryUuid());
            entry.setMaterialId(e.getMaterialId());
            entry.setBatchNumber(e.getBatchNumber());
            BigDecimal pq = e.getProfitQty();
            if (pq != null && pq.compareTo(BigDecimal.ZERO) > 0) {
                entry.setPlKind("SURPLUS");
                entry.setBatchNo(generateBatchNo());
                if (deptSt) {
                    entry.setBookQty(BigDecimal.ZERO);
                    entry.setKcNo(null);
                    entry.setDepInventoryId(null);
                    entry.setReturnWarehouseId(e.getReturnWarehouseId());
                }
                else {
                    entry.setKcNo(e.getKcNo());
                    entry.setBookQty(e.getKcNo() == null ? BigDecimal.ZERO : nz(e.getQty()));
                }
                entry.setOrigBatchNo(e.getOrigBatchNoSnapshot() != null ? e.getOrigBatchNoSnapshot() : e.getBatchNo());
                entry.setOrigBatchId(e.getOrigBatchId());
            }
            else if (pq != null && pq.compareTo(BigDecimal.ZERO) < 0) {
                entry.setPlKind("LOSS");
                entry.setBatchNo(e.getBatchNo());
                if (deptSt) {
                    entry.setDepInventoryId(e.getKcNo());
                    entry.setKcNo(null);
                    entry.setBookQty(nz(e.getQty()));
                    entry.setReturnWarehouseId(e.getReturnWarehouseId());
                }
                else {
                    entry.setKcNo(e.getKcNo());
                    entry.setBookQty(nz(e.getQty()));
                }
                entry.setOrigBatchNo(e.getOrigBatchNoSnapshot() != null ? e.getOrigBatchNoSnapshot() : e.getBatchNo());
                entry.setOrigBatchId(e.getOrigBatchId());
            }
            else {
                entry.setBatchNo(e.getBatchNo());
                if (!deptSt) {
                    entry.setKcNo(e.getKcNo());
                }
            }
            entry.setStockQty(e.getStockQty());
            entry.setProfitQty(e.getProfitQty());
            entry.setUnitPrice(e.getUnitPrice() != null ? e.getUnitPrice() : e.getPrice());
            entry.setProfitAmount(e.getProfitAmount());
            entry.setBeginTime(e.getBeginTime());
            entry.setEndTime(e.getEndTime());
            entry.setMaterial(e.getMaterial());
            if (e.getMaterial() != null) {
                entry.setMaterialNameSnap(e.getMaterial().getName());
                entry.setMaterialSpeciSnap(e.getMaterial().getSpeci());
            }
            if (deptSt) {
                entry.setDepartmentNameSnap(departmentNameSnap);
            }
            else {
                entry.setWarehouseNameSnap(warehouseNameSnap);
            }
            entry.setSupplerId(e.getSupplierId() != null ? String.valueOf(e.getSupplierId()) : null);
            entry.setMainBarcode(e.getMainBarcode());
            entry.setSubBarcode(e.getSubBarcode());
            entry.setDelFlag(0);
            entryList.add(entry);
        }
        draft.setEntryList(entryList);
        return draft;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss) {
        if (stkIoProfitLoss.getStocktakingId() != null) {
            Integer exist = stkIoProfitLossMapper.countByStocktakingId(stkIoProfitLoss.getStocktakingId());
            if (exist != null && exist > 0) {
                throw new ServiceException("该盘点单已生成盈亏单，不可重复保存");
            }
        }
        if (StringUtils.isEmpty(stkIoProfitLoss.getBizScope())) {
            stkIoProfitLoss.setBizScope(SCOPE_WH);
        }
        stkIoProfitLoss.setBillNo(generateBillNo(stkIoProfitLoss.getBizScope()));
        stkIoProfitLoss.setBillStatus(1);
        stkIoProfitLoss.setDelFlag(0);
        stkIoProfitLoss.setCreateTime(DateUtils.getNowDate());
        stkIoProfitLoss.setCreateBy(SecurityUtils.getUserIdStr());
        if (StringUtils.isEmpty(stkIoProfitLoss.getUuidId())) {
            stkIoProfitLoss.setUuidId(UUID7.generateUUID7());
        }
        if (stkIoProfitLoss.getStocktakingId() != null && StringUtils.isEmpty(stkIoProfitLoss.getStocktakingUuid())) {
            StkIoStocktaking st = stkIoStocktakingMapper.selectStkIoStocktakingById(stkIoProfitLoss.getStocktakingId());
            if (st != null) {
                stkIoProfitLoss.setStocktakingUuid(st.getUuidId());
            }
        }
        if (StringUtils.isEmpty(stkIoProfitLoss.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoProfitLoss.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = stkIoProfitLossMapper.insertStkIoProfitLoss(stkIoProfitLoss);
        insertEntries(stkIoProfitLoss);
        return rows;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss) {
        StkIoProfitLoss existing = stkIoProfitLossMapper.selectStkIoProfitLossById(stkIoProfitLoss.getId());
        if (existing == null) {
            throw new ServiceException("盈亏单不存在");
        }
        if (existing.getBillStatus() != null && existing.getBillStatus() == 2) {
            throw new ServiceException("已审核的盈亏单不可修改");
        }
        stkIoProfitLoss.setUpdateTime(DateUtils.getNowDate());
        stkIoProfitLoss.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoProfitLossMapper.deleteStkIoProfitLossEntryByParenId(stkIoProfitLoss.getId(), SecurityUtils.getUserIdStr());
        insertEntries(stkIoProfitLoss);
        return stkIoProfitLossMapper.updateStkIoProfitLoss(stkIoProfitLoss);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteStkIoProfitLossById(Long id) {
        StkIoProfitLoss existing = stkIoProfitLossMapper.selectStkIoProfitLossById(id);
        if (existing == null) {
            throw new ServiceException("盈亏单不存在");
        }
        SecurityUtils.ensureTenantAccess(existing.getTenantId());
        if (existing.getBillStatus() != null && existing.getBillStatus() == 2) {
            throw new ServiceException("已审核的盈亏单不可删除");
        }
        stkIoProfitLossMapper.deleteStkIoProfitLossEntryByParenId(id, SecurityUtils.getUserIdStr());
        return stkIoProfitLossMapper.deleteStkIoProfitLossById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int auditStkIoProfitLoss(Long id) {
        StkIoProfitLoss bill = stkIoProfitLossMapper.selectStkIoProfitLossById(id);
        if (bill == null) {
            throw new ServiceException("盈亏单不存在");
        }
        if (bill.getBillStatus() != null && bill.getBillStatus() == 2) {
            throw new ServiceException("该盈亏单已审核");
        }
        List<StkIoProfitLossEntry> entryList = bill.getEntryList();
        if (entryList == null || entryList.isEmpty()) {
            throw new ServiceException("盈亏单无明细，无法审核");
        }
        Long warehouseId = bill.getWarehouseId();
        String username = SecurityUtils.getUserIdStr();
        Date now = new Date();

        for (StkIoProfitLossEntry entry : entryList) {
            if (SCOPE_DEP.equals(bill.getBizScope())) {
                auditDepartmentProfitLossLine(bill, entry, now, username);
                continue;
            }
            BigDecimal bookQty = entry.getBookQty();
            BigDecimal stockQty = entry.getStockQty();
            BigDecimal profitQty = entry.getProfitQty();
            if (profitQty == null || profitQty.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            String batchNo = entry.getBatchNo();
            if (StringUtils.isEmpty(batchNo) && profitQty.compareTo(BigDecimal.ZERO) > 0) {
                batchNo = generateBatchNo();
                entry.setBatchNo(batchNo);
            }
            if (profitQty.compareTo(BigDecimal.ZERO) < 0) {
                // 盘亏：仅按盘点明细关联的原库存行 kc_no 扣减（原批次）
                if (entry.getKcNo() == null) {
                    throw new ServiceException("盘亏明细必须关联库存明细 kcNo，仅允许从原批次库存扣减");
                }
                StkInventory inventory = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
                if (inventory == null) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。库存行不存在，kcNo=" + entry.getKcNo());
                }
                BigDecimal currentQtyLoss = inventory.getQty();
                BigDecimal expectedCurrent = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
                boolean qtyMatch = expectedCurrent != null && currentQtyLoss != null && isQtyEqual(currentQtyLoss, expectedCurrent);
                log.info("[盈亏审核-盘亏] kcNo={} batchNo={} | stockQty={} | profitQty={} | 应有库存={} | 当前库存={} | 一致={}",
                    entry.getKcNo(), batchNo, stockQty, profitQty, expectedCurrent, currentQtyLoss, qtyMatch);
                if (expectedCurrent == null || currentQtyLoss == null || !qtyMatch) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。原批次库存与账面不一致。");
                }
                BigDecimal absQty = profitQty.abs();
                BigDecimal newQty = inventory.getQty().subtract(absQty);
                inventory.setQty(newQty);
                if (inventory.getUnitPrice() != null) {
                    inventory.setAmt(newQty.multiply(inventory.getUnitPrice()));
                }
                else {
                    inventory.setAmt(BigDecimal.ZERO);
                }
                inventory.setUpdateTime(now);
                inventory.setUpdateBy(username);
                stkInventoryMapper.updateStkInventory(inventory);

                HcCkFlow flow = new HcCkFlow();
                flow.setBillId(bill.getId());
                flow.setEntryId(entry.getId());
                flow.setWarehouseId(warehouseId);
                flow.setMaterialId(entry.getMaterialId());
                flow.setBatchNo(batchNo);
                flow.setBatchNumber(entry.getBatchNumber());
                flow.setQty(absQty);
                flow.setUnitPrice(entry.getUnitPrice());
                flow.setAmt(entry.getProfitAmount() != null ? entry.getProfitAmount().abs() : null);
                flow.setBeginTime(entry.getBeginTime());
                flow.setEndTime(entry.getEndTime());
                flow.setMainBarcode(entry.getMainBarcode() != null ? entry.getMainBarcode() : inventory.getMainBarcode());
                flow.setSubBarcode(entry.getSubBarcode() != null ? entry.getSubBarcode() : inventory.getSubBarcode());
                flow.setSupplierId(inventory.getSupplierId() != null ? inventory.getSupplierId() : parseSupplierIdLong(entry.getSupplerId()));
                flow.setLx("PK");
                flow.setBatchId(inventory.getBatchId());
                flow.setOriginBusinessType("仓库盘亏");
                flow.setKcNo(inventory.getId());
                flow.setFlowTime(now);
                flow.setDelFlag(0);
                flow.setCreateTime(now);
                flow.setCreateBy(username);
                hcCkFlowMapper.insertHcCkFlow(flow);
            }
            else {
                // 盘盈：写入 stk_batch + stk_inventory，流水 lx=PY
                StkInventory invProbe = null;
                if (entry.getKcNo() != null) {
                    invProbe = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
                }
                BigDecimal currentQty = invProbe != null ? invProbe.getQty() : null;
                BigDecimal expectedCurrentProfit = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
                boolean qtyMatchProfit = invProbe == null || expectedCurrentProfit == null || currentQty == null
                    || isQtyEqual(currentQty, expectedCurrentProfit);
                log.info("[盈亏审核-盘盈] kcNo={} batchNo={} | stockQty={} | profitQty={} | 应有库存={} | 当前库存={} | 一致={}",
                    entry.getKcNo(), batchNo, stockQty, profitQty, expectedCurrentProfit, currentQty, qtyMatchProfit);
                if (invProbe != null && expectedCurrentProfit != null && currentQty != null && !isQtyEqual(currentQty, expectedCurrentProfit)) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。批次：" + batchNo);
                }
                StkBatch stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
                if (stkBatch == null) {
                    stkBatch = buildStkBatchForProfit(bill, entry);
                    stkBatchMapper.insertStkBatch(stkBatch);
                }
                BigDecimal addQty = profitQty;
                StkInventory newInv = buildStkInventoryForProfit(bill, entry, stkBatch, addQty, now, username);
                stkInventoryMapper.insertStkInventory(newInv);

                HcCkFlow flow = new HcCkFlow();
                flow.setBillId(bill.getId());
                flow.setEntryId(entry.getId());
                flow.setWarehouseId(warehouseId);
                flow.setMaterialId(entry.getMaterialId());
                flow.setBatchNo(batchNo);
                flow.setBatchNumber(entry.getBatchNumber());
                flow.setQty(addQty);
                flow.setUnitPrice(entry.getUnitPrice());
                flow.setAmt(entry.getProfitAmount());
                flow.setBeginTime(entry.getBeginTime());
                flow.setEndTime(entry.getEndTime());
                flow.setMainBarcode(entry.getMainBarcode());
                flow.setSubBarcode(entry.getSubBarcode());
                flow.setSupplierId(parseSupplierIdLong(entry.getSupplerId()));
                if (stkBatch.getFactoryId() != null) {
                    flow.setFactoryId(stkBatch.getFactoryId());
                }
                flow.setLx("PY");
                flow.setBatchId(stkBatch.getId());
                flow.setOriginBusinessType("仓库盘盈入库");
                flow.setKcNo(newInv.getId());
                flow.setFlowTime(now);
                flow.setDelFlag(0);
                flow.setCreateTime(now);
                flow.setCreateBy(username);
                hcCkFlowMapper.insertHcCkFlow(flow);

                stkIoProfitLossMapper.updateStkIoProfitLossEntryPostingResult(entry.getId(), batchNo, stkBatch.getId(), newInv.getId(), null);
            }
        }

        bill.setBillStatus(2);
        bill.setAuditDate(now);
        bill.setAuditBy(username);
        bill.setUpdateTime(now);
        bill.setUpdateBy(username);
        return stkIoProfitLossMapper.updateStkIoProfitLoss(bill);
    }

    private void insertEntries(StkIoProfitLoss stkIoProfitLoss) {
        List<StkIoProfitLossEntry> list = stkIoProfitLoss.getEntryList();
        if (list == null || list.isEmpty()) {
            return;
        }
        Date now = DateUtils.getNowDate();
        String user = SecurityUtils.getUserIdStr();
        String tenantId = StringUtils.isNotEmpty(stkIoProfitLoss.getTenantId())
            ? stkIoProfitLoss.getTenantId() : SecurityUtils.getCustomerId();
        for (StkIoProfitLossEntry e : list) {
            e.setParenId(stkIoProfitLoss.getId());
            if (StringUtils.isEmpty(e.getEntryUuid())) {
                e.setEntryUuid(UUID7.generateUUID7());
            }
            e.setTenantId(tenantId);
            e.setDelFlag(0);
            e.setCreateTime(now);
            e.setCreateBy(user);
            e.setUpdateTime(now);
            e.setUpdateBy(user);
        }
        stkIoProfitLossMapper.batchStkIoProfitLossEntry(list);
    }

    private String generateBillNo(String bizScope) {
        String prefix = SCOPE_DEP.equals(bizScope) ? "DPL" : "PL";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoProfitLossMapper.selectMaxBillNo(prefix + date);
        return FillRuleUtil.getNumber(prefix, maxNum, date);
    }

    /** 科室盘点：departmentId 有值且非仓库盘点（warehouseId 为空） */
    private boolean isDeptStocktaking(StkIoStocktaking st) {
        if (st == null) {
            return false;
        }
        return st.getDepartmentId() != null && (st.getWarehouseId() == null || st.getWarehouseId() == 0L);
    }

    /**
     * 科室盈亏：盘亏只扣 stk_dep_inventory；盘盈写 stk_batch + stk_dep_inventory 并回写 result_dep_inventory_id。
     */
    private void auditDepartmentProfitLossLine(StkIoProfitLoss bill, StkIoProfitLossEntry entry, Date now, String username) {
        if (bill.getDepartmentId() == null) {
            throw new ServiceException("科室盈亏单缺少科室信息");
        }
        BigDecimal profitQty = entry.getProfitQty();
        if (profitQty == null || profitQty.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        String batchNo = entry.getBatchNo();
        if (StringUtils.isEmpty(batchNo) && profitQty.compareTo(BigDecimal.ZERO) > 0) {
            batchNo = generateBatchNo();
            entry.setBatchNo(batchNo);
        }
        BigDecimal stockQty = entry.getStockQty();
        BigDecimal bookQty = entry.getBookQty();

        if (profitQty.compareTo(BigDecimal.ZERO) < 0) {
            if (entry.getDepInventoryId() == null) {
                throw new ServiceException("科室盘亏明细必须关联科室库存行 depInventoryId");
            }
            StkDepInventory depInv = stkDepInventoryMapper.selectStkDepInventoryById(entry.getDepInventoryId());
            if (depInv == null) {
                throw new ServiceException("科室库存已变动，请重盘。科室库存行不存在，id=" + entry.getDepInventoryId());
            }
            if (!bill.getDepartmentId().equals(depInv.getDepartmentId())) {
                throw new ServiceException("明细科室库存不属于本盈亏单的科室");
            }
            BigDecimal currentQtyLoss = depInv.getQty();
            BigDecimal expectedCurrent = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
            boolean qtyMatch = expectedCurrent != null && currentQtyLoss != null && isQtyEqual(currentQtyLoss, expectedCurrent);
            log.info("[科室盈亏审核-盘亏] depInvId={} batchNo={} | stockQty={} | profitQty={} | 应有库存={} | 当前库存={} | 一致={}",
                entry.getDepInventoryId(), batchNo, stockQty, profitQty, expectedCurrent, currentQtyLoss, qtyMatch);
            if (expectedCurrent == null || currentQtyLoss == null || !qtyMatch) {
                throw new ServiceException("科室库存已变动，请重做盘点与盈亏处理。原批次/行库存与账面不一致。");
            }
            BigDecimal absQty = profitQty.abs();
            BigDecimal newQty = depInv.getQty().subtract(absQty);
            depInv.setQty(newQty);
            if (depInv.getUnitPrice() != null) {
                depInv.setAmt(newQty.multiply(depInv.getUnitPrice()));
            }
            else {
                depInv.setAmt(BigDecimal.ZERO);
            }
            depInv.setUpdateTime(now);
            depInv.setUpdateBy(username);
            stkDepInventoryMapper.updateStkDepInventory(depInv);

            HcCkFlow flow = new HcCkFlow();
            flow.setBillId(bill.getId());
            flow.setEntryId(entry.getId());
            Long flowWh = depInv.getWarehouseId() != null ? depInv.getWarehouseId() : entry.getReturnWarehouseId();
            flow.setWarehouseId(flowWh);
            flow.setMaterialId(entry.getMaterialId());
            flow.setBatchNo(batchNo);
            flow.setBatchNumber(entry.getBatchNumber());
            flow.setQty(absQty);
            flow.setUnitPrice(entry.getUnitPrice());
            flow.setAmt(entry.getProfitAmount() != null ? entry.getProfitAmount().abs() : null);
            flow.setBeginTime(entry.getBeginTime());
            flow.setEndTime(entry.getEndTime());
            flow.setMainBarcode(entry.getMainBarcode() != null ? entry.getMainBarcode() : depInv.getMainBarcode());
            flow.setSubBarcode(entry.getSubBarcode() != null ? entry.getSubBarcode() : depInv.getSubBarcode());
            Long supId = depInv.getSupplierId() != null ? parseSupplierIdLong(depInv.getSupplierId()) : parseSupplierIdLong(entry.getSupplerId());
            flow.setSupplierId(supId);
            flow.setLx("PK");
            flow.setBatchId(depInv.getBatchId());
            flow.setOriginBusinessType("科室盘亏");
            flow.setKcNo(depInv.getId());
            flow.setFlowTime(now);
            flow.setDelFlag(0);
            flow.setCreateTime(now);
            flow.setCreateBy(username);
            if (StringUtils.isEmpty(flow.getTenantId())) {
                flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
            }
            hcCkFlowMapper.insertHcCkFlow(flow);
            return;
        }

        Long returnWhId = resolveReturnWarehouseForDeptSurplus(entry);
        StkInventory invProbe = null;
        if (entry.getKcNo() != null) {
            invProbe = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
        }
        BigDecimal currentQty = invProbe != null ? invProbe.getQty() : null;
        BigDecimal expectedCurrentProfit = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
        boolean qtyMatchProfit = invProbe == null || expectedCurrentProfit == null || currentQty == null
            || isQtyEqual(currentQty, expectedCurrentProfit);
        log.info("[科室盈亏审核-盘盈] kcNo={} batchNo={} | stockQty={} | profitQty={} | 应有库存={} | 当前库存={} | 一致={}",
            entry.getKcNo(), batchNo, stockQty, profitQty, expectedCurrentProfit, currentQty, qtyMatchProfit);
        if (invProbe != null && expectedCurrentProfit != null && currentQty != null && !isQtyEqual(currentQty, expectedCurrentProfit)) {
            throw new ServiceException("关联仓库库存已变动，请重做盘点与盈亏处理。批次：" + batchNo);
        }
        StkBatch stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
        if (stkBatch == null) {
            stkBatch = buildStkBatchForDepartmentProfit(bill, entry, returnWhId);
            stkBatchMapper.insertStkBatch(stkBatch);
        }
        BigDecimal addQty = profitQty;
        StkDepInventory newDep = buildStkDepInventoryForProfit(bill, entry, stkBatch, addQty, returnWhId, now, username);
        stkDepInventoryMapper.insertStkDepInventory(newDep);

        HcCkFlow flow = new HcCkFlow();
        flow.setBillId(bill.getId());
        flow.setEntryId(entry.getId());
        flow.setWarehouseId(returnWhId);
        flow.setMaterialId(entry.getMaterialId());
        flow.setBatchNo(batchNo);
        flow.setBatchNumber(entry.getBatchNumber());
        flow.setQty(addQty);
        flow.setUnitPrice(entry.getUnitPrice());
        flow.setAmt(entry.getProfitAmount());
        flow.setBeginTime(entry.getBeginTime());
        flow.setEndTime(entry.getEndTime());
        flow.setMainBarcode(entry.getMainBarcode());
        flow.setSubBarcode(entry.getSubBarcode());
        flow.setSupplierId(parseSupplierIdLong(entry.getSupplerId()));
        if (stkBatch.getFactoryId() != null) {
            flow.setFactoryId(stkBatch.getFactoryId());
        }
        flow.setLx("PY");
        flow.setBatchId(stkBatch.getId());
        flow.setOriginBusinessType("科室盘盈入库");
        flow.setKcNo(newDep.getId());
        flow.setFlowTime(now);
        flow.setDelFlag(0);
        flow.setCreateTime(now);
        flow.setCreateBy(username);
        if (StringUtils.isEmpty(flow.getTenantId())) {
            flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
        }
        hcCkFlowMapper.insertHcCkFlow(flow);

        stkIoProfitLossMapper.updateStkIoProfitLossEntryPostingResult(entry.getId(), batchNo, stkBatch.getId(), null, newDep.getId());
    }

    private Long resolveReturnWarehouseForDeptSurplus(StkIoProfitLossEntry entry) {
        if (entry == null) {
            return null;
        }
        if (entry.getReturnWarehouseId() != null) {
            return entry.getReturnWarehouseId();
        }
        if (entry.getMaterialId() == null) {
            throw new ServiceException("盘盈明细缺少耗材，无法确定可退库仓库");
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
        if (material == null) {
            throw new ServiceException("耗材档案不存在，无法确定可退库仓库");
        }
        Long defaultWarehouseId = material.getDefaultWarehouseId();
        if (defaultWarehouseId == null || defaultWarehouseId == 0) {
            throw new ServiceException("产品档案未维护默认所属仓库，无法生成科室盘盈批次，请先维护后再审核。");
        }
        return defaultWarehouseId;
    }

    private StkBatch buildStkBatchForDepartmentProfit(StkIoProfitLoss bill, StkIoProfitLossEntry entry, Long returnWarehouseId) {
        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setBatchNumber(entry.getBatchNumber());
        b.setMainBarcode(entry.getMainBarcode());
        b.setSubBarcode(entry.getSubBarcode());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());
        b.setUnitPrice(entry.getUnitPrice());
        b.setBillId(bill.getId());
        b.setBillNo(bill.getBillNo());
        b.setEntryId(entry.getId());
        b.setBatchSource(BATCH_SOURCE_PROFIT);
        b.setOriginBillType(null);
        b.setOriginFlowLx("PY");
        b.setOriginBusinessType("科室盘盈");
        if (returnWarehouseId != null) {
            b.setWarehouseId(returnWarehouseId);
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(returnWarehouseId));
            if (wh != null) {
                b.setWarehouseCode(wh.getCode());
                b.setWarehouseName(wh.getName());
            }
            b.setOriginFromWarehouseId(returnWarehouseId);
            b.setOriginToWarehouseId(returnWarehouseId);
        }
        if (bill.getDepartmentId() != null) {
            b.setDepartmentId(bill.getDepartmentId());
            FdDepartment dep = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(bill.getDepartmentId()));
            if (dep != null) {
                b.setDepartmentCode(dep.getCode());
                b.setDepartmentName(dep.getName());
            }
        }
        Date auditNow = new Date();
        b.setAuditTime(auditNow);
        b.setAuditBy(SecurityUtils.getUserIdStr());
        b.setCreateTime(auditNow);
        b.setCreateBy(SecurityUtils.getUserIdStr());
        b.setDelFlag(0);
        b.setTenantId(bill.getTenantId());
        if (StringUtils.isNotEmpty(entry.getSupplerId())) {
            try {
                Long supplierId = Long.valueOf(entry.getSupplerId().trim());
                FdSupplier sup = fdSupplierMapper.selectFdSupplierById(supplierId);
                if (sup != null) {
                    b.setSupplierId(sup.getId());
                    b.setSupplierCode(sup.getCode());
                    b.setSupplierName(sup.getName());
                }
            } catch (NumberFormatException ignored) {
            }
        }
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
        return b;
    }

    private StkDepInventory buildStkDepInventoryForProfit(StkIoProfitLoss bill, StkIoProfitLossEntry entry, StkBatch stkBatch,
        BigDecimal qty, Long returnWhId, Date now, String username) {
        StkDepInventory inv = new StkDepInventory();
        inv.setMaterialId(entry.getMaterialId());
        inv.setDepartmentId(bill.getDepartmentId());
        inv.setWarehouseId(returnWhId);
        inv.setQty(qty);
        inv.setUnitPrice(entry.getUnitPrice());
        if (entry.getUnitPrice() != null && qty != null) {
            inv.setAmt(qty.multiply(entry.getUnitPrice()));
        }
        else {
            inv.setAmt(BigDecimal.ZERO);
        }
        inv.setBatchNo(entry.getBatchNo());
        inv.setBatchId(stkBatch != null ? stkBatch.getId() : null);
        inv.setMaterialNo(entry.getBatchNumber());
        inv.setBatchNumber(entry.getBatchNumber());
        inv.setMaterialDate(now);
        inv.setWarehouseDate(now);
        if (StringUtils.isNotEmpty(entry.getSupplerId())) {
            inv.setSupplierId(entry.getSupplerId());
        }
        if (stkBatch != null && stkBatch.getFactoryId() != null) {
            inv.setFactoryId(stkBatch.getFactoryId());
        }
        else if (entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null) {
                inv.setFactoryId(m.getFactoryId());
            }
        }
        inv.setBeginDate(entry.getBeginTime());
        inv.setEndDate(entry.getEndTime());
        inv.setBillId(bill.getId());
        inv.setBillEntryId(entry.getId());
        inv.setBillNo(bill.getBillNo());
        inv.setOutOrderNo(bill.getBillNo());
        inv.setReceiptConfirmStatus(1);
        inv.setRemark("科室盈亏单盘盈审核生成");
        inv.setMainBarcode(entry.getMainBarcode());
        inv.setSubBarcode(entry.getSubBarcode());
        inv.setDelFlag(0);
        inv.setTenantId(bill.getTenantId());
        inv.setCreateTime(now);
        inv.setCreateBy(username);
        if (entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null) {
                inv.setSnapMaterialName(m.getName());
                inv.setSnapMaterialSpeci(m.getSpeci());
                inv.setSnapMaterialModel(m.getModel());
                inv.setSnapMaterialFactoryId(m.getFactoryId());
            }
        }
        return inv;
    }

    /**
     * 数量是否一致：按 2 位小数归一化后比较，避免 scale 或精度差异导致误判（如 350 与 350.00、350.001 与 350）
     */
    private boolean isQtyEqual(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.setScale(2, RoundingMode.HALF_UP).compareTo(b.setScale(2, RoundingMode.HALF_UP)) == 0;
    }

    private String generateBatchNo() {
        return "PC" + FillRuleUtil.createBatchNo();
    }

    /** 将明细中的 supplerId（String）解析为 Long，用于写库存/流水 */
    private Long parseSupplierIdLong(String supplerId) {
        if (StringUtils.isEmpty(supplerId)) return null;
        try {
            return Long.valueOf(supplerId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private StkBatch buildStkBatchForProfit(StkIoProfitLoss bill, StkIoProfitLossEntry entry) {
        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setBatchNumber(entry.getBatchNumber());
        b.setMainBarcode(entry.getMainBarcode());
        b.setSubBarcode(entry.getSubBarcode());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());
        b.setUnitPrice(entry.getUnitPrice());
        b.setBillId(bill.getId());
        b.setBillNo(bill.getBillNo());
        b.setEntryId(entry.getId());
        // 批次来源：盘盈对应流水 lx=PY（批次追溯展示用）
        b.setBatchSource(BATCH_SOURCE_PROFIT);
        b.setOriginBillType(null);
        b.setOriginFlowLx("PY");
        b.setOriginBusinessType("仓库盘盈");
        if (bill.getWarehouseId() != null) {
            b.setOriginFromWarehouseId(bill.getWarehouseId());
            b.setOriginToWarehouseId(bill.getWarehouseId());
        }
        Date now = new Date();
        String username = SecurityUtils.getUserIdStr();
        b.setAuditTime(now);
        b.setAuditBy(username);
        b.setCreateTime(now);
        b.setCreateBy(username);
        b.setDelFlag(0);
        b.setTenantId(bill.getTenantId());
        if (StringUtils.isNotEmpty(entry.getSupplerId())) {
            try {
                Long supplierId = Long.valueOf(entry.getSupplerId().trim());
                FdSupplier sup = fdSupplierMapper.selectFdSupplierById(supplierId);
                if (sup != null) {
                    b.setSupplierId(sup.getId());
                    b.setSupplierCode(sup.getCode());
                    b.setSupplierName(sup.getName());
                }
            } catch (NumberFormatException ignored) {
            }
        }
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
        if (bill.getWarehouseId() != null) {
            b.setWarehouseId(bill.getWarehouseId());
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(bill.getWarehouseId()));
            if (wh != null) {
                b.setWarehouseCode(wh.getCode());
                b.setWarehouseName(wh.getName());
            }
        }
        return b;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * 盘盈审核：生成仓库库存行（数量=盘盈数量），与 stk_batch 关联。
     */
    private StkInventory buildStkInventoryForProfit(StkIoProfitLoss bill, StkIoProfitLossEntry entry, StkBatch stkBatch,
        BigDecimal qty, Date now, String username) {
        StkInventory inv = new StkInventory();
        inv.setBatchNo(entry.getBatchNo());
        inv.setBatchId(stkBatch != null ? stkBatch.getId() : null);
        inv.setMaterialNo(entry.getBatchNumber());
        inv.setBatchNumber(entry.getBatchNumber());
        inv.setMaterialId(entry.getMaterialId());
        inv.setWarehouseId(bill.getWarehouseId());
        inv.setQty(qty);
        inv.setUnitPrice(entry.getUnitPrice());
        if (entry.getUnitPrice() != null && qty != null) {
            inv.setAmt(qty.multiply(entry.getUnitPrice()));
        }
        else {
            inv.setAmt(BigDecimal.ZERO);
        }
        inv.setMaterialDate(now);
        inv.setWarehouseDate(now);
        inv.setSupplierId(parseSupplierIdLong(entry.getSupplerId()));
        if (stkBatch != null && stkBatch.getFactoryId() != null) {
            inv.setFactoryId(stkBatch.getFactoryId());
        }
        else if (entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null) {
                inv.setFactoryId(m.getFactoryId());
            }
        }
        inv.setBeginTime(entry.getBeginTime());
        inv.setEndTime(entry.getEndTime());
        inv.setReceiptOrderNo(bill.getBillNo());
        inv.setMainBarcode(entry.getMainBarcode());
        inv.setSubBarcode(entry.getSubBarcode());
        inv.setCreateTime(now);
        inv.setCreateBy(username);
        inv.setDelFlag(0);
        inv.setTenantId(bill.getTenantId());
        if (entry.getMaterialId() != null) {
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (m != null) {
                inv.setSnapMaterialName(m.getName());
                inv.setSnapMaterialSpeci(m.getSpeci());
                inv.setSnapMaterialModel(m.getModel());
                inv.setSnapMaterialFactoryId(m.getFactoryId());
            }
        }
        return inv;
    }

    @Override
    public List<StkProfitLossEntryVo> selectProfitLossEntryList(StkIoProfitLoss stkIoProfitLoss) {
        return stkIoProfitLossMapper.selectProfitLossEntryList(stkIoProfitLoss);
    }

    @Override
    public List<StkProfitLossEntryVo> selectProfitLossEntrySummaryList(StkIoProfitLoss stkIoProfitLoss) {
        return stkIoProfitLossMapper.selectProfitLossEntrySummaryList(stkIoProfitLoss);
    }

    @Override
    public TotalInfo selectProfitLossEntryListTotal(StkIoProfitLoss stkIoProfitLoss) {
        return stkIoProfitLossMapper.selectProfitLossEntryListTotal(stkIoProfitLoss);
    }

    @Override
    public TotalInfo selectProfitLossEntrySummaryListTotal(StkIoProfitLoss stkIoProfitLoss) {
        return stkIoProfitLossMapper.selectProfitLossEntrySummaryListTotal(stkIoProfitLoss);
    }
}
