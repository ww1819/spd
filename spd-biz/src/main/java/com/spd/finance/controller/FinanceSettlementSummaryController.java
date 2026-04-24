package com.spd.finance.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.finance.domain.vo.FinanceSettlementSummaryBundleVo;
import com.spd.finance.service.IFinanceSettlementSummaryService;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.foundation.service.IFdSupplierService;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.system.service.ITenantScopeService;
import com.spd.warehouse.domain.StkIoBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务结算汇总（出退库按供货单位）
 */
@RestController
@RequestMapping("/finance/settlementSummary")
public class FinanceSettlementSummaryController extends BaseController
{
    @Autowired
    private IFinanceSettlementSummaryService financeSettlementSummaryService;

    @Autowired
    private IFdWarehouseService fdWarehouseService;

    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private IFdSupplierService fdSupplierService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    /**
     * 汇总数据（不分页）。仅需登录，数据仍受租户及出退库科室范围 SQL 约束，避免仅有导出权等用户无法查看。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/data")
    public AjaxResult data(StkIoBill query)
    {
        FinanceSettlementSummaryBundleVo vo = financeSettlementSummaryService.summarize(query);
        return AjaxResult.success(vo);
    }

    /**
     * 筛选下拉：仓库列表（与「按用户全部仓库」口径一致，租户 + 非 super 时按仓库权限过滤）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pick/warehouses")
    public AjaxResult pickWarehouses()
    {
        return success(resolveScopedWarehouses());
    }

    /**
     * 筛选下拉：科室列表（与科室 listAll 口径一致）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pick/departments")
    public AjaxResult pickDepartments()
    {
        return success(resolveScopedDepartments());
    }

    /**
     * 筛选下拉：按 id 取供应商名称（无需 foundation:supplier:query）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pick/supplier/{id}")
    public AjaxResult pickSupplierById(@PathVariable("id") Long id)
    {
        if (id == null)
        {
            return error("供应商 id 无效");
        }
        FdSupplier s = fdSupplierService.selectFdSupplierById(id);
        if (s == null)
        {
            return error("供应商不存在");
        }
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId) && s.getTenantId() != null && !customerId.equals(s.getTenantId()))
        {
            return error("无权查看该供应商");
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", s.getId());
        item.put("name", s.getName());
        item.put("code", s.getCode());
        item.put("referredCode", s.getReferredCode());
        return success(item);
    }

    private List<FdWarehouse> resolveScopedWarehouses()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        List<FdWarehouse> list;
        if (StringUtils.isNotEmpty(customerId))
        {
            list = fdWarehouseService.selectwarehouseAll();
            if (list != null && !tenantScopeService.isTenantSuper(userId, customerId))
            {
                List<Long> allowedIds = tenantScopeService.resolveWarehouseScope(userId, customerId);
                if (allowedIds == null || allowedIds.isEmpty())
                {
                    list = new ArrayList<>();
                }
                else
                {
                    list = list.stream()
                        .filter(w -> w.getId() != null && allowedIds.contains(w.getId()))
                        .collect(Collectors.toList());
                }
            }
        }
        else
        {
            if (SysUser.isAdmin(userId))
            {
                list = fdWarehouseService.selectwarehouseAll();
            }
            else
            {
                list = fdWarehouseService.selectUserWarehouseAll(userId);
            }
        }
        return list != null ? list : new ArrayList<>();
    }

    private List<FdDepartment> resolveScopedDepartments()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        List<FdDepartment> list;
        if (StringUtils.isNotEmpty(customerId))
        {
            list = fdDepartmentService.selectdepartmenAll();
            if (list != null && !tenantScopeService.isTenantSuper(userId, customerId))
            {
                List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
                if (allowedIds == null || allowedIds.isEmpty())
                {
                    list = new ArrayList<>();
                }
                else
                {
                    list = list.stream()
                        .filter(d -> d.getId() != null && allowedIds.contains(d.getId()))
                        .collect(Collectors.toList());
                }
            }
        }
        else
        {
            if (SysUser.isAdmin(userId))
            {
                list = fdDepartmentService.selectdepartmenAll();
            }
            else
            {
                list = fdDepartmentService.selectUserDepartmenAll(userId);
            }
        }
        return list != null ? list : new ArrayList<>();
    }
}
