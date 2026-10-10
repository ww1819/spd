package com.spd.caigou.domain;

import com.spd.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 采购调价单明细 purchase_price_adjust_entry
 */
public class PurchasePriceAdjustEntry extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String billNo;
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
    private String manufacturer;
    private String regNo;
    private String regValidDate;
    private String packSpeci;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
