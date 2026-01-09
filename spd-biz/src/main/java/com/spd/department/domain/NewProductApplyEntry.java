package com.spd.department.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;

/**
 * 新品申购申请明细对象 new_product_apply_entry
 * 
 * @author spd
 * @date 2025-01-01
 */
public class NewProductApplyEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parentId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 耗材名称 */
    @Excel(name = "耗材名称")
    private String materialName;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 单位 */
    @Excel(name = "单位")
    private String unitName;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 生产厂家 */
    @Excel(name = "生产厂家")
    private String factoryName;

    /** 供应商 */
    @Excel(name = "供应商")
    private String supplierName;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 品牌要求 */
    private String brandRequirement;

    /** 特殊说明 */
    private String specialNote;

    /** 病案费用类别 */
    private String caseFeeCategory;

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

    public void setMaterialName(String materialName) 
    {
        this.materialName = materialName;
    }

    public String getMaterialName() 
    {
        return materialName;
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

    public void setUnitName(String unitName) 
    {
        this.unitName = unitName;
    }

    public String getUnitName() 
    {
        return unitName;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setQty(BigDecimal qty) 
    {
        this.qty = qty;
    }

    public BigDecimal getQty() 
    {
        return qty;
    }

    public void setAmt(BigDecimal amt) 
    {
        this.amt = amt;
    }

    public BigDecimal getAmt() 
    {
        return amt;
    }

    public void setFactoryName(String factoryName) 
    {
        this.factoryName = factoryName;
    }

    public String getFactoryName() 
    {
        return factoryName;
    }

    public void setSupplierName(String supplierName) 
    {
        this.supplierName = supplierName;
    }

    public String getSupplierName() 
    {
        return supplierName;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    public String getBrandRequirement() 
    {
        return brandRequirement;
    }

    public void setBrandRequirement(String brandRequirement) 
    {
        this.brandRequirement = brandRequirement;
    }

    public String getSpecialNote() 
    {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) 
    {
        this.specialNote = specialNote;
    }

    public String getCaseFeeCategory() 
    {
        return caseFeeCategory;
    }

    public void setCaseFeeCategory(String caseFeeCategory) 
    {
        this.caseFeeCategory = caseFeeCategory;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("materialId", getMaterialId())
            .append("materialName", getMaterialName())
            .append("speci", getSpeci())
            .append("model", getModel())
            .append("unitName", getUnitName())
            .append("price", getPrice())
            .append("qty", getQty())
            .append("amt", getAmt())
            .append("factoryName", getFactoryName())
            .append("supplierName", getSupplierName())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

