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
    private static final String BATCH_SOURCE_PROFIT = "仓库盘盈";

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

    @Override
    public StkIoProfitLoss selectStkIoProfitLossById(Long id) {
        return stkIoProfitLossMapper.selectStkIoProfitLossById(id);
    }

    @Override
    public List<StkIoProfitLoss> selectStkIoProfitLossList(StkIoProfitLoss stkIoProfitLoss) {
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
        Integer exist = stkIoProfitLossMapper.countByStocktakingId(stocktakingId);
        if (exist != null && exist > 0) {
            throw new ServiceException("该盘点单已生成盈亏单，不可重复生成");
        }
        List<StkIoStocktakingEntry> withProfit = stkIoProfitLossMapper.selectStocktakingEntriesWithProfitLoss(stocktakingId);
        if (withProfit == null || withProfit.isEmpty()) {
            throw new ServiceException("该盘点单没有盈亏明细，无需生成盈亏单");
        }
        StkIoProfitLoss draft = new StkIoProfitLoss();
        draft.setStocktakingId(stocktakingId);
        draft.setStocktakingNo(stocktaking.getStockNo());
        draft.setWarehouseId(stocktaking.getWarehouseId());
        draft.setBillStatus(1);
        draft.setDelFlag(0);
        List<StkIoProfitLossEntry> entryList = new ArrayList<>();
        for (StkIoStocktakingEntry e : withProfit) {
            StkIoProfitLossEntry entry = new StkIoProfitLossEntry();
            entry.setStocktakingEntryId(e.getId());
            entry.setKcNo(e.getKcNo());
            entry.setMaterialId(e.getMaterialId());
            entry.setBatchNo(e.getBatchNo());
            entry.setBatchNumber(e.getBatchNumber());
            entry.setBookQty(e.getQty());
            entry.setStockQty(e.getStockQty());
            entry.setProfitQty(e.getProfitQty());
            // 单价优先用 unitPrice，为空时用盘点单的“价格”price
            entry.setUnitPrice(e.getUnitPrice() != null ? e.getUnitPrice() : e.getPrice());
            entry.setProfitAmount(e.getProfitAmount());
            entry.setBeginTime(e.getBeginTime());
            entry.setEndTime(e.getEndTime());
            entry.setMaterial(e.getMaterial());
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
        stkIoProfitLoss.setBillNo(generateBillNo());
        stkIoProfitLoss.setBillStatus(1);
        stkIoProfitLoss.setDelFlag(0);
        stkIoProfitLoss.setCreateTime(DateUtils.getNowDate());
        stkIoProfitLoss.setCreateBy(SecurityUtils.getUsername());
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
        stkIoProfitLoss.setUpdateBy(SecurityUtils.getUsername());
        stkIoProfitLossMapper.deleteStkIoProfitLossEntryByParenId(stkIoProfitLoss.getId());
        insertEntries(stkIoProfitLoss);
        return stkIoProfitLossMapper.updateStkIoProfitLoss(stkIoProfitLoss);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteStkIoProfitLossById(Long id) {
        StkIoProfitLoss existing = stkIoProfitLossMapper.selectStkIoProfitLossById(id);
        if (existing != null && existing.getBillStatus() != null && existing.getBillStatus() == 2) {
            throw new ServiceException("已审核的盈亏单不可删除");
        }
        stkIoProfitLossMapper.deleteStkIoProfitLossEntryByParenId(id);
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
        String username = SecurityUtils.getUsername();
        Date now = new Date();

        for (StkIoProfitLossEntry entry : entryList) {
            BigDecimal bookQty = entry.getBookQty();
            BigDecimal stockQty = entry.getStockQty(); // 盘点数量
            BigDecimal profitQty = entry.getProfitQty();
            if (profitQty == null || profitQty.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            String batchNo = entry.getBatchNo();
            if (StringUtils.isEmpty(batchNo) && profitQty.compareTo(BigDecimal.ZERO) > 0) {
                batchNo = generateBatchNo();
                entry.setBatchNo(batchNo);
            }
            StkInventory inventory = null;
            if (entry.getKcNo() != null) {
                inventory = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
            }
            if (inventory == null && warehouseId != null && StringUtils.isNotEmpty(batchNo) && entry.getMaterialId() != null) {
                inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndMaterialIdAndWarehouse(batchNo, entry.getMaterialId(), warehouseId);
            }
            if (profitQty.compareTo(BigDecimal.ZERO) < 0) {
                // 盘亏
                BigDecimal currentQtyLoss = inventory != null ? inventory.getQty() : null;
                if (inventory == null) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。批次：" + batchNo + " 无库存可扣。");
                }
                // 应有当前库存 = 盘点数量 - 盈亏数量（盈亏为负，即 盘点 - 负数 = 盘点 + |盈亏| = 账面）
                BigDecimal expectedCurrent = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
                boolean qtyMatch = expectedCurrent != null && currentQtyLoss != null && isQtyEqual(currentQtyLoss, expectedCurrent);
                log.info("[盈亏审核-盘亏] kcNo={} batchNo={} | 盘点数量(stockQty)={} | 盈亏数量(profitQty)={} | 应有库存(盘点-盈亏)={} | 当前库存(DB)={} | 是否一致={}",
                    entry.getKcNo(), batchNo, stockQty, profitQty, expectedCurrent, currentQtyLoss, qtyMatch);
                if (expectedCurrent == null || currentQtyLoss == null || !qtyMatch) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。批次：" + batchNo + " 当前库存与账面数量不一致。");
                }
                BigDecimal absQty = profitQty.abs();
                BigDecimal newQty = inventory.getQty().subtract(absQty);
                inventory.setQty(newQty);
                if (inventory.getUnitPrice() != null) {
                    inventory.setAmt(newQty.multiply(inventory.getUnitPrice()));
                } else {
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
                flow.setLx("PK");
                flow.setKcNo(inventory.getId());
                flow.setFlowTime(now);
                flow.setDelFlag(0);
                flow.setCreateTime(now);
                flow.setCreateBy(username);
                hcCkFlowMapper.insertHcCkFlow(flow);
            } else {
                // 盘盈
                BigDecimal currentQty = inventory != null ? inventory.getQty() : null;
                // 应有当前库存 = 盘点数量 - 盈亏数量（盈亏为正，即 盘点 - 正数 = 盘前库存）
                BigDecimal expectedCurrentProfit = (stockQty != null && profitQty != null) ? stockQty.subtract(profitQty) : bookQty;
                boolean qtyMatchProfit = inventory == null || expectedCurrentProfit == null || currentQty == null
                    || isQtyEqual(currentQty, expectedCurrentProfit);
                log.info("[盈亏审核-盘盈] kcNo={} batchNo={} | 盘点数量(stockQty)={} | 盈亏数量(profitQty)={} | 应有库存(盘点-盈亏)={} | 当前库存(DB)={} | 是否一致={}",
                    entry.getKcNo(), batchNo, stockQty, profitQty, expectedCurrentProfit, currentQty, qtyMatchProfit);
                if (inventory != null && expectedCurrentProfit != null && currentQty != null && !isQtyEqual(currentQty, expectedCurrentProfit)) {
                    throw new ServiceException("库存已变动，请重做盘点和盈亏处理。批次：" + batchNo + " 当前库存与账面数量不一致。");
                }
                StkBatch stkBatch = stkBatchMapper.selectByBatchNo(batchNo);
                if (stkBatch == null) {
                    stkBatch = buildStkBatchForProfit(bill, entry);
                    stkBatchMapper.insertStkBatch(stkBatch);
                }
                BigDecimal addQty = profitQty;
                BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal addAmt = addQty.multiply(unitPrice);
                if (inventory == null) {
                    inventory = new StkInventory();
                    inventory.setBatchNo(batchNo);
                    inventory.setBatchId(stkBatch.getId());
                    inventory.setMaterialId(entry.getMaterialId());
                    inventory.setWarehouseId(warehouseId);
                    inventory.setQty(addQty);
                    inventory.setUnitPrice(unitPrice);
                    inventory.setAmt(addAmt);
                    inventory.setMaterialDate(now);
                    inventory.setWarehouseDate(now);
                    inventory.setBeginTime(entry.getBeginTime());
                    inventory.setEndTime(entry.getEndTime());
                    inventory.setCreateTime(now);
                    inventory.setCreateBy(username);
                    stkInventoryMapper.insertStkInventory(inventory);
                } else {
                    BigDecimal newQty = inventory.getQty().add(addQty);
                    inventory.setQty(newQty);
                    inventory.setAmt(newQty.multiply(unitPrice));
                    inventory.setUpdateTime(now);
                    inventory.setUpdateBy(username);
                    stkInventoryMapper.updateStkInventory(inventory);
                }

                HcCkFlow flow = new HcCkFlow();
                flow.setBillId(bill.getId());
                flow.setEntryId(entry.getId());
                flow.setWarehouseId(warehouseId);
                flow.setMaterialId(entry.getMaterialId());
                flow.setBatchNo(batchNo);
                flow.setBatchNumber(entry.getBatchNumber());
                flow.setQty(addQty);
                flow.setUnitPrice(unitPrice);
                flow.setAmt(addAmt);
                flow.setBeginTime(entry.getBeginTime());
                flow.setEndTime(entry.getEndTime());
                flow.setLx("PY");
                flow.setKcNo(inventory.getId());
                flow.setFlowTime(now);
                flow.setDelFlag(0);
                flow.setCreateTime(now);
                flow.setCreateBy(username);
                hcCkFlowMapper.insertHcCkFlow(flow);
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
        String user = SecurityUtils.getUsername();
        for (StkIoProfitLossEntry e : list) {
            e.setParenId(stkIoProfitLoss.getId());
            e.setDelFlag(0);
            e.setCreateTime(now);
            e.setCreateBy(user);
            e.setUpdateTime(now);
            e.setUpdateBy(user);
        }
        stkIoProfitLossMapper.batchStkIoProfitLossEntry(list);
    }

    private String generateBillNo() {
        String prefix = "PL";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoProfitLossMapper.selectMaxBillNo(prefix + date);
        return FillRuleUtil.getNumber(prefix, maxNum, date);
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

    private StkBatch buildStkBatchForProfit(StkIoProfitLoss bill, StkIoProfitLossEntry entry) {
        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setBatchNumber(entry.getBatchNumber());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());
        b.setUnitPrice(entry.getUnitPrice());
        b.setBillId(bill.getId());
        b.setBillNo(bill.getBillNo());
        b.setEntryId(entry.getId());
        b.setBatchSource(BATCH_SOURCE_PROFIT);
        Date now = new Date();
        String username = SecurityUtils.getUsername();
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
