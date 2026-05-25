package com.spd.his.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * HIS 计费镜像行消耗处理：处理方 / 处理情况文案。
 */
public final class HisMirrorProcessConstants
{
    public static final String PARTY_MANUAL = "手动处理";
    public static final String PARTY_AUTO = "自动处理";
    public static final String RESULT_SUCCESS = "处理成功";

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
