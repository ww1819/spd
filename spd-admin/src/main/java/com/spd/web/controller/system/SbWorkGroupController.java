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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SbMenu;
import com.spd.system.domain.SbWorkGroup;
import com.spd.system.service.ISbMenuService;
import com.spd.system.service.ISbWorkGroupService;

/**
 * 设备系统工作组管理
 * 路由：/equipment/system/workgroup
 */
@RestController
@RequestMapping("/equipment/system/workgroup")
public class SbWorkGroupController extends BaseController {

  @Autowired
  private ISbWorkGroupService sbWorkGroupService;
  @Autowired
  private ISbMenuService sbMenuService;

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:list') or @ss.isPlatformUser()")
  @GetMapping("/list")
  public AjaxResult list(@RequestParam String customerId) {
    if (customerId == null || customerId.isEmpty()) {
      if (SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null
          && SecurityUtils.getLoginUser().getUser().getCustomerId() != null) {
        customerId = SecurityUtils.getLoginUser().getUser().getCustomerId();
      } else {
        return error("缺少客户ID");
      }
    }
    List<SbWorkGroup> list = sbWorkGroupService.selectListByCustomerId(customerId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/{groupId}")
  public AjaxResult getInfo(@PathVariable String groupId) {
    return success(sbWorkGroupService.selectByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:add') or @ss.isPlatformUser()")
  @Log(title = "设备工作组", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@Validated @RequestBody SbWorkGroup group) {
    group.setCreateBy(SecurityUtils.getUserIdStr());
    return toAjax(sbWorkGroupService.insertSbWorkGroup(group));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "设备工作组", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@Validated @RequestBody SbWorkGroup group) {
    group.setUpdateBy(SecurityUtils.getUserIdStr());
    return toAjax(sbWorkGroupService.updateSbWorkGroup(group));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:remove') or @ss.isPlatformUser()")
  @Log(title = "设备工作组", businessType = BusinessType.DELETE)
  @DeleteMapping("/{groupId}")
  public AjaxResult remove(@PathVariable String groupId) {
    return toAjax(sbWorkGroupService.deleteByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/users/{groupId}")
  public AjaxResult listUsers(@PathVariable String groupId) {
    return success(sbWorkGroupService.selectUserIdsByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组添加用户", businessType = BusinessType.UPDATE)
  @PostMapping("/users/{groupId}")
  public AjaxResult addUsers(@PathVariable String groupId, @RequestBody Long[] userIds) {
    return toAjax(sbWorkGroupService.addUsersToGroup(groupId, userIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组移除用户", businessType = BusinessType.UPDATE)
  @DeleteMapping("/users/{groupId}/{userId}")
  public AjaxResult removeUser(@PathVariable String groupId, @PathVariable Long userId) {
    return toAjax(sbWorkGroupService.removeUserFromGroup(groupId, userId));
  }

  /** 工作组可分配的菜单树（客户已开启的菜单） */
  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/menuTree")
  public AjaxResult menuTree(@RequestParam String customerId) {
    List<SbMenu> tree = sbMenuService.selectSbMenuTreeByCustomerIdEnabling(customerId);
    return success(tree);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/menuIds/{groupId}")
  public AjaxResult getMenuIds(@PathVariable String groupId) {
    return success(sbWorkGroupService.selectMenuIdsByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组菜单权限", businessType = BusinessType.UPDATE)
  @PutMapping("/menu/{groupId}")
  public AjaxResult saveMenus(@PathVariable String groupId, @RequestParam String customerId, @RequestParam(required = false) String[] menuIds) {
    return toAjax(sbWorkGroupService.saveGroupMenus(groupId, customerId, menuIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/warehouseIds/{groupId}")
  public AjaxResult getWarehouseIds(@PathVariable String groupId) {
    return success(sbWorkGroupService.selectWarehouseIdsByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组仓库权限", businessType = BusinessType.UPDATE)
  @PutMapping("/warehouse/{groupId}")
  public AjaxResult saveWarehouses(@PathVariable String groupId, @RequestParam String customerId, @RequestParam(required = false) Long[] warehouseIds) {
    return toAjax(sbWorkGroupService.saveGroupWarehouses(groupId, customerId, warehouseIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:query') or @ss.isPlatformUser()")
  @GetMapping("/deptIds/{groupId}")
  public AjaxResult getDeptIds(@PathVariable String groupId) {
    return success(sbWorkGroupService.selectDeptIdsByGroupId(groupId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组科室权限", businessType = BusinessType.UPDATE)
  @PutMapping("/dept/{groupId}")
  public AjaxResult saveDepts(@PathVariable String groupId, @RequestParam String customerId, @RequestParam(required = false) Long[] deptIds) {
    return toAjax(sbWorkGroupService.saveGroupDepts(groupId, customerId, deptIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:workgroup:edit') or @ss.isPlatformUser()")
  @Log(title = "工作组权限同步到用户", businessType = BusinessType.UPDATE)
  @PostMapping("/sync/{groupId}")
  public AjaxResult syncToGroupUsers(@PathVariable String groupId) {
    return success(sbWorkGroupService.syncToGroupUsers(groupId));
  }
}
