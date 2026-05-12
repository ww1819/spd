package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 消息提醒：科室库存近效期明细行（与科室库存查询「近效期」Tab 口径一致：0～30 天内到期）
 */
public class DepartmentNearExpiryReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String materialCode;
    private String materialName;
    private String materialSpeci;
    private String materialModel;
    private String unitName;
    private BigDecimal qty;
    private BigDecimal amt;
    private Integer daysToExpiry;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date produceDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    /** 科室库存入库/业务日期（与 stk_dep_inventory.warehouse_date 一致，供消息提醒等展示） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date warehouseDate;

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

    public String getMaterialModel()
    {
        return materialModel;
    }

    public void setMaterialModel(String materialModel)
    {
        this.materialModel = materialModel;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public BigDecimal getQty()
    {
        return qty;
    }

    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }

    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public Integer getDaysToExpiry()
    {
        return daysToExpiry;
    }

    public void setDaysToExpiry(Integer daysToExpiry)
    {
        this.daysToExpiry = daysToExpiry;
    }

    public Date getProduceDate()
    {
        return produceDate;
    }

    public void setProduceDate(Date produceDate)
    {
        this.produceDate = produceDate;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public Date getWarehouseDate()
    {
        return warehouseDate;
    }

    public void setWarehouseDate(Date warehouseDate)
    {
        this.warehouseDate = warehouseDate;
    }
}
