package com.spd.department.controller;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.department.service.IConsumeDetailService;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.service.ITenantScopeService;

/**
 * 科室领用明细Controller
 * 
 * @author spd
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/department/consumeDetail")
public class ConsumeDetailController extends BaseController
{
    @Autowired
    private IConsumeDetailService consumeDetailService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    private void applyDepartmentScopeOrDeny(StkIoBill q) {
        tenantScopeService.applyDepartmentScopeQueryParams(
            q.getParams(), SecurityUtils.getUserId(), SecurityUtils.getCustomerId());
    }

    /**
     * 查询领用明细列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeDetailList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeDetailListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询领用汇总列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeSummaryList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeSummaryListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询领用排名列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/ranking")
    public TableDataInfo ranking(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeRankingList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeRankingListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 科室领用—出退库汇总（出库 201 / 退库 401 净出库，按科室权限过滤）
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/outReturnSummary")
    public TableDataInfo outReturnSummary(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeOutReturnSummaryList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeOutReturnSummaryListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询仓库进销存报表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/selectWarehousePsiReport")
        public TableDataInfo selectWarehousePsiReport(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectWarehousePsiReport(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 进销存汇总报表（按产品档案）；totalInfo.params.psiSums 为全量合计
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/selectWarehousePsiReportByMaterial")
    public TableDataInfo selectWarehousePsiReportByMaterial(
            StkIoBill stkIoBill,
            @RequestParam(value = "showUnitPrice", required = false) String showUnitPrice,
            @RequestParam(value = "showBatchNumber", required = false) String showBatchNumber,
            @RequestParam(value = "showExpiry", required = false) String showExpiry,
            @RequestParam(value = "showBatchNo", required = false) String showBatchNo)
    {
        applyPsiShowColumnFlags(stkIoBill, showUnitPrice, showBatchNumber, showExpiry, showBatchNo);
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectWarehousePsiReportByMaterial(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        Map<String, Object> psiSums = consumeDetailService.selectWarehousePsiReportByMaterialTotal(stkIoBill);
        TotalInfo totalInfo = new TotalInfo();
        totalInfo.getParams().put("psiSums", psiSums != null ? psiSums : java.util.Collections.emptyMap());
        return getDataTable(list, totalInfo, total);
    }

    /** 显式写入勾选列标志，避免仅依赖对象绑定导致 MyBatis 读不到 */
    private static void applyPsiShowColumnFlags(StkIoBill bill, String showUnitPrice, String showBatchNumber,
            String showExpiry, String showBatchNo)
    {
        if (bill == null)
        {
            return;
        }
        String up = firstNonEmpty(showUnitPrice, bill.getShowUnitPrice(), mapFlag(bill, "showUnitPrice"));
        String bn = firstNonEmpty(showBatchNumber, bill.getShowBatchNumber(), mapFlag(bill, "showBatchNumber"));
        String ex = firstNonEmpty(showExpiry, bill.getShowExpiry(), mapFlag(bill, "showExpiry"));
        String bo = firstNonEmpty(showBatchNo, bill.getShowBatchNo(), mapFlag(bill, "showBatchNo"));
        bill.setShowUnitPrice(normalizePsiFlag(up));
        bill.setShowBatchNumber(normalizePsiFlag(bn));
        bill.setShowExpiry(normalizePsiFlag(ex));
        bill.setShowBatchNo(normalizePsiFlag(bo));
        bill.getParams().put("showUnitPrice", bill.getShowUnitPrice());
        bill.getParams().put("showBatchNumber", bill.getShowBatchNumber());
        bill.getParams().put("showExpiry", bill.getShowExpiry());
        bill.getParams().put("showBatchNo", bill.getShowBatchNo());
    }

    private static String mapFlag(StkIoBill bill, String key)
    {
        if (bill.getParams() == null || bill.getParams().get(key) == null)
        {
            return null;
        }
        return String.valueOf(bill.getParams().get(key));
    }

    private static String firstNonEmpty(String... vals)
    {
        if (vals == null)
        {
            return null;
        }
        for (String v : vals)
        {
            if (v != null && !v.trim().isEmpty())
            {
                return v.trim();
            }
        }
        return null;
    }

    private static String normalizePsiFlag(String v)
    {
        if (v == null)
        {
            return "0";
        }
        if ("1".equals(v) || "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v) || "on".equalsIgnoreCase(v))
        {
            return "1";
        }
        return "0";
    }

    // 注意：导出功能暂时未实现，因为查询返回的是Map类型，ExcelUtil需要实体类
    // 如需导出功能，可以：
    // 1. 创建对应的实体类并添加@Excel注解
    // 2. 或者在前端实现导出功能（使用前端表格导出插件）
}
