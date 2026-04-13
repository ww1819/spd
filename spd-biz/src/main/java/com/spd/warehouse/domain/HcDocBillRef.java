package com.spd.warehouse.domain;

import java.math.BigDecimal;

import com.spd.common.core.domain.BaseEntity;

/**
 * 单据引用关联 hc_doc_bill_ref（不落 stk_io_bill 表，随保存写入）
 */
public class HcDocBillRef extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 36 位 UUID7（与 {@link com.spd.common.utils.uuid.UUID7#generateUUID7()} 一致） */
    private String id;
    private String tenantId;
    /** 业务域：默认 STK_IO_BILL，可扩展 */
    private String bizDomain;
    /** @see com.spd.warehouse.constants.HcDocBillRefType */
    private String refType;
    private String srcBillKind;
    private String srcBillId;
    private String srcBillNo;
    private String srcEntryId;
    private Integer srcEntryLineNo;
    private String tgtBillKind;
    private String tgtBillId;
    private String tgtBillNo;
    private String tgtEntryId;
    /** 与目标明细行顺序对齐，从 1 起 */
    private Integer lineNo;
    private BigDecimal refQty;
    private BigDecimal refAmt;
    private String lockWarehouseId;
    private String lockSupplierId;
    private String lockDepartmentId;
    private Integer delFlag;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getBizDomain() { return bizDomain; }
    public void setBizDomain(String bizDomain) { this.bizDomain = bizDomain; }
    public String getRefType() { return refType; }
    public void setRefType(String refType) { this.refType = refType; }
    public String getSrcBillKind() { return srcBillKind; }
    public void setSrcBillKind(String srcBillKind) { this.srcBillKind = srcBillKind; }
    public String getSrcBillId() { return srcBillId; }
    public void setSrcBillId(String srcBillId) { this.srcBillId = srcBillId; }
    public String getSrcBillNo() { return srcBillNo; }
    public void setSrcBillNo(String srcBillNo) { this.srcBillNo = srcBillNo; }
    public String getSrcEntryId() { return srcEntryId; }
    public void setSrcEntryId(String srcEntryId) { this.srcEntryId = srcEntryId; }
    public Integer getSrcEntryLineNo() { return srcEntryLineNo; }
    public void setSrcEntryLineNo(Integer srcEntryLineNo) { this.srcEntryLineNo = srcEntryLineNo; }
    public String getTgtBillKind() { return tgtBillKind; }
    public void setTgtBillKind(String tgtBillKind) { this.tgtBillKind = tgtBillKind; }
    public String getTgtBillId() { return tgtBillId; }
    public void setTgtBillId(String tgtBillId) { this.tgtBillId = tgtBillId; }
    public String getTgtBillNo() { return tgtBillNo; }
    public void setTgtBillNo(String tgtBillNo) { this.tgtBillNo = tgtBillNo; }
    public String getTgtEntryId() { return tgtEntryId; }
    public void setTgtEntryId(String tgtEntryId) { this.tgtEntryId = tgtEntryId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public BigDecimal getRefQty() { return refQty; }
    public void setRefQty(BigDecimal refQty) { this.refQty = refQty; }
    public BigDecimal getRefAmt() { return refAmt; }
    public void setRefAmt(BigDecimal refAmt) { this.refAmt = refAmt; }
    public String getLockWarehouseId() { return lockWarehouseId; }
    public void setLockWarehouseId(String lockWarehouseId) { this.lockWarehouseId = lockWarehouseId; }
    public String getLockSupplierId() { return lockSupplierId; }
    public void setLockSupplierId(String lockSupplierId) { this.lockSupplierId = lockSupplierId; }
    public String getLockDepartmentId() { return lockDepartmentId; }
    public void setLockDepartmentId(String lockDepartmentId) { this.lockDepartmentId = lockDepartmentId; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
}
