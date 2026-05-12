package com.spd.department.dto;

import java.math.BigDecimal;

/** 审核前库存不一致时的用户调整值 */
public class StocktakingQtyAdjustDto
{
    private Long entryId;
    private BigDecimal stockQty;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public BigDecimal getStockQty() { return stockQty; }
    public void setStockQty(BigDecimal stockQty) { this.stockQty = stockQty; }
}
