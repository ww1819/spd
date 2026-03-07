package com.spd.common.enums;

import com.spd.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户枚举：代码内的租户列表，与 sb_customer.tenant_key 关联。
 * 新增租户时从本枚举选择，业务中可根据当前租户对应枚举做条件分支。
 * <p>
 * 枚举常量名（name）存入 sb_customer.tenant_key；customerId/customerCode 用于落库与登录。
 * 新增租户类型时在此增加枚举项并执行数据库表 sb_customer 的 tenant_key 字段维护。
 * </p>
 *
 * @author spd
 */
public enum TenantEnum {

    /** 示例租户A：对应 strategyA 分支 */
    TENANT_A("tenant-a-001", "TENANT_A", "strategyA", "示例租户A"),
    /** 示例租户B：对应 strategyB 分支 */
    TENANT_B("tenant-b-002", "TENANT_B", "strategyB", "示例租户B"),
    /** 衡水市第三人民医院（枚举名用稳定代号，避免客户改名影响 tenant_key） */
    HS_003("hengsui-third-001", "HSSDSRMYY", "hengsui", "衡水市第三人民医院"),
    /** 默认/通用租户 */
    DEFAULT("tenant-default", "DEFAULT", "default", "默认租户");

    /** 客户ID，对应 sb_customer.customer_id，需唯一 */
    private final String customerId;
    /** 客户编码，对应 sb_customer.customer_code，登录等用 */
    private final String customerCode;
    /** 分支标识，业务条件分支使用 */
    private final String branchKey;
    /** 展示名称，前端下拉等 */
    private final String displayName;

    TenantEnum(String customerId, String customerCode, String branchKey, String displayName) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.branchKey = branchKey;
        this.displayName = displayName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getBranchKey() {
        return branchKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据 sb_customer.tenant_key（即枚举 name）解析
     */
    public static TenantEnum fromTenantKey(String tenantKey) {
        if (StringUtils.isEmpty(tenantKey)) {
            return null;
        }
        try {
            return valueOf(tenantKey);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 根据 customer_id 解析（需与枚举中某常量 customerId 一致）
     */
    public static TenantEnum fromCustomerId(String customerId) {
        if (StringUtils.isEmpty(customerId)) {
            return null;
        }
        for (TenantEnum e : values()) {
            if (customerId.equals(e.getCustomerId())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 供前端/接口使用的列表项
     */
    public static List<TenantEnumVo> toVoList() {
        return Arrays.stream(values())
                .map(e -> new TenantEnumVo(e.name(), e.getCustomerId(), e.getCustomerCode(), e.getBranchKey(), e.getDisplayName()))
                .collect(Collectors.toList());
    }

    /** 枚举项 VO，供接口返回 */
    public static class TenantEnumVo {
        private final String name;
        private final String customerId;
        private final String customerCode;
        private final String branchKey;
        private final String displayName;

        public TenantEnumVo(String name, String customerId, String customerCode, String branchKey, String displayName) {
            this.name = name;
            this.customerId = customerId;
            this.customerCode = customerCode;
            this.branchKey = branchKey;
            this.displayName = displayName;
        }

        public String getName() { return name; }
        public String getCustomerId() { return customerId; }
        public String getCustomerCode() { return customerCode; }
        public String getBranchKey() { return branchKey; }
        public String getDisplayName() { return displayName; }
    }
}
