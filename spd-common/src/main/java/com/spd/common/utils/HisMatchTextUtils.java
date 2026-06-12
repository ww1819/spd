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
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++)
        {
            char c = raw.charAt(i);
            if (Character.isISOControl(c) && c != '\t')
            {
                continue;
            }
            int type = Character.getType(c);
            if (type == Character.FORMAT)
            {
                continue;
            }
            sb.append(toHalfWidthChar(c));
        }
        return sb.toString().trim();
    }

    /** 全角 ASCII 及全角空格转半角，兼容中文输入法下扫码枪输入 */
    private static char toHalfWidthChar(char c)
    {
        if (c == '\u3000')
        {
            return ' ';
        }
        if (c >= '\uFF01' && c <= '\uFF5E')
        {
            return (char) (c - 0xFEE0);
        }
        return c;
    }
}
