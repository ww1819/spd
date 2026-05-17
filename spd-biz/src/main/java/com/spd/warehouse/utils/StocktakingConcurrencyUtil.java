package com.spd.warehouse.utils;

import java.util.Date;

import com.spd.common.exception.ServiceException;

/**
 * 盘点单并发：编辑/审核等写操作前校验客户端持有的主表版本时间与库内一致，
 * 避免多人同时改同一单导致数据错乱；配合 Mapper 中 FOR UPDATE 可在事务内串行化写请求。
 */
public final class StocktakingConcurrencyUtil
{
    private StocktakingConcurrencyUtil()
    {
    }

    /**
     * 与库内比对用的版本时间：优先 {@code update_time}；历史数据未写入 update_time 时回退 {@code create_time}。
     */
    public static Date effectiveBillVersion(Date updateTime, Date createTime)
    {
        if (updateTime != null)
        {
            return updateTime;
        }
        return createTime;
    }

    /**
     * 要求客户端传入打开单据时所见的主表版本时间（通常即 {@code update_time}，老单可与 {@code create_time} 一致），
     * 并与库内 {@link #effectiveBillVersion} 比对（精确到秒，兼容 MySQL datetime 与 JSON 反序列化毫秒差）。
     */
    public static void requireExpectedUpdateTime(Date expectedClient, Date dbVersionKey)
    {
        if (expectedClient == null)
        {
            throw new ServiceException("缺少单据更新时间（expectedUpdateTime），请刷新页面后再操作。");
        }
        if (dbVersionKey == null)
        {
            throw new ServiceException("盘点单数据缺少更新时间，请联系管理员。");
        }
        long a = expectedClient.getTime() / 1000L;
        long b = dbVersionKey.getTime() / 1000L;
        if (a != b)
        {
            throw new ServiceException("盘点单已被他人修改或正在审核，请刷新页面后再操作。");
        }
    }
}
