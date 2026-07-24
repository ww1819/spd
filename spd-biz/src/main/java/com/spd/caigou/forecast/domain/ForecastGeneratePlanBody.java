package com.spd.caigou.forecast.domain;

import java.util.List;

/**
 * 生成采购计划入参
 */
public class ForecastGeneratePlanBody {

    private Long taskId;
    /** 为空则取任务中 selected=1 且 confirmQty>0 的行 */
    private List<Long> entryIds;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public List<Long> getEntryIds() { return entryIds; }
    public void setEntryIds(List<Long> entryIds) { this.entryIds = entryIds; }
}
