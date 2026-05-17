package com.spd.department.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.warehouse.domain.StkIoStocktakingEntry;

/**
 * 盘点单追加明细请求体：明细列表 + 主单并发校验时间。
 */
public class StocktakingAppendEntriesBody
{
    /** 打开/上次保存后主表 update_time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectedUpdateTime;

    private List<StkIoStocktakingEntry> entries;

    public Date getExpectedUpdateTime()
    {
        return expectedUpdateTime;
    }

    public void setExpectedUpdateTime(Date expectedUpdateTime)
    {
        this.expectedUpdateTime = expectedUpdateTime;
    }

    public List<StkIoStocktakingEntry> getEntries()
    {
        return entries;
    }

    public void setEntries(List<StkIoStocktakingEntry> entries)
    {
        this.entries = entries;
    }
}
