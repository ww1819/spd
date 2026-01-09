package com.spd.department.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 院内同类产品信息对象 new_product_apply_detail
 * 
 * @author spd
 * @date 2025-01-01
 */
public class NewProductApplyDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parentId;

    /** 我院同类产品 */
    @Excel(name = "我院同类产品")
    private String similarProduct;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

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

    public void setSimilarProduct(String similarProduct) 
    {
        this.similarProduct = similarProduct;
    }

    public String getSimilarProduct() 
    {
        return similarProduct;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("similarProduct", getSimilarProduct())
            .append("speci", getSpeci())
            .append("model", getModel())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
