package com.spd.warehouse.domain;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;

/**
 * 耗材使用排名（出/退库净出库按耗材汇总）
 */
public class StkMaterialUsageRankVo
{
    private Long materialId;

    @Excel(name = "耗材名称")
    private String materialName;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "型号")
    private String model;

    @Excel(name = "单位")
    private String unitName;

    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @Excel(name = "数量")
    private BigDecimal quantity;

    @Excel(name = "金额")
    private BigDecimal amount;

    @Excel(name = "生产厂家")
    private String factoryName;

    @Excel(name = "供应商")
    private String supplierName;

    @Excel(name = "占比(%)")
    private BigDecimal ratioPercent;

    @Excel(name = "高值")
    private String isGzLabel;

    @Excel(name = "备注")
    private String remark;

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getSpecification()
    {
        return specification;
    }

    public void setSpecification(String specification)
    {
        this.specification = specification;
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

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getFactoryName()
    {
        return factoryName;
    }

    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public BigDecimal getRatioPercent()
    {
        return ratioPercent;
    }

    public void setRatioPercent(BigDecimal ratioPercent)
    {
        this.ratioPercent = ratioPercent;
    }

    public String getIsGzLabel()
    {
        return isGzLabel;
    }

    public void setIsGzLabel(String isGzLabel)
    {
        this.isGzLabel = isGzLabel;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
