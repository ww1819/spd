package com.spd.warehouse.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 盈亏明细报表 VO
 */
public class StkProfitLossEntryVo {

    /** 明细ID */
    private Long id;

    /** 盈亏单号 */
    private String billNo;

    /** 盘点单号 */
    private String stocktakingNo;

    /** 仓库名称 */
    private String warehouseName;

    /** 耗材编码 */
    private String materialCode;

    /** 耗材名称 */
    private String materialName;

    /** 规格 */
    private String materialSpeci;

    /** 型号 */
    private String materialModel;

    /** 单位 */
    private String unitName;

    /** 批次号 */
    private String batchNo;

    /** 当前库存 */
    private BigDecimal bookQty;

    /** 盘点库存 */
    private BigDecimal stockQty;

    /** 盈亏数量 */
    private BigDecimal profitQty;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 盈亏金额 */
    private BigDecimal profitAmount;

    /** 制单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 制单人 */
    private String createrName;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    /** 审核人 */
    private String auditBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getStocktakingNo() {
        return stocktakingNo;
    }

    public void setStocktakingNo(String stocktakingNo) {
        this.stocktakingNo = stocktakingNo;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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

    public BigDecimal getProfitQty() {
        return profitQty;
    }

    public void setProfitQty(BigDecimal profitQty) {
        this.profitQty = profitQty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }
}
