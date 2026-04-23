package com.spd.finance.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.finance.domain.vo.FinanceSettlementSummaryBundleVo;
import com.spd.finance.service.IFinanceSettlementSummaryService;
import com.spd.warehouse.domain.StkIoBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务结算汇总（出退库按供货单位）
 */
@RestController
@RequestMapping("/finance/settlementSummary")
public class FinanceSettlementSummaryController extends BaseController
{
    @Autowired
    private IFinanceSettlementSummaryService financeSettlementSummaryService;

    /**
     * 汇总数据（不分页）
     */
    @PreAuthorize("@ss.hasPermi('finance:settlementSummary:list')")
    @GetMapping("/data")
    public AjaxResult data(StkIoBill query)
    {
        FinanceSettlementSummaryBundleVo vo = financeSettlementSummaryService.summarize(query);
        return AjaxResult.success(vo);
    }
}
