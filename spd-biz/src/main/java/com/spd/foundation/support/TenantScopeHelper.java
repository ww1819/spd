package com.spd.foundation.support;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spd.common.core.domain.BaseEntity;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.system.service.ITenantScopeService;

/**
 * 租户科室/仓库下拉与列表范围：走 scoped SQL，避免全量查询 + 内存 filter。
 */
@Component
public class TenantScopeHelper
{
    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private IFdWarehouseService fdWarehouseService;

    public void applyDepartmentListScope(BaseEntity query)
    {
        if (query == null)
        {
            return;
        }
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId))
        {
            if (query instanceof FdDepartment)
            {
                ((FdDepartment) query).setTenantId(customerId);
            }
            else if (query instanceof FdWarehouse)
            {
                ((FdWarehouse) query).setTenantId(customerId);
            }
            tenantScopeService.applyDepartmentScopeQueryParams(
                query.getParams(), SecurityUtils.getUserId(), customerId);
        }
    }

    public void applyWarehouseListScope(BaseEntity query)
    {
        if (query == null)
        {
            return;
        }
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId))
        {
            if (query instanceof FdWarehouse)
            {
                ((FdWarehouse) query).setTenantId(customerId);
            }
            tenantScopeService.applyWarehouseScopeQueryParams(
                query.getParams(), SecurityUtils.getUserId(), customerId);
        }
    }

    /**
     * 当前登录上下文下的科室下拉（租户 super 全量；非 super 走 SQL 子查询过滤）。
     */
    public List<FdDepartment> selectScopedDepartments()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId))
        {
            FdDepartment q = new FdDepartment();
            q.setTenantId(customerId);
            if (!tenantScopeService.isTenantSuper(userId, customerId))
            {
                tenantScopeService.applyDepartmentScopeQueryParams(q.getParams(), userId, customerId);
            }
            List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(q);
            return list != null ? list : new ArrayList<>();
        }
        if (SysUser.isAdmin(userId))
        {
            List<FdDepartment> list = fdDepartmentService.selectdepartmenAll();
            return list != null ? list : new ArrayList<>();
        }
        List<FdDepartment> list = fdDepartmentService.selectUserDepartmenAll(userId);
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 当前登录上下文下的仓库下拉（非 super 空权限时返回空列表）。
     */
    public List<FdWarehouse> selectScopedWarehouses()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId))
        {
            FdWarehouse q = new FdWarehouse();
            q.setTenantId(customerId);
            if (!tenantScopeService.isTenantSuper(userId, customerId))
            {
                tenantScopeService.applyWarehouseScopeQueryParams(q.getParams(), userId, customerId);
            }
            List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(q);
            return list != null ? list : new ArrayList<>();
        }
        if (SysUser.isAdmin(userId))
        {
            List<FdWarehouse> list = fdWarehouseService.selectwarehouseAll();
            return list != null ? list : new ArrayList<>();
        }
        List<FdWarehouse> list = fdWarehouseService.selectUserWarehouseAll(userId);
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 首页仓库：个人仓库范围为空时回退租户全量（与改前 HomeDashboardController 一致）。
     */
    public List<FdWarehouse> selectScopedWarehousesForHomeDashboard()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        List<FdWarehouse> all = fdWarehouseService.selectwarehouseAll();
        if (StringUtils.isNotEmpty(customerId) && all != null
            && !tenantScopeService.isTenantSuper(userId, customerId))
        {
            List<Long> allowedIds = tenantScopeService.resolveWarehouseScope(userId, customerId);
            if (allowedIds != null && !allowedIds.isEmpty())
            {
                all = all.stream()
                    .filter(w -> w.getId() != null && allowedIds.contains(w.getId()))
                    .collect(Collectors.toList());
            }
        }
        return all != null ? all : new ArrayList<>();
    }
}
