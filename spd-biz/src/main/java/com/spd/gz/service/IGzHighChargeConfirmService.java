package com.spd.gz.service;

import java.math.BigDecimal;
import java.util.List;
import com.spd.gz.domain.dto.GzHighChargeConfirmBody;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;

public interface IGzHighChargeConfirmService
{
    List<GzHighChargeConfirmRowVo> selectConfirmList(GzHighChargeConfirmQuery query);

    /** 与列表同筛选条件的合计金额（轻量 SUM，供 totalInfo） */
    BigDecimal selectConfirmListSumAmt(GzHighChargeConfirmQuery query);

    GzHighChargeConfirmResultVo confirm(GzHighChargeConfirmBody body);

    GzHighChargeConfirmResultVo getConfirmDetail(String confirmId);
}
