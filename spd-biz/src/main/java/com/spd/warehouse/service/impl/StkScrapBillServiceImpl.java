package com.spd.warehouse.service.impl;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkScrapBill;
import com.spd.warehouse.domain.StkScrapBillEntry;
import com.spd.warehouse.domain.StkScrapQueryVo;
import com.spd.warehouse.mapper.HcCkFlowMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkScrapBillMapper;
import com.spd.warehouse.service.IStkScrapBillService;
import com.spd.warehouse.utils.InventoryMaterialSnapshotHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库房报废单 Service
 * 单号：BS- + yyyyMMdd + 5位流水（对齐入库 RK / 调价 TJ- 规则）
 * 审核通过时按明细 inventoryId 扣减仓库库存，并写仓库流水 lx=BS。
 */
@Service
public class StkScrapBillServiceImpl implements IStkScrapBillService {

    private static final String BILL_PREFIX = "BS-";
    private static final String FLOW_LX_SCRAP = "BS";
    private static final String FLOW_ORIGIN_SCRAP = "库房报损";
    private static final int BILL_NO_RETRY = 5;

    @Autowired
    private StkScrapBillMapper stkScrapBillMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;

    @Override
    public StkScrapBill selectStkScrapBillById(Long id) {
        StkScrapBill bill = stkScrapBillMapper.selectStkScrapBillById(id);
        if (bill == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(bill.getTenantId());
        bill.setEntryList(stkScrapBillMapper.selectEntryListByParentId(id));
        return bill;
    }

    @Override
    public List<StkScrapQueryVo> selectScrapDetailList(StkScrapQueryVo query) {
        return stkScrapBillMapper.selectScrapDetailList(query);
    }

    @Override
    public TotalInfo selectScrapDetailTotal(StkScrapQueryVo query) {
        return stkScrapBillMapper.selectScrapDetailTotal(query);
    }

    @Override
    public List<StkScrapQueryVo> selectScrapSummaryList(StkScrapQueryVo query) {
        return stkScrapBillMapper.selectScrapSummaryList(query);
    }

    @Override
    public TotalInfo selectScrapSummaryTotal(StkScrapQueryVo query) {
        return stkScrapBillMapper.selectScrapSummaryTotal(query);
    }

    @Override
    public List<StkScrapBill> selectStkScrapBillList(StkScrapBill query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId())
            && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkScrapBillMapper.selectStkScrapBillList(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StkScrapBill insertStkScrapBill(StkScrapBill bill) {
        validateForSave(bill);
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        String tenantId = StringUtils.isNotEmpty(bill.getTenantId())
            ? bill.getTenantId()
            : SecurityUtils.requiredScopedTenantIdForSql();

        bill.setTenantId(tenantId);
        bill.setDelFlag("0");
        bill.setBillStatus("0");
        bill.setCreateBy(uid);
        bill.setCreateTime(now);
        if (bill.getBillDate() == null) {
            bill.setBillDate(now);
        }
        recalcTotalAmount(bill);

        int rows = 0;
        DuplicateKeyException lastDup = null;
        for (int i = 0; i < BILL_NO_RETRY; i++) {
            bill.setBillNo(nextBillNo());
            try {
                rows = stkScrapBillMapper.insertStkScrapBill(bill);
                lastDup = null;
                break;
            } catch (DuplicateKeyException ex) {
                lastDup = ex;
            }
        }
        if (lastDup != null || rows <= 0 || bill.getId() == null) {
            throw new ServiceException("报废单号生成冲突，请重试保存");
        }

        insertEntries(bill, uid, now, tenantId);
        return bill;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateStkScrapBill(StkScrapBill bill) {
        if (bill == null || bill.getId() == null) {
            throw new ServiceException("报废单不存在");
        }
        StkScrapBill existing = stkScrapBillMapper.selectStkScrapBillById(bill.getId());
        if (existing == null) {
            throw new ServiceException("报废单不存在");
        }
        SecurityUtils.ensureTenantAccess(existing.getTenantId());
        if (!"0".equals(existing.getBillStatus())) {
            throw new ServiceException("仅未提交的报废单可修改");
        }
        validateForSave(bill);

        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        bill.setUpdateBy(uid);
        bill.setUpdateTime(now);
        recalcTotalAmount(bill);
        int rows = stkScrapBillMapper.updateStkScrapBill(bill);

        stkScrapBillMapper.deleteEntryByParentId(bill.getId(), uid);
        bill.setBillNo(existing.getBillNo());
        insertEntries(bill, uid, now, existing.getTenantId());
        return rows;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int submitStkScrapBillByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要提交的报废单");
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        int count = 0;
        for (Long id : ids) {
            StkScrapBill existing = stkScrapBillMapper.selectStkScrapBillById(id);
            if (existing == null) {
                throw new ServiceException("报废单不存在");
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if (!"0".equals(String.valueOf(existing.getBillStatus()))) {
                throw new ServiceException("只能提交未提交状态的单据：" + existing.getBillNo());
            }
            StkScrapBill upd = new StkScrapBill();
            upd.setId(id);
            upd.setBillStatus("1");
            upd.setUpdateBy(uid);
            upd.setUpdateTime(now);
            count += stkScrapBillMapper.updateBillStatus(upd);
        }
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int auditStkScrapBillByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要审核的报损单");
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        int count = 0;
        for (Long id : ids) {
            StkScrapBill existing = stkScrapBillMapper.selectStkScrapBillById(id);
            if (existing == null) {
                throw new ServiceException("报损单不存在");
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if (!"1".equals(String.valueOf(existing.getBillStatus()))) {
                throw new ServiceException("只能审核未审核状态的单据：" + existing.getBillNo());
            }
            List<StkScrapBillEntry> entries = stkScrapBillMapper.selectEntryListByParentId(id);
            if (entries == null || entries.isEmpty()) {
                throw new ServiceException("报损单无明细，无法审核：" + existing.getBillNo());
            }
            for (StkScrapBillEntry entry : entries) {
                deductInventoryOnAudit(existing, entry, uid, now);
            }
            StkScrapBill upd = new StkScrapBill();
            upd.setId(id);
            upd.setBillStatus("2");
            upd.setAuditBy(uid);
            upd.setAuditDate(now);
            upd.setUpdateBy(uid);
            upd.setUpdateTime(now);
            count += stkScrapBillMapper.updateBillStatus(upd);
        }
        return count;
    }

    /**
     * 审核扣减仓库库存并记流水；明细须关联库存行 inventoryId。
     */
    private void deductInventoryOnAudit(StkScrapBill bill, StkScrapBillEntry entry, String uid, Date now) {
        if (entry == null || entry.getMaterialId() == null) {
            throw new ServiceException("报损明细耗材不能为空");
        }
        BigDecimal scrapQty = entry.getQty();
        if (scrapQty == null || scrapQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("报损数量必须大于0");
        }
        if (entry.getInventoryId() == null) {
            throw new ServiceException("报损明细未关联库存行，无法扣减。物料："
                + (StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : entry.getMaterialId()));
        }
        StkInventory inventory = stkInventoryMapper.selectStkInventoryById(entry.getInventoryId());
        if (inventory == null) {
            throw new ServiceException("库存行不存在或已删除，无法报损。inventoryId=" + entry.getInventoryId());
        }
        if (bill.getWarehouseId() != null && inventory.getWarehouseId() != null
            && !bill.getWarehouseId().equals(inventory.getWarehouseId())) {
            throw new ServiceException("报损明细库存仓库与单据仓库不一致：" + entry.getMaterialCode());
        }
        if (inventory.getMaterialId() != null && !inventory.getMaterialId().equals(entry.getMaterialId())) {
            throw new ServiceException("报损明细耗材与库存行不一致：" + entry.getMaterialCode());
        }
        BigDecimal currentQty = inventory.getQty() == null ? BigDecimal.ZERO : inventory.getQty();
        if (currentQty.compareTo(scrapQty) < 0) {
            throw new ServiceException(String.format(
                "库存不足，无法报损。物料 %s，报损数量 %s，当前库存 %s",
                StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : entry.getMaterialId(),
                scrapQty.stripTrailingZeros().toPlainString(),
                currentQty.stripTrailingZeros().toPlainString()));
        }
        int decRows = stkInventoryMapper.decreaseStkInventoryQty(inventory.getId(), scrapQty, uid);
        if (decRows == 0) {
            throw new ServiceException("库存扣减失败（可能并发不足），请刷新后重试。物料："
                + (StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : entry.getMaterialId()));
        }

        BigDecimal unitPrice = entry.getPrice() != null ? entry.getPrice() : inventory.getUnitPrice();
        BigDecimal amt = entry.getAmount();
        if (amt == null && unitPrice != null) {
            amt = scrapQty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
        }
        HcCkFlow flow = new HcCkFlow();
        flow.setBillId(bill.getId());
        flow.setEntryId(entry.getId());
        flow.setWarehouseId(bill.getWarehouseId() != null ? bill.getWarehouseId() : inventory.getWarehouseId());
        flow.setMaterialId(entry.getMaterialId());
        flow.setBatchNo(StringUtils.isNotEmpty(entry.getBatchNo()) ? entry.getBatchNo() : inventory.getBatchNo());
        flow.setBatchNumber(inventory.getBatchNumber());
        flow.setQty(scrapQty);
        flow.setUnitPrice(unitPrice);
        flow.setAmt(amt);
        flow.setBeginTime(entry.getProduceDate() != null ? entry.getProduceDate() : inventory.getBeginTime());
        flow.setEndTime(entry.getExpiryDate() != null ? entry.getExpiryDate() : inventory.getEndTime());
        flow.setSupplierId(inventory.getSupplierId());
        flow.setFactoryId(inventory.getFactoryId());
        flow.setMainBarcode(inventory.getMainBarcode());
        flow.setSubBarcode(inventory.getSubBarcode());
        flow.setLx(FLOW_LX_SCRAP);
        flow.setBatchId(inventory.getBatchId());
        flow.setOriginBusinessType(FLOW_ORIGIN_SCRAP);
        flow.setKcNo(inventory.getId());
        flow.setFlowTime(now);
        flow.setDelFlag(0);
        flow.setCreateTime(now);
        flow.setCreateBy(uid);
        flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
        flow.setBillNo(bill.getBillNo());
        flow.setMaterialCode(entry.getMaterialCode());
        flow.setMaterialName(entry.getMaterialName());
        InventoryMaterialSnapshotHelper.applyHcCkFlowWarehouseSnapshots(
            flow, flow.getWarehouseId(), flow.getSupplierId(), null);
        InventoryMaterialSnapshotHelper.applyHcCkFlowNumericStrMirrors(flow);
        hcCkFlowMapper.insertHcCkFlow(flow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteStkScrapBillByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要删除的报废单");
        }
        String uid = SecurityUtils.getUserIdStr();
        int count = 0;
        for (Long id : ids) {
            StkScrapBill existing = stkScrapBillMapper.selectStkScrapBillById(id);
            if (existing == null) {
                continue;
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if ("2".equals(existing.getBillStatus())) {
                throw new ServiceException("已审核的报废单不可删除：" + existing.getBillNo());
            }
            stkScrapBillMapper.deleteEntryByParentId(id, uid);
            count += stkScrapBillMapper.deleteStkScrapBillById(id, uid);
        }
        return count;
    }

    private static final Object BS_BILL_NO_LOCK = new Object();

    private String nextBillNo() {
        synchronized (BS_BILL_NO_LOCK) {
            String date = FillRuleUtil.getDateNum();
            String maxNum = stkScrapBillMapper.selectMaxBillNo(date);
            return FillRuleUtil.getNumber(BILL_PREFIX, maxNum, date);
        }
    }

    private void validateForSave(StkScrapBill bill) {
        if (bill == null) {
            throw new ServiceException("报废单数据不能为空");
        }
        if (bill.getWarehouseId() == null) {
            throw new ServiceException("请选择仓库");
        }
        List<StkScrapBillEntry> entries = bill.getEntryList();
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("请添加报废明细");
        }
        for (StkScrapBillEntry e : entries) {
            if (e == null || e.getMaterialId() == null) {
                throw new ServiceException("明细耗材不能为空");
            }
            if (e.getInventoryId() == null) {
                throw new ServiceException("请从库存选择明细后再报损（须关联库存行）");
            }
            if (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("报废数量必须大于0");
            }
        }
    }

    private void recalcTotalAmount(StkScrapBill bill) {
        BigDecimal sum = BigDecimal.ZERO;
        boolean has = false;
        for (StkScrapBillEntry e : bill.getEntryList()) {
            if (e == null) {
                continue;
            }
            if (e.getAmount() == null && e.getQty() != null && e.getPrice() != null) {
                e.setAmount(e.getQty().multiply(e.getPrice()).setScale(2, RoundingMode.HALF_UP));
            }
            if (e.getAmount() != null) {
                sum = sum.add(e.getAmount());
                has = true;
            }
        }
        bill.setTotalAmount(has ? sum : null);
    }

    private void insertEntries(StkScrapBill bill, String uid, Date now, String tenantId) {
        List<StkScrapBillEntry> src = bill.getEntryList();
        List<StkScrapBillEntry> list = new ArrayList<>();
        int line = 1;
        for (StkScrapBillEntry e : src) {
            if (e == null || e.getMaterialId() == null) {
                continue;
            }
            e.setId(null);
            e.setParentId(bill.getId());
            e.setBillNo(bill.getBillNo());
            e.setLineNo(line++);
            e.setDelFlag("0");
            e.setTenantId(tenantId);
            e.setCreateBy(uid);
            e.setCreateTime(now);
            list.add(e);
        }
        if (list.isEmpty()) {
            throw new ServiceException("请添加报废明细");
        }
        stkScrapBillMapper.batchInsertEntry(list);
    }
}
