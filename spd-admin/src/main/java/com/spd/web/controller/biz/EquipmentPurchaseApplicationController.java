package com.spd.web.controller.biz;

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
import com.spd.biz.domain.EquipmentPurchaseApplication;
import com.spd.biz.service.IEquipmentPurchaseApplicationService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 设备采购申请Controller
 * 
 * @author spd
 * @date 2024-01-15
 */
@RestController
@RequestMapping("/biz/equipmentPurchaseApplication")
public class EquipmentPurchaseApplicationController extends BaseController
{
    @Autowired
    private IEquipmentPurchaseApplicationService equipmentPurchaseApplicationService;

    /**
     * 查询设备采购申请列表
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        startPage();
        List<EquipmentPurchaseApplication> list = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationList(equipmentPurchaseApplication);
        return getDataTable(list);
    }

    /**
     * 导出设备采购申请列表
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:export')")
    @Log(title = "设备采购申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        List<EquipmentPurchaseApplication> list = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationList(equipmentPurchaseApplication);
        ExcelUtil<EquipmentPurchaseApplication> util = new ExcelUtil<EquipmentPurchaseApplication>(EquipmentPurchaseApplication.class);
        util.exportExcel(response, list, "设备采购申请数据");
    }

    /**
     * 获取设备采购申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationById(id));
    }

    /**
     * 新增设备采购申请
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:add')")
    @Log(title = "设备采购申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        return toAjax(equipmentPurchaseApplicationService.insertEquipmentPurchaseApplication(equipmentPurchaseApplication));
    }

    /**
     * 修改设备采购申请
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:edit')")
    @Log(title = "设备采购申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        return toAjax(equipmentPurchaseApplicationService.updateEquipmentPurchaseApplication(equipmentPurchaseApplication));
    }

    /**
     * 删除设备采购申请
     */
    @PreAuthorize("@ss.hasPermi('biz:equipmentPurchaseApplication:remove')")
    @Log(title = "设备采购申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(equipmentPurchaseApplicationService.deleteEquipmentPurchaseApplicationByIds(ids));
    }
} 