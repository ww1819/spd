package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 客户菜单功能启停用时间段 sb_customer_menu_period_log
 */
public class SbCustomerMenuPeriodLog {

  private String periodId;
  private String customerId;
  private String menuId;
  /** usage=使用时段，suspend=停用时段 */
  private String periodType;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;
  private String createBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  public static final String PERIOD_TYPE_USAGE = "usage";
  public static final String PERIOD_TYPE_SUSPEND = "suspend";

  public String getPeriodId() { return periodId; }
  public void setPeriodId(String periodId) { this.periodId = periodId; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getMenuId() { return menuId; }
  public void setMenuId(String menuId) { this.menuId = menuId; }
  public String getPeriodType() { return periodType; }
  public void setPeriodType(String periodType) { this.periodType = periodType; }
  public Date getStartTime() { return startTime; }
  public void setStartTime(Date startTime) { this.startTime = startTime; }
  public Date getEndTime() { return endTime; }
  public void setEndTime(Date endTime) { this.endTime = endTime; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
