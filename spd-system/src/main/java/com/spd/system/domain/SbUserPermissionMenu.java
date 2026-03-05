package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 设备用户菜单权限 sb_user_permission_menu
 */
public class SbUserPermissionMenu {

  private String id;
  private Long userId;
  private String customerId;
  private String menuId;
  private String createBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  private String deleteBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getMenuId() { return menuId; }
  public void setMenuId(String menuId) { this.menuId = menuId; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
  public String getDeleteBy() { return deleteBy; }
  public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
  public Date getDeleteTime() { return deleteTime; }
  public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
}
