package com.spd.warehouse.domain.vo;

import java.math.BigDecimal;

/**
 * 按耗材+仓库聚合库存数量（批量查询用）
 */
public class MaterialWarehouseStockAgg
{
    private Long materialId;
    private BigDecimal sumQty;

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public BigDecimal getSumQty()
    {
        return sumQty;
    }

    public void setSumQty(BigDecimal sumQty)
    {
        this.sumQty = sumQty;
    }
}
