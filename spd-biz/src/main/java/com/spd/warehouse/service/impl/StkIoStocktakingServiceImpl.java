package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.department.dto.StocktakingEntryCountedDto;
import com.spd.department.dto.StocktakingEntryQtyPatchDto;
import com.spd.department.dto.StocktakingPatchSaveDto;
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkIoStocktakingMapper;
import com.spd.warehouse.service.IStkIoStocktakingService;
import com.spd.warehouse.utils.StocktakingConcurrencyUtil;

/**
 * 盘点Service业务层处理
 *
 * @author spd
 * @date 2024-06-27
 */
@Service
public class StkIoStocktakingServiceImpl implements IStkIoStocktakingService
{
    /** 仓库盘点单业务类型（与前端 stock_type=501 一致） */
    private static final int STOCK_TYPE_WH_STOCKTAKING = 501;

    @Autowired
    private StkIoStocktakingMapper stkIoStocktakingMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询盘点
     *
     * @param id 盘点主键
     * @return 盘点
     */
    @Override
    public StkIoStocktaking selectStkIoStocktakingById(Long id)
    {
        StkIoStocktaking stk = stkIoStocktakingMapper.selectStkIoStocktakingById(id);
        if (stk != null) {
            SecurityUtils.ensureTenantAccess(stk.getTenantId());
        }
        return stk;
    }

