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
import com.spd.foundation.domain.FdJcPeriod;
import com.spd.foundation.service.IFdJcPeriodService;

/**
 * 集采周期维护（带年月）
 */
@RestController
@RequestMapping("/foundation/jcPeriod")
public class FdJcPeriodController extends BaseController
{
    @Autowired
    private IFdJcPeriodService fdJcPeriodService;

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdJcPeriod query)
    {
        startPage();
        List<FdJcPeriod> list = fdJcPeriodService.selectFdJcPeriodList(query);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public List<FdJcPeriod> listAll(FdJcPeriod query)
    {
        return fdJcPeriodService.selectFdJcPeriodList(query);
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:export')")
    @Log(title = "集采周期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdJcPeriod query)
    {
        List<FdJcPeriod> list = fdJcPeriodService.selectFdJcPeriodList(query);
        ExcelUtil<FdJcPeriod> util = new ExcelUtil<FdJcPeriod>(FdJcPeriod.class);
        util.exportExcel(response, list, "集采周期");
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(fdJcPeriodService.selectFdJcPeriodById(id));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:add')")
    @Log(title = "集采周期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdJcPeriod row)
    {
        return toAjax(fdJcPeriodService.insertFdJcPeriod(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:edit')")
    @Log(title = "集采周期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdJcPeriod row)
    {
        return toAjax(fdJcPeriodService.updateFdJcPeriod(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcPeriod:remove')")
    @Log(title = "集采周期", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(fdJcPeriodService.deleteFdJcPeriodById(id));
    }
}
