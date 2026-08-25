package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 消息提醒：科室库存预警明细行（按科室+耗材汇总，与科室库存查询「科室库存预警」Tab 口径一致）
 */
public class DepartmentInventoryAlertReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String departmentName;
    private String materialCode;
    private String materialName;
    private String materialSpeci;
    private String unitName;
    private BigDecimal unitPrice;
    private BigDecimal qty;
    private BigDecimal minQtyWarning;
    private BigDecimal maxQtyWarning;
    private String factoryName;

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
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

    public String getMaterialSpeci()
    {
        return materialSpeci;
    }

    public void setMaterialSpeci(String materialSpeci)
    {
        this.materialSpeci = materialSpeci;
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

    public BigDecimal getQty()
    {
        return qty;
    }

    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getMinQtyWarning()
    {
        return minQtyWarning;
    }

    public void setMinQtyWarning(BigDecimal minQtyWarning)
    {
        this.minQtyWarning = minQtyWarning;
    }

    public BigDecimal getMaxQtyWarning()
    {
        return maxQtyWarning;
    }

    public void setMaxQtyWarning(BigDecimal maxQtyWarning)
    {
        this.maxQtyWarning = maxQtyWarning;
    }

    public String getFactoryName()
    {
        return factoryName;
    }

    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }
}
