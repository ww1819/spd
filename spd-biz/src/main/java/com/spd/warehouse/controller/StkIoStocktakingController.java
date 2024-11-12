package com.spd.warehouse.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
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
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.service.IStkIoStocktakingService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 盘点Controller
 *
 * @author spd
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/stocktaking/in")
public class StkIoStocktakingController extends BaseController
{
    @Autowired
    private IStkIoStocktakingService stkIoStocktakingService;

    /**
     * 查询盘点列表
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoStocktaking stkIoStocktaking)
    {
        startPage();
        List<StkIoStocktaking> list = stkIoStocktakingService.selectStkIoStocktakingList(stkIoStocktaking);
        return getDataTable(list);
    }

    /**
     * 导出盘点列表
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:export')")
    @Log(title = "盘点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoStocktaking stkIoStocktaking)
    {
        List<StkIoStocktaking> list = stkIoStocktakingService.selectStkIoStocktakingList(stkIoStocktaking);
        ExcelUtil<StkIoStocktaking> util = new ExcelUtil<StkIoStocktaking>(StkIoStocktaking.class);
        util.exportExcel(response, list, "盘点数据");
    }

    /**
     * 获取盘点详细信息
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoStocktakingService.selectStkIoStocktakingById(id));
    }

    /**
     * 新增盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add')")
    @Log(title = "盘点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        return toAjax(stkIoStocktakingService.insertStkIoStocktaking(stkIoStocktaking));
    }

    /**
     * 修改盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "盘点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        return toAjax(stkIoStocktakingService.updateStkIoStocktaking(stkIoStocktaking));
    }

    /**
     * 删除盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:remove')")
    @Log(title = "盘点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stkIoStocktakingService.deleteStkIoStocktakingByIds(ids));
    }

    /**
     * 审核入库
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:audit')")
    @Log(title = "入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditStocktaking")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = stkIoStocktakingService.auditStkIoBill(json.getString("id"));
        return toAjax(result);
    }
}
