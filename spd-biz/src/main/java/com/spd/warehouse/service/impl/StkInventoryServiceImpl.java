package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.vo.WarehouseNearExpiryReminderRowVo;
import com.spd.warehouse.service.IStkInventoryService;

/**
 * 库存明细Service业务层处理
 *
 * @author spd
 * @date 2023-12-17
 */
@Service
public class StkInventoryServiceImpl implements IStkInventoryService
{
    private static final Logger log = LoggerFactory.getLogger(StkInventoryServiceImpl.class);

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    /**
     * 查询库存明细
     *
     * @param id 库存明细主键
     * @return 库存明细
     */
    @Override
    public StkInventory selectStkInventoryById(Long id)
    {
        StkInventory inv = stkInventoryMapper.selectStkInventoryById(id);
        if (inv != null) {
            SecurityUtils.ensureTenantAccess(inv.getTenantId());
        }
        return inv;
    }

    @Override
    public List<StkInventory> selectStkInventoryRowsByIds(List<Long> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return Collections.emptyList();
        }
        return stkInventoryMapper.selectStkInventoryRowsByIds(ids);
    }

    /**
     * 查询库存明细列表
     *
     * @param stkInventory 库存明细
     * @return 库存明细
     */
    @Override
    public List<StkInventory> selectStkInventoryList(StkInventory stkInventory)
    {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        long t0 = System.currentTimeMillis();
        // material / warehouse / supplier 已由 Mapper 关联查出，勿再按行 selectFdMaterialById（N+1 易超时）
        List<StkInventory> list = stkInventoryMapper.selectStkInventoryList(stkInventory);
        long cost = System.currentTimeMillis() - t0;
        int size = list == null ? 0 : list.size();
        if (cost >= 3000L)
        {
            log.error("仓库库存列表慢查询 costMs={} rows={} warehouseId={} materialId={}",
                cost, size,
                stkInventory == null ? null : stkInventory.getWarehouseId(),
                stkInventory == null ? null : stkInventory.getMaterialId());
        }
        else if (cost >= 1000L)
        {
            log.warn("仓库库存列表偏慢 costMs={} rows={} warehouseId={}",
                cost, size, stkInventory == null ? null : stkInventory.getWarehouseId());
        }
        return list;
    }

    @Override
    public TotalInfo selectStkInventoryListTotal(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        long t0 = System.currentTimeMillis();
        TotalInfo total = this.stkInventoryMapper.selectStkInventoryListTotal(stkInventory);
        long cost = System.currentTimeMillis() - t0;
        if (cost >= 3000L)
        {
            log.error("仓库库存合计慢查询 costMs={} warehouseId={}",
                cost, stkInventory == null ? null : stkInventory.getWarehouseId());
        }
        else if (cost >= 1000L)
        {
            log.warn("仓库库存合计偏慢 costMs={} warehouseId={}",
                cost, stkInventory == null ? null : stkInventory.getWarehouseId());
        }
        return total;
    }

    /**
     * 新增库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    @Override
    public int insertStkInventory(StkInventory stkInventory)
    {
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (StringUtils.isEmpty(stkInventory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkInventory.setCreateBy(SecurityUtils.getUserIdStr());
            stkInventory.setCreateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        return stkInventoryMapper.insertStkInventory(stkInventory);
    }

    /**
     * 修改库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    @Override
    public int updateStkInventory(StkInventory stkInventory)
    {
        if (stkInventory == null) {
            return 0;
        }
        if (stkInventory.getQty() != null && stkInventory.getUnitPrice() != null) {
            stkInventory.setAmt(stkInventory.getQty().multiply(stkInventory.getUnitPrice()));
        } else if (stkInventory.getQty() != null && stkInventory.getAmt() == null) {
            stkInventory.setAmt(BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (StringUtils.isEmpty(stkInventory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkInventory.setUpdateBy(SecurityUtils.getUserIdStr());
            stkInventory.setUpdateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        return stkInventoryMapper.updateStkInventory(stkInventory);
    }

    /**
     * 批量删除库存明细
     *
     * @param ids 需要删除的库存明细主键
     * @return 结果
     */
    @Override
    public int deleteStkInventoryByIds(Long[] ids)
    {
        for (Long id : ids) {
            StkInventory existing = stkInventoryMapper.selectStkInventoryById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        return stkInventoryMapper.deleteStkInventoryByIds(ids, SecurityUtils.getUserIdStr());
    }

    /**
     * 删除库存明细信息
     *
     * @param id 库存明细主键
     * @return 结果
     */
    @Override
    public int deleteStkInventoryById(Long id)
    {
        StkInventory existing = stkInventoryMapper.selectStkInventoryById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        return stkInventoryMapper.deleteStkInventoryById(id, SecurityUtils.getUserIdStr());
    }

    /**
     * 按仓库筛选实时库存耗材
     * @param stkInventory
     * @return
     */
    @Override
    public List<StkInventory> selectStkMaterialList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkMaterialList(stkInventory);
    }

    @Override
    public List<StkInventory> selectPDInventoryFilter(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectPDInventoryFilter(stkInventory);
    }

    /**
     * 查询库存明细汇总列表
     * @param stkInventory
     * @return
     */
    @Override
    public List<Map<String, Object>> selectStkInventoryListSummary(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkInventoryListSummary(stkInventory);
    }

    @Override
    public TotalInfo selectStkInventoryListSummaryTotal(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkInventoryListSummaryTotal(stkInventory);
    }

    @Override
    public List<Map<String, Object>> selectStkInventoryDistribution(StkInventory stkInventory) {
        if (stkInventory == null) {
            stkInventory = new StkInventory();
        }
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (StringUtils.isEmpty(stkInventory.getDistributionGroupBy())) {
            stkInventory.setDistributionGroupBy("warehouse");
        }
        List<Map<String, Object>> list = stkInventoryMapper.selectStkInventoryDistribution(stkInventory);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> selectHistoryInventoryList(StkInventory stkInventory) {
        if (stkInventory == null) {
            stkInventory = new StkInventory();
        }
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        List<Map<String, Object>> list = stkInventoryMapper.selectHistoryInventoryList(stkInventory);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public TotalInfo selectHistoryInventoryTotal(StkInventory stkInventory) {
        if (stkInventory == null) {
            stkInventory = new StkInventory();
        }
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        TotalInfo total = stkInventoryMapper.selectHistoryInventoryTotal(stkInventory);
        return total != null ? total : new TotalInfo();
    }

    @Override
    public List<Map<String, Object>> selectInventoryAlertList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectInventoryAlertList(stkInventory);
    }

    @Override
    public List<Map<String, Object>> selectWarehouseInventoryAlertReminderList()
    {
        StkInventory q = new StkInventory();
        q.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        q.setAlertStatus("1");
        List<Map<String, Object>> list = stkInventoryMapper.selectInventoryAlertList(q);
        if (list == null || list.isEmpty())
        {
            return list;
        }
        if (list.size() <= 500)
        {
            return list;
        }
        return new ArrayList<>(list.subList(0, 500));
    }

    @Override
    public int countWarehouseInventoryAlertReminderLines()
    {
        StkInventory q = new StkInventory();
        q.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        q.setAlertStatus("1");
        return stkInventoryMapper.countInventoryAlertList(q);
    }

    @Override
    public List<Map<String, Object>> selectExpiryAlertList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectExpiryAlertList(stkInventory);
    }

    @Override
    public List<WarehouseNearExpiryReminderRowVo> selectWarehouseNearExpiryReminderList()
    {
        return stkInventoryMapper.selectWarehouseNearExpiryReminderList(SecurityUtils.requiredScopedTenantIdForSql());
    }

    @Override
    public int countWarehouseNearExpiryReminderLines()
    {
        return stkInventoryMapper.countWarehouseNearExpiryReminderList(SecurityUtils.requiredScopedTenantIdForSql());
    }

}
