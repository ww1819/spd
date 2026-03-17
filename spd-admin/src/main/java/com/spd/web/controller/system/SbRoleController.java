package com.spd.web.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SbRole;
import com.spd.system.domain.SbUserRole;
import com.spd.system.service.ISbRoleService;

/**
 * 设备角色管理
 *
 * 路由前缀：/equipment/system/role
 */
@RestController
@RequestMapping("/equipment/system/role")
public class SbRoleController extends BaseController {

    @Autowired
    private ISbRoleService sbRoleService;

    /**
     * 获取设备角色列表
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:list') or @ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbRole role)
    {
        if (role.getCustomerId() == null && SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
            role.setCustomerId(SecurityUtils.getLoginUser().getUser().getCustomerId());
        }
        startPage();
        List<SbRole> list = sbRoleService.selectSbRoleList(role);
        return getDataTable(list);
    }

    /**
     * 获取设备角色详细信息
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:query') or @ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable String roleId)
    {
        return AjaxResult.success(sbRoleService.selectSbRoleById(roleId));
    }

    /**
     * 新增设备角色
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:add') or @ss.hasPermi('system:role:add')")
    @Log(title = "设备角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SbRole role)
    {
        if (!sbRoleService.checkSbRoleNameUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sbRoleService.checkSbRoleKeyUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        if (role.getCustomerId() == null && SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
            role.setCustomerId(SecurityUtils.getLoginUser().getUser().getCustomerId());
        }
        role.setCreateBy(SecurityUtils.getUserIdStr());
        return toAjax(sbRoleService.insertSbRole(role));
    }

    /**
     * 修改保存设备角色
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:edit') or @ss.hasPermi('system:role:edit')")
    @Log(title = "设备角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SbRole role)
    {
        if (!sbRoleService.checkSbRoleNameUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sbRoleService.checkSbRoleKeyUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUserIdStr());
        return toAjax(sbRoleService.updateSbRole(role));
    }

    /**
     * 修改设备角色状态
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:edit') or @ss.hasPermi('system:role:edit')")
    @Log(title = "设备角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SbRole role)
    {
        role.setUpdateBy(SecurityUtils.getUserIdStr());
        return toAjax(sbRoleService.updateSbRoleStatus(role));
    }

    /**
     * 删除设备角色
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:remove') or @ss.hasPermi('system:role:remove')")
    @Log(title = "设备角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable String[] roleIds)
    {
        return toAjax(sbRoleService.deleteSbRoleByIds(roleIds));
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:edit') or @ss.hasPermi('system:role:edit')")
    @Log(title = "设备角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SbUserRole userRole)
    {
        return toAjax(sbRoleService.deleteSbAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:edit') or @ss.hasPermi('system:role:edit')")
    @Log(title = "设备角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(String roleId, Long[] userIds)
    {
        return toAjax(sbRoleService.deleteSbAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('sb:system:role:edit') or @ss.hasPermi('system:role:edit')")
    @Log(title = "设备角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(String roleId, Long[] userIds)
    {
        return toAjax(sbRoleService.insertSbAuthUsers(roleId, userIds));
    }
}

