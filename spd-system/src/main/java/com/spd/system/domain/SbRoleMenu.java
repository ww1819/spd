package com.spd.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备角色和菜单关联 sb_role_menu
 *
 * 对应表：sb_role_menu
 */
public class SbRoleMenu {

  /** 角色ID（关联 sb_role.role_id） */
  private Long roleId;

  /** 菜单ID（关联 sb_menu.menu_id） */
  private Long menuId;

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("roleId", getRoleId())
        .append("menuId", getMenuId())
        .toString();
  }
}

