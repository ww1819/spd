package com.spd.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.system.domain.SbRole;
import com.spd.system.service.ISbMenuService;
import com.spd.system.service.ISbRoleService;

/**
 * 设备侧用户权限处理（基于 sb_* 表）
 */
@Component
public class SbPermissionService {

  @Autowired
  private ISbRoleService sbRoleService;

  /**
   * 获取设备角色数据权限
   */
  public Set<String> getRolePermission(SysUser user) {
    Set<String> roles = new HashSet<>();
    List<SbRole> sbRoles = sbRoleService.selectSbRolesByUserId(user.getUserId());
    for (SbRole role : sbRoles) {
      roles.add(role.getRoleKey());
    }
    return roles;
  }

  @Autowired
  private ISbMenuService sbMenuService;

  /**
   * 获取设备菜单数据权限（基于 sb_menu.perms）
   * 租户时仅返回该客户已开启且未暂停的菜单权限（sb_customer_menu.is_enabled=1 且 status=0）
   */
  public Set<String> getMenuPermission(SysUser user) {
    Set<String> perms = new HashSet<>();
    // 超级管理员在设备端同样拥有所有权限
    if (user.isAdmin()) {
      perms.add("*:*:*");
      return perms;
    }
    String customerId = user.getCustomerId();
    return sbMenuService.selectSbMenuPermsByUserIdAndCustomer(user.getUserId(), customerId);
  }
}

