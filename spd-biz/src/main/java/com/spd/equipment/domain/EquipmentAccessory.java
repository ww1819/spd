package com.spd.equipment.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备配件主数据 equipment_accessory
 */
public class EquipmentAccessory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    @Excel(name = "配件编码")
    private String accessoryCode;
    @Excel(name = "配件名称")
    private String name;
    @Excel(name = "规格型号")
    private String spec;
    @Excel(name = "单位")
    private String unit;
    @Excel(name = "生产厂商")
    private String manufacturerName;
    private Integer delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccessoryCode() {
        return accessoryCode;
    }

    public void setAccessoryCode(String accessoryCode) {
        this.accessoryCode = accessoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
