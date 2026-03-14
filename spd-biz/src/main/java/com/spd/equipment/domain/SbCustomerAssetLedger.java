package com.spd.equipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 客户资产台账表 sb_customer_asset_ledger（主键UUID7，客户id，删除标志/删除者/删除时间）
 */
public class SbCustomerAssetLedger extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    private String equipmentSerialNo;
    private String category68Id;
    private String category68Code;
    private String category68ArchiveNo;
    @Excel(name = "名称")
    private String name;
    private String namePinyin;
    private String spec;
    private String model;
    private String registerCertNo;
    private String brandId;
    private String brandName;
    private String manufacturerId;
    private String manufacturerName;
    private String supplierId;
    private String supplierName;
    private String serialNumber;
    private String unit;
    private BigDecimal originalValue;
    private BigDecimal netValue;
    private String deptId;
    private String deptName;
    private String storagePlace;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date acceptanceDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date storageDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date manufactureDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scrapDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedScrapDate;
    private String assetCategoryId;
    private String assetCategoryName;
    private String measuringCategoryId;
    private String measuringCategoryName;
    private Integer calibrationCycleDays;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastCalibrationDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextCalibrationDate;
    private String useStatus;
    private String useStatusName;
    private String labelPrintStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date labelPrintTime;
    private String repairStatus;
    private String repairStatusName;
    private Integer warrantyDays;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyEndTime;
    private String warrantyType;
    private Integer delFlag;
    private String delBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getEquipmentSerialNo() { return equipmentSerialNo; }
    public void setEquipmentSerialNo(String equipmentSerialNo) { this.equipmentSerialNo = equipmentSerialNo; }
    public String getCategory68Id() { return category68Id; }
    public void setCategory68Id(String category68Id) { this.category68Id = category68Id; }
    public String getCategory68Code() { return category68Code; }
    public void setCategory68Code(String category68Code) { this.category68Code = category68Code; }
    public String getCategory68ArchiveNo() { return category68ArchiveNo; }
    public void setCategory68ArchiveNo(String category68ArchiveNo) { this.category68ArchiveNo = category68ArchiveNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNamePinyin() { return namePinyin; }
    public void setNamePinyin(String namePinyin) { this.namePinyin = namePinyin; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getRegisterCertNo() { return registerCertNo; }
    public void setRegisterCertNo(String registerCertNo) { this.registerCertNo = registerCertNo; }
    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(String manufacturerId) { this.manufacturerId = manufacturerId; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getOriginalValue() { return originalValue; }
    public void setOriginalValue(BigDecimal originalValue) { this.originalValue = originalValue; }
    public BigDecimal getNetValue() { return netValue; }
    public void setNetValue(BigDecimal netValue) { this.netValue = netValue; }
    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getStoragePlace() { return storagePlace; }
    public void setStoragePlace(String storagePlace) { this.storagePlace = storagePlace; }
    public Date getAcceptanceDate() { return acceptanceDate; }
    public void setAcceptanceDate(Date acceptanceDate) { this.acceptanceDate = acceptanceDate; }
    public Date getStorageDate() { return storageDate; }
    public void setStorageDate(Date storageDate) { this.storageDate = storageDate; }
    public Date getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(Date manufactureDate) { this.manufactureDate = manufactureDate; }
    public Date getScrapDate() { return scrapDate; }
    public void setScrapDate(Date scrapDate) { this.scrapDate = scrapDate; }
    public Date getExpectedScrapDate() { return expectedScrapDate; }
    public void setExpectedScrapDate(Date expectedScrapDate) { this.expectedScrapDate = expectedScrapDate; }
    public String getAssetCategoryId() { return assetCategoryId; }
    public void setAssetCategoryId(String assetCategoryId) { this.assetCategoryId = assetCategoryId; }
    public String getAssetCategoryName() { return assetCategoryName; }
    public void setAssetCategoryName(String assetCategoryName) { this.assetCategoryName = assetCategoryName; }
    public String getMeasuringCategoryId() { return measuringCategoryId; }
    public void setMeasuringCategoryId(String measuringCategoryId) { this.measuringCategoryId = measuringCategoryId; }
    public String getMeasuringCategoryName() { return measuringCategoryName; }
    public void setMeasuringCategoryName(String measuringCategoryName) { this.measuringCategoryName = measuringCategoryName; }
    public Integer getCalibrationCycleDays() { return calibrationCycleDays; }
    public void setCalibrationCycleDays(Integer calibrationCycleDays) { this.calibrationCycleDays = calibrationCycleDays; }
    public Date getLastCalibrationDate() { return lastCalibrationDate; }
    public void setLastCalibrationDate(Date lastCalibrationDate) { this.lastCalibrationDate = lastCalibrationDate; }
    public Date getNextCalibrationDate() { return nextCalibrationDate; }
    public void setNextCalibrationDate(Date nextCalibrationDate) { this.nextCalibrationDate = nextCalibrationDate; }
    public String getUseStatus() { return useStatus; }
    public void setUseStatus(String useStatus) { this.useStatus = useStatus; }
    public String getUseStatusName() { return useStatusName; }
    public void setUseStatusName(String useStatusName) { this.useStatusName = useStatusName; }
    public String getLabelPrintStatus() { return labelPrintStatus; }
    public void setLabelPrintStatus(String labelPrintStatus) { this.labelPrintStatus = labelPrintStatus; }
    public Date getLabelPrintTime() { return labelPrintTime; }
    public void setLabelPrintTime(Date labelPrintTime) { this.labelPrintTime = labelPrintTime; }
    public String getRepairStatus() { return repairStatus; }
    public void setRepairStatus(String repairStatus) { this.repairStatus = repairStatus; }
    public String getRepairStatusName() { return repairStatusName; }
    public void setRepairStatusName(String repairStatusName) { this.repairStatusName = repairStatusName; }
    public Integer getWarrantyDays() { return warrantyDays; }
    public void setWarrantyDays(Integer warrantyDays) { this.warrantyDays = warrantyDays; }
    public Date getWarrantyStartTime() { return warrantyStartTime; }
    public void setWarrantyStartTime(Date warrantyStartTime) { this.warrantyStartTime = warrantyStartTime; }
    public Date getWarrantyEndTime() { return warrantyEndTime; }
    public void setWarrantyEndTime(Date warrantyEndTime) { this.warrantyEndTime = warrantyEndTime; }
    public String getWarrantyType() { return warrantyType; }
    public void setWarrantyType(String warrantyType) { this.warrantyType = warrantyType; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }
    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }
}
