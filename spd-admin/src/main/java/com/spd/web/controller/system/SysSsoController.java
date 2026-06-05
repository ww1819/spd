package com.spd.web.controller.system;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.constant.Constants;
import com.spd.common.constant.SsoConstants;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.model.SsoLoginBody;
import com.spd.common.core.domain.model.SsoOnceTokenRequest;
import com.spd.common.utils.StringUtils;
import com.spd.framework.web.service.SysSsoService;

/**
 * 单点登录接口（对齐众阳 2.41.2，增加 tenantId 区分租户与用户）。
 * <p>
 * 流程：对方系统先调用 {@link #issueOnceToken} 获取 onceToken，再 GET {@link #transfer} 重定向或 POST {@link #login} 换取 SPD token。
 * </p>
 */
@RestController
@RequestMapping("/sso")
public class SysSsoController
{
    @Autowired
    private SysSsoService sysSsoService;

    /**
     * 申请一次性令牌（对方服务端调用，请求头 X-Spd-Sso-Key）。
     */
    @PostMapping("/onceToken")
    public AjaxResult issueOnceToken(
        @RequestHeader(value = SsoConstants.HEADER_SSO_KEY, required = false) String apiKey,
        @RequestBody SsoOnceTokenRequest request)
    {
        sysSsoService.assertSsoApiKey(apiKey);
        if (request == null || StringUtils.isEmpty(request.getTenantId()))
        {
            return AjaxResult.error("tenantId 不能为空");
        }
        String onceToken = sysSsoService.issueOnceToken(request.getTenantId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("onceToken", onceToken);
        ajax.put("tenantId", request.getTenantId().trim());
        return ajax;
    }

    /**
     * 单点登录重定向（浏览器 GET，字段与接口文档 2.41.2 一致并增加 tenantId）。
     */
    @GetMapping("/transfer")
    public void transfer(
        @RequestParam String tenantId,
        @RequestParam String onceToken,
        @RequestParam String url,
        @RequestParam String userName,
        @RequestParam String password,
        @RequestParam String deptId,
        @RequestParam String roleId,
        @RequestParam String systemCode,
        HttpServletResponse response) throws IOException
    {
        SsoLoginBody body = new SsoLoginBody();
        body.setTenantId(tenantId);
        body.setOnceToken(onceToken);
        body.setUrl(url);
        body.setUserName(userName);
        body.setPassword(password);
        body.setDeptId(deptId);
        body.setRoleId(roleId);
        body.setSystemCode(systemCode);

        String token = sysSsoService.login(body);
        String redirectUrl = sysSsoService.buildTransferRedirectUrl(token, url);
        response.sendRedirect(redirectUrl);
    }

    /**
     * 单点登录（JSON，供对方服务端或联调使用）。
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody SsoLoginBody body)
    {
        String token = sysSsoService.login(body);
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        if (body != null && StringUtils.isNotEmpty(body.getTenantId()))
        {
            ajax.put("tenantId", body.getTenantId().trim());
        }
        return ajax;
    }
}
