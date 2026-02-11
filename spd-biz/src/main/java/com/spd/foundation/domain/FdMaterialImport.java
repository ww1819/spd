package com.spd.foundation.domain;

import com.spd.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 耗材产品导入中间表实体
 *
 * 用于暂存 Excel 导入的原始数据，便于后续数据清洗和审计。
 *
 * 表：fd_material_import
 */
public class FdMaterialImport extends BaseEntity {

    /** 主键（UUID7） */
    private String id;

    /** 耗材编码 */
    private String code;

    /** 耗材名称 */
    private String name;

    /** 导入的供应商原始值（名称或ID字符串） */
    private String supplierValue;

    /** 导入的生产厂家原始值（名称或ID字符串） */
    private String factoryValue;

    /** 导入的库房分类原始值（名称或ID字符串） */
    private String warehouseCategoryValue;

    /** 导入的财务分类原始值（名称或ID字符串） */
    private String financeCategoryValue;

    /** 导入的单位原始值（名称或ID字符串） */
    private String unitValue;

    /** 导入的货位原始值（名称或ID字符串） */
    private String locationValue;

    /** 导入的原始整行数据（JSON字符串） */
    private String rawData;

    /** 导入时间 */
    private Date importTime;

    /** 导入操作人 */
    private String operator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplierValue() {
        return supplierValue;
    }

    public void setSupplierValue(String supplierValue) {
        this.supplierValue = supplierValue;
    }

    public String getFactoryValue() {
        return factoryValue;
    }

    public void setFactoryValue(String factoryValue) {
        this.factoryValue = factoryValue;
    }

    public String getWarehouseCategoryValue() {
        return warehouseCategoryValue;
    }

    public void setWarehouseCategoryValue(String warehouseCategoryValue) {
        this.warehouseCategoryValue = warehouseCategoryValue;
    }

    public String getFinanceCategoryValue() {
        return financeCategoryValue;
    }

    public void setFinanceCategoryValue(String financeCategoryValue) {
        this.financeCategoryValue = financeCategoryValue;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public void setLocationValue(String locationValue) {
        this.locationValue = locationValue;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

