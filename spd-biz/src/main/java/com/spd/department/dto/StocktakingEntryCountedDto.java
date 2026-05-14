package com.spd.department.dto;

import java.math.BigDecimal;

/** 更新盘点明细「是否已盘」，可选同时落库实盘数量 */
public class StocktakingEntryCountedDto
{
    /** 明细主键 stk_io_stocktaking_entry.id */
    private Long id;
    /** 是否已盘：0 否，1 是 */
    private Integer countedFlag;
    /** 实盘数量（盘点数量）；非空时与已盘一并写入数据库 */
    private BigDecimal stockQty;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getCountedFlag()
    {
        return countedFlag;
    }

    public void setCountedFlag(Integer countedFlag)
    {
        this.countedFlag = countedFlag;
    }

    public BigDecimal getStockQty()
    {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty)
    {
        this.stockQty = stockQty;
    }
}
