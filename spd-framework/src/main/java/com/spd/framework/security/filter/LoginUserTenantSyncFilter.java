package com.spd.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.utils.StringUtils;
import com.spd.framework.web.service.TokenService;
import com.spd.system.service.ISysUserService;

/**
 * 登录态租户同步：Redis 中的 {@link LoginUser} 为登录时快照，若库中已补写 {@code sys_user.customer_id}，
 * 缓存仍可能为空，导致 {@code getCustomerId()} 与库、与请求头不一致。
 * <p>
 * 在 JWT 认证之后、{@link TenantContextFilter} 之前执行：若当前 {@link LoginUser} 中 customer_id 为空，
 * 则按主键做一次轻量查询；若库中有值则写回 LoginUser 并 {@link TokenService#setLoginUser} 刷新 Redis。
 * </p>
 *
 * @author spd
 */
@Component
public class LoginUserTenantSyncFilter extends OncePerRequestFilter
{
    private static final Logger log = LoggerFactory.getLogger(LoginUserTenantSyncFilter.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain chain) throws ServletException, IOException
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof LoginUser)
            {
                syncCustomerIdFromDbIfStale((LoginUser) auth.getPrincipal());
            }
        }
        catch (Exception e)
        {
            log.warn("LoginUser 租户字段同步跳过: {}", e.getMessage());
        }
        chain.doFilter(request, response);
    }

    private void syncCustomerIdFromDbIfStale(LoginUser loginUser)
    {
        if (loginUser == null || loginUser.getUser() == null || loginUser.getUserId() == null)
        {
            return;
        }
        if (StringUtils.isNotEmpty(loginUser.getUser().getCustomerId()))
        {
            return;
        }
        String dbCustomerId = userService.selectCustomerIdByUserId(loginUser.getUserId());
        if (StringUtils.isNotEmpty(dbCustomerId))
        {
            loginUser.getUser().setCustomerId(dbCustomerId.trim());
            tokenService.setLoginUser(loginUser);
            log.debug("已从数据库同步 customer_id 至 LoginUser/Redis, userId={}", loginUser.getUserId());
        }
    }
}
