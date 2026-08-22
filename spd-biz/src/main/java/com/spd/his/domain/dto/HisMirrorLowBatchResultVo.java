package com.spd.his.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HisMirrorLowBatchResultVo
{
    private int successCount;
    private int failCount;
    /** 逐条失败明细（id + 原因），条数多时前端宜截断展示 */
    private List<String> failMessages = new ArrayList<>();
    /** 按失败原因聚合（已去掉行 id 前缀），便于整理失败原因 */
    private List<HisMirrorFailReasonStatVo> failReasonStats = new ArrayList<>();
}
