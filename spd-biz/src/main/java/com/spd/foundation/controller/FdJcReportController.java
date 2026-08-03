package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.foundation.domain.FdJcReport;
import com.spd.foundation.service.IFdJcReportService;

/**
 * 集采报量维护（按当前租户模式：产品或类型）
 */
@RestController
@RequestMapping("/foundation/jcReport")
public class FdJcReportController extends BaseController
{
    @Autowired
    private IFdJcReportService fdJcReportService;

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdJcReport query)
    {
        startPage();
        List<FdJcReport> list = fdJcReportService.selectFdJcReportList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:export')")
    @Log(title = "集采报量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdJcReport query)
    {
        List<FdJcReport> list = fdJcReportService.selectFdJcReportList(query);
        ExcelUtil<FdJcReport> util = new ExcelUtil<FdJcReport>(FdJcReport.class);
        util.exportExcel(response, list, "集采报量");
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(fdJcReportService.selectFdJcReportById(id));
    }

    /** 新增或按周期+维度 upsert */
    @PreAuthorize("@ss.hasPermi('foundation:jcReport:add')")
    @Log(title = "集采报量", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdJcReport row)
    {
        return toAjax(fdJcReportService.saveFdJcReport(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:add')")
    @Log(title = "集采报量批量保存", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batch(@RequestBody List<FdJcReport> rows)
    {
        return toAjax(fdJcReportService.batchSave(rows));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:edit')")
    @Log(title = "集采报量", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdJcReport row)
    {
        return toAjax(fdJcReportService.updateFdJcReport(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcReport:remove')")
    @Log(title = "集采报量", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(fdJcReportService.deleteFdJcReportById(id));
    }
}
