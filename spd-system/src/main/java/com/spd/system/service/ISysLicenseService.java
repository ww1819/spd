package com.spd.system.service;

import com.spd.system.domain.vo.LicenseStatusVo;

/**
 * 系统离线授权
 */
public interface ISysLicenseService
{
    /**
     * 当前授权是否有效（未过期且已激活）
     */
    boolean isCurrentlyValid();

    /**
     * 若未激活或已过期返回 HTTP 业务码，否则返回 null
     */
    Integer getLicenseDenyHttpCode();

    /**
     * 与 {@link #getLicenseDenyHttpCode()} 对应的提示语
     */
    String getLicenseDenyMessage();

    /**
     * 授权状态（懒创建 instance_id）
     */
    LicenseStatusVo getStatus();

    /**
     * 导入离线注册码（已登录后台）
     *
     * @param licenseCode 注册码
     * @param customerId  v2 医院绑定时须与租户一致；v1 可空
     * @param updateBy    操作人
     */
    void activate(String licenseCode, String customerId, String updateBy);

    /**
     * 登录页匿名导入（v2 医院名称注册码）
     *
     * @param licenseCode 注册码
     * @param customerId  多租户必选；单租户可空
     * @param systemType    与 getCustomerOptions 一致，默认 hc，用于单租户推断
     */
    void activateAnonymousFromLogin(String licenseCode, String customerId, String systemType);
}
