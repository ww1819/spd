package com.spd.foundation.service;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 众阳 HIS 出库/退库单据推送门面（已接入租户，见 {@link com.spd.foundation.support.MsunHisTenantRegistry}）。
 * <p>组包、调 HIS、回写 {@code his_push_status} 与推送后校验均由 scminterface {@code MsunSpdBillPushService} 完成；
 * SPD 仅保留租户门禁、退库本地库存校验，并 HTTP 委托 {@code /api/spd/msun/hospitals/{key}/bill-push/push/{billId}}。
 */
public interface IMsunHisBillPushService
{
    /** 当前租户是否已接入众阳 HIS */
    boolean isMsunIntegratedTenant(String tenantId);

    void assertMsunIntegratedTenant(String tenantId);

    /** 201 审核后推送（委托前置机单据推送） */
    void pushAfterOutboundAudit(StkIoBill bill);

    /** 401 推送前 SPD 本地门禁（收货确认、本地科室库存）；HIS 校验在前置机推送时执行 */
    void validateReturnGate(StkIoBill bill);

    /** 401 审核后推送（委托前置机单据推送） */
    void pushAfterReturnAudit(StkIoBill bill);

    /** 401 手动推送（已审核；委托前置机） */
    void pushReturn(Long billId);

    /** 201 手动推送（已审核；委托前置机） */
    void pushOutbound(Long billId);

    /** @deprecated 使用 {@link #pushOutbound(Long)} */
    void repushOutbound(Long billId);
}
