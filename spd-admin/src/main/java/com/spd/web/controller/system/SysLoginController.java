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
import com.spd.common.utils.StringUtils;
import com.spd.framework.web.service.SbPermissionService;
import com.spd.framework.web.service.SysLoginService;
import com.spd.framework.web.service.SysPermissionService;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbMenu;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISbMenuService;
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

    @Autowired
    private ISbMenuService sbMenuService;

    @Autowired
    private ISbCustomerService sbCustomerService;

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
        String username = loginBody.getUsername();
        String customerId = loginBody.getCustomerId();
        String token = loginService.login(username, loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid(), customerId);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 登录页客户下拉选项（未登录可访问，需在安全配置中放行）
     */
    @GetMapping("/getCustomerOptions")
    public AjaxResult getCustomerOptions()
    {
        SbCustomer query = new SbCustomer();
        query.setStatus("0");
        List<SbCustomer> list = sbCustomerService.selectSbCustomerList(query);
        List<java.util.Map<String, String>> options = new java.util.ArrayList<>();
        for (SbCustomer c : list) {
            if (c.getDeleteTime() == null && c.getCustomerId() != null) {
                java.util.Map<String, String> m = new java.util.HashMap<>();
                m.put("customerId", c.getCustomerId());
                m.put("customerCode", c.getCustomerCode());
                m.put("customerName", c.getCustomerName());
                options.add(m);
            }
        }
        return AjaxResult.success(options);
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
        putTenantIfPresent(ajax, user.getCustomerId());
        return ajax;
    }

    /**
     * 获取设备前端用户信息（基于 sb_*）
     * 含租户信息 tenant（首页可展示租户名称；customerId、customerCode 供前端请求使用，界面可隐藏不展示）
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
        putTenantIfPresent(ajax, user.getCustomerId());
        return ajax;
    }

    /**
     * 若为租户用户则放入租户信息（customerName 用于首页展示；customerId、customerCode 供前端请求携带，界面可隐藏）
     */
    private void putTenantIfPresent(AjaxResult ajax, String customerId)
    {
        if (StringUtils.isEmpty(customerId)) {
            return;
        }
        SbCustomer customer = sbCustomerService.selectSbCustomerById(customerId);
        if (customer == null) {
            return;
        }
        java.util.Map<String, Object> tenant = new java.util.HashMap<>();
        tenant.put("customerName", customer.getCustomerName());
        tenant.put("customerId", customer.getCustomerId());
        tenant.put("customerCode", customer.getCustomerCode());
        ajax.put("tenant", tenant);
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
        List<SbMenu> sbMenus = sbMenuService.selectSbMenuTreeByUserId(userId);
        return AjaxResult.success(sbMenuService.buildMenusFromSb(sbMenus));
    }
}
