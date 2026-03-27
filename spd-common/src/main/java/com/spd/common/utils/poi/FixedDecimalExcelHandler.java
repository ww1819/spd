package com.spd.common.utils.poi;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Excel 导出单元格格式化：将数值固定为指定小数位数（默认 4 位）。
 *
 * <p>用于解决 ExcelUtil 在 BigDecimal(scale) 分支中可能导致尾随 0 丢失的问题。</p>
 */
public class FixedDecimalExcelHandler
{
    public String format(Object value, String[] args)
    {
        if (value == null)
        {
            return "";
        }

        int scale = 4;
        if (args != null && args.length > 0)
        {
            try
            {
                scale = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ignored)
            {
                // keep default
            }
        }

        BigDecimal bd = (value instanceof BigDecimal) ? (BigDecimal) value : new BigDecimal(String.valueOf(value));
        return bd.setScale(scale, RoundingMode.HALF_EVEN).toPlainString();
    }
}

