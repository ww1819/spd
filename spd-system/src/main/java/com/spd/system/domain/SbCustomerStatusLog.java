package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.spd.common.annotation.Excel;

/**
 * 客户启停用记录 sb_customer_status_log
 */
public class SbCustomerStatusLog {

  /** 记录ID(UUID7) */
  private String logId;

  @Excel(name = "客户ID")
  private String customerId;

  /** 状态（0启用 1停用） */
  @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
  private String status;

  @Excel(name = "操作时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date operateTime;

  @Excel(name = "操作人")
  private String operateBy;

  @Excel(name = "启停用原因")
  private String reason;

  public String getLogId() {
    return logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Date operateTime) {
    this.operateTime = operateTime;
  }

  public String getOperateBy() {
    return operateBy;
  }

  public void setOperateBy(String operateBy) {
    this.operateBy = operateBy;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
