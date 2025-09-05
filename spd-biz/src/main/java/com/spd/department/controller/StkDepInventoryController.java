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
import com.spd.department.domain.StkDepInventory;
import com.spd.department.service.IStkDepInventoryService;
import com.spd.department.vo.InventorySummaryVo;
import com.spd.department.vo.DepartmentInOutDetailVo;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室库存Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/department/inventory")
public class StkDepInventoryController extends BaseController
{
    @Autowired
    private IStkDepInventoryService stkDepInventoryService;

    /**
     * 查询科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkDepInventory stkDepInventory)
    {
        startPage();
        List<StkDepInventory> list = stkDepInventoryService.selectStkDepInventoryList(stkDepInventory);
        return getDataTable(list);
    }

    /**
     * 导出科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:export')")
    @Log(title = "科室库存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkDepInventory stkDepInventory)
    {
        List<StkDepInventory> list = stkDepInventoryService.selectStkDepInventoryList(stkDepInventory);
        ExcelUtil<StkDepInventory> util = new ExcelUtil<StkDepInventory>(StkDepInventory.class);
        util.exportExcel(response, list, "科室库存数据");
    }

    /**
     * 获取科室库存详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkDepInventoryService.selectStkDepInventoryById(id));
    }

    /**
     * 新增科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:add')")
    @Log(title = "科室库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkDepInventory stkDepInventory)
    {
        return toAjax(stkDepInventoryService.insertStkDepInventory(stkDepInventory));
    }

    /**
     * 修改科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:edit')")
    @Log(title = "科室库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkDepInventory stkDepInventory)
    {
        return toAjax(stkDepInventoryService.updateStkDepInventory(stkDepInventory));
    }

    /**
     * 删除科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:remove')")
    @Log(title = "科室库存", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stkDepInventoryService.deleteStkDepInventoryByIds(ids));
    }

    /**
     * 查询库存汇总列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(StkDepInventory stkDepInventory)
    {
        startPage();
        List<InventorySummaryVo> list = stkDepInventoryService.selectInventorySummaryList(stkDepInventory);
        return getDataTable(list);
    }

    /**
     * 查询科室进销存明细列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/inout")
    public TableDataInfo inout(StkDepInventory stkDepInventory)
    {
        startPage();
        List<DepartmentInOutDetailVo> list = stkDepInventoryService.selectDepartmentInOutDetailList(stkDepInventory);
        return getDataTable(list);
    }
}
