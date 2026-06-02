package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.dto.GzHighChargeConfirmBody;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;

public interface IGzHighChargeConfirmService
{
    List<GzHighChargeConfirmRowVo> selectConfirmList(GzHighChargeConfirmQuery query);

    GzHighChargeConfirmResultVo confirm(GzHighChargeConfirmBody body);

    GzHighChargeConfirmResultVo getConfirmDetail(String confirmId);
}
