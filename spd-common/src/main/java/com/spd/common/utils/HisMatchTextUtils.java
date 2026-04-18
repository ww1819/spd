package com.spd.common.utils;

/**
 * HIS 与主数据对照时去除不可见控制字符，避免视图/Excel 带入的空白、BOM、换行等导致匹配失败。
 */
public final class HisMatchTextUtils
{
    private HisMatchTextUtils()
    {
    }

    /**
     * 去除 Unicode 类别 Cc（除常见空白外）、Cf（如 BOM），并 trim。
     */
    public static String normalizeMatchKey(String raw)
    {
        if (raw == null)
        {
            return "";
        }
        String s = raw;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (Character.isISOControl(c) && c != '\t')
            {
                continue;
            }
            int type = Character.getType(c);
            if (type == Character.FORMAT)
            {
                continue;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }
}
