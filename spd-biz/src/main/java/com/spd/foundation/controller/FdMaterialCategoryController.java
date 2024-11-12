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
import com.spd.foundation.domain.FdMaterialCategory;
import com.spd.foundation.service.IFdMaterialCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 耗材分类维护Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/foundation/materialCategory")
public class FdMaterialCategoryController extends BaseController
{
    @Autowired
    private IFdMaterialCategoryService fdMaterialCategoryService;

    /**
     * 查询耗材分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdMaterialCategory fdMaterialCategory)
    {
        startPage();
        List<FdMaterialCategory> list = fdMaterialCategoryService.selectFdMaterialCategoryList(fdMaterialCategory);
        return getDataTable(list);
    }

    /**
     * 导出耗材分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:export')")
    @Log(title = "耗材分类维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdMaterialCategory fdMaterialCategory)
    {
        List<FdMaterialCategory> list = fdMaterialCategoryService.selectFdMaterialCategoryList(fdMaterialCategory);
        ExcelUtil<FdMaterialCategory> util = new ExcelUtil<FdMaterialCategory>(FdMaterialCategory.class);
        util.exportExcel(response, list, "耗材分类维护数据");
    }

    /**
     * 获取耗材分类维护详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:query')")
    @GetMapping(value = "/{materialCategoryId}")
    public AjaxResult getInfo(@PathVariable("materialCategoryId") Long materialCategoryId)
    {
        return success(fdMaterialCategoryService.selectFdMaterialCategoryByMaterialCategoryId(materialCategoryId));
    }

    /**
     * 新增耗材分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:add')")
    @Log(title = "耗材分类维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdMaterialCategory fdMaterialCategory)
    {
        return toAjax(fdMaterialCategoryService.insertFdMaterialCategory(fdMaterialCategory));
    }

    /**
     * 修改耗材分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:edit')")
    @Log(title = "耗材分类维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdMaterialCategory fdMaterialCategory)
    {
        return toAjax(fdMaterialCategoryService.updateFdMaterialCategory(fdMaterialCategory));
    }

    /**
     * 删除耗材分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:materialCategory:remove')")
    @Log(title = "耗材分类维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{materialCategoryIds}")
    public AjaxResult remove(@PathVariable Long materialCategoryIds)
    {
        return toAjax(fdMaterialCategoryService.deleteFdMaterialCategoryByMaterialCategoryId(materialCategoryIds));
    }
}
