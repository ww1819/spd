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
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.service.IGzDepotInventoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值备货库存明细Controller
 *
 * @author spd
 * @date 2024-06-11
 */
@RestController
@RequestMapping("/gz/depotInventory")
public class GzDepotInventoryController extends BaseController
{
    @Autowired
    private IGzDepotInventoryService gzDepotInventoryService;

    /**
     * 查询高值备货库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzDepotInventory gzDepotInventory)
    {
        startPage();
        List<GzDepotInventory> list = gzDepotInventoryService.selectGzDepotInventoryList(gzDepotInventory);
        return getDataTable(list);
    }

    /**
     * 导出高值备货库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:export')")
    @Log(title = "高值备货库存明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzDepotInventory gzDepotInventory)
    {
        List<GzDepotInventory> list = gzDepotInventoryService.selectGzDepotInventoryList(gzDepotInventory);
        ExcelUtil<GzDepotInventory> util = new ExcelUtil<GzDepotInventory>(GzDepotInventory.class);
        util.exportExcel(response, list, "高值备货库存明细数据");
    }

    /**
     * 获取高值备货库存明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzDepotInventoryService.selectGzDepotInventoryById(id));
    }

    /**
     * 新增高值备货库存明细
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:add')")
    @Log(title = "高值备货库存明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzDepotInventory gzDepotInventory)
    {
        return toAjax(gzDepotInventoryService.insertGzDepotInventory(gzDepotInventory));
    }

    /**
     * 修改高值备货库存明细
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:edit')")
    @Log(title = "高值备货库存明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzDepotInventory gzDepotInventory)
    {
        return toAjax(gzDepotInventoryService.updateGzDepotInventory(gzDepotInventory));
    }

    /**
     * 删除高值备货库存明细
     */
    @PreAuthorize("@ss.hasPermi('gz:depotInventory:remove')")
    @Log(title = "高值备货库存明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gzDepotInventoryService.deleteGzDepotInventoryByIds(ids));
    }
}
