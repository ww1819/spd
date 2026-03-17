package com.spd.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.spd.common.core.domain.entity.SysRole;
import com.spd.common.utils.StringUtils;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.system.service.ISysMenuService;
import com.spd.system.service.ISysRoleService;

/**
 * 用户权限处理
 * 
 * @author spd
 */
@Component
public class SysPermissionService
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单及按钮权限（耗材/管理端：从用户权限表 sys_user_menu 读取，含 F 类型按钮权限）
     * 
     * @param user 用户信息
     * @return 菜单权限信息（含 perms，供 v-hasPermi 等使用）
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
        }
        else
        {
            // 耗材：仅从用户权限表 sys_user_menu 读取；租户时排除平台管理菜单
            boolean forTenant = StringUtils.isNotEmpty(user.getCustomerId());
            Set<String> userPerms = menuService.selectMenuPermsByUserId(user.getUserId(), forTenant);
            perms.addAll(userPerms);
            // 仍为角色设置 permissions，供数据权限等逻辑使用
            List<SysRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                for (SysRole role : roles)
                {
                    role.setPermissions(menuService.selectMenuPermsByRoleId(role.getRoleId()));
                }
            }
        }
        return perms;
    }
}
