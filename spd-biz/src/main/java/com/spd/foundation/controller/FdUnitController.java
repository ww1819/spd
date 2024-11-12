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
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.service.IFdUnitService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 单位明细Controller
 *
 * @author spd
 * @date 2024-04-07
 */
@RestController
@RequestMapping("/foundation/unit")
public class FdUnitController extends BaseController
{
    @Autowired
    private IFdUnitService fdUnitService;

    /**
     * 查询单位明细列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdUnit fdUnit)
    {
        startPage();
        List<FdUnit> list = fdUnitService.selectFdUnitList(fdUnit);
        return getDataTable(list);
    }

    /**
     * 查询所有单位列表明细
     * @param fdUnit
     * @return
     */
    @GetMapping("/listUnitAll")
    public List<FdUnit> listUnitAll(FdUnit fdUnit)
    {
        List<FdUnit> list = fdUnitService.selectFdUnitList(fdUnit);
        return list;
    }

    /**
     * 导出单位明细列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:export')")
    @Log(title = "单位明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdUnit fdUnit)
    {
        List<FdUnit> list = fdUnitService.selectFdUnitList(fdUnit);
        ExcelUtil<FdUnit> util = new ExcelUtil<FdUnit>(FdUnit.class);
        util.exportExcel(response, list, "单位明细数据");
    }

    /**
     * 获取单位明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:query')")
    @GetMapping(value = "/{unitId}")
    public AjaxResult getInfo(@PathVariable("unitId") Long unitId)
    {
        return success(fdUnitService.selectFdUnitByUnitId(unitId));
    }

    /**
     * 新增单位明细
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:add')")
    @Log(title = "单位明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdUnit fdUnit)
    {
        return toAjax(fdUnitService.insertFdUnit(fdUnit));
    }

    /**
     * 修改单位明细
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:edit')")
    @Log(title = "单位明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdUnit fdUnit)
    {
        return toAjax(fdUnitService.updateFdUnit(fdUnit));
    }

    /**
     * 删除单位明细
     */
    @PreAuthorize("@ss.hasPermi('foundation:unit:remove')")
    @Log(title = "单位明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long unitIds)
    {
        return toAjax(fdUnitService.deleteFdUnitByUnitIds(unitIds));
    }
}
