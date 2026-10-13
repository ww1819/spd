package com.spd.caigou.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购调价单明细 purchase_price_adjust_entry
 */
public class PurchasePriceAdjustEntry extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String billNo;
    /** 调价类型（报表联查主表） */
    private String adjustType;
    private String adjustTypeName;
    private Integer lineNo;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String speci;
    private String model;
    private String unitName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String financeClass;
    /** 库房分类（报表联查，非表字段） */
    private String warehouseClass;
    private String manufacturer;
    /** 供应商名称（报表联查主表，非表字段） */
    private String supplierName;
    /** 调价原因（报表联查主表，非表字段） */
    private String adjustReason;
    private String regNo;
    private String regValidDate;
    private String packSpeci;
    /** 制单人（报表联查主表） */
    private String createByName;
    /** 制单日期（报表联查主表） */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date billDate;
    /** 审核人（报表联查主表） */
    private String auditByName;
    /** 审核日期（报表联查主表） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditDate;
    private String delFlag;
    private String tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(String adjustType) {
        this.adjustType = adjustType;
    }

    public String getAdjustTypeName() {
        return adjustTypeName;
    }

    public void setAdjustTypeName(String adjustTypeName) {
        this.adjustTypeName = adjustTypeName;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpeci() {
        return speci;
    }

    public void setSpeci(String speci) {
        this.speci = speci;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getFinanceClass() {
        return financeClass;
    }

    public void setFinanceClass(String financeClass) {
        this.financeClass = financeClass;
    }

    public String getWarehouseClass() {
        return warehouseClass;
    }

    public void setWarehouseClass(String warehouseClass) {
        this.warehouseClass = warehouseClass;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getAdjustReason() {
        return adjustReason;
    }

    public void setAdjustReason(String adjustReason) {
        this.adjustReason = adjustReason;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getRegValidDate() {
        return regValidDate;
    }

    public void setRegValidDate(String regValidDate) {
        this.regValidDate = regValidDate;
    }

    public String getPackSpeci() {
        return packSpeci;
    }

    public void setPackSpeci(String packSpeci) {
        this.packSpeci = packSpeci;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getAuditByName() {
        return auditByName;
    }

    public void setAuditByName(String auditByName) {
        this.auditByName = auditByName;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getDelFlag() {
        return delFlag;
    }
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
