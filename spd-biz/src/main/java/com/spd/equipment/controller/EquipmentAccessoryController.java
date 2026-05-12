package com.spd.equipment.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.spd.equipment.domain.EquipmentAccessory;
import com.spd.equipment.domain.EquipmentAccessoryIo;
import com.spd.equipment.domain.EquipmentAccessoryIoEntry;
import com.spd.equipment.domain.EquipmentAccessoryStock;
import com.spd.equipment.domain.dto.EquipmentAccessoryIoSubmitBody;
import com.spd.equipment.service.IEquipmentAccessoryService;

@RestController
@RequestMapping("/equipment/accessory")
public class EquipmentAccessoryController extends BaseController {

    @Autowired
    private IEquipmentAccessoryService accessoryService;

    @PreAuthorize("@ss.hasPermi('equipment:accessory:list')")
    @GetMapping("/list")
    public TableDataInfo listAccessory(EquipmentAccessory q) {
        startPage();
        List<EquipmentAccessory> list = accessoryService.selectAccessoryList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getAccessory(@PathVariable("id") String id) {
        return success(accessoryService.selectAccessoryById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:add')")
    @Log(title = "设备配件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult addAccessory(@RequestBody EquipmentAccessory row) {
        return toAjax(accessoryService.insertAccessory(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:edit')")
    @Log(title = "设备配件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult editAccessory(@RequestBody EquipmentAccessory row) {
        return toAjax(accessoryService.updateAccessory(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:remove')")
    @Log(title = "设备配件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult removeAccessory(@PathVariable String id) {
        return toAjax(accessoryService.deleteAccessoryById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:list')")
    @GetMapping("/stock/list")
    public TableDataInfo listStock(EquipmentAccessoryStock q) {
        startPage();
        List<EquipmentAccessoryStock> list = accessoryService.selectStockList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:io:list')")
    @GetMapping("/io/list")
    public TableDataInfo listIo(EquipmentAccessoryIo q) {
        startPage();
        List<EquipmentAccessoryIo> list = accessoryService.selectIoList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:io:list')")
    @GetMapping("/io/detail/{id}")
    public AjaxResult getIo(@PathVariable("id") String id) {
        EquipmentAccessoryIo header = accessoryService.selectIoById(id);
        List<EquipmentAccessoryIoEntry> entries = accessoryService.selectIoEntries(id);
        Map<String, Object> data = new HashMap<>(2);
        data.put("header", header);
        data.put("entries", entries);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('equipment:accessory:io:add')")
    @Log(title = "设备配件出入库", businessType = BusinessType.INSERT)
    @PostMapping("/io")
    public AjaxResult submitIo(@RequestBody EquipmentAccessoryIoSubmitBody body) {
        return toAjax(accessoryService.submitIo(body));
    }
}
