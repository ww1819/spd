package com.spd.common.utils;

/**
 * SaaS 多租户上下文（当前请求的租户 ID，与登录用户绑定，供业务层统一获取）
 * 由 TenantContextFilter 在请求入口设置，请求结束时清理。
 *
 * @author spd
 */
public class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(String customerId) {
        TENANT_ID.set(customerId);
    }

    /**
     * 获取当前请求的租户 ID（来自登录用户），未登录或平台管理员可为 null
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
