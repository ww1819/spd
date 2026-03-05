package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备系统客户菜单权限表 sb_customer_menu
 * 控制每个客户可用的设备菜单
 */
public class SbCustomerMenu {

  /** 客户ID（UUID7） */
  private String customerId;

  /** 菜单ID（UUID7） */
  private String menuId;

  /** 暂停状态（0正常 1暂停，仅客户菜单功能管理操作） */
  private String status;

  /** 是否开启（0关闭 1开启），客户管理取消功能时改为0 */
  private String isEnabled;

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

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getMenuId() {
    return menuId;
  }

  public void setMenuId(String menuId) {
    this.menuId = menuId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIsEnabled() {
    return isEnabled;
  }

  public void setIsEnabled(String isEnabled) {
    this.isEnabled = isEnabled;
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

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("customerId", getCustomerId())
        .append("menuId", getMenuId())
        .append("createBy", getCreateBy())
        .append("createTime", getCreateTime())
        .append("deleteBy", getDeleteBy())
        .append("deleteTime", getDeleteTime())
        .toString();
  }
}
