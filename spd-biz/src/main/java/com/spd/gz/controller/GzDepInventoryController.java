package com.spd.gz.controller;

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
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.service.IGzDepInventoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值科室库存Controller
 *
 * @author spd
 * @date 2024-06-22
 */
@RestController
@RequestMapping("/gzDepartment/gzDepInventory")
public class GzDepInventoryController extends BaseController
{
    @Autowired
    private IGzDepInventoryService gzDepInventoryService;

    /**
     * 查询高值科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzDepInventory gzDepInventory)
    {
        startPage();
        List<GzDepInventory> list = gzDepInventoryService.selectGzDepInventoryList(gzDepInventory);
        return getDataTable(list);
    }

    /**
     * 导出高值科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:export')")
    @Log(title = "高值科室库存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzDepInventory gzDepInventory)
    {
        List<GzDepInventory> list = gzDepInventoryService.selectGzDepInventoryList(gzDepInventory);
        ExcelUtil<GzDepInventory> util = new ExcelUtil<GzDepInventory>(GzDepInventory.class);
        util.exportExcel(response, list, "高值科室库存数据");
    }

    /**
     * 获取高值科室库存详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzDepInventoryService.selectGzDepInventoryById(id));
    }

    /**
     * 新增高值科室库存
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:add')")
    @Log(title = "高值科室库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzDepInventory gzDepInventory)
    {
        return toAjax(gzDepInventoryService.insertGzDepInventory(gzDepInventory));
    }

    /**
     * 修改高值科室库存
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:edit')")
    @Log(title = "高值科室库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzDepInventory gzDepInventory)
    {
        return toAjax(gzDepInventoryService.updateGzDepInventory(gzDepInventory));
    }

    /**
     * 删除高值科室库存
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:gzDepInventory:remove')")
    @Log(title = "高值科室库存", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gzDepInventoryService.deleteGzDepInventoryByIds(ids));
    }
}
