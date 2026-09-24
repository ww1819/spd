package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.service.IStkDepInventoryService;
import com.spd.department.vo.InventorySummaryVo;
import com.spd.department.vo.DepartmentInOutDetailVo;
import com.spd.department.vo.DepartmentNearExpiryReminderRowVo;
import com.spd.department.vo.DepartmentInventoryAlertReminderRowVo;
import com.spd.system.service.ITenantScopeService;

/**
 * 科室库存Service业务层处理
 * 
 * @author spd
 * @date 2024-03-04
 */
@Service
public class StkDepInventoryServiceImpl implements IStkDepInventoryService 
{
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private ITenantScopeService tenantScopeService;

    /**
     * 与科室库存列表一致：非租户超管按 sys_user_department / sys_user_warehouse 子查询过滤
     */
    private void applyDepInvListWarehouseDepartmentScope(StkDepInventory q)
    {
        if (q == null)
        {
            return;
        }
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        if (tenantScopeService.isTenantSuper(userId, customerId))
        {
            return;
        }
        tenantScopeService.applyDepartmentScopeQueryParams(q.getParams(), userId, customerId);
        tenantScopeService.applyWarehouseScopeQueryParams(q.getParams(), userId, customerId);
    }

    /**
     * 查询科室库存
     * 
     * @param id 科室库存主键
     * @return 科室库存
     */
    @Override
    public StkDepInventory selectStkDepInventoryById(Long id)
    {
        StkDepInventory inv = stkDepInventoryMapper.selectStkDepInventoryById(id);
        if (inv != null) {
            SecurityUtils.ensureTenantAccess(inv.getTenantId());
        }
        return inv;
    }

    /**
     * 查询科室库存列表
     * 
     * @param stkDepInventory 科室库存
     * @return 科室库存
     */
    @Override
    public List<StkDepInventory> selectStkDepInventoryList(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory != null && StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        List<StkDepInventory> list = stkDepInventoryMapper.selectStkDepInventoryList(stkDepInventory);
        fillOutboundAuditDates(list);
        for (StkDepInventory depInventory : list) {
            FdMaterial fdMaterial = this.fdMaterialMapper.selectFdMaterialById(depInventory.getMaterialId());
            depInventory.setMaterial(fdMaterial);
        }
        return list;
    }

