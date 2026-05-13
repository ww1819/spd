package com.spd.framework.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.alibaba.fastjson2.JSON;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.utils.ServletUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.service.ISysLicenseService;

/**
 * 已登录请求的离线授权校验（在 JWT 解析出 LoginUser 之后执行）
 */
@Component
public class LicenseEnforcementFilter extends OncePerRequestFilter
{
    @Autowired
    private ISysLicenseService sysLicenseService;

    private static final List<String> EXEMPT_PATHS = Arrays.asList(
            "/login",
            "/register",
            "/captchaImage",
            "/getCustomerOptions",
            "/getInfo",
            "/getRouters",
            "/getCurrentTenant",
            "/logout",
            "/system/license/status",
            "/system/license/activate");

    private static String normalizePath(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.isNotEmpty(contextPath) && uri.startsWith(contextPath))
        {
            uri = uri.substring(contextPath.length());
        }
        if (uri.isEmpty())
        {
            uri = "/";
        }
        return uri;
    }

    private boolean isExemptPath(String path)
    {
        for (String p : EXEMPT_PATHS)
        {
            if (path.equals(p) || path.startsWith(p + "/"))
            {
                return true;
            }
        }
        if (path.startsWith("/profile/"))
        {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        Object principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null)
        {
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (!(principal instanceof LoginUser))
        {
            filterChain.doFilter(request, response);
            return;
        }
        String path = normalizePath(request);
        if (isExemptPath(path))
        {
            filterChain.doFilter(request, response);
            return;
        }
        Integer denyCode = sysLicenseService.getLicenseDenyHttpCode();
        if (denyCode == null)
        {
            filterChain.doFilter(request, response);
            return;
        }
        String msg = sysLicenseService.getLicenseDenyMessage();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", denyCode);
        body.put("msg", msg != null ? msg : "");
        ServletUtils.renderString(response, JSON.toJSONString(body));
    }
}
