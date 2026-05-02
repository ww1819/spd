package com.spd.gz.domain;

import java.math.BigDecimal;
import java.util.Date;

public class GzOrderEntryInhospitalcodeList {
    private Long id;
    private Long parentId;
    private String code;
    private Long detailId;
    private Long materialId;
    private BigDecimal price;
    private BigDecimal qty;
    private String batchNo;
    private String batchNumber;
    private String masterBarcode;
    private String secondaryBarcode;
    private Date endDate;
    private String inHospitalCode;
    private Long warehouseId;
    private Long supplierId;
    private Integer delFlag;
    private Date createDate;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String tenantId;
    /** 快照与归属 */
    private String materialName;
    private String materialSpeci;
    private String materialModel;
    private String materialUnitName;
    private Long factoryId;
    private String factoryName;
    private String supplierName;
    private String warehouseName;
    private String financeCategoryName;
    private String registerNo;
    private String brandName;
    private String hcBarcodeMasterId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getMasterBarcode() { return masterBarcode; }
    public void setMasterBarcode(String masterBarcode) { this.masterBarcode = masterBarcode; }
    public String getSecondaryBarcode() { return secondaryBarcode; }
    public void setSecondaryBarcode(String secondaryBarcode) { this.secondaryBarcode = secondaryBarcode; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getInHospitalCode() { return inHospitalCode; }
    public void setInHospitalCode(String inHospitalCode) { this.inHospitalCode = inHospitalCode; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
    public String getMaterialModel() { return materialModel; }
    public void setMaterialModel(String materialModel) { this.materialModel = materialModel; }
    public String getMaterialUnitName() { return materialUnitName; }
    public void setMaterialUnitName(String materialUnitName) { this.materialUnitName = materialUnitName; }
    public Long getFactoryId() { return factoryId; }
    public void setFactoryId(Long factoryId) { this.factoryId = factoryId; }
    public String getFactoryName() { return factoryName; }
    public void setFactoryName(String factoryName) { this.factoryName = factoryName; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getFinanceCategoryName() { return financeCategoryName; }
    public void setFinanceCategoryName(String financeCategoryName) { this.financeCategoryName = financeCategoryName; }
    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getHcBarcodeMasterId() { return hcBarcodeMasterId; }
    public void setHcBarcodeMasterId(String hcBarcodeMasterId) { this.hcBarcodeMasterId = hcBarcodeMasterId; }
}
