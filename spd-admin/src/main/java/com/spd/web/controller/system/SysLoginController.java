package com.spd.web.controller.system;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.constant.Constants;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.core.domain.model.LoginBody;
import com.spd.common.utils.SecurityUtils;
import com.spd.framework.web.service.SbPermissionService;
import com.spd.framework.web.service.SysLoginService;
import com.spd.framework.web.service.SysPermissionService;
import com.spd.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author spd
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SbPermissionService sbPermissionService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取设备前端用户信息（基于 sb_*）
     *
     * @return 用户信息
     */
    @GetMapping("getEquipmentInfo")
    public AjaxResult getEquipmentInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 设备角色集合
        Set<String> roles = sbPermissionService.getRolePermission(user);
        // 设备菜单权限集合
        Set<String> permissions = sbPermissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    /**
     * 获取设备前端路由信息（基于 sb_menu）
     *
     * @return 路由信息
     */
    @GetMapping("getEquipmentRouters")
    public AjaxResult getEquipmentRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectSbMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
