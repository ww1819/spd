package com.spd.warehouse.service;

import java.util.List;

import com.spd.warehouse.domain.StkProfitLossPending;

/**
 * 盘盈待入账明细 Service 接口
 */
public interface IStkProfitLossPendingService
{
    /**
     * 查询待入账明细详情
     *
     * @param id 主键
     * @return 详情
     */
    StkProfitLossPending selectStkProfitLossPendingById(Long id);

    /**
     * 查询待入账明细列表
     *
     * @param pending 查询条件
     * @return 列表
     */
    List<StkProfitLossPending> selectStkProfitLossPendingList(StkProfitLossPending pending);

    /**
     * 更新待入账状态
     *
     * @param id 主键
     * @param applyStatus 入账状态
     * @param settlementEffectStatus 结算影响状态
     * @return 结果
     */
    int updatePendingStatusById(Long id, String applyStatus, String settlementEffectStatus);
}

