package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;

/**
 * 盘点明细对象 stk_io_stocktaking_entry
 *
 * @author spd
 * @date 2024-06-27
 */
public class StkIoStocktakingEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parenId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long commodityId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 批号 */
    @Excel(name = "批号")
    private String batchNumber;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date beginTime;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 删除标识 */
    private Integer delFlag;

    /** 盘点数量 */
    @Excel(name = "盘点数量")
    private BigDecimal stockQty;

    /** 盈亏数量 */
    @Excel(name = "盈亏数量")
    private BigDecimal profitQty;

    /** 盘点金额 */
    @Excel(name = "盘点金额")
    private BigDecimal stockAmount;

    /** 盈亏金额 */
    @Excel(name = "盈亏金额")
    private BigDecimal profitAmount;

    /** 耗材对象 */
    private FdMaterial material;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setParenId(Long parenId)
    {
        this.parenId = parenId;
    }

    public Long getParenId()
    {
        return parenId;
    }
    public void setCommodityId(Long commodityId)
    {
        this.commodityId = commodityId;
    }

    public Long getCommodityId()
    {
        return commodityId;
    }
    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }
    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }
    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getQty()
    {
        return qty;
    }
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }
    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }
    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }
    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }
    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
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

    public BigDecimal getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(BigDecimal stockAmount) {
        this.stockAmount = stockAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parenId", getParenId())
            .append("commodityId", getCommodityId())
            .append("materialId", getMaterialId())
            .append("unitPrice", getUnitPrice())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("batchNumber", getBatchNumber())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .append("stockQty", getStockQty())
            .append("profitQty", getProfitQty())
            .append("stockAmount", getStockAmount())
            .append("profitAmount", getProfitAmount())
            .toString();
    }
}
