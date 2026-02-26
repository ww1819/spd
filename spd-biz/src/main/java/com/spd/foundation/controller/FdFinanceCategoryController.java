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
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.service.IFdFinanceCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 财务分类维护Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/foundation/financeCategory")
public class FdFinanceCategoryController extends BaseController
{
    @Autowired
    private IFdFinanceCategoryService fdFinanceCategoryService;

    /**
     * 查询财务分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFinanceCategory fdFinanceCategory)
    {
        startPage();
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        return getDataTable(list);
    }

    /**
     * 查询所有财务分类维护列表
     */
    @GetMapping("/listAll")
    public List<FdFinanceCategory> listAll(FdFinanceCategory fdFinanceCategory)
    {
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        return list;
    }

    /**
     * 导出财务分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:export')")
    @Log(title = "财务分类维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFinanceCategory fdFinanceCategory)
    {
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        ExcelUtil<FdFinanceCategory> util = new ExcelUtil<FdFinanceCategory>(FdFinanceCategory.class);
        util.exportExcel(response, list, "财务分类维护数据");
    }

    /**
     * 获取财务分类维护详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:query')")
    @GetMapping(value = "/{financeCategoryId}")
    public AjaxResult getInfo(@PathVariable("financeCategoryId") Long financeCategoryId)
    {
        return success(fdFinanceCategoryService.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId));
    }

    /**
     * 新增财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:add')")
    @Log(title = "财务分类维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFinanceCategory fdFinanceCategory)
    {
        return toAjax(fdFinanceCategoryService.insertFdFinanceCategory(fdFinanceCategory));
    }

    /**
     * 修改财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:edit')")
    @Log(title = "财务分类维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFinanceCategory fdFinanceCategory)
    {
        return toAjax(fdFinanceCategoryService.updateFdFinanceCategory(fdFinanceCategory));
    }

    /**
     * 删除财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:remove')")
    @Log(title = "财务分类维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{financeCategoryIds}")
    public AjaxResult remove(@PathVariable Long financeCategoryIds)
    {
        return toAjax(fdFinanceCategoryService.deleteFdFinanceCategoryByFinanceCategoryId(financeCategoryIds));
    }

    /**
     * 批量更新财务分类名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:updateReferred')")
    @Log(title = "财务分类维护", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdFinanceCategoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}
