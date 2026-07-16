package com.spd.gz.service;

import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;

public interface IGzHighValueWriteOffService
{
    /**
     * 高值冲销（按 link 分档）：
     * A 未临床确认 / B 已确认未审核 → 回补科室库存并释放关联；
     * C 已审核 → 若无反向单则先建 301+401，再回补库存并释放。
     */
    GzHighValueWriteOffResultVo writeOff(GzHighValueWriteOffBody body);
}
