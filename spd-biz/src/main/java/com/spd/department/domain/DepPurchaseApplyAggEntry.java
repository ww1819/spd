package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;

/**
 * 科室汇总申购明细 dep_purchase_apply_agg_entry
 */
public class DepPurchaseApplyAggEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    private String parentId;

    private String tenantId;

    private Long materialId;

    private String materialName;

    private String materialSpec;

    private String unit;

    private BigDecimal unitPrice;

    private BigDecimal qty;

    private BigDecimal amt;

    private String reason;

    private String supplierName;

    private String brand;

    private String model;

    private Integer lineNo;

    /** 所属仓库ID（来自仓库定数 wh_fixed_number，varchar 存储） */
    private String warehouseId;

    /** 高低值标志（1高值 2低值，来自产品档案） */
    private String isGz;

    /** 展示用，非表字段 */
    private String warehouseName;

    private Integer delFlag;

    private String deleteBy;

    private Date deleteTime;

    private FdMaterial material;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSpec() {
        return materialSpec;
    }

    public void setMaterialSpec(String materialSpec) {
        this.materialSpec = materialSpec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getIsGz() {
        return isGz;
    }

    public void setIsGz(String isGz) {
        this.isGz = isGz;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }
}
