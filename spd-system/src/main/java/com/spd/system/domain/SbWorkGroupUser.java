package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工作组与用户关联 sb_work_group_user
 */
public class SbWorkGroupUser {

  /** 工作组ID */
  private String groupId;
  /** 用户ID(sys_user.user_id) */
  private Long userId;
  /** 客户ID */
  private String customerId;
  /** 创建者 */
  private String createBy;
  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  /** 删除者 */
  private String deleteBy;
  /** 删除时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;
  /** 删除标志（0正常 1删除） */
  private String delFlag;

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
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

  public String getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("groupId", getGroupId())
        .append("userId", getUserId())
        .append("customerId", getCustomerId())
        .toString();
  }
}
