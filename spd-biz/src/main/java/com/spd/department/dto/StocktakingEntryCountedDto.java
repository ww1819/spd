package com.spd.department.dto;

/** 更新盘点明细「是否已盘」 */
public class StocktakingEntryCountedDto
{
    /** 明细主键 stk_io_stocktaking_entry.id */
    private Long id;
    /** 是否已盘：0 否，1 是 */
    private Integer countedFlag;

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
}
