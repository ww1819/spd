package com.spd.equipment.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.core.page.TableDataInfo;
import com.spd.equipment.domain.SbMeasuringCategory;
import com.spd.equipment.service.ISbMeasuringCategoryService;

@RestController
@RequestMapping("/equipment/measuringCategory")
public class SbMeasuringCategoryController extends BaseController {

    @Autowired
    private ISbMeasuringCategoryService service;

    @PreAuthorize("@ss.hasPermi('equipment:measuringCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbMeasuringCategory q) {
        startPage();
        List<SbMeasuringCategory> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:measuringCategory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:measuringCategory:add')")
    @Log(title = "计量器具分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbMeasuringCategory row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:measuringCategory:edit')")
    @Log(title = "计量器具分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbMeasuringCategory row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:measuringCategory:remove')")
    @Log(title = "计量器具分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
