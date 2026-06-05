package com.spd.common.constant;

/**
 * 单点登录（SSO）配置键与请求头常量，对应 sys_config。
 */
public final class SsoConstants
{
    private SsoConstants()
    {
    }

    /** 是否启用 SSO */
    public static final String CONFIG_ENABLED = "sso.enabled";

    /** 对方系统申请 onceToken 时使用的 API 密钥（请求头 X-Spd-Sso-Key） */
    public static final String CONFIG_API_KEY = "sso.api.key";

    /** RSA 私钥 PEM（PKCS#8），用于解密对方 RSA 加密的 password */
    public static final String CONFIG_RSA_PRIVATE_KEY = "sso.rsa.privateKeyPem";

    /** 是否允许 password 明文（文档过渡期：目前仅支持明文传输） */
    public static final String CONFIG_ALLOW_PLAIN_PASSWORD = "sso.rsa.allowPlainPassword";

    /** 前端基址，用于 /sso/transfer 重定向，如 https://spd.example.com */
    public static final String CONFIG_FRONTEND_BASE_URL = "sso.frontend.baseUrl";

    /** SSO 登录 token 有效期（分钟），默认 360=6 小时 */
    public static final String CONFIG_TOKEN_EXPIRE_MINUTES = "sso.token.expireMinutes";

    /** onceToken 有效期（分钟） */
    public static final String CONFIG_ONCE_TOKEN_EXPIRE_MINUTES = "sso.onceToken.expireMinutes";

    /** 本系统编码，对方 systemCode 须与此一致（可配置多个逗号分隔） */
    public static final String CONFIG_SYSTEM_CODE = "sso.systemCode";

    public static final String HEADER_SSO_KEY = "X-Spd-Sso-Key";

    public static final String REDIS_ONCE_TOKEN_PREFIX = "sso_once_token:";
}
