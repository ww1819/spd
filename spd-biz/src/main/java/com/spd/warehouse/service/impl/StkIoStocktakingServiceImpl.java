package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
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

/**
 * 盘点Service业务层处理
 *
 * @author spd
 * @date 2024-06-27
 */
@Service
public class StkIoStocktakingServiceImpl implements IStkIoStocktakingService
{
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
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());
        validateWarehouseStocktakingEntries(stkIoStocktaking);
        Long parenId = stkIoStocktaking.getId();
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
     * @return
     */
    @Transactional
    @Override
    public int auditStkIoBill(String id) {
        StkIoStocktaking stkIoStocktaking = stkIoStocktakingMapper.selectStkIoStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("盘点业务ID：%s，不存在!", id));
        }

        // 盘点单审核仅更新审核状态，不再改库存；库存变动由盈亏单审核完成
        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);

        int res = stkIoStocktakingMapper.updateStkIoStocktaking(stkIoStocktaking);
        return res;
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
                if (material.getSupplierId() == null) {
                    throw new ServiceException(String.format("耗材[%s]未维护供应商，无法盘盈。", material.getName()));
                }
                entry.setSupplierId(material.getSupplierId());
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
}
