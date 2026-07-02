package com.spd.his.domain.dto;

import lombok.Data;

/**
 * 抓取请求：计费时间闭区间 [beginDate, endDate]（含首尾）。
 * 支持 yyyy-MM-dd（开始默认 00:00:00，结束默认 23:59:59）或 yyyy-MM-dd HH:mm:ss。
 */
@Data
public class HisPatientChargeFetchBody
{
    /** 开始时间：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss */
    private String beginDate;
    /** 结束时间：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss */
    private String endDate;

    /** 分段间隔天数（自然日，默认 5，最大 7；单次请求内按此间隔查询 HIS） */
    private Integer chunkDays;
}
