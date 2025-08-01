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
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 设备信息管理Controller
 * 
 * @author spd
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/equipment/info")
public class EquipmentInfoController extends BaseController
{
    @Autowired
    private IEquipmentInfoService equipmentInfoService;

    /**
     * 查询设备信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentInfo equipmentInfo)
    {
        startPage();
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoList(equipmentInfo);
        return getDataTable(list);
    }

    /**
     * 导出设备信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:export')")
    @Log(title = "设备信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoList(equipmentInfo);
        ExcelUtil<EquipmentInfo> util = new ExcelUtil<EquipmentInfo>(EquipmentInfo.class);
        util.exportExcel(response, list, "设备信息管理数据");
    }

    /**
     * 获取设备信息管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(equipmentInfoService.selectEquipmentInfoById(id));
    }

    /**
     * 根据资产编号查询设备信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:query')")
    @GetMapping(value = "/assetCode/{assetCode}")
    public AjaxResult getInfoByAssetCode(@PathVariable("assetCode") String assetCode)
    {
        return success(equipmentInfoService.selectEquipmentInfoByAssetCode(assetCode));
    }

    /**
     * 新增设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:add')")
    @Log(title = "设备信息管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentInfo equipmentInfo)
    {
        return toAjax(equipmentInfoService.insertEquipmentInfo(equipmentInfo));
    }

    /**
     * 修改设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:edit')")
    @Log(title = "设备信息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentInfo equipmentInfo)
    {
        return toAjax(equipmentInfoService.updateEquipmentInfo(equipmentInfo));
    }

    /**
     * 删除设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:remove')")
    @Log(title = "设备信息管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(equipmentInfoService.deleteEquipmentInfoByIds(ids));
    }

    /**
     * 查询设备信息统计
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:statistics')")
    @GetMapping("/statistics")
    public AjaxResult statistics(EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoStatistics(equipmentInfo);
        return success(list);
    }
} 