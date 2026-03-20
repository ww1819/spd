package com.spd.framework.web.service;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.spd.common.core.domain.entity.SysRole;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.framework.security.context.PermissionContextHolder;
import com.spd.system.domain.SbWorkGroup;
import com.spd.system.service.ISbWorkGroupService;

/**
 * 自定义权限实现，ss取自SpringSecurity首字母
 * 
 * @author spd
 */
@Service("ss")
public class PermissionService
{
    @Autowired
    private ISbWorkGroupService sbWorkGroupService;

    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";

    /** 管理员角色权限标识 */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMETER = ",";

    private static final String PERMISSION_DELIMETER = ",";

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission)
    {
        if (StringUtils.isEmpty(permission))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        PermissionContextHolder.setContext(permission);
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 是否为平台用户（无客户ID或超级管理员，可访问客户管理等仅平台功能）
     */
    public boolean isPlatformUser()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        if (loginUser.getUser().isAdmin())
        {
            return true;
        }
        String customerId = loginUser.getUser().getCustomerId();
        return customerId == null || customerId.trim().isEmpty();
    }

    /**
     * 是否已绑定客户（租户端用户），与 {@link #isPlatformUser()} 互斥（超级管理员视为平台侧）。
     */
    public boolean isTenantUser()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        if (loginUser.getUser().isAdmin())
        {
            return false;
        }
        return StringUtils.isNotEmpty(loginUser.getUser().getCustomerId());
    }

    /**
     * 是否为租户用户且请求的客户即本人所属客户（用于允许访问“本人客户”下的数据，如工作组列表）。
     * 当 requestCustomerId 为空时视为访问本人客户。
     */
    public boolean isTenantUserWithCustomer(String requestCustomerId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        String myCustomerId = loginUser.getUser().getCustomerId();
        if (StringUtils.isEmpty(myCustomerId))
        {
            return false;
        }
        if (StringUtils.isEmpty(requestCustomerId))
        {
            return true;
        }
        return myCustomerId.trim().equals(requestCustomerId.trim());
    }

    /**
     * 是否为租户用户且该工作组属于本人所属客户（用于允许访问本人客户下的工作组的菜单/仓库/科室等接口）。
     */
    public boolean isTenantUserWithGroup(String groupId)
    {
        if (StringUtils.isEmpty(groupId))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        String myCustomerId = loginUser.getUser().getCustomerId();
        if (StringUtils.isEmpty(myCustomerId))
        {
            return false;
        }
        SbWorkGroup group = sbWorkGroupService.selectByGroupId(groupId);
        if (group == null || group.getCustomerId() == null)
        {
            return false;
        }
        return myCustomerId.trim().equals(group.getCustomerId().trim());
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPermi(String permission)
    {
        return hasPermi(permission) != true;
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions)
    {
        if (StringUtils.isEmpty(permissions))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        PermissionContextHolder.setContext(permissions);
        Set<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMETER))
        {
            if (permission != null && hasPermissions(authorities, permission))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否拥有某个角色
     * 
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role)
    {
        if (StringUtils.isEmpty(role))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            return false;
        }
        for (SysRole sysRole : loginUser.getUser().getRoles())
        {
            String roleKey = sysRole.getRoleKey();
            if (SUPER_ADMIN.equals(roleKey) || roleKey.equals(StringUtils.trim(role)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反。
     *
     * @param role 角色名称
     * @return 用户是否不具备某角色
     */
    public boolean lacksRole(String role)
    {
        return hasRole(role) != true;
    }

    /**
     * 验证用户是否具有以下任意一个角色
     *
     * @param roles 以 ROLE_NAMES_DELIMETER 为分隔符的角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public boolean hasAnyRoles(String roles)
    {
        if (StringUtils.isEmpty(roles))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            return false;
        }
        for (String role : roles.split(ROLE_DELIMETER))
        {
            if (hasRole(role))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含权限
     * 
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission)
    {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }
}
