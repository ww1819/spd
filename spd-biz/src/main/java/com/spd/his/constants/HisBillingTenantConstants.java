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
}
