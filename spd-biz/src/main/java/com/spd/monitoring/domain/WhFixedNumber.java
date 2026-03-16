package com.spd.monitoring.domain;

import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库定数监测对象 wh_fixed_number
 *
 * 对应表：wh_fixed_number
 */
public class WhFixedNumber extends BaseEntity {

    /** 主键（UUID7） */
    private String id;

    /** 耗材产品ID（fd_material.id） */
    private Long materialId;

    /** 仓库ID */
    private Long warehouseId;

    /** 上限数量 */
    private Integer upperLimit;

    /** 下限数量 */
    private Integer lowerLimit;

    /** 有效期提醒天数 */
    private Integer expiryReminder;

    /** 是否监测 1=是 2=否 */
    private String monitoring;

    /** 货位名称 */
    private String location;

    /** 货位ID */
    private Long locationId;

    /** 删除标志（0正常 1删除） */
    private Integer delFlag;

    /** 租户ID（同sb_customer.customer_id） */
    private String tenantId;

    /** 关联：仓库 */
    private FdWarehouse warehouse;

    /** 关联：耗材 */
    private FdMaterial material;

    /** 查询字段：供应商名称 */
    private String supplierName;

    /** 查询字段：生产厂家名称 */
    private String factoryName;

    /** 查询字段：注册证号 */
    private String registerNo;

    /** 查询字段：单位名称 */
    private String unitName;

    /** 查询字段：单价 */
    private java.math.BigDecimal price;

    /** 查询字段：库房分类名称 */
    private String warehouseCategoryName;

    /** 查询参数：耗材名称/编码关键词（用于列表筛选） */
    private String materialName;

    /** 查询参数：供应商ID（用于列表筛选） */
    private Long supplierId;
    /** 查询参数：生产厂家ID（用于列表筛选） */
    private Long factoryId;
    /** 查询参数：库房分类ID（用于列表筛选） */
    private Long storeroomId;

    /** 查询参数：是否高值（1=高值 2=非高值，科室申购只查非高值） */
    private String isGz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getExpiryReminder() {
        return expiryReminder;
    }

    public void setExpiryReminder(Integer expiryReminder) {
        this.expiryReminder = expiryReminder;
    }

    public String getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(String monitoring) {
        this.monitoring = monitoring;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    public String getWarehouseCategoryName() {
        return warehouseCategoryName;
    }

    public void setWarehouseCategoryName(String warehouseCategoryName) {
        this.warehouseCategoryName = warehouseCategoryName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Long getStoreroomId() {
        return storeroomId;
    }

    public void setStoreroomId(Long storeroomId) {
        this.storeroomId = storeroomId;
    }

    public String getIsGz() {
        return isGz;
    }

    public void setIsGz(String isGz) {
        this.isGz = isGz;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("materialId", getMaterialId())
                .append("warehouseId", getWarehouseId())
                .append("upperLimit", getUpperLimit())
                .append("lowerLimit", getLowerLimit())
                .append("expiryReminder", getExpiryReminder())
                .append("monitoring", getMonitoring())
                .append("location", getLocation())
                .append("locationId", getLocationId())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

