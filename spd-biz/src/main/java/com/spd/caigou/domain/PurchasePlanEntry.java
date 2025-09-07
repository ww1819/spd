package com.spd.caigou.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 采购计划明细对象 purchase_plan_entry
 *
 * @author spd
 * @date 2024-01-15
 */
public class PurchasePlanEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 主表ID */
    private Long parentId;

    /** 耗材ID */
    private Long materialId;

    /** 计划数量 */
    @Excel(name = "计划数量")
    private BigDecimal qty;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 耗材信息 */
    private FdMaterial material;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setMaterialId(Long materialId) 
    {
        this.materialId = materialId;
    }

    public Long getMaterialId() 
    {
        return materialId;
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

    public void setSpeci(String speci) 
    {
        this.speci = speci;
    }

    public String getSpeci() 
    {
        return speci;
    }

    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public FdMaterial getMaterial() 
    {
        return material;
    }

    public void setMaterial(FdMaterial material) 
    {
        this.material = material;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("materialId", getMaterialId())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("speci", getSpeci())
            .append("model", getModel())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
