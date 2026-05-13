package com.spd.common.core.domain.model;

import java.io.Serializable;

/**
 * 登录页匿名导入注册码
 */
public class LicenseRegisterBody implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String licenseCode;

    /** 多租户时与登录所选组织机构一致；单租户可空由后端推断 */
    private String customerId;

    /** 与 /getCustomerOptions 一致：hc=耗材启用租户；其它=设备启用租户；用于单租户推断 */
    private String systemType;

    public String getLicenseCode()
    {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode)
    {
        this.licenseCode = licenseCode;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getSystemType()
    {
        return systemType;
    }

    public void setSystemType(String systemType)
    {
        this.systemType = systemType;
    }
}
