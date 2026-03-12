package com.spd.caigou.domain;

import com.spd.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 采购计划明细关联科室申购单明细对象 purchase_plan_entry_apply
 *
 * @author spd
 */
public class PurchasePlanEntryApply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 采购计划明细ID */
    private Long purchasePlanEntryId;
    /** 科室申购单明细ID */
    private Long basApplyEntryId;
    /** 删除标志（0存在 1删除） */
    private String delFlag;
    /** 删除者 */
    private String deleteBy;
    /** 删除时间 */
    private Date deleteTime;
    /** 租户ID */
    private String tenantId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPurchasePlanEntryId() { return purchasePlanEntryId; }
    public void setPurchasePlanEntryId(Long purchasePlanEntryId) { this.purchasePlanEntryId = purchasePlanEntryId; }
    public Long getBasApplyEntryId() { return basApplyEntryId; }
    public void setBasApplyEntryId(Long basApplyEntryId) { this.basApplyEntryId = basApplyEntryId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
