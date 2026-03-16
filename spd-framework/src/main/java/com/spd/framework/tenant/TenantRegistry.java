package com.spd.framework.tenant;

import com.spd.common.enums.TenantEnum;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.TenantContext;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 租户注册表：根据当前租户做条件分支。
 * 优先从 sb_customer.tenant_key 关联的 TenantEnum 解析分支；未关联时可根据 customerId 匹配枚举。
 *
 * @author spd
 */
@Component
public class TenantRegistry {

    @Autowired
    private ISbCustomerService sbCustomerService;

    /**
     * 当前请求的租户 ID（来自登录用户）
     */
    public String getCurrentTenantId() {
        return TenantContext.getTenantId();
    }

    /**
     * 当前租户对应的枚举（通过 sb_customer.tenant_key 或 customerId 解析），未关联时可为 null
     */
    public TenantEnum getCurrentTenantEnum() {
        String tenantId = getCurrentTenantId();
        if (StringUtils.isEmpty(tenantId)) {
            return null;
        }
        SbCustomer customer = sbCustomerService.selectSbCustomerById(tenantId);
        if (customer != null && StringUtils.isNotEmpty(customer.getTenantKey())) {
            return TenantEnum.fromTenantKey(customer.getTenantKey());
        }
        return TenantEnum.fromCustomerId(tenantId);
    }

    /**
     * 当前租户的分支标识（来自 TenantEnum.branchKey），用于业务条件分支
     */
    public String getBranchForCurrentTenant() {
        TenantEnum e = getCurrentTenantEnum();
        return e != null ? e.getBranchKey() : null;
    }

    /**
     * 当前租户是否在给定的租户 ID 列表中
     */
    public boolean isCurrentTenantIn(String... tenantIds) {
        String current = getCurrentTenantId();
        if (StringUtils.isEmpty(current) || tenantIds == null) {
            return false;
        }
        for (String id : tenantIds) {
            if (current.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前租户是否属于指定分支（与 TenantEnum.branchKey 一致）
     */
    public boolean isCurrentTenantBranch(String branch) {
        if (StringUtils.isEmpty(branch)) {
            return false;
        }
        return branch.equals(getBranchForCurrentTenant());
    }

    /**
     * 当前租户是否为指定枚举
     */
    public boolean isCurrentTenant(TenantEnum tenantEnum) {
        return tenantEnum != null && tenantEnum == getCurrentTenantEnum();
    }

    /**
     * 代码内租户枚举列表（与 TenantEnum 一致）
     */
    public static Set<String> getConfiguredTenantKeys() {
        return Arrays.stream(TenantEnum.values()).map(TenantEnum::name).collect(Collectors.toSet());
    }

    /**
     * 指定分支下的枚举常量名列表
     */
    public static List<String> getTenantKeysByBranch(String branch) {
        if (StringUtils.isEmpty(branch)) {
            return Collections.emptyList();
        }
        return Arrays.stream(TenantEnum.values())
                .filter(e -> branch.equals(e.getBranchKey()))
                .map(TenantEnum::name)
                .collect(Collectors.toList());
    }
}
