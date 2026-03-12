package com.spd.caigou.domain;

import com.spd.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 采购计划明细关联科室申购单明细(dep) purchase_plan_entry_dep_apply
 *
 * @author spd
 */
public class PurchasePlanEntryDepApply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long purchasePlanEntryId;
    private Long depPurchaseApplyEntryId;
    /** 采购计划主表ID */
    private Long purchasePlanId;
    /** 采购计划单号 */
    private String planNo;
    /** 申购单主表ID */
    private Long depPurchaseApplyId;
    /** 申购单号 */
    private String purchaseBillNo;
    private String delFlag;
    private String deleteBy;
    private Date deleteTime;
    private String tenantId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPurchasePlanEntryId() { return purchasePlanEntryId; }
    public void setPurchasePlanEntryId(Long purchasePlanEntryId) { this.purchasePlanEntryId = purchasePlanEntryId; }
    public Long getDepPurchaseApplyEntryId() { return depPurchaseApplyEntryId; }
    public void setDepPurchaseApplyEntryId(Long depPurchaseApplyEntryId) { this.depPurchaseApplyEntryId = depPurchaseApplyEntryId; }
    public Long getPurchasePlanId() { return purchasePlanId; }
    public void setPurchasePlanId(Long purchasePlanId) { this.purchasePlanId = purchasePlanId; }
    public String getPlanNo() { return planNo; }
    public void setPlanNo(String planNo) { this.planNo = planNo; }
    public Long getDepPurchaseApplyId() { return depPurchaseApplyId; }
    public void setDepPurchaseApplyId(Long depPurchaseApplyId) { this.depPurchaseApplyId = depPurchaseApplyId; }
    public String getPurchaseBillNo() { return purchaseBillNo; }
    public void setPurchaseBillNo(String purchaseBillNo) { this.purchaseBillNo = purchaseBillNo; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
