package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.foundation.domain.FdJcType;
import com.spd.foundation.service.IFdJcTypeService;

/**
 * 集采类型维护
 */
@RestController
@RequestMapping("/foundation/jcType")
public class FdJcTypeController extends BaseController
{
    @Autowired
    private IFdJcTypeService fdJcTypeService;

    @PreAuthorize("@ss.hasPermi('foundation:jcType:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdJcType query)
    {
        startPage();
        List<FdJcType> list = fdJcTypeService.selectFdJcTypeList(query);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public List<FdJcType> listAll(FdJcType query)
    {
        return fdJcTypeService.selectFdJcTypeList(query);
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcType:export')")
    @Log(title = "集采类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdJcType query)
    {
        List<FdJcType> list = fdJcTypeService.selectFdJcTypeList(query);
        ExcelUtil<FdJcType> util = new ExcelUtil<FdJcType>(FdJcType.class);
        util.exportExcel(response, list, "集采类型");
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcType:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(fdJcTypeService.selectFdJcTypeById(id));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcType:add')")
    @Log(title = "集采类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdJcType row)
    {
        return toAjax(fdJcTypeService.insertFdJcType(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcType:edit')")
    @Log(title = "集采类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdJcType row)
    {
        return toAjax(fdJcTypeService.updateFdJcType(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:jcType:remove')")
    @Log(title = "集采类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(fdJcTypeService.deleteFdJcTypeById(id));
    }
}
