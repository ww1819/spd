package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 财务分类维护对象 fd_finance_category
 * 
 * @author spd
 * @date 2024-03-04
 */
public class FdFinanceCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long financeCategoryId;

    /** 财务分类编码 */
    @Excel(name = "财务分类编码")
    private String financeCategoryCode;

    /** 财务分类名称 */
    @Excel(name = "财务分类名称")
    private String financeCategoryName;

    /** 财务分类地址 */
    @Excel(name = "财务分类地址")
    private String financeCategoryAddress;

    /** 财务分类联系方式 */
    @Excel(name = "财务分类联系方式")
    private String financeCategoryContact;

    /** 删除标识 */
    private Integer delFlag;

    public void setFinanceCategoryId(Long financeCategoryId) 
    {
        this.financeCategoryId = financeCategoryId;
    }

    public Long getFinanceCategoryId() 
    {
        return financeCategoryId;
    }
    public void setFinanceCategoryCode(String financeCategoryCode) 
    {
        this.financeCategoryCode = financeCategoryCode;
    }

    public String getFinanceCategoryCode() 
    {
        return financeCategoryCode;
    }
    public void setFinanceCategoryName(String financeCategoryName) 
    {
        this.financeCategoryName = financeCategoryName;
    }

    public String getFinanceCategoryName() 
    {
        return financeCategoryName;
    }
    public void setFinanceCategoryAddress(String financeCategoryAddress) 
    {
        this.financeCategoryAddress = financeCategoryAddress;
    }

    public String getFinanceCategoryAddress() 
    {
        return financeCategoryAddress;
    }
    public void setFinanceCategoryContact(String financeCategoryContact) 
    {
        this.financeCategoryContact = financeCategoryContact;
    }

    public String getFinanceCategoryContact() 
    {
        return financeCategoryContact;
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
            .append("financeCategoryId", getFinanceCategoryId())
            .append("financeCategoryCode", getFinanceCategoryCode())
            .append("financeCategoryName", getFinanceCategoryName())
            .append("financeCategoryAddress", getFinanceCategoryAddress())
            .append("financeCategoryContact", getFinanceCategoryContact())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
