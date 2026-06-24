package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.department.dto.StocktakingEntryCountedDto;
import com.spd.department.dto.StocktakingEntryQtyPatchDto;
import com.spd.department.dto.StocktakingPatchSaveDto;
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.warehouse.domain.dto.WhStocktakingProfitImportRow;
import com.spd.warehouse.util.InitialImportDateParser;
import com.spd.warehouse.util.WhStocktakingProfitImportUtil;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.mapper.HcCkFlowMapper;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkIoStocktakingMapper;
import com.spd.warehouse.service.IStkIoStocktakingService;
import com.spd.warehouse.utils.InventoryMaterialSnapshotHelper;
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

    @Autowired
    private StkBatchMapper stkBatchMapper;

    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;

    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;

    @Autowired
    private FdSupplierMapper fdSupplierMapper;

    /**
     * 查询盘点
     *
     * @param id 盘点主键
     * @return 盘点
     */
    @Override
    public StkIoStocktaking selectStkIoStocktakingById(Long id)
    {
        StkIoStocktaking stk = stkIoStocktakingMapper.selectStkIoStocktakingHeadById(id);
        if (stk == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(stk.getTenantId());
        List<StkIoStocktakingEntry> entries = stkIoStocktakingMapper.selectStkIoStocktakingEntryListByParenId(id);
        stk.setStkIoStocktakingEntryList(entries != null ? entries : new ArrayList<>());
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
        java.util.Date now = DateUtils.getNowDate();
        stkIoStocktaking.setCreateTime(now);
        stkIoStocktaking.setUpdateTime(now);
        // 制单人存用户ID（varchar），避免前端误传 nickName 到 create_by
        stkIoStocktaking.setCreateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
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
            if (stkIoStocktaking.getStockType() != null && stkIoStocktaking.getStockType() == STOCK_TYPE_WH_STOCKTAKING) {
                stkIoStocktaking.setAuditAdjustsInventory(1);
            } else {
                stkIoStocktaking.setAuditAdjustsInventory(0);
            }
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
            Map<Long, StkInventory> invCache = buildInventoryCacheForEntries(entryList);
            for (StkIoStocktakingEntry entry : entryList) {
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                boolean isNew = entry.getId() == null;
                prepareStocktakingEntry(stkIoStocktaking, entry, isNew, invCache);
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
        StkIoStocktaking head = selectStkIoStocktakingById(billId);
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
            Map<Long, BigDecimal> liveQtyMap = buildLiveWhInventoryQtyMapForPatches(patches, oldEntryMap);
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
                applyWhEntryQtyPatch(head, old, patch, opUser, liveQtyMap);
            }
        }
        return selectStkIoStocktakingById(billId);
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
            throw new ServiceException("已审核的盘点单不可删除或变更明细。");
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
        StocktakingConcurrencyUtil.requireExpectedUpdateTime(expectedClient,
            StocktakingConcurrencyUtil.effectiveBillVersion(locked.getUpdateTime(), locked.getCreateTime()));
    }

    private void applyWhEntryQtyPatch(StkIoStocktaking bill, StkIoStocktakingEntry old,
        StocktakingEntryQtyPatchDto patch, String opUser, Map<Long, BigDecimal> liveQtyMap)
    {
        BigDecimal stockQty = patch.getStockQty();
        if (stockQty == null)
        {
            stockQty = old.getStockQty() == null ? BigDecimal.ZERO : old.getStockQty();
        }
        BigDecimal bookQty = old.getQty() == null ? BigDecimal.ZERO : old.getQty();
        if (patch.getBookQty() != null)
        {
            BigDecimal live = resolveLiveWhInventoryQty(old, liveQtyMap);
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
            calc.getStockAmount(), calc.getProfitAmount(), countedFlag,
            patch.getBatchNumber(), patch.getRemark());
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
                ensureWhStocktakingEditable(existing);
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
            ensureWhStocktakingEditable(existing);
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

        com.spd.common.utils.MasterDetailValidateUtil.assertHasActiveEntryForAudit(
            stkIoStocktaking.getStkIoStocktakingEntryList(),
            e -> e != null && com.spd.common.utils.MasterDetailValidateUtil.isNotDeletedFlag(e.getDelFlag()),
            "仓库盘点");

        Integer stockType = stkIoStocktaking.getStockType();
        if (stockType != null && stockType == STOCK_TYPE_WH_STOCKTAKING) {
            normalizeWhStocktakingQtyToLiveBeforeAudit(stkIoStocktaking);
            applyWhQtyAdjustmentsIfNeeded(stkIoStocktaking, adjustList);
            List<StocktakingQtyMismatchVo> mismatches = buildWhQtyMismatches(stkIoStocktaking);
            if (!mismatches.isEmpty()) {
                throw new ServiceException("盘点明细库存数量与当前仓库账面库存不一致，请先逐条确认后再审核。");
            }
            validateWarehouseStocktakingEntries(stkIoStocktaking);
            stkIoStocktaking.setAuditAdjustsInventory(1);
            assertWhExistingInventoryLinesNotProfit(stkIoStocktaking);
            updateWhInventory(stkIoStocktaking, stkIoStocktaking.getStkIoStocktakingEntryList());
            verifyWhProfitEntriesInventoryAndBatch(stkIoStocktaking, stkIoStocktaking.getStkIoStocktakingEntryList());
        }

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
                throwWhExistingInventoryProfitNotAllowed(entry, sq, currentQty);
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
        return resolveLiveWhInventoryQty(entry, null);
    }

    private BigDecimal resolveLiveWhInventoryQty(StkIoStocktakingEntry entry, Map<Long, BigDecimal> liveQtyMap) {
        if (entry == null || entry.getKcNo() == null) {
            return BigDecimal.ZERO;
        }
        if (liveQtyMap != null && liveQtyMap.containsKey(entry.getKcNo())) {
            BigDecimal cached = liveQtyMap.get(entry.getKcNo());
            return cached != null ? cached : BigDecimal.ZERO;
        }
        StkInventory inv = stkInventoryMapper.selectStkInventoryRowById(entry.getKcNo());
        return inv == null || inv.getQty() == null ? BigDecimal.ZERO : inv.getQty();
    }

    private Map<Long, BigDecimal> buildLiveWhInventoryQtyMapForPatches(
        List<StocktakingEntryQtyPatchDto> patches, Map<Long, StkIoStocktakingEntry> oldEntryMap)
    {
        if (patches == null || patches.isEmpty() || oldEntryMap == null || oldEntryMap.isEmpty())
        {
            return Collections.emptyMap();
        }
        Set<Long> kcIds = new HashSet<>();
        for (StocktakingEntryQtyPatchDto patch : patches)
        {
            if (patch == null || patch.getBookQty() == null || patch.getId() == null)
            {
                continue;
            }
            StkIoStocktakingEntry old = oldEntryMap.get(patch.getId());
            if (old != null && old.getKcNo() != null)
            {
                kcIds.add(old.getKcNo());
            }
        }
        return buildLiveWhInventoryQtyMap(kcIds);
    }

    private Map<Long, BigDecimal> buildLiveWhInventoryQtyMap(Set<Long> kcIds)
    {
        if (kcIds == null || kcIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<StkInventory> rows = stkInventoryMapper.selectStkInventoryRowsByIds(new ArrayList<>(kcIds));
        Map<Long, BigDecimal> map = new HashMap<>();
        if (rows != null)
        {
            for (StkInventory inv : rows)
            {
                if (inv != null && inv.getId() != null)
                {
                    map.put(inv.getId(), inv.getQty() == null ? BigDecimal.ZERO : inv.getQty());
                }
            }
        }
        return map;
    }

    private Map<Long, StkInventory> buildInventoryCacheForEntries(List<StkIoStocktakingEntry> entries)
    {
        if (entries == null || entries.isEmpty())
        {
            return Collections.emptyMap();
        }
        Set<Long> ids = new HashSet<>();
        for (StkIoStocktakingEntry entry : entries)
        {
            if (entry != null && entry.getKcNo() != null)
            {
                ids.add(entry.getKcNo());
            }
        }
        if (ids.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<StkInventory> rows = stkInventoryMapper.selectStkInventoryRowsByIds(new ArrayList<>(ids));
        Map<Long, StkInventory> map = new HashMap<>();
        if (rows != null)
        {
            for (StkInventory inv : rows)
            {
                if (inv != null && inv.getId() != null)
                {
                    map.put(inv.getId(), inv);
                }
            }
        }
        return map;
    }

    private Map<Long, FdMaterial> buildMaterialMapForStocktakingEntries(List<StkIoStocktakingEntry> entries)
    {
        if (entries == null || entries.isEmpty())
        {
            return Collections.emptyMap();
        }
        Set<Long> materialIds = new HashSet<>();
        for (StkIoStocktakingEntry entry : entries)
        {
            if (entry != null && entry.getMaterialId() != null)
            {
                materialIds.add(entry.getMaterialId());
            }
        }
        if (materialIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<FdMaterial> materials = fdMaterialMapper.selectFdMaterialByIds(new ArrayList<>(materialIds));
        Map<Long, FdMaterial> map = new HashMap<>();
        if (materials != null)
        {
            for (FdMaterial m : materials)
            {
                if (m != null && m.getId() != null)
                {
                    map.put(m.getId(), m);
                }
            }
        }
        return map;
    }

    /**
     * 账面已与仓库现库存对齐后：仓库原有库存行（有 kc_no）不允许实盘大于账面，否则审核失败。
     */
    private void assertWhExistingInventoryLinesNotProfit(StkIoStocktaking bill) {
        if (bill == null || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getKcNo() == null) {
                continue;
            }
            BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            BigDecimal stockQty = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
            if (stockQty.compareTo(bookQty) > 0) {
                throwWhExistingInventoryProfitNotAllowed(entry, stockQty, bookQty);
            }
        }
    }

    private void throwWhExistingInventoryProfitNotAllowed(StkIoStocktakingEntry entry, BigDecimal stockQty, BigDecimal bookQty) {
        String matName = entry.getMaterial() != null && StringUtils.isNotEmpty(entry.getMaterial().getName())
            ? entry.getMaterial().getName()
            : String.valueOf(entry.getMaterialId());
        String batchLabel = StringUtils.isNotEmpty(entry.getBatchNo()) ? entry.getBatchNo() : "--";
        throw new ServiceException(String.format(
            "审核失败：耗材[%s]批次[%s]为仓库原有库存，实盘数量(%s)不能大于账面数量(%s)。盘盈请使用「新增盘盈明细」。",
            matName, batchLabel, formatQtyForMessage(stockQty), formatQtyForMessage(bookQty)));
    }

    private static String formatQtyForMessage(BigDecimal qty) {
        if (qty == null) {
            return "0";
        }
        return qty.stripTrailingZeros().toPlainString();
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
            Map<Long, StkInventory> invCache = buildInventoryCacheForEntries(stkIoStocktakingEntryList);
            List<StkIoStocktakingEntry> list = new ArrayList<StkIoStocktakingEntry>();
            for (StkIoStocktakingEntry stkIoStocktakingEntry : stkIoStocktakingEntryList)
            {
                stkIoStocktakingEntry.setParenId(id);
                if(StringUtils.isEmpty(stkIoStocktakingEntry.getBatchNo())){
                    stkIoStocktakingEntry.setBatchNo(getBatchNumber());
                }
                prepareStocktakingEntry(stkIoStocktaking, stkIoStocktakingEntry, true, invCache);
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
    private void prepareStocktakingEntry(StkIoStocktaking parent, StkIoStocktakingEntry entry, boolean newLine,
        Map<Long, StkInventory> invCache)
    {
        enrichOrigBatchFromInventory(entry, invCache);
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
        if (bill == null || bill.getStockType() == null || bill.getStockType() != 501) {
            return;
        }
        com.spd.common.utils.MasterDetailValidateUtil.assertHasMaterialLine(
            bill.getStkIoStocktakingEntryList(), StkIoStocktakingEntry::getMaterialId, "仓库盘点");
        if (bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        if (bill.getWarehouseId() == null) {
            throw new ServiceException("仓库盘点必须选择仓库。");
        }
        Map<Long, FdMaterial> materialMap = buildMaterialMapForStocktakingEntries(bill.getStkIoStocktakingEntryList());
        Set<String> kcSeen = new HashSet<>();
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null) {
                continue;
            }
            if (entry.getMaterialId() == null) {
                throw new ServiceException("盘点明细缺少耗材。");
            }
            FdMaterial material = materialMap.get(entry.getMaterialId());
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
                if (entry.getSupplierId() == null && material.getSupplierId() != null) {
                    entry.setSupplierId(material.getSupplierId());
                }
                if (entry.getSupplierId() == null) {
                    throw new ServiceException(String.format(
                        "耗材[%s]：盘盈明细必须选择供应商后再保存。", material.getName()));
                }
                FdSupplier profitSupplier = fdSupplierMapper.selectFdSupplierById(entry.getSupplierId());
                if (profitSupplier == null) {
                    throw new ServiceException(String.format(
                        "耗材[%s]：盘盈明细供应商ID「%s」在系统中不存在。", material.getName(), entry.getSupplierId()));
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

    private void enrichOrigBatchFromInventory(StkIoStocktakingEntry entry, Map<Long, StkInventory> invCache) {
        if (entry.getKcNo() == null) {
            return;
        }
        if (entry.getOrigBatchId() != null && StringUtils.isNotEmpty(entry.getOrigBatchNoSnapshot())) {
            return;
        }
        StkInventory inv = invCache != null ? invCache.get(entry.getKcNo()) : null;
        if (inv == null) {
            inv = stkInventoryMapper.selectStkInventoryRowById(entry.getKcNo());
        }
        if (inv == null) {
            return;
        }
        if (entry.getOrigBatchId() == null) {
            entry.setOrigBatchId(inv.getBatchId());
        }
        if (StringUtils.isEmpty(entry.getOrigBatchNoSnapshot())) {
            entry.setOrigBatchNoSnapshot(inv.getBatchNo());
        }
        if (StringUtils.isEmpty(entry.getHisId()) && StringUtils.isNotEmpty(inv.getHisId())) {
            entry.setHisId(inv.getHisId().trim());
        }
        if (StringUtils.isEmpty(entry.getThirdPartyBatchNo()) && StringUtils.isNotEmpty(inv.getThirdPartyBatchNo())) {
            entry.setThirdPartyBatchNo(inv.getThirdPartyBatchNo().trim());
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
        StkIoStocktaking countedBill = stkIoStocktakingMapper.selectStkIoStocktakingById(parenId);
        ensureWhStocktakingEditable(countedBill);

        BigDecimal stockQtyToPersist = dto.getStockQty();
        BigDecimal amt = null;
        String profitLossFlag = null;
        BigDecimal profitQty = null;
        BigDecimal stockAmount = null;
        BigDecimal profitAmount = null;
        if (stockQtyToPersist != null)
        {
            StkIoStocktaking bill = countedBill;
            if (bill == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING)
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
        assertNoOtherPendingWhStocktaking(patch.getWarehouseId(), patch.getId());
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

    /** 同仓库仅允许一张未审核仓库盘点单（排除当前编辑中的单据） */
    private void assertNoOtherPendingWhStocktaking(Long warehouseId, Long excludeBillId)
    {
        if (warehouseId == null)
        {
            return;
        }
        List<String> stockNos = stkIoStocktakingMapper.selectPendingWhStocktakingStockNos(warehouseId, excludeBillId);
        if (stockNos != null && !stockNos.isEmpty())
        {
            throw new ServiceException("你有盘点单，单号（" + stockNos.get(0) + "）未处理！请先处理。");
        }
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
        if (StringUtils.isNotEmpty(inv.getHisId())) {
            e.setHisId(inv.getHisId().trim());
        }
        if (StringUtils.isNotEmpty(inv.getThirdPartyBatchNo())) {
            e.setThirdPartyBatchNo(inv.getThirdPartyBatchNo().trim());
        }
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
        Map<Long, StkInventory> invCache = buildInventoryCacheForEntries(clean);
        for (StkIoStocktakingEntry entry : clean)
        {
            entry.setParenId(billId);
            if (StringUtils.isEmpty(entry.getBatchNo()))
            {
                entry.setBatchNo(getBatchNumber());
            }
            prepareStocktakingEntry(head, entry, true, invCache);
            stkIoStocktakingMapper.insertStkIoStocktakingEntrySingle(entry);
        }
        head.setUpdateTime(now);
        head.setUpdateBy(opUser);
        stkIoStocktakingMapper.updateStkIoStocktaking(head);
        return clean.size();
    }

    /**
     * 仓库盘点审核：盘亏扣减原库存行；盘盈生成 stk_batch + stk_inventory 并回写 kc_no。
     */
    private void updateWhInventory(StkIoStocktaking bill, List<StkIoStocktakingEntry> entryList) {
        if (bill == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING
            || entryList == null || bill.getWarehouseId() == null) {
            return;
        }
        if (bill.getAuditAdjustsInventory() == null || bill.getAuditAdjustsInventory() != 1) {
            return;
        }
        Long warehouseId = bill.getWarehouseId();
        Date flowNow = DateUtils.getNowDate();
        String flowUser = SecurityUtils.getUserIdStr();
        Set<Long> materialIdSet = new HashSet<>();
        for (StkIoStocktakingEntry e : entryList) {
            if (e != null && e.getMaterialId() != null) {
                materialIdSet.add(e.getMaterialId());
            }
        }
        Map<Long, FdMaterial> materialById = Collections.emptyMap();
        if (!materialIdSet.isEmpty()) {
            List<FdMaterial> materials = fdMaterialMapper.selectFdMaterialByIds(new ArrayList<>(materialIdSet));
            if (materials != null && !materials.isEmpty()) {
                materialById = materials.stream()
                    .filter(Objects::nonNull)
                    .filter(m -> m.getId() != null)
                    .collect(Collectors.toMap(FdMaterial::getId, m -> m, (a, b) -> a));
            }
        }
        for (StkIoStocktakingEntry entry : entryList) {
            if (entry == null) {
                continue;
            }
            FdMaterial material = materialById.get(entry.getMaterialId());
            if (material == null) {
                throw new ServiceException(String.format("耗材ID：%s，产品档案不存在。", entry.getMaterialId()));
            }
            if (entry.getKcNo() != null) {
                processWhStocktakingLossLine(bill, entry, material, warehouseId, flowNow, flowUser);
            } else {
                processWhStocktakingProfitLine(bill, entry, material, warehouseId, flowNow, flowUser);
            }
        }
    }

    private void processWhStocktakingLossLine(StkIoStocktaking bill, StkIoStocktakingEntry entry, FdMaterial material,
        Long warehouseId, Date flowNow, String flowUser) {
        StkInventory inv = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
        if (inv == null) {
            throw new ServiceException("库存已变动，请重做盘点。库存行不存在，kcNo=" + entry.getKcNo());
        }
        if (inv.getWarehouseId() != null && !warehouseId.equals(inv.getWarehouseId())) {
            throw new ServiceException("盘点明细库存行不属于本盘点仓库。");
        }
        BigDecimal bookQty = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
        BigDecimal stockQty = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
        BigDecimal liveQty = inv.getQty() != null ? inv.getQty() : BigDecimal.ZERO;
        if (bookQty.compareTo(liveQty) != 0) {
            throw new ServiceException(String.format("库存已变动，请重做盘点。批次：%s", entry.getBatchNo()));
        }
        if (stockQty.compareTo(bookQty) > 0) {
            throw new ServiceException("来源于仓库库存的明细仅允许盘亏或持平，不允许盘盈。");
        }
        if (stockQty.compareTo(bookQty) == 0) {
            return;
        }
        inv.setQty(stockQty);
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        if (unitPrice == null) {
            unitPrice = inv.getUnitPrice();
        }
        if (unitPrice != null) {
            inv.setAmt(stockQty.multiply(unitPrice));
        } else {
            inv.setAmt(BigDecimal.ZERO);
        }
        inv.setUpdateTime(flowNow);
        inv.setUpdateBy(flowUser);
        stkInventoryMapper.updateStkInventory(inv);
        BigDecimal lossQty = bookQty.subtract(stockQty);
        insertWhStocktakingHcCkFlow(bill, entry, inv, lossQty, "PK", "仓库盘点盘亏", flowNow, flowUser, material);
        applyWhStocktakingEntryAuditFields(entry, false);
        computeWhEntryAmountFields(entry);
        if (StringUtils.isNotEmpty(bill.getStockNo())) {
            entry.setStockNo(bill.getStockNo());
        }
        stkIoStocktakingMapper.updateStkIoStocktakingEntry(entry);
    }

    private void processWhStocktakingProfitLine(StkIoStocktaking bill, StkIoStocktakingEntry entry, FdMaterial material,
        Long warehouseId, Date flowNow, String flowUser) {
        BigDecimal bookQty = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
        BigDecimal stockQty = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
        if (stockQty.compareTo(bookQty) <= 0) {
            return;
        }
        if (StringUtils.isEmpty(entry.getBatchNo())) {
            entry.setBatchNo(getBatchNumber());
        }
        entry.setReturnWarehouseId(warehouseId);
        StkBatch stkBatch = ensureStkBatchByWhStocktaking(bill, entry, material);
        if (stkBatch == null || stkBatch.getId() == null) {
            throw new ServiceException(String.format("盘盈明细审核未能生成批次对象，批次号：%s", entry.getBatchNo()));
        }
        BigDecimal profitQty = stockQty.subtract(bookQty);
        StkInventory newInv = buildWhInventoryForStocktakingProfit(bill, entry, stkBatch, profitQty, flowNow, flowUser, material);
        stkInventoryMapper.insertStkInventory(newInv);
        entry.setKcNo(newInv.getId());
        entry.setKcNoStr(String.valueOf(newInv.getId()));
        applyWhStocktakingEntryAuditFields(entry, false);
        computeWhEntryAmountFields(entry);
        if (StringUtils.isNotEmpty(bill.getStockNo())) {
            entry.setStockNo(bill.getStockNo());
        }
        stkIoStocktakingMapper.updateStkIoStocktakingEntry(entry);
        insertWhStocktakingHcCkFlow(bill, entry, newInv, profitQty, "PY", "仓库盘点盘盈入库", flowNow, flowUser, material);
    }

    private StkBatch ensureStkBatchByWhStocktaking(StkIoStocktaking bill, StkIoStocktakingEntry entry, FdMaterial material) {
        if (entry == null || StringUtils.isEmpty(entry.getBatchNo())) {
            return null;
        }
        StkBatch existing = stkBatchMapper.selectByBatchNo(entry.getBatchNo());
        if (existing != null) {
            return existing;
        }
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        Long supplierId = entry.getSupplierId() != null ? entry.getSupplierId() : material.getSupplierId();
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
        b.setWarehouseId(bill.getWarehouseId());
        b.setBillId(bill.getId());
        b.setBillNo(bill.getStockNo());
        if (entry.getId() != null) {
            b.setEntryId(entry.getId());
        }
        b.setBatchSource("仓库盘盈");
        b.setOriginBusinessType("仓库盘点盘盈入库");
        b.setOriginFlowLx("PY");
        if (bill.getWarehouseId() != null) {
            b.setOriginFromWarehouseId(bill.getWarehouseId());
            b.setOriginToWarehouseId(bill.getWarehouseId());
        }
        b.setDelFlag(0);
        Date now = DateUtils.getNowDate();
        String user = SecurityUtils.getUserIdStr();
        b.setCreateTime(now);
        b.setCreateBy(user);
        b.setUpdateTime(now);
        b.setUpdateBy(user);
        b.setAuditTime(now);
        b.setAuditBy(user);
        b.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
        stkBatchMapper.insertStkBatch(b);
        return stkBatchMapper.selectByBatchNo(entry.getBatchNo());
    }

    private StkInventory buildWhInventoryForStocktakingProfit(StkIoStocktaking bill, StkIoStocktakingEntry entry,
        StkBatch stkBatch, BigDecimal qty, Date now, String username, FdMaterial material) {
        StkInventory inv = new StkInventory();
        inv.setBatchNo(entry.getBatchNo());
        inv.setBatchId(stkBatch != null ? stkBatch.getId() : null);
        inv.setMaterialNo(entry.getBatchNumber());
        inv.setBatchNumber(entry.getBatchNumber());
        inv.setMaterialId(entry.getMaterialId());
        inv.setWarehouseId(bill.getWarehouseId());
        inv.setQty(qty);
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        inv.setUnitPrice(unitPrice);
        if (unitPrice != null && qty != null) {
            inv.setAmt(qty.multiply(unitPrice));
        } else {
            inv.setAmt(BigDecimal.ZERO);
        }
        inv.setMaterialDate(now);
        inv.setWarehouseDate(now);
        Long supplierId = entry.getSupplierId() != null ? entry.getSupplierId() : material.getSupplierId();
        inv.setSupplierId(supplierId);
        if (stkBatch != null && stkBatch.getFactoryId() != null) {
            inv.setFactoryId(stkBatch.getFactoryId());
        } else {
            inv.setFactoryId(material.getFactoryId());
        }
        inv.setBeginTime(entry.getBeginTime());
        inv.setEndTime(entry.getEndTime());
        if (StringUtils.isNotEmpty(bill.getStockNo())) {
            inv.setReceiptOrderNo(bill.getStockNo());
        }
        inv.setMainBarcode(entry.getMainBarcode());
        inv.setSubBarcode(entry.getSubBarcode());
        if (StringUtils.isNotEmpty(entry.getHisId())) {
            inv.setHisId(entry.getHisId().trim());
        }
        if (StringUtils.isNotEmpty(entry.getThirdPartyBatchNo())) {
            inv.setThirdPartyBatchNo(entry.getThirdPartyBatchNo().trim());
        }
        inv.setCreateTime(now);
        inv.setCreateBy(username);
        inv.setDelFlag(0);
        inv.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
        InventoryMaterialSnapshotHelper.fillWarehouseRowFromStocktaking(inv, entry, fdMaterialMapper, bill.getTenantId());
        return inv;
    }

    private void insertWhStocktakingHcCkFlow(StkIoStocktaking bill, StkIoStocktakingEntry entry, StkInventory inventory,
        BigDecimal qty, String lx, String originBusinessType, Date flowNow, String flowUser, FdMaterial material) {
        if (bill == null || entry == null || inventory == null || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        HcCkFlow flow = new HcCkFlow();
        flow.setBillId(bill.getId());
        flow.setEntryId(entry.getId());
        flow.setWarehouseId(bill.getWarehouseId());
        flow.setMaterialId(entry.getMaterialId());
        flow.setBatchNo(entry.getBatchNo());
        flow.setBatchNumber(entry.getBatchNumber());
        flow.setQty(qty);
        flow.setUnitPrice(unitPrice);
        if (unitPrice != null) {
            flow.setAmt(qty.multiply(unitPrice));
        }
        flow.setBeginTime(entry.getBeginTime());
        flow.setEndTime(entry.getEndTime());
        flow.setMainBarcode(entry.getMainBarcode() != null ? entry.getMainBarcode() : inventory.getMainBarcode());
        flow.setSubBarcode(entry.getSubBarcode() != null ? entry.getSubBarcode() : inventory.getSubBarcode());
        flow.setSupplierId(inventory.getSupplierId() != null ? inventory.getSupplierId() : entry.getSupplierId());
        if (material != null && material.getFactoryId() != null) {
            flow.setFactoryId(material.getFactoryId());
        }
        flow.setLx(lx);
        flow.setBatchId(inventory.getBatchId());
        flow.setOriginBusinessType(originBusinessType);
        flow.setKcNo(inventory.getId());
        flow.setFlowTime(flowNow);
        flow.setDelFlag(0);
        flow.setCreateTime(flowNow);
        flow.setCreateBy(flowUser);
        flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
        InventoryMaterialSnapshotHelper.enrichHcCkFlowAfterWhStocktaking(flow, bill, entry, bill.getWarehouseId(), fdMaterialMapper);
        hcCkFlowMapper.insertHcCkFlow(flow);
    }

    /**
     * 审核后兜底：盘盈明细须已生成仓库库存行并关联有效批次。
     */
    private void verifyWhProfitEntriesInventoryAndBatch(StkIoStocktaking bill, List<StkIoStocktakingEntry> entryList) {
        if (bill == null || entryList == null || bill.getStockType() == null || bill.getStockType() != STOCK_TYPE_WH_STOCKTAKING) {
            return;
        }
        for (StkIoStocktakingEntry entry : entryList) {
            if (entry == null) {
                continue;
            }
            BigDecimal bookQty = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            BigDecimal stockQty = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
            if (stockQty.compareTo(bookQty) <= 0) {
                continue;
            }
            if (entry.getKcNo() == null) {
                throw new ServiceException(String.format("盘盈明细审核后未生成仓库库存，耗材ID：%s，批次号：%s",
                    entry.getMaterialId(), entry.getBatchNo()));
            }
            StkInventory inv = stkInventoryMapper.selectStkInventoryById(entry.getKcNo());
            if (inv == null || inv.getId() == null) {
                throw new ServiceException(String.format("盘盈明细审核后未生成仓库库存，耗材ID：%s，批次号：%s",
                    entry.getMaterialId(), entry.getBatchNo()));
            }
            if (inv.getBatchId() == null) {
                StkBatch batch = StringUtils.isEmpty(entry.getBatchNo()) ? null : stkBatchMapper.selectByBatchNo(entry.getBatchNo());
                if (batch == null || batch.getId() == null) {
                    throw new ServiceException(String.format("盘盈明细审核后未生成批次对象，耗材ID：%s，批次号：%s",
                        entry.getMaterialId(), entry.getBatchNo()));
                }
                inv.setBatchId(batch.getId());
                inv.setUpdateTime(DateUtils.getNowDate());
                inv.setUpdateBy(SecurityUtils.getUserIdStr());
                stkInventoryMapper.updateStkInventory(inv);
            }
        }
    }

    private void applyWhStocktakingEntryAuditFields(StkIoStocktakingEntry entry, boolean newLine) {
        if (entry == null) {
            return;
        }
        Date now = DateUtils.getNowDate();
        String user = SecurityUtils.getUserIdStr();
        if (newLine) {
            entry.setCreateTime(now);
            entry.setCreateBy(user);
        }
        entry.setUpdateTime(now);
        entry.setUpdateBy(user);
    }

    @Override
    public AjaxResult previewWhStocktakingProfitImport(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择要导入的文件");
        }
        ExcelUtil<WhStocktakingProfitImportRow> util = new ExcelUtil<>(WhStocktakingProfitImportRow.class);
        List<WhStocktakingProfitImportRow> rows;
        try {
            rows = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            return AjaxResult.error("解析文件失败：" + e.getMessage());
        }
        if (rows == null || rows.isEmpty()) {
            return AjaxResult.error("文件中没有有效数据");
        }
        List<Map<String, Object>> previewList = new ArrayList<>();
        Map<Long, Map<String, Object>> warehouseSummaryMap = new LinkedHashMap<>();
        Set<String> hisIdSeen = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            WhStocktakingProfitImportRow row = rows.get(i);
            WhStocktakingProfitImportUtil.normalizeRow(row);
            Map<String, Object> item = new HashMap<>();
            int rowNum = i + 2;
            item.put("rowIndex", rowNum);
            item.put("data", row);
            String err = validateWhProfitImportRow(row, hisIdSeen);
            item.put("error", err);
            if (err == null) {
                enrichWhProfitImportPreviewItem(item, row);
                Long whId = row.getWarehouseId();
                Map<String, Object> sum = warehouseSummaryMap.computeIfAbsent(whId, k -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("warehouseId", whId);
                    m.put("warehouseName", item.get("warehouseName"));
                    m.put("rowCount", 0);
                    return m;
                });
                sum.put("rowCount", ((Integer) sum.get("rowCount")) + 1);
            }
            previewList.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", previewList);
        data.put("warehouseSummary", new ArrayList<>(warehouseSummaryMap.values()));
        data.put("totalRows", previewList.size());
        long validRows = previewList.stream().filter(p -> p.get("error") == null).count();
        data.put("validRows", validRows);
        data.put("canImport", validRows > 0 && validRows == previewList.size());
        return AjaxResult.success(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirmWhStocktakingProfitImport(List<WhStocktakingProfitImportRow> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException("导入数据不能为空");
        }
        List<String> allErrors = collectWhProfitImportRowErrors(rows);
        if (!allErrors.isEmpty()) {
            throw new ServiceException(buildWhProfitImportFailureMessage(allErrors));
        }
        Set<String> hisIdSeen = new HashSet<>();
        Map<Long, List<WhStocktakingProfitImportRow>> byWarehouse = new LinkedHashMap<>();
        for (WhStocktakingProfitImportRow row : rows) {
            WhStocktakingProfitImportUtil.normalizeRow(row);
            byWarehouse.computeIfAbsent(row.getWarehouseId(), k -> new ArrayList<>()).add(row);
        }
        Date stockDate = DateUtils.getNowDate();
        List<Map<String, Object>> createdBills = new ArrayList<>();
        for (Map.Entry<Long, List<WhStocktakingProfitImportRow>> e : byWarehouse.entrySet()) {
            Long warehouseId = e.getKey();
            List<WhStocktakingProfitImportRow> whRows = e.getValue();
            FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseByIdIgnoreTenant(String.valueOf(warehouseId));
            if (wh == null) {
                throw new ServiceException("仓库ID「" + warehouseId + "」不存在");
            }
            SecurityUtils.ensureTenantAccess(wh.getTenantId());
            List<StkIoStocktakingEntry> entries = new ArrayList<>();
            for (WhStocktakingProfitImportRow row : whRows) {
                entries.add(buildWhProfitEntryFromImportRow(row, warehouseId));
            }
            StkIoStocktaking head = new StkIoStocktaking();
            head.setWarehouseId(warehouseId);
            head.setStockDate(stockDate);
            head.setStockStatus(1);
            head.setStockType(STOCK_TYPE_WH_STOCKTAKING);
            head.setAuditAdjustsInventory(1);
            head.setRemark("盘盈明细导入，共" + entries.size() + "条");
            head.setStkIoStocktakingEntryList(entries);
            insertStkIoStocktaking(head);
            Map<String, Object> billInfo = new HashMap<>();
            billInfo.put("id", head.getId());
            billInfo.put("stockNo", head.getStockNo());
            billInfo.put("warehouseId", warehouseId);
            billInfo.put("warehouseName", wh.getName());
            billInfo.put("entryCount", entries.size());
            createdBills.add(billInfo);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("bills", createdBills);
        data.put("billCount", createdBills.size());
        return AjaxResult.success("导入成功，已按仓库生成 " + createdBills.size() + " 张盘点单", data);
    }

    private List<String> collectWhProfitImportRowErrors(List<WhStocktakingProfitImportRow> rows) {
        List<String> errors = new ArrayList<>();
        Set<String> hisIdSeen = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            WhStocktakingProfitImportRow row = rows.get(i);
            WhStocktakingProfitImportUtil.normalizeRow(row);
            String err = validateWhProfitImportRow(row, hisIdSeen);
            if (err != null) {
                errors.add("第" + (i + 2) + "行：" + err);
            }
        }
        return errors;
    }

    private String buildWhProfitImportFailureMessage(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "导入失败";
        }
        StringBuilder sb = new StringBuilder("导入失败：共 ").append(errors.size()).append(" 行数据校验未通过。");
        int show = Math.min(errors.size(), 8);
        for (int i = 0; i < show; i++) {
            sb.append('\n').append(errors.get(i));
        }
        if (errors.size() > show) {
            sb.append("\n... 另有 ").append(errors.size() - show).append(" 行未展示");
        }
        return sb.toString();
    }

    private String validateWhProfitImportRow(WhStocktakingProfitImportRow row, Set<String> hisIdSeen) {
        if (row == null) {
            return "空行";
        }
        if (!isPositiveId(row.getWarehouseId())) {
            return "SPD仓库ID不能为空或无效";
        }
        if (!isPositiveId(row.getMaterialId())) {
            return "SPD产品档案ID不能为空或无效";
        }
        if (!isPositiveId(row.getSupplierId())) {
            return "SPD供应商ID不能为空或无效";
        }
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseByIdIgnoreTenant(String.valueOf(row.getWarehouseId()));
        if (wh == null) {
            return "SPD仓库ID「" + row.getWarehouseId() + "」无法匹配到系统仓库";
        }
        try {
            SecurityUtils.ensureTenantAccess(wh.getTenantId());
        } catch (Exception ex) {
            return "SPD仓库ID「" + row.getWarehouseId() + "」无权访问";
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(row.getMaterialId());
        if (material == null) {
            return "SPD产品档案ID「" + row.getMaterialId() + "」无法匹配到系统产品档案";
        }
        FdSupplier supplier = fdSupplierMapper.selectFdSupplierById(row.getSupplierId());
        if (supplier == null) {
            return "SPD供应商ID「" + row.getSupplierId() + "」无法匹配到系统供应商";
        }
        if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
            return "数量必须大于0";
        }
        if (row.getUnitPrice() == null) {
            return "单价不能为空";
        }
        if (row.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            return "单价不能为负数";
        }
        if (StringUtils.isEmpty(row.getBatchNumber())) {
            return "批号不能为空";
        }
        String endErr = InitialImportDateParser.validateOrError(row.getEndDateRaw(), "有效期");
        if (endErr != null) {
            return endErr;
        }
        if (StringUtils.isNotEmpty(row.getBeginDateRaw())) {
            String beginErr = InitialImportDateParser.validateOrError(row.getBeginDateRaw(), "生产日期");
            if (beginErr != null) {
                return beginErr;
            }
            Date begin = WhStocktakingProfitImportUtil.parseBeginDate(row);
            Date end = WhStocktakingProfitImportUtil.parseEndDate(row);
            if (begin != null && end != null && end.before(begin)) {
                return "有效期不能早于生产日期";
            }
        }
        if (StringUtils.isNotEmpty(row.getHisId())) {
            String hk = row.getHisId().trim();
            if (hisIdSeen.contains(hk)) {
                return "第三方系统库存明细id「" + hk + "」在导入文件中重复";
            }
            hisIdSeen.add(hk);
        }
        return null;
    }

    private static boolean isPositiveId(Long id) {
        return id != null && id > 0L;
    }

    private void enrichWhProfitImportPreviewItem(Map<String, Object> item, WhStocktakingProfitImportRow row) {
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseByIdIgnoreTenant(String.valueOf(row.getWarehouseId()));
        if (wh != null) {
            item.put("warehouseName", wh.getName());
            item.put("warehouseCode", wh.getCode());
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(row.getMaterialId());
        if (material != null) {
            item.put("materialCode", material.getCode());
            item.put("materialName", material.getName());
        }
        FdSupplier supplier = fdSupplierMapper.selectFdSupplierById(row.getSupplierId());
        if (supplier != null) {
            item.put("supplierName", supplier.getName());
        }
    }

    private StkIoStocktakingEntry buildWhProfitEntryFromImportRow(WhStocktakingProfitImportRow row, Long warehouseId) {
        StkIoStocktakingEntry e = new StkIoStocktakingEntry();
        e.setMaterialId(row.getMaterialId());
        e.setSupplierId(row.getSupplierId());
        e.setUnitPrice(row.getUnitPrice());
        e.setPrice(row.getUnitPrice());
        e.setQty(BigDecimal.ZERO);
        e.setStockQty(row.getQty());
        e.setBatchNumber(row.getBatchNumber());
        e.setThirdPartyBatchNo(row.getThirdPartyBatchNo());
        e.setHisId(StringUtils.trimToNull(row.getHisId()));
        e.setBeginTime(WhStocktakingProfitImportUtil.parseBeginDate(row));
        e.setEndTime(WhStocktakingProfitImportUtil.parseEndDate(row));
        e.setReturnWarehouseId(warehouseId);
        e.setCountedFlag(0);
        e.setRemark("盘盈明细导入");
        e.setDelFlag(0);
        if (row.getUnitPrice() != null && row.getQty() != null) {
            e.setAmt(row.getQty().multiply(row.getUnitPrice()));
        }
        fillProfitLossFlagWarehouse(e);
        return e;
    }
}
