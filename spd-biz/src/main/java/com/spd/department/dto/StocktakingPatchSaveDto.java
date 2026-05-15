package com.spd.department.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 未审核盘点单精简保存：主表少量字段 + 变更明细补丁（不触发全量明细 replace/delete）。
 */
public class StocktakingPatchSaveDto
{
    /** 主单 id */
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date stockDate;
    private String remark;
    private Integer isMonthInit;
    /** 科室盘点可传；有明细后服务端仍拒绝变更科室 */
    private Long departmentId;
    /** 变更的明细；可为空表示仅保存主表 */
    private List<StocktakingEntryQtyPatchDto> entryPatches;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getStockDate()
    {
        return stockDate;
    }

    public void setStockDate(Date stockDate)
    {
        this.stockDate = stockDate;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getIsMonthInit()
    {
        return isMonthInit;
    }

    public void setIsMonthInit(Integer isMonthInit)
    {
        this.isMonthInit = isMonthInit;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public List<StocktakingEntryQtyPatchDto> getEntryPatches()
    {
        return entryPatches;
    }

    public void setEntryPatches(List<StocktakingEntryQtyPatchDto> entryPatches)
    {
        this.entryPatches = entryPatches;
    }
}
