package com.spd.warehouse.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import com.spd.common.utils.StringUtils;

/**
 * 期初库存导入：生产日期、效期可能为 {@code YYYYMMDD} 或 {@code yyyy-MM-dd}（亦兼容 {@code yyyy/MM/dd}）。
 */
public final class InitialImportDateParser
{
    private static final DateTimeFormatter BASIC_ISO = DateTimeFormatter.BASIC_ISO_DATE;

    private InitialImportDateParser()
    {
    }

    /**
     * @param raw Excel 单元格文本或日期序列化结果
     * @return 解析失败返回 {@code null}（空串视为 null）
     */
    public static Date parseToSqlDate(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty())
        {
            return null;
        }
        // 纯 8 位数字：YYYYMMDD
        if (s.matches("\\d{8}"))
        {
            try
            {
                LocalDate d = LocalDate.parse(s, BASIC_ISO);
                return java.sql.Date.valueOf(d);
            }
            catch (DateTimeParseException e)
            {
                return null;
            }
        }
        // yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-')
        {
            try
            {
                LocalDate d = LocalDate.parse(s.substring(0, 10));
                return java.sql.Date.valueOf(d);
            }
            catch (DateTimeParseException e)
            {
                return null;
            }
        }
        // yyyy/MM/dd
        if (s.length() >= 10 && s.charAt(4) == '/' && s.charAt(7) == '/')
        {
            try
            {
                String norm = s.substring(0, 10).replace('/', '-');
                LocalDate d = LocalDate.parse(norm);
                return java.sql.Date.valueOf(d);
            }
            catch (DateTimeParseException e)
            {
                return null;
            }
        }
        return null;
    }

    /**
     * @return 无法解析时返回明确错误信息；空值返回 {@code null} 表示无错误
     */
    public static String validateOrError(String raw, String columnLabel)
    {
        if (StringUtils.isEmpty(StringUtils.trim(raw)))
        {
            return null;
        }
        if (parseToSqlDate(raw) != null)
        {
            return null;
        }
        return columnLabel + "格式无法识别，请使用 YYYYMMDD 或 yyyy-MM-dd（当前值：" + StringUtils.trim(raw) + "）";
    }
}
