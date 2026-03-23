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
import com.spd.equipment.domain.SbAssetCategory;
import com.spd.equipment.service.ISbAssetCategoryService;

@RestController
@RequestMapping("/equipment/assetCategory")
public class SbAssetCategoryController extends BaseController {

    @Autowired
    private ISbAssetCategoryService service;

    @PreAuthorize("@ss.hasPermi('equipment:assetCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbAssetCategory q) {
        startPage();
        List<SbAssetCategory> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetCategory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetCategory:add')")
    @Log(title = "资产分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbAssetCategory row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetCategory:edit')")
    @Log(title = "资产分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbAssetCategory row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetCategory:remove')")
    @Log(title = "资产分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
