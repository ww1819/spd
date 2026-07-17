package com.spd.gz.service;

import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;

public interface IGzHighValueWriteOffService
{
    /**
     * 高值冲销（按 link 分档，入口见 HV-Q-006）：
     * CONFIRM 页仅 A（未临床确认）；INSTANT_IO 待审核=B、已审核=C；
     * A/B 回补科室库存并释放关联；C 若无反向单则先建 301+401，再回补。
     */
    GzHighValueWriteOffResultVo writeOff(GzHighValueWriteOffBody body);
}
