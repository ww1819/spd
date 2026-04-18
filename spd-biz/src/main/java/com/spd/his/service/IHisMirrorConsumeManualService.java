package com.spd.his.service;

import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.his.domain.dto.HisMirrorManualRowBody;

public interface IHisMirrorConsumeManualService
{
    HisGenerateConsumeResultVo processLowValue(HisMirrorManualRowBody body);

    HisMirrorHighScanResultVo scanHighBarcode(HisMirrorHighScanBody body);

    HisMirrorHighApplyResultVo applyHighConsume(HisMirrorHighApplyBody body);
}
