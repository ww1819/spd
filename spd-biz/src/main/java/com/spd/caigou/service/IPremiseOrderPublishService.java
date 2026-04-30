package com.spd.caigou.service;

import java.util.List;

import com.spd.common.core.domain.AjaxResult;

/**
 * 采购订单推送到前置机 scminterface（再转平台），支持「前置机查库组装」与「SPD 组装体」两种链路及可配置兜底。
 */
public interface IPremiseOrderPublishService
{
    /**
     * 按订单 ID 列表推送；具体走 {@code /publish} 还是 {@code /publishPayload} 由参数 {@code spd.order.push.mode} 决定。
     *
     * @param ids 采购订单主键列表
     */
    AjaxResult publish(List<Long> ids);
}