    /**
     * 查询盘点列表
     *
     * @param stkIoStocktaking 盘点
     * @return 盘点
     */
    @Override
    public List<StkIoStocktaking> selectStkIoStocktakingList(StkIoStocktaking stkIoStocktaking)
    {
        if (stkIoStocktaking != null && StringUtils.isEmpty(stkIoStocktaking.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoStocktaking.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkIoStocktakingMapper.selectStkIoStocktakingList(stkIoStocktaking);
    }

    /**
     * 新增盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int insertStkIoStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setStockNo(getNumber());
        stkIoStocktaking.setCreateTime(DateUtils.getNowDate());
        // 制单人存用户ID（varchar），避免前端误传 nickName 到 create_by
        stkIoStocktaking.setCreateBy(SecurityUtils.getUserIdStr());
        Long opUserId = SecurityUtils.getUserId();
        if (opUserId != null) {
            stkIoStocktaking.setUserId(opUserId);
        }
        if (StringUtils.isEmpty(stkIoStocktaking.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkIoStocktaking.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(stkIoStocktaking.getUuidId())) {
            stkIoStocktaking.setUuidId(UUID7.generateUUID7());
        }
        if (stkIoStocktaking.getAuditAdjustsInventory() == null) {
            stkIoStocktaking.setAuditAdjustsInventory(0);
        }
        validateWarehouseStocktakingEntries(stkIoStocktaking);
        int rows = stkIoStocktakingMapper.insertStkIoStocktaking(stkIoStocktaking);
        insertStkIoStocktakingEntry(stkIoStocktaking);
        return rows;
    }

    //流水号
    public String getNumber() {
        String str = "PD";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoStocktakingMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int updateStkIoStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        Long parenId = stkIoStocktaking.getId();
        Date expectedClient = stkIoStocktaking.getUpdateTime();
        lockAndAssertWhStocktakingVersion(parenId, expectedClient);
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        StkIoStocktaking existing = stkIoStocktakingMapper.selectStkIoStocktakingById(stkIoStocktaking.getId());
        ensureWhStocktakingEditable(existing);
        validateWarehouseStocktakingEntries(stkIoStocktaking);
        List<StkIoStocktakingEntry> entryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        List<Long> keepIds = new ArrayList<>();
        String opUser = SecurityUtils.getUserIdStr();
        if (StringUtils.isNotNull(entryList)) {
            for (StkIoStocktakingEntry entry : entryList) {
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                boolean isNew = entry.getId() == null;
                prepareStocktakingEntry(stkIoStocktaking, entry, isNew);
                if (entry.getId() != null) {
                    stkIoStocktakingMapper.updateStkIoStocktakingEntry(entry);
                    keepIds.add(entry.getId());
                } else {
                    stkIoStocktakingMapper.insertStkIoStocktakingEntrySingle(entry);
                }
            }
            stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenIdExceptIds(parenId, keepIds, opUser);
        }
        return stkIoStocktakingMapper.updateStkIoStocktaking(stkIoStocktaking);
    }

    @Transactional
    @Override
    public StkIoStocktaking patchSaveWhStocktaking(StocktakingPatchSaveDto save)
    {
        if (save == null || save.getId() == null)
        {
            throw new ServiceException("盘点单ID不能为空。");
        }
        Long billId = save.getId();
        lockAndAssertWhStocktakingVersion(billId, save.getExpectedUpdateTime());
        StkIoStocktaking head = stkIoStocktakingMapper.selectStkIoStocktakingById(billId);
        ensureWhStocktakingEditable(head);
        SecurityUtils.ensureTenantAccess(head.getTenantId());

        Map<Long, StkIoStocktakingEntry> oldEntryMap = buildWhEntryMap(head);
        if (save.getStockDate() != null)
        {
            head.setStockDate(save.getStockDate());
        }
        if (save.getRemark() != null)
        {
            head.setRemark(save.getRemark());
        }
        if (save.getIsMonthInit() != null)
        {
            head.setIsMonthInit(save.getIsMonthInit());
        }
        head.setUpdateTime(DateUtils.getNowDate());
        head.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktakingMapper.updateStkIoStocktaking(head);

        List<StocktakingEntryQtyPatchDto> patches = save.getEntryPatches();
        if (patches != null && !patches.isEmpty())
        {
            String opUser = SecurityUtils.getUserIdStr();
            for (StocktakingEntryQtyPatchDto patch : patches)
            {
                if (patch == null || patch.getId() == null)
                {
                    continue;
                }
                StkIoStocktakingEntry old = oldEntryMap.get(patch.getId());
                if (old == null)
                {
                    throw new ServiceException("存在无法识别的历史明细，禁止保存。");
                }
                applyWhEntryQtyPatch(head, old, patch, opUser);
            }
        }
        return stkIoStocktakingMapper.selectStkIoStocktakingById(billId);
    }

    private Map<Long, StkIoStocktakingEntry> buildWhEntryMap(StkIoStocktaking bill)
    {
        Map<Long, StkIoStocktakingEntry> map = new HashMap<>();
        if (bill != null && bill.getStkIoStocktakingEntryList() != null)
        {
            for (StkIoStocktakingEntry e : bill.getStkIoStocktakingEntryList())
            {
                if (e != null && e.getId() != null)
                {
                    map.put(e.getId(), e);
                }
            }
        }
        return map;
    }

    private void ensureWhStocktakingEditable(StkIoStocktaking bill)
    {
        if (bill == null)
        {
            throw new ServiceException("盘点单不存在或无权访问。");
        }
        if (bill.getStockStatus() != null && bill.getStockStatus() == 2)
        {
            throw new ServiceException("已审核的盘点单不可修改。");
        }
        if (bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING)
        {
            throw new ServiceException("单据类型不是仓库盘点，无法保存。");
        }
    }

    private void lockAndAssertWhStocktakingVersion(Long billId, Date expectedClient)
    {
        if (billId == null)
        {
            throw new ServiceException("盘点单ID不能为空。");
        }
        StkIoStocktaking locked = stkIoStocktakingMapper.lockStkIoStocktakingHeadById(billId);
        if (locked == null)
        {
            throw new ServiceException("盘点单不存在或已删除。");
        }
        SecurityUtils.ensureTenantAccess(locked.getTenantId());
        StocktakingConcurrencyUtil.requireExpectedUpdateTime(expectedClient, locked.getUpdateTime());
    }

    private void applyWhEntryQtyPatch(StkIoStocktaking bill, StkIoStocktakingEntry old,
        StocktakingEntryQtyPatchDto patch, String opUser)
    {
        BigDecimal stockQty = patch.getStockQty();
        if (stockQty == null)
        {
            stockQty = old.getStockQty() == null ? BigDecimal.ZERO : old.getStockQty();
        }
        BigDecimal bookQty = old.getQty() == null ? BigDecimal.ZERO : old.getQty();
        if (patch.getBookQty() != null)
        {
            BigDecimal live = queryCurrentWhInventoryQty(old);
            if (patch.getBookQty().compareTo(live) != 0)
            {
                throw new ServiceException(String.format(
                    "明细[%s]账面数量与当前仓库库存不一致，请刷新后重新确认。",
                    old.getBatchNo() != null ? old.getBatchNo() : patch.getId()));
            }
            bookQty = patch.getBookQty();
        }
        if (old.getKcNo() != null && stockQty.compareTo(bookQty) > 0)
        {
            throw new ServiceException("来源于仓库库存的明细仅允许盘亏或持平，不允许盘盈。");
        }
        Integer countedFlag = patch.getCountedFlag();
        if (countedFlag != null && countedFlag != 0 && countedFlag != 1)
        {
            throw new ServiceException("已盘标志只能为 0 或 1。");
        }

        StkIoStocktakingEntry calc = new StkIoStocktakingEntry();
        calc.setQty(bookQty);
        calc.setStockQty(stockQty);
        calc.setUnitPrice(old.getUnitPrice());
        calc.setPrice(old.getPrice());
        fillProfitLossFlagWarehouse(calc);
        computeWhEntryAmountFields(calc);

        int n = stkIoStocktakingMapper.updateStocktakingEntryQtyPatch(old.getId(), STOCK_TYPE_WH_STOCKTAKING, opUser,
            bookQty, stockQty, calc.getAmt(), calc.getProfitLossFlag(), calc.getProfitQty(),
            calc.getStockAmount(), calc.getProfitAmount(), countedFlag);
        if (n == 0)
        {
            throw new ServiceException("明细保存失败（可能已审核或无权访问）。");
        }
    }

    private static void computeWhEntryAmountFields(StkIoStocktakingEntry entry)
    {
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        BigDecimal stockQty = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
        BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
        BigDecimal profitQty = stockQty.subtract(bookQty);
        entry.setProfitQty(profitQty);
        if (unitPrice == null)
        {
            entry.setAmt(BigDecimal.ZERO);
            entry.setStockAmount(BigDecimal.ZERO);
            entry.setProfitAmount(BigDecimal.ZERO);
        }
        else
        {
            entry.setAmt(stockQty.multiply(unitPrice));
            entry.setStockAmount(entry.getAmt());
            entry.setProfitAmount(profitQty.multiply(unitPrice));
        }
    }

    /**
     * 批量删除盘点
     *
     * @param ids 需要删除的盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoStocktakingByIds(Long[] ids)
    {
        for (Long id : ids) {
            StkIoStocktaking existing = stkIoStocktakingMapper.selectStkIoStocktakingById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenIds(ids, deleteBy);
        return stkIoStocktakingMapper.deleteStkIoStocktakingByIds(ids, deleteBy);
    }

    /**
     * 删除盘点信息
     *
     * @param id 盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoStocktakingById(Long id)
    {
        StkIoStocktaking existing = stkIoStocktakingMapper.selectStkIoStocktakingById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenId(id, deleteBy);
        return stkIoStocktakingMapper.deleteStkIoStocktakingById(id, deleteBy);
    }

    /**
     * 审核盘点信息
     * @param id
     * @param adjustList 明细账面与仓库实物不一致时，用户逐条确认后的盘点数量
     * @return
     */
    @Transactional
    @Override
    public int auditStkIoBill(String id, List<StocktakingQtyAdjustDto> adjustList, Date expectedUpdateTime) {
        Long billId = Long.valueOf(id);
        lockAndAssertWhStocktakingVersion(billId, expectedUpdateTime);
        StkIoStocktaking stkIoStocktaking = stkIoStocktakingMapper.selectStkIoStocktakingById(billId);
        if (stkIoStocktaking == null) {
            throw new ServiceException(String.format("盘点业务ID：%s，不存在!", id));
        }

        Integer stockType = stkIoStocktaking.getStockType();
        if (stockType != null && stockType == STOCK_TYPE_WH_STOCKTAKING) {
            normalizeWhStocktakingQtyToLiveBeforeAudit(stkIoStocktaking);
            applyWhQtyAdjustmentsIfNeeded(stkIoStocktaking, adjustList);
            List<StocktakingQtyMismatchVo> mismatches = buildWhQtyMismatches(stkIoStocktaking);
            if (!mismatches.isEmpty()) {
                throw new ServiceException("盘点明细库存数量与当前仓库账面库存不一致，请先逐条确认后再审核。");
            }
        }

        // 盘点单审核仅更新审核状态，不再改库存；库存变动由盈亏单审核完成
        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());

        return stkIoStocktakingMapper.updateStkIoStocktaking(stkIoStocktaking);
    }

    @Override
    public List<StocktakingQtyMismatchVo> checkWhStocktakingQtyMismatch(String id) {
        StkIoStocktaking bill = stkIoStocktakingMapper.selectStkIoStocktakingById(Long.valueOf(id));
        if (bill == null) {
            throw new ServiceException(String.format("盘点业务ID：%s，不存在!", id));
        }
        return buildWhQtyMismatches(bill);
    }

    private void normalizeWhStocktakingQtyToLiveBeforeAudit(StkIoStocktaking bill) {
        if (bill == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING
            || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getId() == null || entry.getKcNo() == null) {
                continue;
            }
            BigDecimal live = queryCurrentWhInventoryQty(entry);
            if (live == null) {
                live = BigDecimal.ZERO;
            }
            BigDecimal book = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            boolean changed = false;
            if (book.compareTo(live) != 0) {
                entry.setQty(live);
                changed = true;
            }
            BigDecimal bookAfter = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            BigDecimal sq = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
            // 来源于仓库库存的明细不允许盘盈：账面已对齐为现库存后，实盘不得大于账面
            if (sq.compareTo(bookAfter) > 0) {
                entry.setStockQty(bookAfter);
                changed = true;
            }
            if (changed) {
                fillProfitLossFlagWarehouse(entry);
                computeWhEntryAmountFields(entry);
                entry.setUpdateBy(SecurityUtils.getUserIdStr());
                entry.setUpdateTime(DateUtils.getNowDate());
                if (StringUtils.isNotEmpty(bill.getStockNo())) {
                    entry.setStockNo(bill.getStockNo());
                }
                stkIoStocktakingMapper.updateStkIoStocktakingEntry(entry);
            }
        }
    }

