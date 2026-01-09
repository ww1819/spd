package com.spd.equipment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.equipment.domain.EquipmentStorage;
import com.spd.equipment.service.IEquipmentStorageService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 设备入库Controller
 * 
 * @author spd
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/equipment/storage")
public class EquipmentStorageController extends BaseController
{
    @Autowired
    private IEquipmentStorageService equipmentStorageService;

    /**
     * 查询设备入库列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentStorage equipmentStorage)
    {
        startPage();
        List<EquipmentStorage> list = equipmentStorageService.selectEquipmentStorageList(equipmentStorage);
        return getDataTable(list);
    }

    /**
     * 导出设备入库列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:export')")
    @Log(title = "设备入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentStorage equipmentStorage)
    {
        List<EquipmentStorage> list = equipmentStorageService.selectEquipmentStorageList(equipmentStorage);
        ExcelUtil<EquipmentStorage> util = new ExcelUtil<EquipmentStorage>(EquipmentStorage.class);
        util.exportExcel(response, list, "设备入库数据");
    }

    /**
     * 获取设备入库详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:query')")
    @GetMapping(value = "/{storageId}")
    public AjaxResult getInfo(@PathVariable("storageId") Long storageId)
    {
        return success(equipmentStorageService.selectEquipmentStorageById(storageId));
    }

    /**
     * 新增设备入库
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:add')")
    @Log(title = "设备入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentStorage equipmentStorage)
    {
        return toAjax(equipmentStorageService.insertEquipmentStorage(equipmentStorage));
    }

    /**
     * 修改设备入库
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:edit')")
    @Log(title = "设备入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentStorage equipmentStorage)
    {
        return toAjax(equipmentStorageService.updateEquipmentStorage(equipmentStorage));
    }

    /**
     * 删除设备入库
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:remove')")
    @Log(title = "设备入库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{storageIds}")
    public AjaxResult remove(@PathVariable Long[] storageIds)
    {
        return toAjax(equipmentStorageService.deleteEquipmentStorageByIds(storageIds));
    }

    /**
     * 审核设备入库
     */
    @PreAuthorize("@ss.hasPermi('equipment:storage:audit')")
    @Log(title = "设备入库", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{storageId}")
    public AjaxResult audit(@PathVariable Long storageId)
    {
        return toAjax(equipmentStorageService.auditEquipmentStorage(storageId));
    }
}
