package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 耗材分类维护对象 fd_material_category
 * 
 * @author spd
 * @date 2024-03-04
 */
public class FdMaterialCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long materialCategoryId;

    /** 耗材分类编码 */
    @Excel(name = "耗材分类编码")
    private String materialCategoryCode;

    /** 耗材分类名称 */
    @Excel(name = "耗材分类名称")
    private String materialCategoryName;

    /** 耗材分类地址 */
    @Excel(name = "耗材分类地址")
    private String materialCategoryAddress;

    /** 耗材分类联系方式 */
    @Excel(name = "耗材分类联系方式")
    private String materialCategoryContact;

    /** 删除标识 */
    private Integer delFlag;

    public void setMaterialCategoryId(Long materialCategoryId) 
    {
        this.materialCategoryId = materialCategoryId;
    }

    public Long getMaterialCategoryId() 
    {
        return materialCategoryId;
    }
    public void setMaterialCategoryCode(String materialCategoryCode) 
    {
        this.materialCategoryCode = materialCategoryCode;
    }

    public String getMaterialCategoryCode() 
    {
        return materialCategoryCode;
    }
    public void setMaterialCategoryName(String materialCategoryName) 
    {
        this.materialCategoryName = materialCategoryName;
    }

    public String getMaterialCategoryName() 
    {
        return materialCategoryName;
    }
    public void setMaterialCategoryAddress(String materialCategoryAddress) 
    {
        this.materialCategoryAddress = materialCategoryAddress;
    }

    public String getMaterialCategoryAddress() 
    {
        return materialCategoryAddress;
    }
    public void setMaterialCategoryContact(String materialCategoryContact) 
    {
        this.materialCategoryContact = materialCategoryContact;
    }

    public String getMaterialCategoryContact() 
    {
        return materialCategoryContact;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("materialCategoryId", getMaterialCategoryId())
            .append("materialCategoryCode", getMaterialCategoryCode())
            .append("materialCategoryName", getMaterialCategoryName())
            .append("materialCategoryAddress", getMaterialCategoryAddress())
            .append("materialCategoryContact", getMaterialCategoryContact())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
