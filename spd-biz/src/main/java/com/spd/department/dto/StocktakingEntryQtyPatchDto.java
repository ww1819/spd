package com.spd.department.dto;

import java.math.BigDecimal;

/** 盘点明细增量保存：仅允许改实盘数量、账面数量（对账确认）、是否已盘 */
public class StocktakingEntryQtyPatchDto
{
    /** 明细主键 stk_io_stocktaking_entry.id */
    private Long id;
    /** 实盘数量（盘点数量） */
    private BigDecimal stockQty;
    /**
     * 明细账面数量；仅当与当前科室/仓库实时库存一致时服务端才接受更新 qty。
     * 用于保存前「账面与实物不一致」逐条确认场景。
     */
    private BigDecimal bookQty;
    /** 是否已盘：0 否，1 是；不传则不修改 */
    private Integer countedFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public BigDecimal getStockQty()
    {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty)
    {
        this.stockQty = stockQty;
    }

    public BigDecimal getBookQty()
    {
        return bookQty;
    }

    public void setBookQty(BigDecimal bookQty)
    {
        this.bookQty = bookQty;
    }

    public Integer getCountedFlag()
    {
        return countedFlag;
    }

    public void setCountedFlag(Integer countedFlag)
    {
        this.countedFlag = countedFlag;
    }
}
