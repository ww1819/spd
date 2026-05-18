package com.spd.his.constants;

/**
 * 科室计费/退费（衡水三院租户）相关常量
 */
public final class HisBillingTenantConstants
{
    private HisBillingTenantConstants()
    {
    }

    /** 衡水市第三人民医院 SPD 租户 */
    public static final String TENANT_HENGSHUI_THIRD = "hengsui-third-001";

    /**
     * 低值：HIS 计费镜像抓取后是否自动生成科室消耗（0/1）
     */
    public static final String SETTING_LV_AUTO_CONSUME_ENABLED = "dept.billing.lv.auto_consume_enabled";

    /**
     * 计费退费：HIS 镜像抓取后是否自动按退费关联 ID 返还库存（反消耗）（0/1）
     */
    public static final String SETTING_BILLING_AUTO_REFUND_ENABLED = "dept.billing.auto_refund_enabled";

    /** scminterface 调用 SPD 内部计费处理接口的共享密钥（耗材 sys_config） */
    public static final String SETTING_INTERNAL_API_KEY = "his.internal.api_key";

    /** scminterface 调用 SPD 内部接口基址（耗材 sys_config） */
    public static final String CONFIG_SPD_INTERNAL_BASE_URL = "spd.internal.base_url";

    /** 内部处理操作人用户 ID（sb_user.user_id） */
    public static final String SETTING_INTERNAL_OPERATOR_USER_ID = "his.internal.operator_user_id";
}
