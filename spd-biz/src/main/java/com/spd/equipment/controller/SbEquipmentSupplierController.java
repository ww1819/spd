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
import com.spd.equipment.domain.SbEquipmentSupplier;
import com.spd.equipment.service.ISbEquipmentSupplierService;

@RestController
@RequestMapping("/equipment/supplier")
public class SbEquipmentSupplierController extends BaseController {

    @Autowired
    private ISbEquipmentSupplierService service;

    @PreAuthorize("@ss.hasPermi('equipment:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbEquipmentSupplier q) {
        startPage();
        List<SbEquipmentSupplier> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:supplier:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    /** 按名称查找或创建供应商（当前客户下），用于表单可输入新供应商名时自动新增 */
    @PreAuthorize("@ss.hasPermi('equipment:supplier:list')")
    @PostMapping("/getOrCreate")
    public AjaxResult getOrCreate(@RequestParam String name) {
        return success(service.getOrCreateByName(name));
    }

    @PreAuthorize("@ss.hasPermi('equipment:supplier:add')")
    @Log(title = "设备供应商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbEquipmentSupplier row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:supplier:edit')")
    @Log(title = "设备供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbEquipmentSupplier row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:supplier:remove')")
    @Log(title = "设备供应商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
