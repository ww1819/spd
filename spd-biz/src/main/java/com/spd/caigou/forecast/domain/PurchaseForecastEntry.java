package com.spd.caigou.forecast.domain;

import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购预测补货建议明细 purchase_forecast_entry
 */
public class PurchaseForecastEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private Long warehouseId;
    private Long materialId;
    private Long supplierId;
    private BigDecimal stockQty;
    private BigDecimal inTransitQty;
    private BigDecimal avgDailyQty;
    private BigDecimal lowerLimit;
    private BigDecimal upperLimit;
    private BigDecimal ropQty;
    private BigDecimal suggestQty;
    private BigDecimal confirmQty;
    /** 0否 1是 */
    private String selected;
    private BigDecimal price;
    private String formulaRemark;
    private String tenantId;
    private String delFlag;
    private String deleteBy;
    private Date deleteTime;

    private FdMaterial material;
    private FdSupplier supplier;
    private String materialCode;
    private String materialName;
    private String speci;
    private String model;
    private String unitName;
    private String supplierName;
    private BigDecimal minPackageQty;
    /** 高低值（查询填充，来自档案） */
    private String isGz;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public BigDecimal getStockQty() { return stockQty; }
    public void setStockQty(BigDecimal stockQty) { this.stockQty = stockQty; }
    public BigDecimal getInTransitQty() { return inTransitQty; }
    public void setInTransitQty(BigDecimal inTransitQty) { this.inTransitQty = inTransitQty; }
    public BigDecimal getAvgDailyQty() { return avgDailyQty; }
    public void setAvgDailyQty(BigDecimal avgDailyQty) { this.avgDailyQty = avgDailyQty; }
    public BigDecimal getLowerLimit() { return lowerLimit; }
    public void setLowerLimit(BigDecimal lowerLimit) { this.lowerLimit = lowerLimit; }
    public BigDecimal getUpperLimit() { return upperLimit; }
    public void setUpperLimit(BigDecimal upperLimit) { this.upperLimit = upperLimit; }
    public BigDecimal getRopQty() { return ropQty; }
    public void setRopQty(BigDecimal ropQty) { this.ropQty = ropQty; }
    public BigDecimal getSuggestQty() { return suggestQty; }
    public void setSuggestQty(BigDecimal suggestQty) { this.suggestQty = suggestQty; }
    public BigDecimal getConfirmQty() { return confirmQty; }
    public void setConfirmQty(BigDecimal confirmQty) { this.confirmQty = confirmQty; }
    public String getSelected() { return selected; }
    public void setSelected(String selected) { this.selected = selected; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getFormulaRemark() { return formulaRemark; }
    public void setFormulaRemark(String formulaRemark) { this.formulaRemark = formulaRemark; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public FdMaterial getMaterial() { return material; }
    public void setMaterial(FdMaterial material) { this.material = material; }
    public FdSupplier getSupplier() { return supplier; }
    public void setSupplier(FdSupplier supplier) { this.supplier = supplier; }
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
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public BigDecimal getMinPackageQty() { return minPackageQty; }
    public void setMinPackageQty(BigDecimal minPackageQty) { this.minPackageQty = minPackageQty; }
    public String getIsGz() { return isGz; }
    public void setIsGz(String isGz) { this.isGz = isGz; }
}
