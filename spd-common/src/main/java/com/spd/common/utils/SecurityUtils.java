package com.spd.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * 获取当前登录用户的租户 ID（SaaS 多租户，平台管理员可为 null）
     **/
    public static String getCustomerId()
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
