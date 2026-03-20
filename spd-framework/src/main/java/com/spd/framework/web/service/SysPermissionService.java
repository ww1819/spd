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
     * 获取菜单及按钮权限：非管理员统一从 {@code sys_user_menu} 关联 {@code sys_menu.perms} 读取（含 F 按钮）；
     * 租户（有 customerId）时额外排除 {@code is_platform=1} 的菜单权限，与 {@code getRouters} 菜单树数据源一致。
     * 设备端 sb 权限由 SbPermissionService#getMenuPermission 在登录/getInfo 时与上述结果合并进 LoginUser。
     *
     * @param user 用户信息
     * @return 菜单权限信息（供 v-hasPermi、@PreAuthorize 等使用）
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
