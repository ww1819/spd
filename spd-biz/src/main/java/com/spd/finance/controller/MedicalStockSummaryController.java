package com.spd.finance.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.finance.domain.vo.MedicalInboundSummaryVo;
import com.spd.finance.domain.vo.MedicalOutboundSummaryVo;
import com.spd.finance.service.IMedicalStockSummaryService;
import com.spd.warehouse.domain.StkIoBill;

/**
 * 卫材入出库汇总（统计口径：耗材出库数据）
 */
@RestController
@RequestMapping("/finance/medicalStockSummary")
public class MedicalStockSummaryController extends BaseController
{
    @Autowired
    private IMedicalStockSummaryService medicalStockSummaryService;

    @PreAuthorize("@ss.hasPermi('finance:medicalStockSummary:list') || @ss.hasPermi('finance:medicalStockSummary:query')")
    @GetMapping("/inbound/list")
    public TableDataInfo inboundList(StkIoBill query)
    {
        startPage();
        List<MedicalInboundSummaryVo> list = medicalStockSummaryService.listInboundSummary(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('finance:medicalStockSummary:list') || @ss.hasPermi('finance:medicalStockSummary:query')")
    @GetMapping("/outbound/list")
    public TableDataInfo outboundList(StkIoBill query)
    {
        startPage();
        List<MedicalOutboundSummaryVo> list = medicalStockSummaryService.listOutboundSummary(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('finance:medicalStockSummary:export')")
    @Log(title = "卫材入库汇总导出", businessType = BusinessType.EXPORT)
    @PostMapping("/inbound/export")
    public void exportInbound(HttpServletResponse response, StkIoBill query)
    {
        List<MedicalInboundSummaryVo> list = medicalStockSummaryService.listInboundSummary(query);
        ExcelUtil<MedicalInboundSummaryVo> util = new ExcelUtil<MedicalInboundSummaryVo>(MedicalInboundSummaryVo.class);
        util.exportExcel(response, list, "卫材入库汇总");
    }

    @PreAuthorize("@ss.hasPermi('finance:medicalStockSummary:export')")
    @Log(title = "卫材出库汇总导出", businessType = BusinessType.EXPORT)
    @PostMapping("/outbound/export")
    public void exportOutbound(HttpServletResponse response, StkIoBill query)
    {
        List<MedicalOutboundSummaryVo> list = medicalStockSummaryService.listOutboundSummary(query);
        ExcelUtil<MedicalOutboundSummaryVo> util = new ExcelUtil<MedicalOutboundSummaryVo>(MedicalOutboundSummaryVo.class);
        util.exportExcel(response, list, "卫材出库汇总");
    }
}
