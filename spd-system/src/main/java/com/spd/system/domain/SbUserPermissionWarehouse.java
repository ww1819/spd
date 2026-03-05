package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 设备用户仓库权限 sb_user_permission_warehouse
 */
public class SbUserPermissionWarehouse {

  private String id;
  private Long userId;
  private String customerId;
  private Long warehouseId;
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
  public Long getWarehouseId() { return warehouseId; }
  public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
  public String getDeleteBy() { return deleteBy; }
  public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
  public Date getDeleteTime() { return deleteTime; }
  public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
}
