package com.spd.warehouse.controller;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 出库Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/outWarehouse")
public class StkIoBillOutController extends BaseController {

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询出库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 获取入库详细信息
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增出库
     */
    @Log(title = "出库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:add')")
    @PostMapping("/addOutWarehouse")
    public AjaxResult addOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.insertOutStkIoBill(stkIoBill));
    }

    /**
     * 修改出库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:edit')")
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @PutMapping("/updateOutWarehouse")
    public AjaxResult updateOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.updateOutStkIoBill(stkIoBill));
    }

    /**
     * 审核出库
     */
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:audit')")
    @PutMapping("/auditOutWarehouse")
    public AjaxResult auditOutWarehouse(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }

    /**
     * 删除入库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:remove')")
    @Log(title = "入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }

    /**
     * 导出入库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:export')")
    @Log(title = "出入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "出入库数据");
    }
}
