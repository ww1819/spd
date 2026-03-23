package com.spd.system.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备系统客户表（SaaS 租户） sb_customer
 */
public class SbCustomer extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 客户ID（UUID7） */
  @Excel(name = "客户编号")
  private String customerId;

  /** 客户名称 */
  @Excel(name = "客户名称")
  private String customerName;

  /** 客户编码 */
  @Excel(name = "客户编码")
  private String customerCode;

  /** 状态（0正常 1停用） */
  @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
  private String status;

  /** 计划停用时间（设备侧），到达后租户无法使用设备系统 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date plannedDisableTime;

  /** 耗材侧状态（0正常 1停用），与设备侧 status 分开 */
  private String hcStatus;

  /** 计划停用时间（耗材侧），到达后租户无法使用耗材系统 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date hcPlannedDisableTime;

  /** 启停用原因（仅更新状态时使用，不入库） */
  private transient String statusChangeReason;

  /** 租户枚举键（关联 TenantEnum.name），用于与代码内租户列表一致并区分条件分支 */
  private String tenantKey;

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

  @NotBlank(message = "客户名称不能为空")
  @Size(min = 0, max = 100, message = "客户名称长度不能超过100个字符")
  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @Size(min = 0, max = 64, message = "客户编码长度不能超过64个字符")
  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getPlannedDisableTime() {
    return plannedDisableTime;
  }

  public void setPlannedDisableTime(Date plannedDisableTime) {
    this.plannedDisableTime = plannedDisableTime;
  }

  public String getHcStatus() {
    return hcStatus;
  }

  public void setHcStatus(String hcStatus) {
    this.hcStatus = hcStatus;
  }

  public Date getHcPlannedDisableTime() {
    return hcPlannedDisableTime;
  }

  public void setHcPlannedDisableTime(Date hcPlannedDisableTime) {
    this.hcPlannedDisableTime = hcPlannedDisableTime;
  }

  public String getStatusChangeReason() {
    return statusChangeReason;
  }

  public void setStatusChangeReason(String statusChangeReason) {
    this.statusChangeReason = statusChangeReason;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public void setTenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
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
        .append("customerName", getCustomerName())
        .append("customerCode", getCustomerCode())
        .append("status", getStatus())
        .append("tenantKey", getTenantKey())
        .append("createBy", getCreateBy())
        .append("createTime", getCreateTime())
        .append("updateBy", getUpdateBy())
        .append("updateTime", getUpdateTime())
        .append("remark", getRemark())
        .append("deleteBy", getDeleteBy())
        .append("deleteTime", getDeleteTime())
        .toString();
  }
}
