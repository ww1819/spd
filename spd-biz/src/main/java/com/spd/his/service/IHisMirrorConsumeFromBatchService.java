package com.spd.his.service;

import com.spd.his.domain.dto.HisGenerateConsumeBody;
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;

/**
 * 按 HIS 抓取批次生成科室批量消耗（已审核）及追溯
 */
public interface IHisMirrorConsumeFromBatchService
{
    HisGenerateConsumeResultVo generateFromFetchBatch(HisGenerateConsumeBody body);
}
