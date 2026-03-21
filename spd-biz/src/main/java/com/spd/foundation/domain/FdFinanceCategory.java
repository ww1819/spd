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

    /** 上级分类ID */
    @Excel(name = "上级分类id")
    private Long parentId;

    /** 财务分类编码 */
    @Excel(name = "财务分类编码")
    private String financeCategoryCode;

    /** 财务分类名称 */
    @Excel(name = "财务分类名称")
    private String financeCategoryName;

    /** 拼音简码 */
    @Excel(name = "简码")
    private String referredName;

    /** 财务分类地址 */
    @Excel(name = "财务分类地址")
    private String financeCategoryAddress;

    /** 财务分类联系方式 */
    @Excel(name = "财务分类联系方式")
    private String financeCategoryContact;

    /** 删除标识 */
    @Excel(name = "删除标识")
    private Integer delFlag;

    @Excel(name = "使用状态", readConverterExp = "停用/在用")
    private String isUse;

    /** 租户ID（同 sb_customer.customer_id） */
    private String tenantId;

    /** HIS系统财务分类ID */
    @Excel(name = "HIS系统ID", width = 22, prompt = "与 HIS 财务分类主键对接时使用")
    private String hisId;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public void setFinanceCategoryId(Long financeCategoryId) 
    {
        this.financeCategoryId = financeCategoryId;
    }

    public Long getFinanceCategoryId() 
    {
        return financeCategoryId;
    }
    public Long getParentId()
    {
        return parentId;
    }
    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
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

    public String getReferredName() {
        return referredName;
    }

    public void setReferredName(String referredName) {
        this.referredName = referredName;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("financeCategoryId", getFinanceCategoryId())
            .append("parentId", getParentId())
            .append("financeCategoryCode", getFinanceCategoryCode())
            .append("financeCategoryName", getFinanceCategoryName())
            .append("referredName", getReferredName())
            .append("financeCategoryAddress", getFinanceCategoryAddress())
            .append("financeCategoryContact", getFinanceCategoryContact())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("isUse", getIsUse())
            .append("tenantId", getTenantId())
            .append("hisId", getHisId())
            .append("validationResult", getValidationResult())
            .toString();
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }
}
