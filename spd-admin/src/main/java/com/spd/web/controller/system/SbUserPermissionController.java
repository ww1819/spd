package com.spd.web.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SbMenu;
import com.spd.system.service.ISbMenuService;
import com.spd.system.service.ISbUserPermissionService;

/**
 * 设备用户权限（菜单/仓库/科室）
 * 路由：/equipment/system/userPermission
 */
@RestController
@RequestMapping("/equipment/system/userPermission")
public class SbUserPermissionController extends BaseController {

  @Autowired
  private ISbUserPermissionService sbUserPermissionService;
  @Autowired
  private ISbMenuService sbMenuService;

  /** 用户可分配的菜单树（客户已开启的菜单） */
  @PreAuthorize("@ss.hasPermi('sb:system:user:query') or @ss.isPlatformUser()")
  @GetMapping("/menuTree")
  public AjaxResult menuTree(@RequestParam String customerId) {
    List<SbMenu> tree = sbMenuService.selectSbMenuTreeByCustomerIdEnabling(customerId);
    return success(tree);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:query') or @ss.isPlatformUser()")
  @GetMapping("/menuIds")
  public AjaxResult getMenuIds(@RequestParam Long userId, @RequestParam(required = false) String customerId) {
    if (customerId == null || customerId.isEmpty()) {
      if (SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
        customerId = SecurityUtils.getLoginUser().getUser().getCustomerId();
      }
    }
    return success(sbUserPermissionService.selectMenuIdsByUserId(userId, customerId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:edit') or @ss.isPlatformUser()")
  @Log(title = "用户菜单权限", businessType = BusinessType.UPDATE)
  @PutMapping("/menu")
  public AjaxResult saveMenus(@RequestParam Long userId, @RequestParam String customerId, @RequestParam(required = false) String[] menuIds) {
    return toAjax(sbUserPermissionService.saveUserMenus(userId, customerId, menuIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:query') or @ss.isPlatformUser()")
  @GetMapping("/warehouseIds")
  public AjaxResult getWarehouseIds(@RequestParam Long userId, @RequestParam(required = false) String customerId) {
    if ((customerId == null || customerId.isEmpty()) && SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
      customerId = SecurityUtils.getLoginUser().getUser().getCustomerId();
    }
    return success(sbUserPermissionService.selectWarehouseIdsByUserId(userId, customerId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:edit') or @ss.isPlatformUser()")
  @Log(title = "用户仓库权限", businessType = BusinessType.UPDATE)
  @PutMapping("/warehouse")
  public AjaxResult saveWarehouses(@RequestParam Long userId, @RequestParam String customerId, @RequestParam(required = false) Long[] warehouseIds) {
    return toAjax(sbUserPermissionService.saveUserWarehouses(userId, customerId, warehouseIds));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:query') or @ss.isPlatformUser()")
  @GetMapping("/deptIds")
  public AjaxResult getDeptIds(@RequestParam Long userId, @RequestParam(required = false) String customerId) {
    if ((customerId == null || customerId.isEmpty()) && SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
      customerId = SecurityUtils.getLoginUser().getUser().getCustomerId();
    }
    return success(sbUserPermissionService.selectDeptIdsByUserId(userId, customerId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:user:edit') or @ss.isPlatformUser()")
  @Log(title = "用户科室权限", businessType = BusinessType.UPDATE)
  @PutMapping("/dept")
  public AjaxResult saveDepts(@RequestParam Long userId, @RequestParam String customerId, @RequestParam(required = false) Long[] deptIds) {
    return toAjax(sbUserPermissionService.saveUserDepts(userId, customerId, deptIds));
  }
}
