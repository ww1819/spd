package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 客户菜单功能启停用记录 sb_customer_menu_status_log
 */
public class SbCustomerMenuStatusLog {

  private String logId;
  private String customerId;
  private String menuId;
  /** 状态（0启用 1停用） */
  private String status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date operateTime;
  private String operateBy;
  private String reason;

  public String getLogId() { return logId; }
  public void setLogId(String logId) { this.logId = logId; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getMenuId() { return menuId; }
  public void setMenuId(String menuId) { this.menuId = menuId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Date getOperateTime() { return operateTime; }
  public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }
  public String getOperateBy() { return operateBy; }
  public void setOperateBy(String operateBy) { this.operateBy = operateBy; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
}
