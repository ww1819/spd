package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.enums.BusinessType;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.service.ITenantScopeService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室Controller
 *
 * @author spd
 * @date 2023-11-26
 */
@RestController
@RequestMapping("/foundation/depart")
public class FdDepartmentController extends BaseController
{
    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    /**
     * 查询科室列表（租户非 super 组用户按 sb_user_permission_dept 过滤）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdDepartment fdDepartment)
    {
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId)) {
            fdDepartment.setTenantId(customerId);
        }
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds != null && !allowedIds.isEmpty()) {
                fdDepartment.getParams().put("allowedDeptIds", allowedIds);
            } else {
                fdDepartment.getParams().put("allowedDeptIds", new ArrayList<Long>());
            }
        }
        startPage();
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        return getDataTable(list);
    }

    /**
     * 查询所有科室列表（租户下：super 组返回客户下全部，否则返回当前用户有权限的科室）
     */
    @GetMapping("/listAll/{userId}")
    public List<FdDepartment> listAll(@PathVariable(value = "userId") Long userId)
    {
        String customerId = SecurityUtils.getCustomerId();
        List<FdDepartment> list;
        if (StringUtils.isNotEmpty(customerId)) {
            list = fdDepartmentService.selectdepartmenAll();
            if (list != null && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
                List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
                if (allowedIds == null || allowedIds.isEmpty()) list = new ArrayList<>();
                else list = list.stream().filter(d -> d.getId() != null && allowedIds.contains(d.getId())).collect(Collectors.toList());
            }
        } else {
            if (SysUser.isAdmin(userId)) list = fdDepartmentService.selectdepartmenAll();
            else list = fdDepartmentService.selectUserDepartmenAll(userId);
        }
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 导出科室列表（租户非 super 组用户按权限过滤）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:export')")
    @Log(title = "科室", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdDepartment fdDepartment)
    {
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId)) {
            fdDepartment.setTenantId(customerId);
        }
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds != null && !allowedIds.isEmpty()) {
                fdDepartment.getParams().put("allowedDeptIds", allowedIds);
            } else {
                fdDepartment.getParams().put("allowedDeptIds", new ArrayList<Long>());
            }
        }
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        util.exportExcel(response, list, "科室数据");
    }

    /**
     * 获取科室详细信息（仅本客户可查）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        FdDepartment dept = fdDepartmentService.selectFdDepartmentById(id);
        if (dept == null) {
            return error("科室不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && !customerId.equals(dept.getTenantId())) {
            return error("无权查看非本客户的科室");
        }
        return success(dept);
    }

    /**
     * 新增科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:add')")
    @Log(title = "科室", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdDepartment fdDepartment)
    {
        return toAjax(fdDepartmentService.insertFdDepartment(fdDepartment));
    }

    /**
     * 修改科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdDepartment fdDepartment)
    {
        return toAjax(fdDepartmentService.updateFdDepartment(fdDepartment));
    }

    /**
     * 删除科室（支持单个或逗号分隔多个 id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:remove')")
    @Log(title = "科室", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids)
    {
        if (StringUtils.isEmpty(ids)) {
            return error("科室 id 不能为空");
        }
        String[] idArr = ids.split(",");
        int n = 0;
        for (String id : idArr) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            n += fdDepartmentService.deleteFdDepartmentById(id.trim());
        }
        return toAjax(n);
    }

    /**
     * 获取科室列表（租户下：super 组返回客户下全部，否则返回当前用户有权限的科室）
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<FdDepartment> fdDepartmentList = fdDepartmentService.selectdepartmenAll();
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && fdDepartmentList != null && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty()) fdDepartmentList = new ArrayList<>();
            else fdDepartmentList = fdDepartmentList.stream().filter(d -> d.getId() != null && allowedIds.contains(d.getId())).collect(Collectors.toList());
        }
        return success(fdDepartmentList != null ? fdDepartmentList : new ArrayList<>());
    }

    /**
     * 批量更新科室名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdDepartmentService.updateReferred(ids);
        return success("更新简码成功");
    }
}
