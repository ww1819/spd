package com.spd.his.service;

import com.spd.his.domain.dto.HisMirrorWriteOffBody;
import com.spd.his.domain.dto.HisMirrorWriteOffResultVo;

/**
 * HIS 计费镜像低值冲销（反消耗）：恢复为待处理并回补/扣减科室库存。
 */
public interface IHisMirrorConsumeWriteOffService
{
    HisMirrorWriteOffResultVo processLowValueWriteOff(HisMirrorWriteOffBody body);
}
