package com.spd.web.controller.system;

import java.util.HashSet;
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
import com.spd.common.core.domain.model.LoginUser;
import com.spd.framework.web.service.SbPermissionService;
import com.spd.framework.web.service.SysLoginService;
import com.spd.framework.web.service.SysPermissionService;
import com.spd.framework.web.service.TokenService;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbMenu;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISbMenuService;
import com.spd.system.service.ISysConfigService;
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

    @Autowired
    private HcCustomerMenuMapper hcCustomerMenuMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

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
        String systemType = loginBody.getSystemType();
        String token = loginService.login(username, loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid(), customerId, systemType);
        ajax.put(Constants.TOKEN, token);
        if (StringUtils.isNotEmpty(customerId)) {
            putTenantIfPresent(ajax, customerId);
        }
        return ajax;
    }

    /**
     * 登录页租户下拉选项（未登录可访问，需在安全配置中放行）
     * 仿照设备系统：耗材登录传 systemType=hc 仅返回耗材启用租户（hc_status=0），设备登录不传或传其他仅返回设备启用租户（status=0）
     *
     * @param systemType 可选，hc=耗材系统（只返回 hc_status=0 的租户），不传或其它=设备系统（只返回 status=0 的租户）
     */
    @GetMapping("/getCustomerOptions")
    public AjaxResult getCustomerOptions(String systemType)
    {
        SbCustomer q = new SbCustomer();
        if ("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType))) {
            q.setHcStatus("0");
        } else {
            q.setStatus("0");
        }
        List<SbCustomer> list = sbCustomerService.selectSbCustomerList(q);
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
        AjaxResult ajax = AjaxResult.success(options);
        if ("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType))) {
            String def = StringUtils.trimToEmpty(configService.selectConfigByKey("hc.login.defaultCustomerId"));
            if (StringUtils.isNotEmpty(def)) {
                ajax.put("defaultCustomerId", def);
            }
        }
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合：与 UserDetailsServiceImpl.createLoginUser 一致（sys_user_menu 耗材权限 + 设备 sb 权限）
        Set<String> permissions = new HashSet<>(permissionService.getMenuPermission(user));
        permissions.addAll(sbPermissionService.getMenuPermission(user));
        // 与 @PreAuthorize 一致：必须写回 LoginUser 并刷新 Redis，否则仅前端 getInfo 有最新权限、接口仍用登录时旧权限 → 403
        loginUser.setPermissions(permissions);
        tokenService.setLoginUser(loginUser);
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 设备角色集合
        Set<String> roles = sbPermissionService.getRolePermission(user);
        // 设备菜单权限集合（与登录时一致：平台 + 设备，供前端 v-hasPermi 使用）
        Set<String> permissions = new HashSet<>(permissionService.getMenuPermission(user));
        permissions.addAll(sbPermissionService.getMenuPermission(user));
        // 同步到 Redis 中 LoginUser，避免「能看到按钮但点击 403」
        loginUser.setPermissions(permissions);
        tokenService.setLoginUser(loginUser);
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
        tenant.put("tenantKey", StringUtils.isNotEmpty(customer.getTenantKey()) ? customer.getTenantKey() : null);
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
        SysUser user = SecurityUtils.getLoginUser() != null ? SecurityUtils.getLoginUser().getUser() : null;
        boolean forTenant = user != null && StringUtils.isNotEmpty(user.getCustomerId());
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId, forTenant);
        Set<Long> pausedMenuIds = null;
        if (user != null && StringUtils.isNotEmpty(user.getCustomerId())) {
            List<Long> list = hcCustomerMenuMapper.selectPausedMenuIdsByTenantId(user.getCustomerId());
            if (list != null && !list.isEmpty()) {
                pausedMenuIds = new HashSet<>(list);
            }
        }
        return AjaxResult.success(menuService.buildMenus(menus, pausedMenuIds));
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
