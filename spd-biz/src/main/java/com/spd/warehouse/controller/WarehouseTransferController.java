package com.spd.warehouse.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 调拨申请Controller
 *
 * @author spd
 * @date 2026-01-03
 */
@RestController
@RequestMapping("/warehouse/warehouseTransfer")
public class WarehouseTransferController extends BaseController
{
    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询调拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        // 设置单据类型为调拨单
        stkIoBill.setBillType(501);
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 导出调拨申请列表
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:export')")
    @Log(title = "调拨申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        stkIoBill.setBillType(501);
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "调拨申请数据");
    }

    /**
     * 获取调拨申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增调拨申请
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:add')")
    @Log(title = "调拨申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoBill stkIoBill)
    {
        // 设置单据类型为调拨单
        stkIoBill.setBillType(501);
        return toAjax(stkIoBillService.insertStkIoBill(stkIoBill));
    }

    /**
     * 修改调拨申请
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:edit')")
    @Log(title = "调拨申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoBill stkIoBill)
    {
        stkIoBill.setBillType(501);
        return toAjax(stkIoBillService.updateStkIoBill(stkIoBill));
    }

    /**
     * 删除调拨申请
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:remove')")
    @Log(title = "调拨申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(id));
    }

    /**
     * 审核调拨申请
     */
    @PreAuthorize("@ss.hasPermi('warehouseTransfer:apply:audit')")
    @Log(title = "调拨申请审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditTransfer")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }
}

