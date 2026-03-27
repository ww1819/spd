package com.spd.common.utils;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.spd.common.constant.HttpStatus;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.exception.ServiceException;

/**
 * 安全服务工具类
 * 
 * @author spd
 */
public class SecurityUtils
{
    /**
     * 请求头：耗材/租户工作台当前租户 ID（与 {@code TenantContextFilter} 一致）
     */
    public static final String X_TENANT_ID_HEADER = "X-Tenant-Id";

    /**
     * 用户ID
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 当前用户ID字符串（用于 createBy/updateBy/deleteBy 等字段，存 sys_user.user_id）
     **/
    public static String getUserIdStr()
    {
        return String.valueOf(getUserId());
    }

    /**
     * 获取当前登录态中的租户 ID（仅 LoginUser 快照值，不做任何兜底）
     */
    public static String getLoginCustomerId()
    {
        try
        {
            LoginUser loginUser = getLoginUser();
            return loginUser.getUser() != null ? loginUser.getUser().getCustomerId() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 获取当前请求的有效租户 ID。
     * 兼容历史调用方：优先登录态，其次 TenantContext，最后请求头 X-Tenant-Id。
     * 说明：保留方法名不变，以覆盖历史 Mapper 中大量直接调用 getCustomerId() 的场景。
     **/
    public static String getCustomerId()
    {
        return resolveEffectiveTenantId(null);
    }

    /**
     * 写入租户字段时解析有效客户 ID：优先实体上已带有的 tenantId，其次登录用户 customerId，再次 {@link TenantContext}（请求线程内），
     * 最后读当前请求头 {@link #X_TENANT_ID_HEADER}（与前端工作台一致，避免仅依赖 Filter 写入 ThreadLocal 的时机差异）。
     * <p>说明：MyBatis 中 {@code #{tenantId}} 会与实体属性同名绑定冲突，应使用本方法结果写入独立变量名。</p>
     */
    public static String resolveEffectiveTenantId(String entityTenantId)
    {
        if (StringUtils.isNotEmpty(entityTenantId))
        {
            return entityTenantId.trim();
        }
        String c = getLoginCustomerId();
        if (StringUtils.isNotEmpty(c))
        {
            return c;
        }
        String ctx = TenantContext.getTenantId();
        if (StringUtils.isNotEmpty(ctx))
        {
            return ctx;
        }
        try
        {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            if (ra instanceof ServletRequestAttributes)
            {
                HttpServletRequest req = ((ServletRequestAttributes) ra).getRequest();
                if (req != null)
                {
                    String h = req.getHeader(X_TENANT_ID_HEADER);
                    if (StringUtils.isNotEmpty(h))
                    {
                        return h.trim();
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * 供 MyBatis bind 调用（无参），避免 OGNL 对 {@code resolveEffectiveTenantId(null)} 中 null 字面量兼容问题。
     */
    public static String scopedTenantIdForSql()
    {
        return resolveEffectiveTenantId(null);
    }

    /**
     * 严格模式：要求当前请求必须能解析到租户，否则抛出业务异常。
     * 可用于需要强租户隔离的 SQL 绑定点逐步替换。
     */
    public static String requiredScopedTenantIdForSql()
    {
        String tenantId = resolveEffectiveTenantId(null);
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("无法解析当前租户，请重新登录后重试");
        }
        return tenantId;
    }

    /**
     * 租户登录渠道：hc=耗材，equipment=设备；无租户或未设置时为 null。
     */
    public static String getLoginChannel()
    {
        try
        {
            return getLoginUser().getLoginChannel();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId()
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 校验当前用户是否可访问指定租户的数据，防止跨客户查询/修改/删除。
     * 若当前用户有客户ID且数据所属租户非空且不一致则抛异常。
     *
     * @param entityTenantId 数据所属租户ID（实体 getTenantId()），可为 null
     */
    public static void ensureTenantAccess(String entityTenantId)
    {
        String customerId = getCustomerId();
        if (org.apache.commons.lang3.StringUtils.isEmpty(customerId)) {
            return;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(entityTenantId)) {
            return;
        }
        if (!customerId.equals(entityTenantId)) {
            throw new ServiceException("无权访问该数据或数据不存在");
        }
    }
}
