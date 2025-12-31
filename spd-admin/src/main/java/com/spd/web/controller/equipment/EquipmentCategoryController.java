package com.spd.web.controller.equipment;

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
import com.spd.equipment.domain.EquipmentCategory;
import com.spd.equipment.service.IEquipmentCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 设备分类Controller
 *
 * @author spd
 * @date 2024-12-17
 */
@RestController
@RequestMapping("/equipment/category")
public class EquipmentCategoryController extends BaseController
{
    @Autowired
    private IEquipmentCategoryService equipmentCategoryService;

    /**
     * 查询设备分类列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentCategory equipmentCategory)
    {
        startPage();
        List<EquipmentCategory> list = equipmentCategoryService.selectEquipmentCategoryList(equipmentCategory);
        return getDataTable(list);
    }

    /**
     * 查询所有设备分类列表（无需权限）
     */
    @GetMapping("/listAll")
    public List<EquipmentCategory> listAll(EquipmentCategory equipmentCategory)
    {
        List<EquipmentCategory> list = equipmentCategoryService.selectEquipmentCategoryList(equipmentCategory);
        return list;
    }

    /**
     * 查询设备分类树形列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<EquipmentCategory> list = equipmentCategoryService.selectEquipmentCategoryTree();
        return success(list);
    }

    /**
     * 导出设备分类列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:export')")
    @Log(title = "设备分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentCategory equipmentCategory)
    {
        List<EquipmentCategory> list = equipmentCategoryService.selectEquipmentCategoryList(equipmentCategory);
        ExcelUtil<EquipmentCategory> util = new ExcelUtil<EquipmentCategory>(EquipmentCategory.class);
        util.exportExcel(response, list, "设备分类数据");
    }

    /**
     * 获取设备分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(equipmentCategoryService.selectEquipmentCategoryByCategoryId(categoryId));
    }

    /**
     * 新增设备分类
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:add')")
    @Log(title = "设备分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentCategory equipmentCategory)
    {
        return toAjax(equipmentCategoryService.insertEquipmentCategory(equipmentCategory));
    }

    /**
     * 修改设备分类
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:edit')")
    @Log(title = "设备分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentCategory equipmentCategory)
    {
        return toAjax(equipmentCategoryService.updateEquipmentCategory(equipmentCategory));
    }

    /**
     * 删除设备分类
     */
    @PreAuthorize("@ss.hasPermi('equipment:category:remove')")
    @Log(title = "设备分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(equipmentCategoryService.deleteEquipmentCategoryByCategoryIds(categoryIds));
    }
}

