package com.spd.foundation.controller;

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
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.service.IFdWarehouseCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 库房分类Controller
 *
 * @author spd
 * @date 2024-04-12
 */
@RestController
@RequestMapping("/foundation/warehouseCategory")
public class FdWarehouseCategoryController extends BaseController
{
    @Autowired
    private IFdWarehouseCategoryService fdWarehouseCategoryService;

    /**
     * 查询库房分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdWarehouseCategory fdWarehouseCategory)
    {
        startPage();
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        return getDataTable(list);
    }

    /**
     * 查询所有库房分类列表
     */
    @GetMapping("/listAll")
    public List<FdWarehouseCategory> listAll(FdWarehouseCategory fdWarehouseCategory)
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        return list;
    }

    /**
     * 查询库房分类树形列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryTree();
        return success(list);
    }

    /**
     * 导出库房分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:export')")
    @Log(title = "库房分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdWarehouseCategory fdWarehouseCategory)
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        ExcelUtil<FdWarehouseCategory> util = new ExcelUtil<FdWarehouseCategory>(FdWarehouseCategory.class);
        util.exportExcel(response, list, "库房分类数据");
    }

    /**
     * 获取库房分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:query')")
    @GetMapping(value = "/{warehouseCategoryId}")
    public AjaxResult getInfo(@PathVariable("warehouseCategoryId") Long warehouseCategoryId)
    {
        return success(fdWarehouseCategoryService.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId));
    }

    /**
     * 新增库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:add')")
    @Log(title = "库房分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdWarehouseCategory fdWarehouseCategory)
    {
        return toAjax(fdWarehouseCategoryService.insertFdWarehouseCategory(fdWarehouseCategory));
    }

    /**
     * 修改库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:edit')")
    @Log(title = "库房分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdWarehouseCategory fdWarehouseCategory)
    {
        return toAjax(fdWarehouseCategoryService.updateFdWarehouseCategory(fdWarehouseCategory));
    }

    /**
     * 删除库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:remove')")
    @Log(title = "库房分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{warehouseCategoryIds}")
    public AjaxResult remove(@PathVariable Long warehouseCategoryIds)
    {
        return toAjax(fdWarehouseCategoryService.deleteFdWarehouseCategoryByWarehouseCategoryIds(warehouseCategoryIds));
    }

    /**
     * 批量更新库房分类名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:updateReferred')")
    @Log(title = "库房分类", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdWarehouseCategoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}
