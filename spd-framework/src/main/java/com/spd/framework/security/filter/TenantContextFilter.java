package com.spd.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.TenantContext;

/**
 * SaaS 多租户上下文过滤器：
 * 1. 将当前登录用户的 customerId 写入 TenantContext，供业务层使用；
 * 2. 校验请求头 X-Tenant-Id 与登录用户租户一致，防止篡改请求头越权访问其他租户数据。
 *
 * @author spd
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantContextFilter.class);
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        try {
            Object principal = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
            if (principal instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) principal;
                String userCustomerId = loginUser.getUser() != null ? loginUser.getUser().getCustomerId() : null;
                if (StringUtils.isNotEmpty(userCustomerId)) {
                    TenantContext.setTenantId(userCustomerId);
                    String headerTenantId = request.getHeader(HEADER_TENANT_ID);
                    if (StringUtils.isNotEmpty(headerTenantId) && !headerTenantId.equals(userCustomerId)) {
                        log.warn("租户校验失败: 请求头 X-Tenant-Id={} 与当前用户租户 {} 不一致", headerTenantId, userCustomerId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"code\":403,\"msg\":\"租户标识与登录用户不一致\"}");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.error("TenantContextFilter 处理异常", e);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
