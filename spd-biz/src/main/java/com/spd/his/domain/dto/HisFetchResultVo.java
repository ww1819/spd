package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisFetchResultVo
{
    private String fetchBatchId;
    private int insertedCount;
    private int skippedCount;
    private int driftCount;
}