    private void applyWhQtyAdjustmentsIfNeeded(StkIoStocktaking bill, List<StocktakingQtyAdjustDto> adjustList) {
        if (bill == null || adjustList == null || adjustList.isEmpty() || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        Map<Long, StocktakingQtyAdjustDto> adjustMap = adjustList.stream()
            .filter(a -> a != null && a.getEntryId() != null)
            .collect(Collectors.toMap(StocktakingQtyAdjustDto::getEntryId, a -> a, (a, b) -> b));
        if (adjustMap.isEmpty()) {
            return;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getId() == null) {
                continue;
            }
            StocktakingQtyAdjustDto adjust = adjustMap.get(entry.getId());
            if (adjust == null) {
                continue;
            }
            BigDecimal currentQty = queryCurrentWhInventoryQty(entry);
            if (currentQty == null) {
                currentQty = BigDecimal.ZERO;
            }
            entry.setQty(currentQty);
            BigDecimal sq = adjust.getStockQty() == null ? currentQty : adjust.getStockQty();
            if (entry.getKcNo() != null && sq.compareTo(currentQty) > 0) {
                sq = currentQty;
            }
            entry.setStockQty(sq);
            fillProfitLossFlagWarehouse(entry);
            computeWhEntryAmountFields(entry);
            if (StringUtils.isNotEmpty(bill.getStockNo())) {
                entry.setStockNo(bill.getStockNo());
            }
            stkIoStocktakingMapper.updateStkIoStocktakingEntry(entry);
        }
    }

