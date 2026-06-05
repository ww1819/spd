package com.spd.framework.web.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spd.common.constant.Constants;
import com.spd.common.constant.SsoConstants;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.core.domain.model.SsoLoginBody;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SsoRsaUtils;
import com.spd.common.utils.StringUtils;
import com.spd.framework.manager.AsyncManager;
import com.spd.framework.manager.factory.AsyncFactory;
import com.spd.framework.security.context.AuthenticationContextHolder;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISysConfigService;

/**
 * 单点登录业务（对齐众阳 2.41.2，增加 tenantId 区分租户与用户）。
 */
@Component
public class SysSsoService
{
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISbCustomerService sbCustomerService;

    @Autowired
    private SsoOnceTokenService ssoOnceTokenService;

    @Autowired
    private SysLoginService loginService;

    public void assertSsoEnabled()
    {
        String enabled = configService.selectConfigByKey(SsoConstants.CONFIG_ENABLED);
        if (!"true".equalsIgnoreCase(StringUtils.trimToEmpty(enabled)))
        {
            throw new ServiceException("单点登录未启用");
        }
    }

    public void assertSsoApiKey(String provided)
    {
        String expected = configService.selectConfigByKey(SsoConstants.CONFIG_API_KEY);
        if (StringUtils.isEmpty(expected))
        {
            throw new ServiceException("未配置 SSO API 密钥（sys_config：sso.api.key）");
        }
        if (StringUtils.isEmpty(provided) || !expected.trim().equals(provided.trim()))
        {
            throw new ServiceException("SSO API 密钥无效");
        }
    }

    public String issueOnceToken(String tenantId)
    {
        assertSsoEnabled();
        validateTenant(tenantId);
        return ssoOnceTokenService.issueOnceToken(tenantId.trim());
    }

    /**
     * SSO 登录并返回 JWT。
     */
    public String login(SsoLoginBody body)
    {
        assertSsoEnabled();
        validateLoginBody(body);
        ssoOnceTokenService.consumeOnceToken(body.getOnceToken(), body.getTenantId());
        validateSystemCode(body.getSystemCode());

        String tenantId = body.getTenantId().trim();
        validateTenant(tenantId);

        String userName = body.getUserName().trim();
        String plainPassword = resolvePlainPassword(body.getPassword());
        String systemType = resolveSystemType(body.getSystemCode());

        String token = loginService.ssoLogin(userName, plainPassword, tenantId, systemType, resolveTokenExpireMinutes());
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(
            userName,
            Constants.LOGIN_SUCCESS,
            "SSO登录成功 tenantId=" + tenantId + " deptId=" + StringUtils.nvl(body.getDeptId(), "")
                + " roleId=" + StringUtils.nvl(body.getRoleId(), "")));
        return token;
    }

    /**
     * 构建前端重定向 URL：{base}/#/sso-callback?token=...&redirect=...
     */
    public String buildTransferRedirectUrl(String token, String url)
    {
        String base = StringUtils.trimToEmpty(configService.selectConfigByKey(SsoConstants.CONFIG_FRONTEND_BASE_URL));
        if (StringUtils.isEmpty(base))
        {
            throw new ServiceException("未配置 SSO 前端基址（sys_config：sso.frontend.baseUrl）");
        }
        base = base.replaceAll("/+$", "");
        String redirect = StringUtils.isNotEmpty(url) ? url.trim() : "/";
        try
        {
            String encodedRedirect = URLEncoder.encode(redirect, StandardCharsets.UTF_8.name());
            return base + "/#/sso-callback?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8.name())
                + "&redirect=" + encodedRedirect;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ServiceException("构建重定向地址失败");
        }
    }

    private void validateLoginBody(SsoLoginBody body)
    {
        if (body == null)
        {
            throw new ServiceException("请求体不能为空");
        }
        if (StringUtils.isEmpty(body.getTenantId()))
        {
            throw new ServiceException("tenantId 不能为空");
        }
        if (StringUtils.isEmpty(body.getOnceToken()))
        {
            throw new ServiceException("onceToken 不能为空");
        }
        if (StringUtils.isEmpty(body.getUserName()))
        {
            throw new ServiceException("userName 不能为空");
        }
        if (StringUtils.isEmpty(body.getPassword()))
        {
            throw new ServiceException("password 不能为空");
        }
        if (StringUtils.isEmpty(body.getSystemCode()))
        {
            throw new ServiceException("systemCode 不能为空");
        }
    }

    private void validateTenant(String tenantId)
    {
        SbCustomer customer = sbCustomerService.selectSbCustomerById(tenantId.trim());
        if (customer == null)
        {
            throw new ServiceException("租户不存在或已删除：" + tenantId);
        }
    }

    private void validateSystemCode(String systemCode)
    {
        String configured = configService.selectConfigByKey(SsoConstants.CONFIG_SYSTEM_CODE);
        if (StringUtils.isEmpty(configured))
        {
            return;
        }
        Set<String> allowed = Arrays.stream(configured.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toSet());
        if (!allowed.isEmpty() && !allowed.contains(StringUtils.trimToEmpty(systemCode)))
        {
            throw new ServiceException("systemCode 无效：" + systemCode);
        }
    }

    private String resolvePlainPassword(String passwordCipher)
    {
        String pem = configService.selectConfigByKey(SsoConstants.CONFIG_RSA_PRIVATE_KEY);
        String decrypted = SsoRsaUtils.decryptByPrivateKeyPem(passwordCipher, pem);
        if (StringUtils.isNotEmpty(decrypted))
        {
            return decrypted;
        }
        String allowPlain = configService.selectConfigByKey(SsoConstants.CONFIG_ALLOW_PLAIN_PASSWORD);
        if ("true".equalsIgnoreCase(StringUtils.trimToEmpty(allowPlain)))
        {
            return passwordCipher;
        }
        throw new ServiceException("密码解密失败，请检查 RSA 私钥或开启明文过渡期配置");
    }

    private String resolveSystemType(String systemCode)
    {
        if ("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemCode)))
        {
            return "hc";
        }
        return "equipment";
    }

    private int resolveTokenExpireMinutes()
    {
        String v = configService.selectConfigByKey(SsoConstants.CONFIG_TOKEN_EXPIRE_MINUTES);
        if (StringUtils.isEmpty(v))
        {
            return 360;
        }
        try
        {
            int n = Integer.parseInt(v.trim());
            return n > 0 ? n : 360;
        }
        catch (NumberFormatException e)
        {
            return 360;
        }
    }
}
