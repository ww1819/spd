package com.spd.warehouse.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 进销存明细列表
 */
public class PurInventoryVo {

    private static final long serialVersionUID = 1L;

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

    /** 科室 */
    private String departmentName;

    /** 业务单号 */
    private String billNo;

    /** 业务日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date billDate;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 批次 */
    private String batchNo;

    /** 批号 */
    private String batchNumber;

    /** 业务类型 */
    private Integer billType;

    /** 财务分类 */
    private String financeCategoryName;

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getFinanceCategoryName() {
        return financeCategoryName;
    }

    public void setFinanceCategoryName(String financeCategoryName) {
        this.financeCategoryName = financeCategoryName;
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
                .append("departmentName", getDepartmentName())
                .append("billNo", getBillNo())
                .append("billDate", getBillDate())
                .append("beginDate", getBeginDate())
                .append("endDate", getEndDate())
                .append("batchNo", getBatchNo())
                .append("batchNumber", getBatchNumber())
                .append("billType", getBillType())
                .append("financeCategoryName", getFinanceCategoryName())
                .toString();
    }


}
