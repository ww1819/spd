package com.spd.gz.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 高值库存查询 — 备货出/退库明细行
 */
public class GzStockQueryEntryVo
{
    private Long entryId;
    private String billKind;
    private String billTypeName;
    private String orderNo;
    private Integer orderStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;
    private String createBy;
    private String updateBy;
    private String auditBy;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpeci;
    private String materialModel;
    private String unitName;
    private String factoryName;
    private String registerNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date periodDate;
    private String udiNo;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal amt;
    private String batchNo;
    private String batchNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private String inHospitalCode;
    private Long warehouseId;
    private String warehouseName;
    private Long departmentId;
    private String departmentName;
    private Long supplierId;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getBillKind() { return billKind; }
    public void setBillKind(String billKind) { this.billKind = billKind; }
    public String getBillTypeName() { return billTypeName; }
    public void setBillTypeName(String billTypeName) { this.billTypeName = billTypeName; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Date getAuditDate() { return auditDate; }
    public void setAuditDate(Date auditDate) { this.auditDate = auditDate; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
    public String getMaterialModel() { return materialModel; }
    public void setMaterialModel(String materialModel) { this.materialModel = materialModel; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getFactoryName() { return factoryName; }
    public void setFactoryName(String factoryName) { this.factoryName = factoryName; }
    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }
    public Date getPeriodDate() { return periodDate; }
    public void setPeriodDate(Date periodDate) { this.periodDate = periodDate; }
    public String getUdiNo() { return udiNo; }
    public void setUdiNo(String udiNo) { this.udiNo = udiNo; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public BigDecimal getAmt() { return amt; }
    public void setAmt(BigDecimal amt) { this.amt = amt; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getInHospitalCode() { return inHospitalCode; }
    public void setInHospitalCode(String inHospitalCode) { this.inHospitalCode = inHospitalCode; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}
