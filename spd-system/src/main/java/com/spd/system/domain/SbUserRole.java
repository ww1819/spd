package com.spd.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备用户和角色关联 sb_user_role
 *
 * 对应表：sb_user_role
 */
public class SbUserRole {

  /** 用户ID（关联 sys_user.user_id） */
  private Long userId;

  /** 角色ID（关联 sb_role.role_id） */
  private Long roleId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("userId", getUserId())
        .append("roleId", getRoleId())
        .toString();
  }
}

