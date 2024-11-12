package com.spd.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 供应商对象 fd_supplier
 *
 * @author spd
 */
public class FdSupplier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 供应商编码 */
    @Excel(name = "供应商编码")
    private String code;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String name;

    /** 删除标识 */
    private Integer delFlag;

    /** 税号 */
    @Excel(name = "税号")
    private String taxNumber;

    /** 名称简码 */
    @Excel(name = "名称简码")
    private String referredCode;

    /** 注册资金 */
    @Excel(name = "注册资金")
    private BigDecimal regMoney;

    /** 资质有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "资质有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validTime;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contacts;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactsPhone;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 法人 */
    @Excel(name = "法人")
    private String legalPerson;

    /** 邮编 */
    @Excel(name = "邮编")
    private String zipCode;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 公司负责人 */
    @Excel(name = "公司负责人")
    private String companyPerson;

    /** 电话 */
    @Excel(name = "电话")
    private String phone;

    /** 证件号 */
    @Excel(name = "证件号")
    private String certNumber;

    /** 传真 */
    @Excel(name = "传真")
    private String fax;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 公司简称 */
    @Excel(name = "公司简称")
    private String companyReferred;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String supplierRange;

    /** 状态（启用/停用） */
    @Excel(name = "状态", readConverterExp = "启用/停用")
    private String supplierStatus;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }
    public void setTaxNumber(String taxNumber)
    {
        this.taxNumber = taxNumber;
    }

    public String getTaxNumber()
    {
        return taxNumber;
    }
    public void setReferredCode(String referredCode)
    {
        this.referredCode = referredCode;
    }

    public String getReferredCode()
    {
        return referredCode;
    }
    public void setRegMoney(BigDecimal regMoney)
    {
        this.regMoney = regMoney;
    }

    public BigDecimal getRegMoney()
    {
        return regMoney;
    }
    public void setValidTime(Date validTime)
    {
        this.validTime = validTime;
    }

    public Date getValidTime()
    {
        return validTime;
    }
    public void setContacts(String contacts)
    {
        this.contacts = contacts;
    }

    public String getContacts()
    {
        return contacts;
    }
    public void setContactsPhone(String contactsPhone)
    {
        this.contactsPhone = contactsPhone;
    }

    public String getContactsPhone()
    {
        return contactsPhone;
    }
    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getWebsite()
    {
        return website;
    }
    public void setLegalPerson(String legalPerson)
    {
        this.legalPerson = legalPerson;
    }

    public String getLegalPerson()
    {
        return legalPerson;
    }
    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getZipCode()
    {
        return zipCode;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setCompanyPerson(String companyPerson)
    {
        this.companyPerson = companyPerson;
    }

    public String getCompanyPerson()
    {
        return companyPerson;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setCertNumber(String certNumber)
    {
        this.certNumber = certNumber;
    }

    public String getCertNumber()
    {
        return certNumber;
    }
    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getFax()
    {
        return fax;
    }
    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }
    public void setCompanyReferred(String companyReferred)
    {
        this.companyReferred = companyReferred;
    }

    public String getCompanyReferred()
    {
        return companyReferred;
    }
    public void setsupplierRange(String supplierRange)
    {
        this.supplierRange = supplierRange;
    }

    public String getSupplierRange()
    {
        return supplierRange;
    }
    public void setSupplierStatus(String supplierStatus)
    {
        this.supplierStatus = supplierStatus;
    }

    public String getSupplierStatus()
    {
        return supplierStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("delFlag", getDelFlag())
            .append("taxNumber", getTaxNumber())
            .append("referredCode", getReferredCode())
            .append("regMoney", getRegMoney())
            .append("validTime", getValidTime())
            .append("contacts", getContacts())
            .append("contactsPhone", getContactsPhone())
            .append("website", getWebsite())
            .append("legalPerson", getLegalPerson())
            .append("zipCode", getZipCode())
            .append("email", getEmail())
            .append("address", getAddress())
            .append("companyPerson", getCompanyPerson())
            .append("phone", getPhone())
            .append("certNumber", getCertNumber())
            .append("fax", getFax())
            .append("bankAccount", getBankAccount())
            .append("companyReferred", getCompanyReferred())
            .append("supplierRange", getSupplierRange())
            .append("supplierStatus", getSupplierStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