    private List<StocktakingQtyMismatchVo> buildWhQtyMismatches(StkIoStocktaking bill) {
        List<StocktakingQtyMismatchVo> out = new ArrayList<>();
        if (bill == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING
            || bill.getStkIoStocktakingEntryList() == null) {
            return out;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getId() == null || entry.getKcNo() == null) {
                continue;
            }
            BigDecimal detailQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            BigDecimal currentQty = queryCurrentWhInventoryQty(entry);
            if (currentQty == null) {
                currentQty = BigDecimal.ZERO;
            }
            if (detailQty.compareTo(currentQty) != 0) {
                StocktakingQtyMismatchVo r = new StocktakingQtyMismatchVo();
                r.setEntryId(entry.getId());
                r.setMaterialName(entry.getMaterial() != null ? entry.getMaterial().getName() : null);
                r.setBatchNo(entry.getBatchNo());
                r.setDetailQty(detailQty);
                r.setCurrentQty(currentQty);
                r.setStockQty(entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty());
                out.add(r);
            }
        }
        return out;
    }

    private BigDecimal queryCurrentWhInventoryQty(StkIoStocktakingEntry entry) {
        if (entry == null || entry.getKcNo() == null) {
            return BigDecimal.ZERO;
        }
        StkInventory inv = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
        return inv == null || inv.getQty() == null ? BigDecimal.ZERO : inv.getQty();
    }

    @Override
    public List<StkIoStocktaking> getMonthHandleDataList(String beginDate, String endDate) {
        return stkIoStocktakingMapper.getMonthHandleDataList(beginDate,endDate);
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
                prepareStocktakingEntry(stkIoStocktaking, stkIoStocktakingEntry, true);
                list.add(stkIoStocktakingEntry);
            }
            if (list.size() > 0)
            {
                stkIoStocktakingMapper.batchStkIoStocktakingEntry(list);
            }
        }
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }

    /**
     * 保存前：明细 UUID、租户、审计字段；有 kc_no 时回填账面批次快照（供盘亏/盈亏追溯）。
     */
    private void prepareStocktakingEntry(StkIoStocktaking parent, StkIoStocktakingEntry entry, boolean newLine) {
        enrichOrigBatchFromInventory(entry);
        if (StringUtils.isEmpty(entry.getEntryUuid())) {
            entry.setEntryUuid(UUID7.generateUUID7());
        }
        String tid = StringUtils.isNotEmpty(parent.getTenantId()) ? parent.getTenantId() : SecurityUtils.getCustomerId();
        entry.setTenantId(tid);
        if (entry.getDelFlag() == null) {
            entry.setDelFlag(0);
        }
        Date now = DateUtils.getNowDate();
        String user = SecurityUtils.getUserIdStr();
        if (newLine) {
            entry.setCreateTime(now);
            entry.setCreateBy(user);
        }
        entry.setUpdateTime(now);
        entry.setUpdateBy(user);
        fillStocktakingEntryRefStrings(parent, entry);
        if (StringUtils.isNotEmpty(parent.getStockNo())) {
            entry.setStockNo(parent.getStockNo());
        }
    }

    /**
     * 仓库盘点（stock_type=501）：有 kc_no 的明细为账面库存行，仅允许盘亏/平；无 kc_no 为盘盈（字典），不要求手工选归属仓库。
     */
    private void validateWarehouseStocktakingEntries(StkIoStocktaking bill) {
        if (bill == null || bill.getStockType() == null || bill.getStockType() != 501
            || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        if (bill.getWarehouseId() == null) {
            throw new ServiceException("仓库盘点必须选择仓库。");
        }
        Set<String> kcSeen = new HashSet<>();
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null) {
                continue;
            }
            if (entry.getMaterialId() == null) {
                throw new ServiceException("盘点明细缺少耗材。");
            }
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (material == null) {
                throw new ServiceException(String.format("耗材ID：%s，产品档案不存在。", entry.getMaterialId()));
            }
            if (entry.getKcNo() != null) {
                String kck = String.valueOf(entry.getKcNo());
                if (kcSeen.contains(kck)) {
                    throw new ServiceException("同一仓库库存明细不允许重复加入盘点单。");
                }
                kcSeen.add(kck);
                if (entry.getStockQty() == null) {
                    entry.setStockQty(entry.getQty());
                }
                BigDecimal book = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
                if (entry.getStockQty().compareTo(book) > 0) {
                    throw new ServiceException("来源于仓库库存的明细仅允许盘亏或持平，不允许盘盈。");
                }
            } else {
                if (entry.getStockQty() == null || entry.getStockQty().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException("新增盘盈明细必须填写大于 0 的盘点数量。");
                }
                if (StringUtils.isEmpty(entry.getBatchNumber()) || entry.getEndTime() == null) {
                    throw new ServiceException("新增盘盈明细必须录入批号、有效期。");
                }
                if (material.getSupplierId() != null) {
                    entry.setSupplierId(material.getSupplierId());
                }
                if (entry.getSupplierId() == null) {
                    throw new ServiceException(String.format(
                        "耗材[%s]：产品档案未维护供应商时，请在新增盘盈明细中选择供应商后再保存。", material.getName()));
                }
                if (entry.getReturnWarehouseId() == null) {
                    Long dw = material.getDefaultWarehouseId() != null ? material.getDefaultWarehouseId() : material.getWarehouseId();
                    entry.setReturnWarehouseId(dw);
                }
                if (entry.getQty() == null) {
                    entry.setQty(BigDecimal.ZERO);
                }
                BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                if (unitPrice == null) {
                    unitPrice = material.getPrice() != null ? material.getPrice() : material.getSalePrice();
                }
                entry.setUnitPrice(unitPrice);
                entry.setPrice(unitPrice);
            }
            BigDecimal upAll = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
            if (upAll != null && entry.getStockQty() != null) {
                entry.setAmt(entry.getStockQty().multiply(upAll));
            }
            fillProfitLossFlagWarehouse(entry);
        }
    }

    private void fillProfitLossFlagWarehouse(StkIoStocktakingEntry entry) {
        BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
        BigDecimal stockQty = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
        int cmp = stockQty.compareTo(bookQty);
        if (cmp > 0) {
            entry.setProfitLossFlag("PROFIT");
        } else if (cmp < 0) {
            entry.setProfitLossFlag("LOSS");
        } else {
            entry.setProfitLossFlag("EQUAL");
        }
    }

    private void fillStocktakingEntryRefStrings(StkIoStocktaking bill, StkIoStocktakingEntry entry) {
        if (entry == null) {
            return;
        }
        if (entry.getKcNo() != null) {
            entry.setKcNoStr(String.valueOf(entry.getKcNo()));
        } else {
            entry.setKcNoStr(null);
        }
        if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
            entry.setDepInventoryId(String.valueOf(entry.getDepInventoryId()).trim());
        }
        Long wh = bill.getWarehouseId() != null ? bill.getWarehouseId() : entry.getReturnWarehouseId();
        entry.setWarehouseIdStr(wh != null ? String.valueOf(wh) : null);
        entry.setDepartmentIdStr(bill.getDepartmentId() != null ? String.valueOf(bill.getDepartmentId()) : null);
        entry.setSupplierIdStr(entry.getSupplierId() != null ? String.valueOf(entry.getSupplierId()) : null);
    }

    private void enrichOrigBatchFromInventory(StkIoStocktakingEntry entry) {
        if (entry.getKcNo() == null) {
            return;
        }
        if (entry.getOrigBatchId() != null && StringUtils.isNotEmpty(entry.getOrigBatchNoSnapshot())) {
            return;
        }
        StkInventory inv = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
        if (inv == null) {
            return;
        }
        if (entry.getOrigBatchId() == null) {
            entry.setOrigBatchId(inv.getBatchId());
        }
        if (StringUtils.isEmpty(entry.getOrigBatchNoSnapshot())) {
            entry.setOrigBatchNoSnapshot(inv.getBatchNo());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateStocktakingEntryCounted(StocktakingEntryCountedDto dto)
    {
        if (dto == null || dto.getId() == null || dto.getCountedFlag() == null)
        {
            throw new ServiceException("参数错误。");
        }
        Integer countedFlag = dto.getCountedFlag();
        if (countedFlag != 0 && countedFlag != 1)
        {
            throw new ServiceException("已盘标志只能为 0 或 1。");
        }
        Long parenId = stkIoStocktakingMapper.selectParenIdByStocktakingEntryId(dto.getId());
        if (parenId == null)
        {
            throw new ServiceException("未找到盘点明细。");
        }
        lockAndAssertWhStocktakingVersion(parenId, dto.getExpectedUpdateTime());

        BigDecimal stockQtyToPersist = dto.getStockQty();
        BigDecimal amt = null;
        String profitLossFlag = null;
        BigDecimal profitQty = null;
        BigDecimal stockAmount = null;
        BigDecimal profitAmount = null;
        if (stockQtyToPersist != null)
        {
            StkIoStocktaking bill = stkIoStocktakingMapper.selectStkIoStocktakingById(parenId);
            if (bill == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING
                || (bill.getStockStatus() != null && bill.getStockStatus() == 2))
            {
                throw new ServiceException("未找到可更新的明细（可能已审核或不属于仓库盘点）。");
            }
            StkIoStocktakingEntry entry = null;
            if (bill.getStkIoStocktakingEntryList() != null)
            {
                for (StkIoStocktakingEntry e : bill.getStkIoStocktakingEntryList())
                {
                    if (e != null && dto.getId().equals(e.getId()))
                    {
                        entry = e;
                        break;
                    }
                }
            }
            if (entry == null)
            {
                throw new ServiceException("未找到盘点明细。");
            }
            if (entry.getKcNo() != null)
            {
                BigDecimal book = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
                if (stockQtyToPersist.compareTo(book) > 0)
                {
                    throw new ServiceException("来源于仓库库存的明细仅允许盘亏或持平，不允许盘盈。");
                }
            }
            else if (stockQtyToPersist.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("新增盘盈明细必须填写大于 0 的盘点数量。");
            }
            entry.setStockQty(stockQtyToPersist);
            fillProfitLossFlagWarehouse(entry);
            profitLossFlag = entry.getProfitLossFlag();
            BigDecimal up = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
            amt = up != null ? stockQtyToPersist.multiply(up) : BigDecimal.ZERO;
            BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            profitQty = stockQtyToPersist.subtract(bookQty);
            stockAmount = amt;
            profitAmount = up != null ? profitQty.multiply(up) : BigDecimal.ZERO;
        }
        int n = stkIoStocktakingMapper.updateStocktakingEntryCountedFlag(dto.getId(), countedFlag,
            STOCK_TYPE_WH_STOCKTAKING, SecurityUtils.getUserIdStr(),
            stockQtyToPersist, amt, profitLossFlag, profitQty, stockAmount, profitAmount);
        if (n == 0)
        {
            throw new ServiceException("未找到可更新的明细（可能已审核或不属于仓库盘点）。");
        }
        return n;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StkIoStocktaking initWarehouseStocktakingFromInventory(StkIoStocktaking patch)
    {
        try
        {
            return doInitWhStocktakingFromInventory(patch);
        }
        catch (ServiceException e)
        {
            String m = e.getMessage();
            throw new ServiceException(
                "盘点初始化未保存单据。" + (StringUtils.isNotEmpty(m) ? (" " + m) : "")
                    + " 请排查仓库库存及关联耗材、供应商、批次数据后重试。");
        }
    }

    private StkIoStocktaking doInitWhStocktakingFromInventory(StkIoStocktaking patch)
    {
        if (patch == null || patch.getWarehouseId() == null)
        {
            throw new ServiceException("请先选择仓库。");
        }
        Long billId = patch.getId();
        List<StkInventory> invRows = loadWhInventoryForStocktaking(patch.getWarehouseId());
        if (invRows == null || invRows.isEmpty())
        {
            throw new ServiceException("当前仓库下没有可用的库存明细，无法生成盘点单。");
        }
        invRows.sort(Comparator
            .comparing((StkInventory i) -> i.getMaterialId() == null ? Long.MAX_VALUE : i.getMaterialId())
            .thenComparing(i -> i.getId() == null ? 0L : i.getId()));
        List<StkIoStocktakingEntry> entries = new ArrayList<>();
        for (StkInventory inv : invRows)
        {
            if (inv == null || inv.getId() == null)
            {
                continue;
            }
            entries.add(buildWhInitEntryFromInventory(inv));
        }
        if (entries.isEmpty())
        {
            throw new ServiceException("仓库库存明细无效或缺少主键，无法生成盘点明细。");
        }
        Integer stockStatus = patch.getStockStatus();
        if (billId == null)
        {
            StkIoStocktaking head = new StkIoStocktaking();
            head.setWarehouseId(patch.getWarehouseId());
            head.setDepartmentId(patch.getDepartmentId());
            head.setStockDate(patch.getStockDate() != null ? patch.getStockDate() : DateUtils.getNowDate());
            head.setRemark(patch.getRemark());
            head.setStockStatus(stockStatus != null ? stockStatus : 1);
            head.setStockType(STOCK_TYPE_WH_STOCKTAKING);
            head.setStkIoStocktakingEntryList(entries);
            insertStkIoStocktaking(head);
            return selectStkIoStocktakingById(head.getId());
        }
        lockAndAssertWhStocktakingVersion(billId, patch.getUpdateTime());
        StkIoStocktaking head = stkIoStocktakingMapper.selectStkIoStocktakingById(billId);
        if (head == null)
        {
            throw new ServiceException("盘点单不存在或无权访问。");
        }
        SecurityUtils.ensureTenantAccess(head.getTenantId());
        if (head.getStockType() == null || head.getStockType() != STOCK_TYPE_WH_STOCKTAKING)
        {
            throw new ServiceException("仅支持仓库盘点单进行盘点初始化。");
        }
        if (head.getStockStatus() != null && head.getStockStatus() == 2)
        {
            throw new ServiceException("已审核的盘点单不可进行盘点初始化。");
        }
        if (head.getStkIoStocktakingEntryList() != null && !head.getStkIoStocktakingEntryList().isEmpty())
        {
            throw new ServiceException("盘点单已有明细，请先删除后再进行盘点初始化。");
        }
        if (!Objects.equals(head.getWarehouseId(), patch.getWarehouseId()))
        {
            throw new ServiceException("仓库与当前盘点单不一致，请刷新页面后重试。");
        }
        if (patch.getStockDate() != null)
        {
            head.setStockDate(patch.getStockDate());
        }
        if (patch.getRemark() != null)
        {
            head.setRemark(patch.getRemark());
        }
        head.setStkIoStocktakingEntryList(entries);
        validateWarehouseStocktakingEntries(head);
        insertStkIoStocktakingEntry(head);
        head.setUpdateTime(DateUtils.getNowDate());
        head.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktakingMapper.updateStkIoStocktaking(head);
        return selectStkIoStocktakingById(billId);
    }

    private List<StkInventory> loadWhInventoryForStocktaking(Long warehouseId)
    {
        StkInventory q = new StkInventory();
        q.setWarehouseId(warehouseId);
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkInventoryMapper.selectStkInventoryList(q);
    }

    private StkIoStocktakingEntry buildWhInitEntryFromInventory(StkInventory inv)
    {
        StkIoStocktakingEntry e = new StkIoStocktakingEntry();
        e.setMaterialId(inv.getMaterialId());
        e.setKcNo(inv.getId());
        BigDecimal qty = inv.getQty() != null ? inv.getQty() : BigDecimal.ZERO;
        e.setQty(qty);
        e.setStockQty(qty);
        BigDecimal up = inv.getUnitPrice();
        if (up == null && inv.getMaterial() != null)
        {
            FdMaterial m = inv.getMaterial();
            up = m.getPrice() != null ? m.getPrice() : m.getSalePrice();
        }
        e.setUnitPrice(up);
        e.setPrice(up);
        if (up != null)
        {
            e.setAmt(qty.multiply(up));
        }
        else
        {
            e.setAmt(BigDecimal.ZERO);
        }
        if (StringUtils.isNotEmpty(inv.getBatchNo()))
        {
            e.setBatchNo(inv.getBatchNo().trim());
        }
        String bn = StringUtils.isNotEmpty(inv.getBatchNumber()) ? inv.getBatchNumber()
            : (StringUtils.isNotEmpty(inv.getMaterialNo()) ? inv.getMaterialNo() : "");
        e.setBatchNumber(bn != null ? bn : "");
        e.setBeginTime(inv.getBeginTime());
        e.setEndTime(inv.getEndTime());
        e.setSupplierId(inv.getSupplierId());
        e.setCountedFlag(0);
        e.setRemark("");
        e.setDelFlag(0);
        return e;
    }

    @Transactional
    @Override
    public int appendWarehouseStocktakingEntries(Long billId, List<StkIoStocktakingEntry> newEntries, Date expectedUpdateTime)
    {
        if (billId == null || newEntries == null || newEntries.isEmpty())
        {
            return 0;
        }
        lockAndAssertWhStocktakingVersion(billId, expectedUpdateTime);
        for (StkIoStocktakingEntry e : newEntries)
        {
            if (e != null && e.getId() != null)
            {
                throw new ServiceException("追加明细必须为未落库的新行（不能带明细 id）。");
            }
        }
        StkIoStocktaking head = stkIoStocktakingMapper.selectStkIoStocktakingById(billId);
        if (head == null)
        {
            throw new ServiceException("盘点单不存在或无权访问。");
        }
        SecurityUtils.ensureTenantAccess(head.getTenantId());
        if (head.getStockType() == null || head.getStockType() != STOCK_TYPE_WH_STOCKTAKING)
        {
            throw new ServiceException("仅支持仓库盘点单追加明细。");
        }
        if (head.getStockStatus() != null && head.getStockStatus() == 2)
        {
            throw new ServiceException("已审核的盘点单不可追加明细。");
        }
        List<StkIoStocktakingEntry> existing = head.getStkIoStocktakingEntryList();
        if (existing == null)
        {
            existing = Collections.emptyList();
        }
        Set<String> usedKcKeys = new HashSet<>();
        for (StkIoStocktakingEntry ex : existing)
        {
            if (ex != null && ex.getKcNo() != null)
            {
                usedKcKeys.add(String.valueOf(ex.getKcNo()).trim());
            }
        }
        List<StkIoStocktakingEntry> clean = new ArrayList<>();
        for (StkIoStocktakingEntry e : newEntries)
        {
            if (e == null)
            {
                continue;
            }
            if (e.getKcNo() != null)
            {
                String kk = String.valueOf(e.getKcNo()).trim();
                if (usedKcKeys.contains(kk))
                {
                    throw new ServiceException("同一仓库库存明细不允许重复加入盘点单。");
                }
                usedKcKeys.add(kk);
            }
            clean.add(e);
        }
        if (clean.isEmpty())
        {
            return 0;
        }
        for (StkIoStocktakingEntry entry : clean)
        {
            if (StringUtils.isEmpty(entry.getBatchNo()))
            {
                entry.setBatchNo(getBatchNumber());
            }
        }
        StkIoStocktaking slice = new StkIoStocktaking();
        slice.setId(head.getId());
        slice.setStockNo(head.getStockNo());
        slice.setWarehouseId(head.getWarehouseId());
        slice.setDepartmentId(head.getDepartmentId());
        slice.setStockType(STOCK_TYPE_WH_STOCKTAKING);
        slice.setTenantId(head.getTenantId());
        slice.setStkIoStocktakingEntryList(clean);
        validateWarehouseStocktakingEntries(slice);
        Date now = DateUtils.getNowDate();
        String opUser = SecurityUtils.getUserIdStr();
        for (StkIoStocktakingEntry entry : clean)
        {
            entry.setParenId(billId);
            if (StringUtils.isEmpty(entry.getBatchNo()))
            {
                entry.setBatchNo(getBatchNumber());
            }
            prepareStocktakingEntry(head, entry, true);
            stkIoStocktakingMapper.insertStkIoStocktakingEntrySingle(entry);
        }
        head.setUpdateTime(now);
        head.setUpdateBy(opUser);
        stkIoStocktakingMapper.updateStkIoStocktaking(head);
        return clean.size();
    }
}
