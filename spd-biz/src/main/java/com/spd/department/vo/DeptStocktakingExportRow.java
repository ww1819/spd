package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;

/**
 * 科室盘点导出行（主单字段 + 一条明细，多明细多行）
 */
public class DeptStocktakingExportRow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "盘点单号", width = 22)
    private String stockNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "盘点日期", width = 14, dateFormat = "yyyy-MM-dd")
    private Date stockDate;

    @Excel(name = "供应商", width = 28)
    private String supplierName;

    @Excel(name = "科室编码", width = 14)
    private String departmentCode;

    @Excel(name = "科室名称", width = 22)
    private String departmentName;

    @Excel(name = "仓库", width = 18)
    private String warehouseName;

    @Excel(name = "单据状态", width = 12)
    private String stockStatusLabel;

    @Excel(name = "单头盈亏金额汇总", width = 18)
    private BigDecimal stocktakingProfitAmount;

    @Excel(name = "单头总金额汇总", width = 18)
    private BigDecimal stocktakingTotalAmount;

    @Excel(name = "主单备注", width = 24)
    private String stocktakingRemark;

    @Excel(name = "制单人", width = 14)
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "制单时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 14, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    @Excel(name = "驳回原因", width = 24)
    private String rejectReason;

    @Excel(name = "耗材编码", width = 18)
    private String materialCode;

    @Excel(name = "耗材名称", width = 28)
    private String materialName;

    @Excel(name = "规格", width = 18)
    private String materialSpeci;

    @Excel(name = "型号", width = 18)
    private String materialModel;

    @Excel(name = "单位", width = 10)
    private String unitName;

    @Excel(name = "单价", width = 12)
    private BigDecimal unitPrice;

    @Excel(name = "库存数量", width = 12)
    private BigDecimal bookQty;

    @Excel(name = "盘点数量", width = 12)
    private BigDecimal stockQty;

    @Excel(name = "金额", width = 14)
    private BigDecimal amt;

    @Excel(name = "盈亏数量", width = 12)
    private BigDecimal profitQty;

    @Excel(name = "盈亏金额", width = 14)
    private BigDecimal profitAmount;

    @Excel(name = "批次号", width = 28)
    private String batchNo;

    @Excel(name = "批号", width = 20)
    private String batchNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 14, dateFormat = "yyyy-MM-dd")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 14, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    @Excel(name = "生产厂家", width = 24)
    private String factoryName;

    @Excel(name = "耗材供应商", width = 24)
    private String materialSupplierName;

    @Excel(name = "明细备注", width = 28)
    private String entryRemark;

    public String getStockNo() {
        return stockNo;
    }

    public void setStockNo(String stockNo) {
        this.stockNo = stockNo;
    }

    public Date getStockDate() {
        return stockDate;
    }

    public void setStockDate(Date stockDate) {
        this.stockDate = stockDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
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

    public String getStockStatusLabel() {
        return stockStatusLabel;
    }

    public void setStockStatusLabel(String stockStatusLabel) {
        this.stockStatusLabel = stockStatusLabel;
    }

    public BigDecimal getStocktakingProfitAmount() {
        return stocktakingProfitAmount;
    }

    public void setStocktakingProfitAmount(BigDecimal stocktakingProfitAmount) {
        this.stocktakingProfitAmount = stocktakingProfitAmount;
    }

    public BigDecimal getStocktakingTotalAmount() {
        return stocktakingTotalAmount;
    }

    public void setStocktakingTotalAmount(BigDecimal stocktakingTotalAmount) {
        this.stocktakingTotalAmount = stocktakingTotalAmount;
    }

    public String getStocktakingRemark() {
        return stocktakingRemark;
    }

    public void setStocktakingRemark(String stocktakingRemark) {
        this.stocktakingRemark = stocktakingRemark;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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

    public BigDecimal getBookQty() {
        return bookQty;
    }

    public void setBookQty(BigDecimal bookQty) {
        this.bookQty = bookQty;
    }

    public BigDecimal getStockQty() {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public BigDecimal getProfitQty() {
        return profitQty;
    }

    public void setProfitQty(BigDecimal profitQty) {
        this.profitQty = profitQty;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
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

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getMaterialSupplierName() {
        return materialSupplierName;
    }

    public void setMaterialSupplierName(String materialSupplierName) {
        this.materialSupplierName = materialSupplierName;
    }

    public String getEntryRemark() {
        return entryRemark;
    }

    public void setEntryRemark(String entryRemark) {
        this.entryRemark = entryRemark;
    }
}
