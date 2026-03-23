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
import com.spd.equipment.domain.SbEquipmentBrand;
import com.spd.equipment.service.ISbEquipmentBrandService;

@RestController
@RequestMapping("/equipment/brand")
public class SbEquipmentBrandController extends BaseController {

    @Autowired
    private ISbEquipmentBrandService service;

    @PreAuthorize("@ss.hasPermi('equipment:brand:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbEquipmentBrand q) {
        startPage();
        List<SbEquipmentBrand> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:brand:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:brand:add')")
    @Log(title = "设备品牌", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbEquipmentBrand row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:brand:edit')")
    @Log(title = "设备品牌", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbEquipmentBrand row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:brand:remove')")
    @Log(title = "设备品牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
