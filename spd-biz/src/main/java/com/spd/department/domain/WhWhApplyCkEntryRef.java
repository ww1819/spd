package com.spd.department.domain;

import java.math.BigDecimal;
import com.spd.common.core.domain.BaseEntity;

/**
 * 库房申请单明细与出库单明细关联 wh_wh_apply_ck_entry_ref
 */
public class WhWhApplyCkEntryRef extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String whApplyId;
    private String whApplyBillNo;
    private String whApplyEntryId;
    private String ckBillId;
    private String ckBillNo;
    private String ckEntryId;
    private BigDecimal refQty;
    private BigDecimal refAmt;
    /** 1有效 0已解除 */
    private Integer linkStatus;
    private Integer delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getWhApplyId() {
        return whApplyId;
    }

    public void setWhApplyId(String whApplyId) {
        this.whApplyId = whApplyId;
    }

    public String getWhApplyBillNo() {
        return whApplyBillNo;
    }

    public void setWhApplyBillNo(String whApplyBillNo) {
        this.whApplyBillNo = whApplyBillNo;
    }

    public String getWhApplyEntryId() {
        return whApplyEntryId;
    }

    public void setWhApplyEntryId(String whApplyEntryId) {
        this.whApplyEntryId = whApplyEntryId;
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
