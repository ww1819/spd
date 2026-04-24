package com.spd.caigou.domain.vo;

/**
 * 采购计划明细与科室申购明细关联（批量查询一行）
 */
public class PurchasePlanEntryDepRefRow
{
    private Long entryId;
    private Long depApplyEntryId;

    public Long getEntryId()
    {
        return entryId;
    }

    public void setEntryId(Long entryId)
    {
        this.entryId = entryId;
    }

    public Long getDepApplyEntryId()
    {
        return depApplyEntryId;
    }

    public void setDepApplyEntryId(Long depApplyEntryId)
    {
        this.depApplyEntryId = depApplyEntryId;
    }
}
