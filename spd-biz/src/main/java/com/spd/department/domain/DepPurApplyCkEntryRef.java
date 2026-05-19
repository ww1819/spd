package com.spd.department.domain;

import java.math.BigDecimal;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室申购单明细与出库单明细关联 dep_pur_apply_ck_entry_ref
 */
public class DepPurApplyCkEntryRef extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private Long depPurApplyId;
    private String depPurApplyBillNo;
    private Long depPurApplyEntryId;
    private String ckBillId;
    private String ckBillNo;
    private String ckEntryId;
    private BigDecimal refQty;
    private BigDecimal refAmt;
    /** 1有效 0已解除 */
    private Integer linkStatus;
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getDepPurApplyId() {
        return depPurApplyId;
    }

    public void setDepPurApplyId(Long depPurApplyId) {
        this.depPurApplyId = depPurApplyId;
    }

    public String getDepPurApplyBillNo() {
        return depPurApplyBillNo;
    }

    public void setDepPurApplyBillNo(String depPurApplyBillNo) {
        this.depPurApplyBillNo = depPurApplyBillNo;
    }

    public Long getDepPurApplyEntryId() {
        return depPurApplyEntryId;
    }

    public void setDepPurApplyEntryId(Long depPurApplyEntryId) {
        this.depPurApplyEntryId = depPurApplyEntryId;
    }

    public String getCkBillId() {
        return ckBillId;
    }

    public void setCkBillId(String ckBillId) {
        this.ckBillId = ckBillId;
    }

    public String getCkBillNo() {
        return ckBillNo;
    }

    public void setCkBillNo(String ckBillNo) {
        this.ckBillNo = ckBillNo;
    }

    public String getCkEntryId() {
        return ckEntryId;
    }

    public void setCkEntryId(String ckEntryId) {
        this.ckEntryId = ckEntryId;
    }

    public BigDecimal getRefQty() {
        return refQty;
    }

    public void setRefQty(BigDecimal refQty) {
        this.refQty = refQty;
    }

    public BigDecimal getRefAmt() {
        return refAmt;
    }

    public void setRefAmt(BigDecimal refAmt) {
        this.refAmt = refAmt;
    }

    public Integer getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(Integer linkStatus) {
        this.linkStatus = linkStatus;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
