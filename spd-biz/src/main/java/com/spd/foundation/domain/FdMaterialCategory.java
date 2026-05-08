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
    private String materialCategoryId;
    /** 上级分类ID */
    private String parentId;
    /** 上级分类编码 */
    private String parentCode;
    /** 上级分类名称 */
    private String parentName;

    /** 耗材分类编码 */
    @Excel(name = "耗材分类编码")
    private String materialCategoryCode;

    /** 耗材分类名称 */
    @Excel(name = "耗材分类名称")
    private String materialCategoryName;
    /** 拼音简码 */
    @Excel(name = "拼音简码")
    private String pinyinCode;

    /** 耗材分类地址 */
    @Excel(name = "耗材分类地址")
    private String materialCategoryAddress;

    /** 耗材分类联系方式 */
    @Excel(name = "耗材分类联系方式")
    private String materialCategoryContact;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    public void setMaterialCategoryId(String materialCategoryId) 
    {
        this.materialCategoryId = materialCategoryId;
    }

    public String getMaterialCategoryId() 
    {
        return materialCategoryId;
    }
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId()
    {
        return parentId;
    }
    public void setParentCode(String parentCode)
    {
        this.parentCode = parentCode;
    }

    public String getParentCode()
    {
        return parentCode;
    }
    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getParentName()
    {
        return parentName;
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
    public void setPinyinCode(String pinyinCode)
    {
        this.pinyinCode = pinyinCode;
    }

    public String getPinyinCode()
    {
        return pinyinCode;
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

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("materialCategoryId", getMaterialCategoryId())
            .append("parentId", getParentId())
            .append("parentCode", getParentCode())
            .append("parentName", getParentName())
            .append("materialCategoryCode", getMaterialCategoryCode())
            .append("materialCategoryName", getMaterialCategoryName())
            .append("pinyinCode", getPinyinCode())
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
