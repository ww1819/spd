package com.spd.his.support;

import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.utils.TenantContext;

/**
 * 前置机/内部接口调用 SPD 计费消耗链时，注入租户与操作人上下文。
 */
public final class HisInternalRequestContext
{
    private static final String DEFAULT_OPERATOR = "scminterface";

    private HisInternalRequestContext()
    {
    }

    public static void run(String tenantId, Long operatorUserId, Runnable action)
    {
        if (action == null)
        {
            return;
        }
        String tid = tenantId == null ? null : tenantId.trim();
        Long uid = operatorUserId == null ? 0L : operatorUserId;
        TenantContext.setTenantId(tid);
        LoginUser loginUser = buildLoginUser(tid, uid);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            loginUser, null, Collections.emptyList());
        Authentication previous = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(auth);
        try
        {
            action.run();
        }
        finally
        {
            SecurityContextHolder.getContext().setAuthentication(previous);
            TenantContext.clear();
        }
    }

    private static LoginUser buildLoginUser(String tenantId, Long userId)
    {
        SysUser u = new SysUser();
        u.setUserId(userId);
        u.setUserName(DEFAULT_OPERATOR);
        u.setCustomerId(tenantId);
        LoginUser loginUser = new LoginUser(userId, null, u, Collections.emptySet());
        loginUser.setLoginChannel("hc");
        return loginUser;
    }
}
