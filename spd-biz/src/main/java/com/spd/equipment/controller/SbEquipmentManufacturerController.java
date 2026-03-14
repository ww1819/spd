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
import com.spd.equipment.domain.SbEquipmentManufacturer;
import com.spd.equipment.service.ISbEquipmentManufacturerService;

@RestController
@RequestMapping("/equipment/manufacturer")
public class SbEquipmentManufacturerController extends BaseController {

    @Autowired
    private ISbEquipmentManufacturerService service;

    @PreAuthorize("@ss.hasPermi('equipment:manufacturer:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbEquipmentManufacturer q) {
        startPage();
        List<SbEquipmentManufacturer> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:manufacturer:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:manufacturer:add')")
    @Log(title = "设备生产厂家", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbEquipmentManufacturer row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:manufacturer:edit')")
    @Log(title = "设备生产厂家", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbEquipmentManufacturer row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:manufacturer:remove')")
    @Log(title = "设备生产厂家", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
