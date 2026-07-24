package com.spd.caigou.forecast.domain;

import java.math.BigDecimal;

/**
 * 定数监测启用物料（含档案字段）
 */
public class ForecastFixedMaterialRow {

    private Long materialId;
    private Long warehouseId;
    private Integer lowerLimit;
    private Integer upperLimit;
    private Long supplierId;
    private BigDecimal price;
    private BigDecimal minPackageQty;
    private String materialCode;
    private String materialName;
    private String speci;
    private String model;
    private String unitName;
    private String isGz;
    private String supplierName;

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Integer getLowerLimit() { return lowerLimit; }
    public void setLowerLimit(Integer lowerLimit) { this.lowerLimit = lowerLimit; }
    public Integer getUpperLimit() { return upperLimit; }
    public void setUpperLimit(Integer upperLimit) { this.upperLimit = upperLimit; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getMinPackageQty() { return minPackageQty; }
    public void setMinPackageQty(BigDecimal minPackageQty) { this.minPackageQty = minPackageQty; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getSpeci() { return speci; }
    public void setSpeci(String speci) { this.speci = speci; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getIsGz() { return isGz; }
    public void setIsGz(String isGz) { this.isGz = isGz; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
