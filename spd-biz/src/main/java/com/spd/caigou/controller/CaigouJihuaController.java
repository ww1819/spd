package com.spd.caigou.controller;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 采购计划Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/caigou/jihua")
public class CaigouJihuaController extends BaseController
{
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询采购计划列表
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 导出采购计划列表
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:export')")
    @Log(title = "出采购计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "出采购计划数据");
    }

    /**
     * 获取采购计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:add')")
    @Log(title = "采购计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.insertStkIoBill(stkIoBill));
    }

    /**
     * 修改采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:edit')")
    @Log(title = "采购计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.updateStkIoBill(stkIoBill));
    }

    /**
     * 审核采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:audit')")
    @Log(title = "采购计划", businessType = BusinessType.UPDATE)
    @PutMapping("/auditWarehouse")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"));
        return toAjax(result);
    }

    /**
     * 删除采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:remove')")
    @Log(title = "采购计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }



}
