package com.spd.gz.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 备货出库明细引用关系 gz_shipment_entry_ref
 */
public class GzShipmentEntryRef implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String shipmentEntryId;
    private String srcBillKind;
    private String srcMainId;
    private String srcBillNo;
    private String srcDetailId;
    private String srcInHospitalCode;
    private String tgtBillKind;
    private String tgtMainId;
    private String tgtBillNo;
    private String tgtEntryId;
    private String refPurpose;
    private Long materialId;
    private String materialName;
    private String createBy;
    private Date createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getShipmentEntryId() { return shipmentEntryId; }
    public void setShipmentEntryId(String shipmentEntryId) { this.shipmentEntryId = shipmentEntryId; }
    public String getSrcBillKind() { return srcBillKind; }
    public void setSrcBillKind(String srcBillKind) { this.srcBillKind = srcBillKind; }
    public String getSrcMainId() { return srcMainId; }
    public void setSrcMainId(String srcMainId) { this.srcMainId = srcMainId; }
    public String getSrcBillNo() { return srcBillNo; }
    public void setSrcBillNo(String srcBillNo) { this.srcBillNo = srcBillNo; }
    public String getSrcDetailId() { return srcDetailId; }
    public void setSrcDetailId(String srcDetailId) { this.srcDetailId = srcDetailId; }
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
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
