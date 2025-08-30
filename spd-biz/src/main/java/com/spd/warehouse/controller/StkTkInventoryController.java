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
 * 退库Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/warehouse/tkInventory")
public class StkTkInventoryController extends BaseController {

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询退库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:list')")
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
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增退库
     */
    @Log(title = "退库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:add')")
    @PostMapping("/addTkInventory")
    public AjaxResult addTkInventory(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.insertTkStkIoBill(stkIoBill));
    }

    /**
     * 修改退库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:edit')")
    @Log(title = "退库", businessType = BusinessType.UPDATE)
    @PutMapping("/updateTkInventory")
    public AjaxResult updateTkInventory(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.updateTKStkIoBill(stkIoBill));
    }

    /**
     * 审核退库
     */
    @Log(title = "退库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:audit')")
    @PutMapping("/auditTkInventory")
    public AjaxResult auditTkInventory(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }

    /**
     * 删除退库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:remove')")
    @Log(title = "退库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }

    /**
     * 导出退库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:refundDepotApply:export')")
    @Log(title = "退库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "退库数据");
    }
}
