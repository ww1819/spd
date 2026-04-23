package com.spd.finance.service;

import com.spd.finance.domain.vo.FinanceSettlementSummaryBundleVo;
import com.spd.warehouse.domain.StkIoBill;

/**
 * 财务结算汇总（出退库按供货单位、材料/试剂/未识别分类）
 */
public interface IFinanceSettlementSummaryService
{
    /**
     * 按审核日期等条件统计：库房分类 11/12 为材料，13 为试剂，空或其它为「未识别分类」；出库 201 为正、退库 401 为负；金额=单价×数量。
     */
    FinanceSettlementSummaryBundleVo summarize(StkIoBill query);
}
