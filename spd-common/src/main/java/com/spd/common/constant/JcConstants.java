package com.spd.common.constant;

/**
 * 集采报量相关常量
 */
public final class JcConstants
{
    private JcConstants()
    {
    }

    /** 租户设置：报量模式 PRODUCT | TYPE */
    public static final String SETTING_REPORT_MODE = "jc.report_mode";

    /** 按产品报量 */
    public static final String MODE_PRODUCT = "PRODUCT";

    /** 按集采类型报量 */
    public static final String MODE_TYPE = "TYPE";

    /** 默认报量模式 */
    public static final String DEFAULT_REPORT_MODE = MODE_PRODUCT;
}
