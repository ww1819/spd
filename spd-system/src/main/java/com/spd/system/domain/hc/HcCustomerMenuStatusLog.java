package com.spd.system.domain.hc;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 耗材客户菜单功能启停用记录 hc_customer_menu_status_log
 */
public class HcCustomerMenuStatusLog {

  private String logId;
  private String tenantId;
  private Long menuId;
  private String status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date operateTime;
  private String operateBy;
  private String reason;

  public String getLogId() { return logId; }
  public void setLogId(String logId) { this.logId = logId; }
  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Date getOperateTime() { return operateTime; }
  public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }
  public String getOperateBy() { return operateBy; }
  public void setOperateBy(String operateBy) { this.operateBy = operateBy; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
}
