package com.spd.framework.web.service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.spd.common.constant.CacheConstants;
import com.spd.common.constant.Constants;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.core.redis.RedisCache;
import com.spd.common.enums.UserStatus;
import com.spd.common.exception.ServiceException;
import com.spd.common.exception.user.BlackListException;
import com.spd.common.exception.user.CaptchaException;
import com.spd.common.exception.user.CaptchaExpireException;
import com.spd.common.exception.user.UserNotExistsException;
import com.spd.common.exception.user.UserPasswordNotMatchException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.MessageUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.ip.IpUtils;
import com.spd.framework.manager.AsyncManager;
import com.spd.framework.manager.factory.AsyncFactory;
import com.spd.framework.security.context.AuthenticationContextHolder;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISysConfigService;
import com.spd.system.service.ISysUserService;

/**
 * 登录校验方法
 *
 * @author spd
 */
@Component
public class SysLoginService
{
    private static final String PLATFORM_ADMIN_USERNAME = "admin";
    private static final String TENANT_SUPER_USERNAME = "super_01";

    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISbCustomerService sbCustomerService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SbPermissionService sbPermissionService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @param customerId 客户ID
     * @param systemType 登录入口：hc=耗材系统（校验 hc_status/hc_planned_disable_time），否则设备系统
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid, String customerId, String systemType)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password, customerId);
        // 平台管理员选租户后，自动以租户 super_01 身份登录，保证后续数据操作主体与租户一致
        if (isPlatformAdminTenantSwitch(username, customerId)) {
            return loginAsTenantSuperUser(password, customerId, systemType);
        }

        // 有客户ID时拼成「id:customerId|username」或「hc:customerId|username」，供 loadUserByUsername 按客户+用户名唯一定位并区分耗材/设备校验
        String effectiveUsername = username;
        if (StringUtils.isNotEmpty(customerId)) {
            String prefix = "hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType)) ? "hc:" : "id:";
            effectiveUsername = prefix + customerId.trim() + "|" + username;
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(effectiveUsername, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (StringUtils.isNotEmpty(customerId))
        {
            loginUser.setLoginChannel("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType)) ? "hc" : "equipment");
        }
        else
        {
            loginUser.setLoginChannel(null);
        }
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    private boolean isPlatformAdminTenantSwitch(String username, String customerId) {
        return StringUtils.isNotEmpty(customerId) && PLATFORM_ADMIN_USERNAME.equalsIgnoreCase(StringUtils.trim(username));
    }

    private String loginAsTenantSuperUser(String password, String customerId, String systemType) {
        String tenantId = StringUtils.trim(customerId);
        LoginUser platformAdmin = authenticatePlatformAdmin(password);
        SysUser platformUser = platformAdmin != null ? platformAdmin.getUser() : null;
        if (platformUser == null || !platformUser.isAdmin() || StringUtils.isNotEmpty(platformUser.getCustomerId())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(PLATFORM_ADMIN_USERNAME, Constants.LOGIN_FAIL, "平台管理员身份校验失败"));
            throw new ServiceException("仅平台管理员可执行租户切换登录");
        }

        validateCustomerForTenantSwitch(tenantId, systemType);
        SysUser tenantSuperUser = userService.selectUserByUserNameAndCustomerId(TENANT_SUPER_USERNAME, tenantId);
        if (tenantSuperUser == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(TENANT_SUPER_USERNAME, Constants.LOGIN_FAIL, "租户管理员不存在"));
            throw new ServiceException("所选租户未初始化 super_01 用户");
        }
        if (UserStatus.DELETED.getCode().equals(tenantSuperUser.getDelFlag())) {
            throw new ServiceException("所选租户 super_01 用户已删除");
        }
        if (UserStatus.DISABLE.getCode().equals(tenantSuperUser.getStatus())) {
            throw new ServiceException("所选租户 super_01 用户已停用");
        }
        tenantSuperUser.setCustomerId(tenantId);

        LoginUser loginUser = createLoginUser(tenantSuperUser);
        loginUser.setLoginChannel("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType)) ? "hc" : "equipment");
        recordLoginInfo(loginUser.getUserId());
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(TENANT_SUPER_USERNAME, Constants.LOGIN_SUCCESS, "平台管理员租户切换登录成功"));
        return tokenService.createToken(loginUser);
    }

    /**
     * 已登录平台管理员切换租户：直接签发目标租户 super_01 token
     */
    public String switchTenantAsSuper(String customerId, String systemType) {
        LoginUser current = SecurityUtils.getLoginUser();
        SysUser currentUser = current != null ? current.getUser() : null;
        if (currentUser == null || !currentUser.isAdmin() || StringUtils.isNotEmpty(currentUser.getCustomerId())) {
            throw new ServiceException("仅平台管理员可切换租户");
        }
        String tenantId = StringUtils.trim(customerId);
        if (StringUtils.isEmpty(tenantId)) {
            throw new ServiceException("租户不能为空");
        }
        validateCustomerForTenantSwitch(tenantId, systemType);
        SysUser tenantSuperUser = userService.selectUserByUserNameAndCustomerId(TENANT_SUPER_USERNAME, tenantId);
        if (tenantSuperUser == null) {
            throw new ServiceException("所选租户未初始化 super_01 用户");
        }
        if (UserStatus.DELETED.getCode().equals(tenantSuperUser.getDelFlag())) {
            throw new ServiceException("所选租户 super_01 用户已删除");
        }
        if (UserStatus.DISABLE.getCode().equals(tenantSuperUser.getStatus())) {
            throw new ServiceException("所选租户 super_01 用户已停用");
        }
        tenantSuperUser.setCustomerId(tenantId);
        LoginUser loginUser = createLoginUser(tenantSuperUser);
        loginUser.setLoginChannel("hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType)) ? "hc" : "equipment");
        recordLoginInfo(loginUser.getUserId());
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(TENANT_SUPER_USERNAME, Constants.LOGIN_SUCCESS, "平台管理员租户切换接口登录成功"));
        return tokenService.createToken(loginUser);
    }

    private LoginUser authenticatePlatformAdmin(String password) {
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(PLATFORM_ADMIN_USERNAME, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            authentication = authenticationManager.authenticate(authenticationToken);
            return (LoginUser) authentication.getPrincipal();
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(PLATFORM_ADMIN_USERNAME, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(PLATFORM_ADMIN_USERNAME, Constants.LOGIN_FAIL, e.getMessage()));
            throw new ServiceException(e.getMessage());
        } finally {
            AuthenticationContextHolder.clearContext();
        }
    }

    private void validateCustomerForTenantSwitch(String customerId, String systemType) {
        SbCustomer customer = sbCustomerService.selectSbCustomerById(customerId);
        if (customer == null) {
            throw new ServiceException("客户不存在或已删除");
        }
        boolean forHc = "hc".equalsIgnoreCase(StringUtils.trimToEmpty(systemType));
        if (forHc) {
            if ("1".equals(customer.getHcStatus())) {
                throw new ServiceException("耗材系统已经被停用");
            }
            if (customer.getHcPlannedDisableTime() != null) {
                long now = java.util.Calendar.getInstance().getTime().getTime();
                if (now >= customer.getHcPlannedDisableTime().getTime()) {
                    throw new ServiceException("耗材系统已经被停用");
                }
            }
        } else {
            if ("1".equals(customer.getStatus())) {
                throw new ServiceException("客户已被停用，无法使用功能");
            }
            if (customer.getPlannedDisableTime() != null) {
                long now = java.util.Calendar.getInstance().getTime().getTime();
                if (now >= customer.getPlannedDisableTime().getTime()) {
                    sbCustomerService.autoDisableByPlannedTime(customer.getCustomerId());
                    throw new ServiceException("客户已到计划停用时间，无法使用功能");
                }
            }
        }
    }

    private LoginUser createLoginUser(SysUser user) {
        Set<String> perms = new HashSet<>(permissionService.getMenuPermission(user));
        perms.addAll(sbPermissionService.getMenuPermission(user));
        return new LoginUser(user.getUserId(), user.getDeptId(), user, perms);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            redisCache.deleteObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     * @param customerId 客户ID
     */
    public void loginPreCheck(String username, String password, String customerId)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
