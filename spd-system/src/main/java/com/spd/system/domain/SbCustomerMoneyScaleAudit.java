package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 客户金额小数位变更审核 sb_customer_money_scale_audit
 */
public class SbCustomerMoneyScaleAudit extends BaseEntity {

  private static final long serialVersionUID = 1L;

  public static final String STATUS_PENDING = "0";
  public static final String STATUS_APPROVED = "1";
  public static final String STATUS_REJECTED = "2";

  private String auditId;
  private String customerId;
  private Integer priceDecimalPlaces;
  private Integer amountDecimalPlaces;
  private String moneyRoundMode;
  /** 0待审 1通过 2驳回 */
  private String auditStatus;
  private String applyBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date applyTime;
  private String applyRemark;
  private String auditBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date auditTime;
  private String auditRemark;
  private Integer oldPriceDecimalPlaces;
  private Integer oldAmountDecimalPlaces;
  private String oldMoneyRoundMode;

  /** 列表展示用 */
  private String customerName;

  public String getAuditId() {
    return auditId;
  }

  public void setAuditId(String auditId) {
    this.auditId = auditId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public Integer getPriceDecimalPlaces() {
    return priceDecimalPlaces;
  }

  public void setPriceDecimalPlaces(Integer priceDecimalPlaces) {
    this.priceDecimalPlaces = priceDecimalPlaces;
  }

  public Integer getAmountDecimalPlaces() {
    return amountDecimalPlaces;
  }

  public void setAmountDecimalPlaces(Integer amountDecimalPlaces) {
    this.amountDecimalPlaces = amountDecimalPlaces;
  }

  public String getMoneyRoundMode() {
    return moneyRoundMode;
  }

  public void setMoneyRoundMode(String moneyRoundMode) {
    this.moneyRoundMode = moneyRoundMode;
  }

  public String getAuditStatus() {
    return auditStatus;
  }

  public void setAuditStatus(String auditStatus) {
    this.auditStatus = auditStatus;
  }

  public String getApplyBy() {
    return applyBy;
  }

  public void setApplyBy(String applyBy) {
    this.applyBy = applyBy;
  }

  public Date getApplyTime() {
    return applyTime;
  }

  public void setApplyTime(Date applyTime) {
    this.applyTime = applyTime;
  }

  public String getApplyRemark() {
    return applyRemark;
  }

  public void setApplyRemark(String applyRemark) {
    this.applyRemark = applyRemark;
  }

  public String getAuditBy() {
    return auditBy;
  }

  public void setAuditBy(String auditBy) {
    this.auditBy = auditBy;
  }

  public Date getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Date auditTime) {
    this.auditTime = auditTime;
  }

  public String getAuditRemark() {
    return auditRemark;
  }

  public void setAuditRemark(String auditRemark) {
    this.auditRemark = auditRemark;
  }

  public Integer getOldPriceDecimalPlaces() {
    return oldPriceDecimalPlaces;
  }

  public void setOldPriceDecimalPlaces(Integer oldPriceDecimalPlaces) {
    this.oldPriceDecimalPlaces = oldPriceDecimalPlaces;
  }

  public Integer getOldAmountDecimalPlaces() {
    return oldAmountDecimalPlaces;
  }

  public void setOldAmountDecimalPlaces(Integer oldAmountDecimalPlaces) {
    this.oldAmountDecimalPlaces = oldAmountDecimalPlaces;
  }

  public String getOldMoneyRoundMode() {
    return oldMoneyRoundMode;
  }

  public void setOldMoneyRoundMode(String oldMoneyRoundMode) {
    this.oldMoneyRoundMode = oldMoneyRoundMode;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
}
