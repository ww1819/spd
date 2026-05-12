package com.spd.finance.service;

import java.math.BigDecimal;
import java.util.List;

import com.spd.finance.domain.vo.MedicalInboundSummaryVo;
import com.spd.finance.domain.vo.MedicalOutboundSummaryVo;
import com.spd.warehouse.domain.StkIoBill;

/**
 * 卫材入出库汇总服务（统计口径：耗材出库数据）
 */
public interface IMedicalStockSummaryService
{
    /**
     * 为列表接口写入分页参数（params.medicalSummaryOffset / medicalSummaryLimit），并应用科室范围等。
     */
    void preparePagedSummaryQuery(StkIoBill query, int pageNum, int pageSize);

    long countInboundSummary(StkIoBill query);

    long countOutboundSummary(StkIoBill query);

    BigDecimal sumInboundAmount(StkIoBill query);

    BigDecimal sumOutboundAmount(StkIoBill query);

    List<MedicalInboundSummaryVo> listInboundSummary(StkIoBill query);

    List<MedicalOutboundSummaryVo> listOutboundSummary(StkIoBill query);
}
