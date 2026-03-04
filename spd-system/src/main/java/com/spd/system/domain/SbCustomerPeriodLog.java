package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.spd.common.annotation.Excel;

/**
 * 客户实际使用/停用时间段记录 sb_customer_period_log
 */
public class SbCustomerPeriodLog {

  /** 记录ID(UUID7) */
  private String periodId;

  @Excel(name = "客户ID")
  private String customerId;

  /** 类型：usage=实际使用时段，suspend=实际停用时段 */
  @Excel(name = "类型", readConverterExp = "usage=使用,suspend=停用")
  private String periodType;

  @Excel(name = "开始时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;

  @Excel(name = "结束时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;

  private String createBy;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  public static final String PERIOD_TYPE_USAGE = "usage";
  public static final String PERIOD_TYPE_SUSPEND = "suspend";

  public String getPeriodId() {
    return periodId;
  }

  public void setPeriodId(String periodId) {
    this.periodId = periodId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getPeriodType() {
    return periodType;
  }

  public void setPeriodType(String periodType) {
    this.periodType = periodType;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
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
}
