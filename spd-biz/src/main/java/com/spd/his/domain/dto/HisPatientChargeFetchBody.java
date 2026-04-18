package com.spd.his.domain.dto;

import lombok.Data;

/**
 * 抓取请求：按自然日闭区间 [beginDate, endDate]
 */
@Data
public class HisPatientChargeFetchBody
{
    /** yyyy-MM-dd */
    private String beginDate;
    /** yyyy-MM-dd */
    private String endDate;
}
