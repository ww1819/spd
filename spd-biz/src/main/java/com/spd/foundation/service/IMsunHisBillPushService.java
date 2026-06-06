package com.spd.foundation.service;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 众阳 HIS 出库/退库单据推送（枣强租户）。
 */
public interface IMsunHisBillPushService
{
    boolean isZaoqiangTenant(String tenantId);

    void assertZaoqiangTenant(String tenantId);

    /** 201 审核后推送 2.5.41 并回写 pharmacyStockId */
    void pushAfterOutboundAudit(StkIoBill bill);

    /** 401 审核前门禁（含实时 2.5.43） */
    void validateReturnGate(StkIoBill bill);

    /** 401 审核后推送 2.5.42 */
    void pushAfterReturnAudit(StkIoBill bill);

    /** 201 补退：仅推未成功/失败明细 */
    void repushOutbound(Long billId);
}
