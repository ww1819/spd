package com.spd.foundation.domain;

/**
 * 写快照用的产品档案轻量视图（白名单字段）。
 * 由 {@link FdMaterial}（含已 JOIN 的供应商/厂家/单位/分类等嵌套）投影，避免业务侧再按维表单查。
 */
public class MaterialArchiveView {

    private Long id;
    private String code;
    private String name;
    private String speci;
    private String model;
    private String registerNo;
    private String permitNo;

    private Long unitId;
    private String unitCode;
    private String unitName;

    private Long factoryId;
    private String factoryCode;
    private String factoryName;

    private Long supplierId;
    private String supplierCode;
    private String supplierName;

    private Long storeroomId;
    private String storeroomCode;
    private String storeroomName;

    private Long financeCategoryId;
    private String financeCategoryCode;
    private String financeCategoryName;

    private Long defaultWarehouseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getStoreroomId() {
        return storeroomId;
    }

    public void setStoreroomId(Long storeroomId) {
        this.storeroomId = storeroomId;
    }

    public String getStoreroomCode() {
        return storeroomCode;
    }

    public void setStoreroomCode(String storeroomCode) {
        this.storeroomCode = storeroomCode;
    }

    public String getStoreroomName() {
        return storeroomName;
    }

    public void setStoreroomName(String storeroomName) {
        this.storeroomName = storeroomName;
    }

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public String getFinanceCategoryCode() {
        return financeCategoryCode;
    }

    public void setFinanceCategoryCode(String financeCategoryCode) {
        this.financeCategoryCode = financeCategoryCode;
    }

    public String getFinanceCategoryName() {
        return financeCategoryName;
    }

    public void setFinanceCategoryName(String financeCategoryName) {
        this.financeCategoryName = financeCategoryName;
    }

    public Long getDefaultWarehouseId() {
        return defaultWarehouseId;
    }

    public void setDefaultWarehouseId(Long defaultWarehouseId) {
        this.defaultWarehouseId = defaultWarehouseId;
    }
}
