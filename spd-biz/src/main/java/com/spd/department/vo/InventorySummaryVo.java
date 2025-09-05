package com.spd.department.vo;

import java.math.BigDecimal;

/**
 * 库存汇总VO
 * 
 * @author spd
 * @date 2024-12-19
 */
public class InventorySummaryVo {
    
    /** 耗材编码 */
    private String materialCode;
    
    /** 耗材名称 */
    private String materialName;
    
    /** 规格 */
    private String specification;
    
    /** 型号 */
    private String model;
    
    /** 单位 */
    private String unit;
    
    /** 科室名称 */
    private String departmentName;
    
    /** 仓库名称 */
    private String warehouseName;
    
    /** 库存数量 */
    private BigDecimal totalQty;
    
    /** 平均单价 */
    private BigDecimal avgUnitPrice;
    
    /** 库存金额 */
    private BigDecimal totalAmount;
    
    /** 批次数量 */
    private Integer batchCount;

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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getAvgUnitPrice() {
        return avgUnitPrice;
    }

    public void setAvgUnitPrice(BigDecimal avgUnitPrice) {
        this.avgUnitPrice = avgUnitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(Integer batchCount) {
        this.batchCount = batchCount;
    }
}
