package com.spd.his.domain.dto;

import com.spd.his.domain.HisChargeFetchBatch;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计费明细抓取追溯：在抓取批次上标记是否为本条落库成功批次。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisChargeFetchBatchTraceVo extends HisChargeFetchBatch
{
    private static final long serialVersionUID = 1L;

    /** 本条计费是否由此次抓取下载落库 */
    private Boolean downloadSuccess;

    /** 查询条件摘要（住院/门诊 + 窗口起止） */
    private String queryCondition;
}
