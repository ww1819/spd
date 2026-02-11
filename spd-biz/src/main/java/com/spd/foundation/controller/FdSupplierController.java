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
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.service.IFdSupplierService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 供应商Controller
 *
 * @author spd
 * @date 2023-12-05
 */
@RestController
@RequestMapping("/foundation/supplier")
public class FdSupplierController extends BaseController
{
    @Autowired
    private IFdSupplierService fdSupplierService;

    /**
     * 查询供应商列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdSupplier fdSupplier)
    {
        startPage();
        List<FdSupplier> list = fdSupplierService.selectFdSupplierList(fdSupplier);
        return getDataTable(list);
    }

    /**
     * 查询所有供应商列表
     */
    @GetMapping("/listAll")
    public List<FdSupplier> listAll(FdSupplier fdSupplier)
    {
        List<FdSupplier> suppliers = fdSupplierService.selectFdSupplierList(fdSupplier);
        return suppliers;
    }

    /**
     * 导出供应商列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:export')")
    @Log(title = "供应商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdSupplier fdSupplier)
    {
        List<FdSupplier> list = fdSupplierService.selectFdSupplierList(fdSupplier);
        ExcelUtil<FdSupplier> util = new ExcelUtil<FdSupplier>(FdSupplier.class);
        util.exportExcel(response, list, "供应商数据");
    }

    /**
     * 获取供应商详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdSupplierService.selectFdSupplierById(id));
    }

    /**
     * 新增供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:add')")
    @Log(title = "供应商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdSupplier fdSupplier)
    {
        return toAjax(fdSupplierService.insertFdSupplier(fdSupplier));
    }

    /**
     * 修改供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:edit')")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdSupplier fdSupplier)
    {
        return toAjax(fdSupplierService.updateFdSupplier(fdSupplier));
    }

    /**
     * 删除供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:remove')")
    @Log(title = "供应商", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdSupplierService.deleteFdSupplierById(ids));
    }

    /**
     * 批量更新供应商名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:updateReferred')")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdSupplierService.updateReferred(ids);
        return success("更新简码成功");
    }
}
