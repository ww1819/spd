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
 * 退货Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/warehouse/thInventory")
public class StkThInventoryController extends BaseController {

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询退货列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:list')")
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
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增退货
     */
    @Log(title = "退货", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:add')")
    @PostMapping("/addThInventory")
    public AjaxResult addThInventory(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.insertTHStkIoBill(stkIoBill));
    }

    /**
     * 修改退货
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:edit')")
    @Log(title = "退货", businessType = BusinessType.UPDATE)
    @PutMapping("/updateThInventory")
    public AjaxResult updateThInventory(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.updateOutStkIoBill(stkIoBill));
    }

    /**
     * 审核退货
     */
    @Log(title = "退货", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:audit')")
    @PutMapping("/auditThInventory")
    public AjaxResult auditThInventory(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }

    /**
     * 删除退货
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:remove')")
    @Log(title = "退货", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }

    /**
     * 导出退货列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:refundGoodsApply:export')")
    @Log(title = "退货", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "退货数据");
    }
}
