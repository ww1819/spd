package com.spd.hc.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;

/** 高低值条码归属主档 */
public class HcBarcodeMaster {
    private String id;
    private String tenantId;
    @Excel(name = "条码值")
    private String barcodeValue;
    @Excel(name = "高低值", readConverterExp = "1=高值,2=低值")
    private String valueLevel;
    @Excel(name = "业务类型编码")
    private String businessTypeCode;
    @Excel(name = "业务类型")
    private String businessTypeName;
    private String sourceTable;
    private String sourceRowId;
    @Excel(name = "单据域")
    private String billDomain;
    private String billId;
    @Excel(name = "单据号")
    private String billNo;
    private String billType;
    private String billEntryId;
    private String materialId;
    @Excel(name = "耗材编码")
    private String materialCode;
    @Excel(name = "耗材名称")
    private String materialName;
    @Excel(name = "规格")
    private String materialSpeci;
    @Excel(name = "型号")
    private String materialModel;
    @Excel(name = "单位")
    private String materialUnitName;
    private String factoryId;
    @Excel(name = "厂家")
    private String factoryName;
    private String supplierId;
    @Excel(name = "供应商")
    private String supplierName;
    private String warehouseId;
    @Excel(name = "归属仓库")
    private String warehouseName;
    private String departmentId;
    @Excel(name = "归属科室")
    private String departmentName;
    @Excel(name = "批次号")
    private String batchNo;
    @Excel(name = "批号")
    private String batchNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 12, dateFormat = "yyyy-MM-dd")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 12, dateFormat = "yyyy-MM-dd")
    private Date endTime;
    @Excel(name = "主条码")
    private String masterBarcode;
    @Excel(name = "辅条码")
    private String secondaryBarcode;
    @Excel(name = "定数包含量")
    private BigDecimal fixedPackageQty;
    @Excel(name = "当前持有方", readConverterExp = "WH=仓库,DEPT=科室,UNKNOWN=未确定")
    private String currentHolderType;
    @Excel(name = "当前仓库ID")
    private String currentWarehouseId;
    @Excel(name = "当前科室ID")
    private String currentDepartmentId;
    @Excel(name = "状态", readConverterExp = "ACTIVE=有效,CONSUMED=已消耗,CANCELLED=作废")
    private String status;
    private Integer delFlag;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String deleteBy;
    private Date deleteTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getBarcodeValue() { return barcodeValue; }
    public void setBarcodeValue(String barcodeValue) { this.barcodeValue = barcodeValue; }
    public String getValueLevel() { return valueLevel; }
    public void setValueLevel(String valueLevel) { this.valueLevel = valueLevel; }
    public String getBusinessTypeCode() { return businessTypeCode; }
    public void setBusinessTypeCode(String businessTypeCode) { this.businessTypeCode = businessTypeCode; }
    public String getBusinessTypeName() { return businessTypeName; }
    public void setBusinessTypeName(String businessTypeName) { this.businessTypeName = businessTypeName; }
    public String getSourceTable() { return sourceTable; }
    public void setSourceTable(String sourceTable) { this.sourceTable = sourceTable; }
    public String getSourceRowId() { return sourceRowId; }
    public void setSourceRowId(String sourceRowId) { this.sourceRowId = sourceRowId; }
    public String getBillDomain() { return billDomain; }
    public void setBillDomain(String billDomain) { this.billDomain = billDomain; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }
    public String getBillEntryId() { return billEntryId; }
    public void setBillEntryId(String billEntryId) { this.billEntryId = billEntryId; }
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
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getMasterBarcode() { return masterBarcode; }
    public void setMasterBarcode(String masterBarcode) { this.masterBarcode = masterBarcode; }
    public String getSecondaryBarcode() { return secondaryBarcode; }
    public void setSecondaryBarcode(String secondaryBarcode) { this.secondaryBarcode = secondaryBarcode; }
    public BigDecimal getFixedPackageQty() { return fixedPackageQty; }
    public void setFixedPackageQty(BigDecimal fixedPackageQty) { this.fixedPackageQty = fixedPackageQty; }
    public String getCurrentHolderType() { return currentHolderType; }
    public void setCurrentHolderType(String currentHolderType) { this.currentHolderType = currentHolderType; }
    public String getCurrentWarehouseId() { return currentWarehouseId; }
    public void setCurrentWarehouseId(String currentWarehouseId) { this.currentWarehouseId = currentWarehouseId; }
    public String getCurrentDepartmentId() { return currentDepartmentId; }
    public void setCurrentDepartmentId(String currentDepartmentId) { this.currentDepartmentId = currentDepartmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
