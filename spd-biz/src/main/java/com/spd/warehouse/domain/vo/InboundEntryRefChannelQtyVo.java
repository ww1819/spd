package com.spd.warehouse.domain.vo;

import java.math.BigDecimal;

/**
 * 入库单明细在某一引用通道（如 RK_TO_CK / RK_TO_TH）上的已审核占用、待审核占用（不落库）。
 */
public class InboundEntryRefChannelQtyVo {

    private BigDecimal auditedQty;
    private BigDecimal pendingQty;

    public BigDecimal getAuditedQty() {
        return auditedQty;
    }

    public void setAuditedQty(BigDecimal auditedQty) {
        this.auditedQty = auditedQty;
    }

    public BigDecimal getPendingQty() {
        return pendingQty;
    }

    public void setPendingQty(BigDecimal pendingQty) {
        this.pendingQty = pendingQty;
    }
}
