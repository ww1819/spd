package com.spd.warehouse.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 库存明细汇总列表
 */
public class StkInventorySummaryVo {

    /** ID */
    private Long id;

    /** 耗材Code */
    private String materialCode;

    /** 耗材名称 */
    private String materialName;

    /** 耗材数量 */
    private BigDecimal materialQty;

    /** 规格 */
    private String materialSpeci;

    /** 型号 */
    private String materialModel;

    /** 单位 */
    private String unitName;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 金额 */
    private BigDecimal materialAmt;

    /** 仓库 */
    private String warehouseName;

    /** 厂家 */
    private String factoryName;

    /** 供应商 */
    private String supplierName;

    /** 产品档案启停用（fd_material.is_use：1=启用，2=停用） */
    private String materialIsUse;

    /** HIS收费项目编码 */
    private String hisChargeItemCode;
    /** HIS收费项目名称 */
    private String hisChargeItemName;
    /** HIS收费项目规格 */
    private String hisChargeItemSpeci;
    /** HIS收费项目单位 */
    private String hisChargeItemUnit;
    /** HIS收费项目单价 */
    private BigDecimal hisChargeItemPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getMaterialQty() {
        return materialQty;
    }

    public void setMaterialQty(BigDecimal materialQty) {
        this.materialQty = materialQty;
    }

    public String getMaterialSpeci() {
        return materialSpeci;
    }

    public void setMaterialSpeci(String materialSpeci) {
        this.materialSpeci = materialSpeci;
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getMaterialAmt() {
        return materialAmt;
    }

    public void setMaterialAmt(BigDecimal materialAmt) {
        this.materialAmt = materialAmt;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getMaterialIsUse() {
        return materialIsUse;
    }

    public void setMaterialIsUse(String materialIsUse) {
        this.materialIsUse = materialIsUse;
    }

    public String getHisChargeItemCode() {
        return hisChargeItemCode;
    }

    public void setHisChargeItemCode(String hisChargeItemCode) {
        this.hisChargeItemCode = hisChargeItemCode;
    }

    public String getHisChargeItemName() {
        return hisChargeItemName;
    }

    public void setHisChargeItemName(String hisChargeItemName) {
        this.hisChargeItemName = hisChargeItemName;
    }

    public String getHisChargeItemSpeci() {
        return hisChargeItemSpeci;
    }

    public void setHisChargeItemSpeci(String hisChargeItemSpeci) {
        this.hisChargeItemSpeci = hisChargeItemSpeci;
    }

    public String getHisChargeItemUnit() {
        return hisChargeItemUnit;
    }

    public void setHisChargeItemUnit(String hisChargeItemUnit) {
        this.hisChargeItemUnit = hisChargeItemUnit;
    }

    public BigDecimal getHisChargeItemPrice() {
        return hisChargeItemPrice;
    }

    public void setHisChargeItemPrice(BigDecimal hisChargeItemPrice) {
        this.hisChargeItemPrice = hisChargeItemPrice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("materialCode", getMaterialCode())
                .append("materialName", getMaterialName())
                .append("materialQty", getMaterialAmt())
                .append("materialSpeci", getMaterialSpeci())
                .append("materialModel", getMaterialModel())
                .append("unitName", getUnitName())
                .append("unitPrice", getUnitPrice())
                .append("materialAmt", getMaterialAmt())
                .append("warehouseName", getWarehouseName())
                .append("factoryName", getFactoryName())
                .append("supplierName", getSupplierName())
                .toString();
    }


}
