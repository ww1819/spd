package com.spd.caigou.domain.vo;

/**
 * 采购计划明细与科室申购明细关联（批量查询一行）
 */
public class PurchasePlanEntryDepRefRow
{
    private Long entryId;
    private Long depApplyEntryId;
    /** 申购单科室ID（展示申请科室用） */
    private Long departmentId;
    /** 申购单科室名称 */
    private String departmentName;

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

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }
}
