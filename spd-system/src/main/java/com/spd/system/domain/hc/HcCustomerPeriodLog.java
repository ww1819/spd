package com.spd.system.domain.hc;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 耗材客户实际使用/停用时间段 hc_customer_period_log
 */
public class HcCustomerPeriodLog {

  public static final String PERIOD_TYPE_USAGE = "usage";
  public static final String PERIOD_TYPE_SUSPEND = "suspend";

  private String periodId;
  private String tenantId;
  private String periodType;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;
  private String createBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  public String getPeriodId() { return periodId; }
  public void setPeriodId(String periodId) { this.periodId = periodId; }
  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
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
