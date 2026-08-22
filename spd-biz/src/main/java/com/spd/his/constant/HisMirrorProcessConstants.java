package com.spd.his.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * HIS 计费镜像行消耗处理：处理方 / 处理情况文案。
 */
public final class HisMirrorProcessConstants
{
    public static final String PARTY_MANUAL = "手动处理";
    public static final String PARTY_AUTO = "自动处理";
    public static final String RESULT_SUCCESS = "核销成功";
    public static final String RESULT_WRITE_OFF_SUCCESS = "冲销成功，已恢复待处理";

    /** 操作类型：低值核销 / 低值冲销 / 高值核销 */
    public static final String OP_LOW_CONSUME = "LOW_CONSUME";
    public static final String OP_LOW_WRITE_OFF = "LOW_WRITE_OFF";
    public static final String OP_HIGH_CONSUME = "HIGH_CONSUME";

    public static final String OUTCOME_SUCCESS = "SUCCESS";
    public static final String OUTCOME_FAIL = "FAIL";

    public static final String PROC_TYPE_LOW = "LOW_VALUE";
    public static final String PROC_TYPE_HIGH = "HIGH_VALUE";

    private HisMirrorProcessConstants()
    {
    }

    public static String resolveParty(String raw)
    {
        if (PARTY_AUTO.equals(StringUtils.trimToEmpty(raw)))
        {
            return PARTY_AUTO;
        }
        return PARTY_MANUAL;
    }

    public static String truncateSituation(String message)
    {
        String m = StringUtils.trimToEmpty(message);
        if (m.length() <= 500)
        {
            return m;
        }
        return m.substring(0, 500);
    }
}
