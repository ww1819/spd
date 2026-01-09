package com.spd.department.controller;

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
import com.spd.department.domain.DepInventoryWarning;
import com.spd.department.service.IDepInventoryWarningService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室库存预警设置Controller
 *
 * @author spd
 * @date 2026-01-03
 */
@RestController
@RequestMapping("/department/inventoryWarning")
public class DepInventoryWarningController extends BaseController
{
    @Autowired
    private IDepInventoryWarningService depInventoryWarningService;

    /**
     * 查询科室库存预警设置列表
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepInventoryWarning depInventoryWarning)
    {
        startPage();
        List<DepInventoryWarning> list = depInventoryWarningService.selectDepInventoryWarningList(depInventoryWarning);
        return getDataTable(list);
    }

    /**
     * 导出科室库存预警设置列表
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:export')")
    @Log(title = "科室库存预警设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DepInventoryWarning depInventoryWarning)
    {
        List<DepInventoryWarning> list = depInventoryWarningService.selectDepInventoryWarningList(depInventoryWarning);
        ExcelUtil<DepInventoryWarning> util = new ExcelUtil<DepInventoryWarning>(DepInventoryWarning.class);
        util.exportExcel(response, list, "科室库存预警设置数据");
    }

    /**
     * 获取科室库存预警设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(depInventoryWarningService.selectDepInventoryWarningById(id));
    }

    /**
     * 新增科室库存预警设置
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:add')")
    @Log(title = "科室库存预警设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DepInventoryWarning depInventoryWarning)
    {
        return toAjax(depInventoryWarningService.insertDepInventoryWarning(depInventoryWarning));
    }

    /**
     * 修改科室库存预警设置
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:edit')")
    @Log(title = "科室库存预警设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DepInventoryWarning depInventoryWarning)
    {
        return toAjax(depInventoryWarningService.updateDepInventoryWarning(depInventoryWarning));
    }

    /**
     * 删除科室库存预警设置
     */
    @PreAuthorize("@ss.hasPermi('department:inventoryWarning:remove')")
    @Log(title = "科室库存预警设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(depInventoryWarningService.deleteDepInventoryWarningByIds(ids));
    }
}

