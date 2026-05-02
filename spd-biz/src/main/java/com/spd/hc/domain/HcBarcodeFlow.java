package com.spd.hc.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;

public class HcBarcodeFlow {
    private String id;
    private String tenantId;
    @Excel(name = "主档ID")
    private String hcBarcodeMasterId;
    @Excel(name = "条码值")
    private String barcodeValue;
    @Excel(name = "高低值", readConverterExp = "1=高值,2=低值")
    private String valueLevel;
    @Excel(name = "序号")
    private Integer seqNo;
    @Excel(name = "事件码")
    private String eventCode;
    @Excel(name = "事件名称")
    private String eventName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "事件时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date eventTime;
    @Excel(name = "单据域")
    private String billDomain;
    private String billId;
    @Excel(name = "单据号")
    private String billNo;
    private String billEntryId;
    @Excel(name = "流出仓库")
    private String fromWarehouseId;
    @Excel(name = "流入仓库")
    private String toWarehouseId;
    @Excel(name = "流出科室")
    private String fromDepartmentId;
    @Excel(name = "流入科室")
    private String toDepartmentId;
    @Excel(name = "数量")
    private BigDecimal qty;
    @Excel(name = "品名")
    private String materialName;
    @Excel(name = "规格")
    private String materialSpeci;
    @Excel(name = "型号")
    private String materialModel;
    private String operatorId;
    private String operatorName;
    private String remark;
    private Integer delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String deleteBy;
    private Date deleteTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getHcBarcodeMasterId() { return hcBarcodeMasterId; }
    public void setHcBarcodeMasterId(String hcBarcodeMasterId) { this.hcBarcodeMasterId = hcBarcodeMasterId; }
    public String getBarcodeValue() { return barcodeValue; }
    public void setBarcodeValue(String barcodeValue) { this.barcodeValue = barcodeValue; }
    public String getValueLevel() { return valueLevel; }
    public void setValueLevel(String valueLevel) { this.valueLevel = valueLevel; }
    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public Date getEventTime() { return eventTime; }
    public void setEventTime(Date eventTime) { this.eventTime = eventTime; }
    public String getBillDomain() { return billDomain; }
    public void setBillDomain(String billDomain) { this.billDomain = billDomain; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public String getBillEntryId() { return billEntryId; }
    public void setBillEntryId(String billEntryId) { this.billEntryId = billEntryId; }
    public String getFromWarehouseId() { return fromWarehouseId; }
    public void setFromWarehouseId(String fromWarehouseId) { this.fromWarehouseId = fromWarehouseId; }
    public String getToWarehouseId() { return toWarehouseId; }
    public void setToWarehouseId(String toWarehouseId) { this.toWarehouseId = toWarehouseId; }
    public String getFromDepartmentId() { return fromDepartmentId; }
    public void setFromDepartmentId(String fromDepartmentId) { this.fromDepartmentId = fromDepartmentId; }
    public String getToDepartmentId() { return toDepartmentId; }
    public void setToDepartmentId(String toDepartmentId) { this.toDepartmentId = toDepartmentId; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
    public String getMaterialModel() { return materialModel; }
    public void setMaterialModel(String materialModel) { this.materialModel = materialModel; }
    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
}
