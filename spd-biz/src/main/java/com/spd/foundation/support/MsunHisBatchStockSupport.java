package com.spd.foundation.support;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.StringUtils;
import java.math.BigDecimal;

/**
 * 2.5.43 批次库存 / 2.5.41 出库回参行字段兼容。
 * <p>2.5.43 使用 {@code ycStockQueryId}；2.5.41/2.5.82 使用 {@code stockQueryId}。
 */
public final class MsunHisBatchStockSupport
{
    private MsunHisBatchStockSupport()
    {
    }

    public static String resolveStockQueryId(JSONObject row)
    {
        if (row == null)
        {
            return null;
        }
        String yc = row.getString("ycStockQueryId");
        if (StringUtils.isNotEmpty(yc))
        {
            return yc;
        }
        return row.getString("stockQueryId");
    }

    public static String resolveReturnPushStockId(JSONObject row)
    {
        if (row == null)
        {
            return null;
        }
        String stockQueryId = resolveStockQueryId(row);
        String pharmacyStockId = row.getString("pharmacyStockId");
        return firstNonEmpty(stockQueryId, pharmacyStockId);
    }

    public static BigDecimal resolveStockAmount(JSONObject row)
    {
        if (row == null)
        {
            return null;
        }
        Object amt = row.get("stockAmount");
        if (amt == null)
        {
            amt = row.get("quantity");
        }
        if (amt == null)
        {
            return null;
        }
        if (amt instanceof BigDecimal)
        {
            return (BigDecimal) amt;
        }
        return new BigDecimal(String.valueOf(amt));
    }

    public static boolean matchesReturnStockFilter(JSONObject row, String returnStockId)
    {
        if (row == null || StringUtils.isEmpty(returnStockId))
        {
            return true;
        }
        String stockQueryId = resolveStockQueryId(row);
        String pharmacyStockId = row.getString("pharmacyStockId");
        return returnStockId.equals(stockQueryId) || returnStockId.equals(pharmacyStockId);
    }

    private static String firstNonEmpty(String a, String b)
    {
        if (StringUtils.isNotEmpty(a))
        {
            return a;
        }
        return StringUtils.isNotEmpty(b) ? b : null;
    }
}
