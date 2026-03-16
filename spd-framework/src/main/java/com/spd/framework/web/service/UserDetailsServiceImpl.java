package com.spd.framework.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.core.domain.model.LoginUser;
import com.spd.common.enums.UserStatus;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.MessageUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISysUserService;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户验证处理
 * <p>设备系统允许不同租户内用户使用相同用户名，因此登录时通过「客户ID + 用户名」唯一定位用户，
 * 再交由 Spring Security 比对密码是否正确。</p>
 *
 * @author spd
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISbCustomerService sbCustomerService;
    
    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SbPermissionService sbPermissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        String userName = username;
        String customerId = null;
        boolean forHc = false;

        if (username != null && username.contains("|")) {
            String[] parts = username.split("\\|", 2);
            String first = parts[0].trim();
            userName = parts.length > 1 ? parts[1].trim() : "";
            if (StringUtils.isEmpty(userName)) {
                log.info("登录：用户名为空.");
                throw new ServiceException(MessageUtils.message("user.not.exists"));
            }
            if (first.startsWith("hc:")) {
                forHc = true;
                first = first.length() > 3 ? "id:" + first.substring(3) : first;
            }
            SbCustomer customer;
            if (first.startsWith("id:")) {
                customerId = first.length() > 3 ? first.substring(3).trim() : "";
                if (StringUtils.isEmpty(customerId)) {
                    log.info("登录：客户ID为空.");
                    throw new ServiceException("客户不存在或已删除");
                }
                customer = sbCustomerService.selectSbCustomerById(customerId);
            } else {
                if (StringUtils.isEmpty(first)) {
                    log.info("登录：客户编码为空.");
                    throw new ServiceException("客户不存在或已删除");
                }
                customer = sbCustomerService.selectSbCustomerByCode(first);
                if (customer != null) {
                    customerId = customer.getCustomerId();
                }
            }

            // 先校验客户：存在、未停用、合法（耗材登录校验 hc_status/hc_planned_disable_time，设备校验 status/planned_disable_time）
            validateCustomerForLogin(customer, customerId, forHc);
        }

        // 通过「客户ID + 用户名」唯一定位用户（不同租户可同名），定位后再由上层比对密码
        SysUser user = StringUtils.isNotEmpty(customerId)
            ? userService.selectUserByUserNameAndCustomerId(userName, customerId)
            : userService.selectUserByUserNameNoCustomer(userName);

        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        passwordService.validate(user);

        if (StringUtils.isNotEmpty(customerId)) {
            user.setCustomerId(customerId);
        }
        return createLoginUser(user);
    }

    /**
     * 登录前校验客户：是否存在、是否已停用、是否合法（未到计划停用时间等）
     * 仅在租户登录时调用，校验不通过直接抛异常，不继续校验用户名密码。
     * @param forHc true=耗材系统登录，校验 hc_status、hc_planned_disable_time，停用提示「耗材系统已经被停用」；false=设备系统，校验 status、planned_disable_time
     */
    private void validateCustomerForLogin(SbCustomer customer, String customerId, boolean forHc) {
        if (customer == null) {
            log.info("登录：客户不存在，customerId={}", customerId);
            throw new ServiceException("客户不存在或已删除");
        }
        // 设备侧登录时打出客户状态与计划停用时间，便于排查“客户已被停用”误报
        if (!forHc) {
            log.debug("登录：客户校验 customerId={}, status={}, plannedDisableTime={}", customer.getCustomerId(), customer.getStatus(), customer.getPlannedDisableTime());
        }
        if (forHc) {
            if ("1".equals(customer.getHcStatus())) {
                log.info("登录：耗材系统已停用，customerId={}", customer.getCustomerId());
                throw new ServiceException("耗材系统已经被停用");
            }
            if (customer.getHcPlannedDisableTime() != null) {
                long now = java.util.Calendar.getInstance().getTime().getTime();
                if (now >= customer.getHcPlannedDisableTime().getTime()) {
                    log.info("登录：耗材系统已到计划停用时间，customerId={}", customer.getCustomerId());
                    throw new ServiceException("耗材系统已经被停用");
                }
            }
        } else {
            if ("1".equals(customer.getStatus())) {
                log.info("登录：客户已停用(status=1)，customerId={}, status={}", customer.getCustomerId(), customer.getStatus());
                throw new ServiceException("客户已被停用，无法使用功能");
            }
            if (customer.getPlannedDisableTime() != null) {
                long now = java.util.Calendar.getInstance().getTime().getTime();
                if (now >= customer.getPlannedDisableTime().getTime()) {
                    log.info("登录：客户已到计划停用时间，执行自动停用并写启停用记录，customerId={}, plannedDisableTime={}", customer.getCustomerId(), customer.getPlannedDisableTime());
                    sbCustomerService.autoDisableByPlannedTime(customer.getCustomerId());
                    throw new ServiceException("客户已到计划停用时间，无法使用功能");
                }
            }
        }
    }

    public UserDetails createLoginUser(SysUser user)
    {
        Set<String> perms = new HashSet<>(permissionService.getMenuPermission(user));
        perms.addAll(sbPermissionService.getMenuPermission(user));
        return new LoginUser(user.getUserId(), user.getDeptId(), user, perms);
    }
}
