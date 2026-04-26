package com.spd.caigou.domain.vo;

import com.spd.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * 采购计划汇总导出（按供应商+物资维度汇总）
 */
public class PurchasePlanSummaryExportVO
{
    @Excel(name = "供应商", width = 24)
    private String supplierName;

    @Excel(name = "产品编码", width = 20)
    private String materialCode;

    @Excel(name = "名称", width = 28)
    private String materialName;

    @Excel(name = "规格", width = 22)
    private String speci;

    @Excel(name = "型号", width = 18)
    private String model;

    @Excel(name = "单位", width = 10)
    private String unitName;

    @Excel(name = "单价")
    private BigDecimal price;

    @Excel(name = "申购数量")
    private BigDecimal applyQty;

    @Excel(name = "库存数量")
    private BigDecimal stockQty;

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public String getMaterialCode()
    {
        return materialCode;
    }

    public void setMaterialCode(String materialCode)
    {
        this.materialCode = materialCode;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getSpeci()
    {
        return speci;
    }

    public void setSpeci(String speci)
    {
        this.speci = speci;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getApplyQty()
    {
        return applyQty;
    }

    public void setApplyQty(BigDecimal applyQty)
    {
        this.applyQty = applyQty;
    }

    public BigDecimal getStockQty()
    {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty)
    {
        this.stockQty = stockQty;
    }
}
