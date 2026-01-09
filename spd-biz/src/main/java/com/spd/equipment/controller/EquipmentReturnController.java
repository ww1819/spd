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
import com.spd.equipment.domain.EquipmentReturn;
import com.spd.equipment.service.IEquipmentReturnService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 设备退货Controller
 * 
 * @author spd
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/equipment/return")
public class EquipmentReturnController extends BaseController
{
    @Autowired
    private IEquipmentReturnService equipmentReturnService;

    /**
     * 查询设备退货列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentReturn equipmentReturn)
    {
        startPage();
        List<EquipmentReturn> list = equipmentReturnService.selectEquipmentReturnList(equipmentReturn);
        return getDataTable(list);
    }

    /**
     * 导出设备退货列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:export')")
    @Log(title = "设备退货", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentReturn equipmentReturn)
    {
        List<EquipmentReturn> list = equipmentReturnService.selectEquipmentReturnList(equipmentReturn);
        ExcelUtil<EquipmentReturn> util = new ExcelUtil<EquipmentReturn>(EquipmentReturn.class);
        util.exportExcel(response, list, "设备退货数据");
    }

    /**
     * 获取设备退货详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:query')")
    @GetMapping(value = "/{returnId}")
    public AjaxResult getInfo(@PathVariable("returnId") Long returnId)
    {
        return success(equipmentReturnService.selectEquipmentReturnById(returnId));
    }

    /**
     * 新增设备退货
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:add')")
    @Log(title = "设备退货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentReturn equipmentReturn)
    {
        return toAjax(equipmentReturnService.insertEquipmentReturn(equipmentReturn));
    }

    /**
     * 修改设备退货
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:edit')")
    @Log(title = "设备退货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentReturn equipmentReturn)
    {
        return toAjax(equipmentReturnService.updateEquipmentReturn(equipmentReturn));
    }

    /**
     * 删除设备退货
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:remove')")
    @Log(title = "设备退货", businessType = BusinessType.DELETE)
	@DeleteMapping("/{returnIds}")
    public AjaxResult remove(@PathVariable Long[] returnIds)
    {
        return toAjax(equipmentReturnService.deleteEquipmentReturnByIds(returnIds));
    }

    /**
     * 审核设备退货
     */
    @PreAuthorize("@ss.hasPermi('equipment:return:audit')")
    @Log(title = "设备退货", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{returnId}")
    public AjaxResult audit(@PathVariable Long returnId)
    {
        return toAjax(equipmentReturnService.auditEquipmentReturn(returnId));
    }
}
