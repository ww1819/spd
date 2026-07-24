package com.spd.caigou.forecast.controller;

import com.spd.caigou.forecast.domain.ForecastCalcRequest;
import com.spd.caigou.forecast.domain.ForecastEntryUpdateBody;
import com.spd.caigou.forecast.domain.ForecastGeneratePlanBody;
import com.spd.caigou.forecast.domain.PurchaseForecastTask;
import com.spd.caigou.forecast.service.IForecastReplenishService;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 采购预测补货
 */
@RestController
@RequestMapping("/caigou/forecast")
public class ForecastReplenishController extends BaseController {

    @Autowired
    private IForecastReplenishService forecastReplenishService;

    /** 历史任务列表 */
    @PreAuthorize("@ss.hasPermi('caigou:forecast:list')")
    @GetMapping("/list")
    public TableDataInfo list(PurchaseForecastTask query) {
        startPage();
        List<PurchaseForecastTask> list = forecastReplenishService.selectTaskList(query);
        return getDataTable(list);
    }

    /** 任务详情（含建议明细） */
    @PreAuthorize("@ss.hasPermi('caigou:forecast:query')")
    @GetMapping("/task/{id}")
    public AjaxResult getTask(@PathVariable("id") Long id) {
        return success(forecastReplenishService.selectTaskDetail(id));
    }

    /** 计算建议 */
    @PreAuthorize("@ss.hasPermi('caigou:forecast:calc')")
    @Log(title = "预测补货计算", businessType = BusinessType.OTHER)
    @PostMapping("/calc")
    public AjaxResult calc(@RequestBody ForecastCalcRequest request) {
        return success(forecastReplenishService.calc(request));
    }

    /** 更新确认量/勾选 */
    @PreAuthorize("@ss.hasPermi('caigou:forecast:calc')")
    @Log(title = "预测补货改量", businessType = BusinessType.UPDATE)
    @PutMapping("/entry")
    public AjaxResult updateEntry(@RequestBody ForecastEntryUpdateBody body) {
        forecastReplenishService.updateEntries(body);
        return success();
    }

    /** 生成草稿采购计划 */
    @PreAuthorize("@ss.hasPermi('caigou:forecast:generate')")
    @Log(title = "预测补货生成计划", businessType = BusinessType.INSERT)
    @PostMapping("/generatePlan")
    public AjaxResult generatePlan(@RequestBody ForecastGeneratePlanBody body) {
        Map<String, Object> result = forecastReplenishService.generatePlan(body);
        return success(result);
    }
}