    /**
     * 列表「出库日期」强制回填为出库单审核日（空则制单日），避免仍显示入库 material_date。
     */
    private void fillOutboundAuditDates(List<StkDepInventory> list)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }
        Set<Long> billIds = new HashSet<Long>();
        Set<String> billNos = new HashSet<String>();
        for (StkDepInventory row : list)
        {
            if (row == null)
            {
                continue;
            }
            if (row.getBillId() != null)
            {
                billIds.add(row.getBillId());
            }
            if (StringUtils.isNotEmpty(row.getOutOrderNo()))
            {
                billNos.add(row.getOutOrderNo().trim());
            }
            if (StringUtils.isNotEmpty(row.getBillNo()))
            {
                billNos.add(row.getBillNo().trim());
            }
        }
        if (billIds.isEmpty() && billNos.isEmpty())
        {
            return;
        }
        List<Map<String, Object>> rows = stkDepInventoryMapper.selectOutboundAuditDateRows(
            billIds.isEmpty() ? null : new ArrayList<Long>(billIds),
            billNos.isEmpty() ? null : new ArrayList<String>(billNos));
        if (rows == null || rows.isEmpty())
        {
            return;
        }
        Map<Long, Date> byId = new HashMap<Long, Date>();
        Map<String, Date> byNo = new HashMap<String, Date>();
        for (Map<String, Object> m : rows)
        {
            if (m == null)
            {
                continue;
            }
            Date outDate = toDate(mapGetIgnoreCase(m, "outDate"));
            if (outDate == null)
            {
                continue;
            }
            Object idObj = mapGetIgnoreCase(m, "billId");
            if (idObj instanceof Number)
            {
                byId.put(((Number) idObj).longValue(), outDate);
            }
            else if (idObj != null && StringUtils.isNotEmpty(String.valueOf(idObj)))
            {
                try
                {
                    byId.put(Long.parseLong(String.valueOf(idObj)), outDate);
                }
                catch (NumberFormatException ignored)
                {
                    // ignore
                }
            }
            Object noObj = mapGetIgnoreCase(m, "billNo");
            if (noObj != null && StringUtils.isNotEmpty(String.valueOf(noObj)))
            {
                byNo.put(String.valueOf(noObj).trim(), outDate);
            }
        }
        for (StkDepInventory row : list)
        {
            if (row == null)
            {
                continue;
            }
            Date outDate = null;
            if (row.getBillId() != null)
            {
                outDate = byId.get(row.getBillId());
            }
            if (outDate == null && StringUtils.isNotEmpty(row.getOutOrderNo()))
            {
                outDate = byNo.get(row.getOutOrderNo().trim());
            }
            if (outDate == null && StringUtils.isNotEmpty(row.getBillNo()))
            {
                outDate = byNo.get(row.getBillNo().trim());
            }
            if (outDate != null)
            {
                row.setMaterialDate(outDate);
            }
        }
    }

    private static Object mapGetIgnoreCase(Map<String, Object> m, String key)
    {
        if (m == null || key == null)
        {
            return null;
        }
        if (m.containsKey(key))
        {
            return m.get(key);
        }
        for (Map.Entry<String, Object> e : m.entrySet())
        {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key))
            {
                return e.getValue();
            }
        }
        return null;
    }

    private static Date toDate(Object raw)
    {
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Date)
        {
            return (Date) raw;
        }
        if (raw instanceof java.sql.Timestamp)
        {
            return new Date(((java.sql.Timestamp) raw).getTime());
        }
        if (raw instanceof java.time.LocalDateTime)
        {
            java.time.LocalDateTime ldt = (java.time.LocalDateTime) raw;
            return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        if (raw instanceof java.time.LocalDate)
        {
            java.time.LocalDate ld = (java.time.LocalDate) raw;
            return Date.from(ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    @Override
    public TotalInfo selectStkDepInventoryListTotal(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory != null && StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkDepInventoryMapper.selectStkDepInventoryListTotal(stkDepInventory);
    }

    /**
     * 新增科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int insertStkDepInventory(StkDepInventory stkDepInventory)
    {
        if (StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(stkDepInventory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        return stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
    }

    /**
     * 修改科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int updateStkDepInventory(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory == null) {
            return 0;
        }
        if (stkDepInventory.getQty() != null && stkDepInventory.getUnitPrice() != null) {
            stkDepInventory.setAmt(stkDepInventory.getQty().multiply(stkDepInventory.getUnitPrice()));
        } else if (stkDepInventory.getQty() != null && stkDepInventory.getAmt() == null) {
            stkDepInventory.setAmt(BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(stkDepInventory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkDepInventory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
    }

    /**
     * 批量删除科室库存
     * 
     * @param ids 需要删除的科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryByIds(Long[] ids)
    {
        for (Long id : ids) {
            StkDepInventory existing = stkDepInventoryMapper.selectStkDepInventoryById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        return stkDepInventoryMapper.deleteStkDepInventoryByIds(ids, SecurityUtils.getUserIdStr());
    }

    /**
     * 删除科室库存信息
     * 
     * @param id 科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryById(Long id)
    {
        StkDepInventory existing = stkDepInventoryMapper.selectStkDepInventoryById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        return stkDepInventoryMapper.deleteStkDepInventoryById(id, SecurityUtils.getUserIdStr());
    }

    /**
     * 查询库存汇总列表
     * 
     * @param stkDepInventory 查询条件
     * @return 库存汇总集合
     */
    @Override
    public List<InventorySummaryVo> selectInventorySummaryList(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectInventorySummaryList(stkDepInventory);
    }

    /**
     * 查询科室进销存明细列表
     * 
     * @param stkDepInventory 查询条件
     * @return 进销存明细集合
     */
    @Override
    public List<DepartmentInOutDetailVo> selectDepartmentInOutDetailList(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectDepartmentInOutDetailList(stkDepInventory);
    }

    @Override
    public TotalInfo selectInventorySummaryListTotal(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory != null && StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkDepInventoryMapper.selectInventorySummaryListTotal(stkDepInventory);
    }

    @Override
    public TotalInfo selectDepartmentInOutDetailListTotal(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectDepartmentInOutDetailListTotal(stkDepInventory);
    }

    @Override
    public List<DepartmentNearExpiryReminderRowVo> selectDepartmentNearExpiryReminderMonitorList()
    {
        StkDepInventory q = new StkDepInventory();
        q.setDepInventoryNearExpiryDays(30);
        applyDepInvListWarehouseDepartmentScope(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkDepInventoryMapper.selectDepartmentNearExpiryReminderList(q);
    }

    @Override
    public long countDepartmentNearExpiryReminderMonitor()
    {
        StkDepInventory q = new StkDepInventory();
        q.setDepInventoryNearExpiryDays(30);
        applyDepInvListWarehouseDepartmentScope(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        Long c = stkDepInventoryMapper.countDepartmentNearExpiryReminder(q);
        return c != null ? c.longValue() : 0L;
    }

    @Override
    public List<DepartmentInventoryAlertReminderRowVo> selectDepartmentInventoryAlertReminderMonitorList()
    {
        StkDepInventory q = new StkDepInventory();
        applyDepInvListWarehouseDepartmentScope(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkDepInventoryMapper.selectDepartmentInventoryAlertReminderList(q);
    }

    @Override
    public long countDepartmentInventoryAlertReminderMonitor()
    {
        StkDepInventory q = new StkDepInventory();
        applyDepInvListWarehouseDepartmentScope(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        Long c = stkDepInventoryMapper.countDepartmentInventoryAlertReminder(q);
        return c != null ? c.longValue() : 0L;
    }

    @Override
    public BigDecimal sumDepartmentInventoryQtyMonitor()
    {
        StkDepInventory q = new StkDepInventory();
        applyDepInvListWarehouseDepartmentScope(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        TotalInfo total = stkDepInventoryMapper.selectStkDepInventoryListTotal(q);
        if (total == null || total.getTotalQty() == null)
        {
            return BigDecimal.ZERO;
        }
        return total.getTotalQty();
    }
}
