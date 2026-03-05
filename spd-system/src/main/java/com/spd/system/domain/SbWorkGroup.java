package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.spd.common.core.domain.BaseEntity;

/**
 * 设备系统工作组表 sb_work_group
 */
public class SbWorkGroup extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 工作组ID(UUID7) */
  private String groupId;
  /** 所属客户ID */
  private String customerId;
  /** 工作组名称 */
  private String groupName;
  /** 工作组标识如 super */
  private String groupKey;
  /** 显示顺序 */
  private Integer orderNum;
  /** 删除者 */
  private String deleteBy;
  /** 删除时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupKey() {
    return groupKey;
  }

  public void setGroupKey(String groupKey) {
    this.groupKey = groupKey;
  }

  public Integer getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
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
        .append("groupId", getGroupId())
        .append("customerId", getCustomerId())
        .append("groupName", getGroupName())
        .append("groupKey", getGroupKey())
        .toString();
  }
}
