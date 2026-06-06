package com.spd.foundation.service;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 众阳 HIS 出库/退库单据推送（已接入租户，见 {@link com.spd.foundation.support.MsunHisTenantRegistry}）。
 */
public interface IMsunHisBillPushService
{
    /** 当前租户是否已接入众阳 HIS */
    boolean isMsunIntegratedTenant(String tenantId);

    void assertMsunIntegratedTenant(String tenantId);

    /** 201 审核后推送 2.5.41 并回写 pharmacyStockId */
    void pushAfterOutboundAudit(StkIoBill bill);

    /** 401 审核前门禁（含实时 2.5.43） */
    void validateReturnGate(StkIoBill bill);

    /** 401 审核后推送 2.5.42 */
    void pushAfterReturnAudit(StkIoBill bill);

    /** 201 补退：仅推未成功/失败明细 */
    void repushOutbound(Long billId);
}
