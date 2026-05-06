package com.spd.hc.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 低值出入库定数包/院内码明细
 */
public class StkLvIoInhospitalBarcode {
    private String id;
    private String tenantId;
    private Integer ioDirection;
    private String stkIoBillId;
    private String stkIoBillNo;
    private Integer stkIoBillType;
    private String stkIoBillEntryId;
    private String warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String departmentId;
    private String departmentCode;
    private String departmentName;
    private String materialId;
    private String materialCode;
    private String materialName;
    private String materialSpeci;
    private String materialModel;
    private String materialUnitName;
    private String factoryId;
    private String factoryName;
    private String supplierId;
    private String supplierName;
    private String batchNo;
    private String batchNumber;
    private Date beginTime;
    private Date endTime;
    private BigDecimal unitPrice;
    private BigDecimal qtyInPackage;
    private Integer packageIndex;
    private Integer packageTotal;
    private String inHospitalCode;
    private String fixedPackageBarcode;
    private String stkInventoryId;
    private String stkDepInventoryId;
    private String hcBarcodeMasterId;
    private String auditBy;
    private Date auditTime;
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
    public Integer getIoDirection() { return ioDirection; }
    public void setIoDirection(Integer ioDirection) { this.ioDirection = ioDirection; }
    public String getStkIoBillId() { return stkIoBillId; }
    public void setStkIoBillId(String stkIoBillId) { this.stkIoBillId = stkIoBillId; }
    public String getStkIoBillNo() { return stkIoBillNo; }
    public void setStkIoBillNo(String stkIoBillNo) { this.stkIoBillNo = stkIoBillNo; }
    public Integer getStkIoBillType() { return stkIoBillType; }
    public void setStkIoBillType(Integer stkIoBillType) { this.stkIoBillType = stkIoBillType; }
    public String getStkIoBillEntryId() { return stkIoBillEntryId; }
    public void setStkIoBillEntryId(String stkIoBillEntryId) { this.stkIoBillEntryId = stkIoBillEntryId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
    public String getMaterialModel() { return materialModel; }
    public void setMaterialModel(String materialModel) { this.materialModel = materialModel; }
    public String getMaterialUnitName() { return materialUnitName; }
    public void setMaterialUnitName(String materialUnitName) { this.materialUnitName = materialUnitName; }
    public String getFactoryId() { return factoryId; }
    public void setFactoryId(String factoryId) { this.factoryId = factoryId; }
    public String getFactoryName() { return factoryName; }
    public void setFactoryName(String factoryName) { this.factoryName = factoryName; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getQtyInPackage() { return qtyInPackage; }
    public void setQtyInPackage(BigDecimal qtyInPackage) { this.qtyInPackage = qtyInPackage; }
    public Integer getPackageIndex() { return packageIndex; }
    public void setPackageIndex(Integer packageIndex) { this.packageIndex = packageIndex; }
    public Integer getPackageTotal() { return packageTotal; }
    public void setPackageTotal(Integer packageTotal) { this.packageTotal = packageTotal; }
    public String getInHospitalCode() { return inHospitalCode; }
    public void setInHospitalCode(String inHospitalCode) { this.inHospitalCode = inHospitalCode; }
    public String getFixedPackageBarcode() { return fixedPackageBarcode; }
    public void setFixedPackageBarcode(String fixedPackageBarcode) { this.fixedPackageBarcode = fixedPackageBarcode; }
    public String getStkInventoryId() { return stkInventoryId; }
    public void setStkInventoryId(String stkInventoryId) { this.stkInventoryId = stkInventoryId; }
    public String getStkDepInventoryId() { return stkDepInventoryId; }
    public void setStkDepInventoryId(String stkDepInventoryId) { this.stkDepInventoryId = stkDepInventoryId; }
    public String getHcBarcodeMasterId() { return hcBarcodeMasterId; }
    public void setHcBarcodeMasterId(String hcBarcodeMasterId) { this.hcBarcodeMasterId = hcBarcodeMasterId; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
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
