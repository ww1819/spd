package com.spd.caigou.forecast.domain;

import java.math.BigDecimal;

/**
 * 仓+物料数量汇总行（消耗/在途）
 */
public class ForecastMaterialQtyRow {

    private Long materialId;
    private BigDecimal qty;

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
}
