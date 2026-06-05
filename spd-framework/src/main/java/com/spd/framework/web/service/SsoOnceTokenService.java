package com.spd.framework.web.service;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spd.common.constant.SsoConstants;
import com.spd.common.core.redis.RedisCache;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.system.service.ISysConfigService;

/**
 * SSO 一次性令牌：对方先申请 onceToken，再在重定向/登录接口中携带（单次有效）。
 */
@Component
public class SsoOnceTokenService
{
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;

    public String issueOnceToken(String tenantId)
    {
        String token = IdUtils.fastUUID();
        String key = SsoConstants.REDIS_ONCE_TOKEN_PREFIX + token;
        int ttl = resolveOnceTokenExpireMinutes();
        redisCache.setCacheObject(key, tenantId.trim(), ttl, TimeUnit.MINUTES);
        return token;
    }

    public void consumeOnceToken(String onceToken, String tenantId)
    {
        if (StringUtils.isEmpty(onceToken))
        {
            throw new ServiceException("onceToken 不能为空");
        }
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("tenantId 不能为空");
        }
        String key = SsoConstants.REDIS_ONCE_TOKEN_PREFIX + onceToken.trim();
        String boundTenant = redisCache.getCacheObject(key);
        if (StringUtils.isEmpty(boundTenant))
        {
            throw new ServiceException("onceToken 无效或已过期");
        }
        redisCache.deleteObject(key);
        if (!tenantId.trim().equals(boundTenant))
        {
            throw new ServiceException("onceToken 与 tenantId 不匹配");
        }
    }

    private int resolveOnceTokenExpireMinutes()
    {
        String v = configService.selectConfigByKey(SsoConstants.CONFIG_ONCE_TOKEN_EXPIRE_MINUTES);
        if (StringUtils.isEmpty(v))
        {
            return 5;
        }
        try
        {
            int n = Integer.parseInt(v.trim());
            return n > 0 ? n : 5;
        }
        catch (NumberFormatException e)
        {
            return 5;
        }
    }
}
