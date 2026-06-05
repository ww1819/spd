package com.spd.common.core.domain.model;

/**
 * 申请 SSO 一次性令牌。
 */
public class SsoOnceTokenRequest
{
    /** 租户 ID（sb_customer.customer_id） */
    private String tenantId;

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }
}
