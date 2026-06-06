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

    /** 401 推送前门禁（含实时 2.5.43 查库存并补全 pharmacyStockId） */
    void validateReturnGate(StkIoBill bill);

    /** 401 审核后推送 2.5.42 */
    void pushAfterReturnAudit(StkIoBill bill);

    /** 401 手动推送：推送前校验 HIS 库存，仅推未成功/失败明细（已审核） */
    void pushReturn(Long billId);

    /** 201 手动推送：仅推未成功/失败明细（已审核） */
    void pushOutbound(Long billId);

    /** @deprecated 使用 {@link #pushOutbound(Long)} */
    void repushOutbound(Long billId);
}
