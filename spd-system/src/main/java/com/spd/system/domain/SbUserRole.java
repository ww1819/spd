package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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

  /** 角色ID（关联 sb_role.role_id，UUID7） */
  private String roleId;

  /** 客户ID（UUID7），归属客户/租户 */
  private String customerId;

  /** 删除者 */
  private String deleteBy;

  /** 删除时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getDeleteBy() {
    return deleteBy;
  }

  public void setDeleteBy(String deleteBy) {
    this.deleteBy = deleteBy;
  }

  public Date getDeleteTime() {
    return deleteTime;
  }

  public void setDeleteTime(Date deleteTime) {
    this.deleteTime = deleteTime;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("userId", getUserId())
        .append("roleId", getRoleId())
        .append("customerId", getCustomerId())
        .append("deleteBy", getDeleteBy())
        .append("deleteTime", getDeleteTime())
        .toString();
  }
}

