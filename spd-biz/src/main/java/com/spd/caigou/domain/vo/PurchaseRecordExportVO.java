package com.spd.caigou.domain.vo;

import com.spd.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * 耗材采购记录导出（年份月份耗材采购记录）
 * 列：物资名称，物资规格，数量，单位，供货单位，收货人，收货日期
 */
public class PurchaseRecordExportVO {

    @Excel(name = "物资名称")
    private String materialName;

    @Excel(name = "物资规格")
    private String materialSpec;

    @Excel(name = "数量")
    private BigDecimal qty;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "供货单位")
    private String supplierName;

    @Excel(name = "收货人")
    private String receiver;

    @Excel(name = "收货日期")
    private String receiveDate;

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpec() { return materialSpec; }
    public void setMaterialSpec(String materialSpec) { this.materialSpec = materialSpec; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getReceiveDate() { return receiveDate; }
    public void setReceiveDate(String receiveDate) { this.receiveDate = receiveDate; }
}
