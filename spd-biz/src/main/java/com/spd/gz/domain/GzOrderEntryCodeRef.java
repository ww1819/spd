package com.spd.gz.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 备货验收条码明细引用关系 gz_order_entry_code_ref
 */
public class GzOrderEntryCodeRef implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String srcAcceptanceId;
    private String srcAcceptanceNo;
    private String srcOrderEntryId;
    private String srcBarcodeLineId;
    private String srcInHospitalCode;
    private String tgtBillKind;
    private String tgtMainId;
    private String tgtBillNo;
    private String tgtEntryId;
    private String refPurpose;
    private Long materialId;
    private String materialName;
    private Long warehouseId;
    private String createBy;
    private Date createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getSrcAcceptanceId() { return srcAcceptanceId; }
    public void setSrcAcceptanceId(String srcAcceptanceId) { this.srcAcceptanceId = srcAcceptanceId; }
    public String getSrcAcceptanceNo() { return srcAcceptanceNo; }
    public void setSrcAcceptanceNo(String srcAcceptanceNo) { this.srcAcceptanceNo = srcAcceptanceNo; }
    public String getSrcOrderEntryId() { return srcOrderEntryId; }
    public void setSrcOrderEntryId(String srcOrderEntryId) { this.srcOrderEntryId = srcOrderEntryId; }
    public String getSrcBarcodeLineId() { return srcBarcodeLineId; }
    public void setSrcBarcodeLineId(String srcBarcodeLineId) { this.srcBarcodeLineId = srcBarcodeLineId; }
    public String getSrcInHospitalCode() { return srcInHospitalCode; }
    public void setSrcInHospitalCode(String srcInHospitalCode) { this.srcInHospitalCode = srcInHospitalCode; }
    public String getTgtBillKind() { return tgtBillKind; }
    public void setTgtBillKind(String tgtBillKind) { this.tgtBillKind = tgtBillKind; }
    public String getTgtMainId() { return tgtMainId; }
    public void setTgtMainId(String tgtMainId) { this.tgtMainId = tgtMainId; }
    public String getTgtBillNo() { return tgtBillNo; }
    public void setTgtBillNo(String tgtBillNo) { this.tgtBillNo = tgtBillNo; }
    public String getTgtEntryId() { return tgtEntryId; }
    public void setTgtEntryId(String tgtEntryId) { this.tgtEntryId = tgtEntryId; }
    public String getRefPurpose() { return refPurpose; }
    public void setRefPurpose(String refPurpose) { this.refPurpose = refPurpose; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
