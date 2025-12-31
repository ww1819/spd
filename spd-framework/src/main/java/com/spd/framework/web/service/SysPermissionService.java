package com.spd.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.spd.common.core.domain.entity.SysRole;
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
     * 获取菜单数据权限
     * 
     * @param user 用户信息
     * @return 菜单权限信息
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
            // 先获取角色关联的菜单权限（用于设置role.setPermissions）
            List<SysRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (SysRole role : roles)
                {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                    role.setPermissions(rolePerms);
                    perms.addAll(rolePerms);
                }
            }
            // 获取用户直接关联的菜单权限并合并（selectMenuPermsByUserId已包含角色和用户直接权限）
            // 这里调用它会自动包含角色权限和用户直接权限，Set会自动去重
            Set<String> allUserPerms = menuService.selectMenuPermsByUserId(user.getUserId());
            perms.addAll(allUserPerms);
        }
        return perms;
    }
}
