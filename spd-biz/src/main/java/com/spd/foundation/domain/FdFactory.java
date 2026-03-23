package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 厂家维护对象 fd_factory
 *
 * @author spd
 * @date 2024-03-04
 */
public class FdFactory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long factoryId;

    /** 厂家编码 */
    @Excel(name = "厂家编码")
    private String factoryCode;

    /** 厂家名称 */
    @Excel(name = "厂家名称")
    private String factoryName;

    /** HIS 生产厂家 ID（衡水市第三人民医院租户内必填且唯一，保存后不可通过维护接口修改） */
    @Excel(name = "HIS生产厂家ID", width = 22, prompt = "衡水市第三人民医院新增与导入时必填，租户内唯一；保存后不可修改")
    private String hisId;

    /** 厂家地址 */
    @Excel(name = "厂家地址")
    private String factoryAddress;

    /** 厂家联系方式 */
    @Excel(name = "厂家联系方式")
    private String factoryContact;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    /** 厂家简码 */
    @Excel(name = "厂家简码")
    private String factoryReferredCode;

    /** 厂家状态（启用/停用，存库 0/1） */
    @Excel(name = "厂家状态", readConverterExp = "0=启用,1=停用")
    private String factoryStatus;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public void setFactoryId(Long factoryId)
    {
        this.factoryId = factoryId;
    }

    public Long getFactoryId()
    {
        return factoryId;
    }
    public void setFactoryCode(String factoryCode)
    {
        this.factoryCode = factoryCode;
    }

    public String getFactoryCode()
    {
        return factoryCode;
    }
    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public String getFactoryName()
    {
        return factoryName;
    }

    public String getHisId()
    {
        return hisId;
    }

    public void setHisId(String hisId)
    {
        this.hisId = hisId;
    }

    public void setFactoryAddress(String factoryAddress)
    {
        this.factoryAddress = factoryAddress;
    }

    public String getFactoryAddress()
    {
        return factoryAddress;
    }
    public void setFactoryContact(String factoryContact)
    {
        this.factoryContact = factoryContact;
    }

    public String getFactoryContact()
    {
        return factoryContact;
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

    public String getFactoryReferredCode() {
        return factoryReferredCode;
    }

    public void setFactoryReferredCode(String factoryReferredCode) {
        this.factoryReferredCode = factoryReferredCode;
    }

    public String getFactoryStatus() {
        return factoryStatus;
    }

    public void setFactoryStatus(String factoryStatus) {
        this.factoryStatus = factoryStatus;
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
            .append("factoryId", getFactoryId())
            .append("factoryCode", getFactoryCode())
            .append("factoryName", getFactoryName())
            .append("hisId", getHisId())
            .append("factoryAddress", getFactoryAddress())
            .append("factoryContact", getFactoryContact())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("factoryReferredCode", getFactoryReferredCode())
            .append("factoryStatus", getFactoryStatus())
            .append("validationResult", getValidationResult())
            .toString();
    }
}
