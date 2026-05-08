package com.spd.finance.service;

import java.util.List;

import com.spd.finance.domain.vo.MedicalInboundSummaryVo;
import com.spd.finance.domain.vo.MedicalOutboundSummaryVo;
import com.spd.warehouse.domain.StkIoBill;

/**
 * 卫材入出库汇总服务（统计口径：耗材出库数据）
 */
public interface IMedicalStockSummaryService
{
    List<MedicalInboundSummaryVo> listInboundSummary(StkIoBill query);

    List<MedicalOutboundSummaryVo> listOutboundSummary(StkIoBill query);
}
