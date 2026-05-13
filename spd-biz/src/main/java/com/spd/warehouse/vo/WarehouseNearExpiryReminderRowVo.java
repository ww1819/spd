package com.spd.warehouse.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 消息提醒：仓库库存近效期明细行（有效期距今天在 30 天及以内且未过期）
 */
public class WarehouseNearExpiryReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String materialCode;
    private String materialName;
    private String materialSpeci;
    private String materialModel;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal amt;
    /** 批号（优先 batch_number，否则 material_no） */
    private String batchNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    private String factoryName;

    /** 仓库名称（便于核对，前端可不展示） */
    private String warehouseName;

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

    public BigDecimal getQty()
    {
        return qty;
    }

    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }

    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public String getFactoryName()
    {
        return factoryName;
    }

    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public String getWarehouseName()
    {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName)
    {
        this.warehouseName = warehouseName;
    }
}
