package com.spd.common.core.domain.model;

/**
 * 单点登录请求体（JSON 方式，字段对齐众阳 2.41.2 并增加 tenantId）。
 */
public class SsoLoginBody
{
    /** 租户 ID（sb_customer.customer_id），必传 */
    private String tenantId;

    private String onceToken;

    /** 跳转目标相对路径（可选，URLEncoder 编码后的值在 GET 重定向中使用） */
    private String url;

    private String userName;

    /** RSA 加密或明文密码 */
    private String password;

    private String deptId;

    private String roleId;

    /** 对方系统编码，须与 sso.systemCode 配置一致 */
    private String systemCode;

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getOnceToken()
    {
        return onceToken;
    }

    public void setOnceToken(String onceToken)
    {
        this.onceToken = onceToken;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public String getSystemCode()
    {
        return systemCode;
    }

    public void setSystemCode(String systemCode)
    {
        this.systemCode = systemCode;
    }
}
