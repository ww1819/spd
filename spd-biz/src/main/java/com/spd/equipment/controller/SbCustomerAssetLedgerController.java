package com.spd.equipment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.equipment.domain.SbCustomerAssetLedger;
import com.spd.equipment.service.ISbCustomerAssetLedgerService;

@RestController
@RequestMapping("/equipment/assetLedger")
public class SbCustomerAssetLedgerController extends BaseController {

    @Autowired
    private ISbCustomerAssetLedgerService service;

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbCustomerAssetLedger q) {
        startPage();
        List<SbCustomerAssetLedger> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:export')")
    @Log(title = "资产台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SbCustomerAssetLedger q) {
        List<SbCustomerAssetLedger> list = service.selectList(q);
        ExcelUtil<SbCustomerAssetLedger> util = new ExcelUtil<>(SbCustomerAssetLedger.class);
        util.exportExcel(response, list, "资产台账");
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:add')")
    @Log(title = "客户资产台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbCustomerAssetLedger row) {
        return toAjax(service.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:edit')")
    @Log(title = "资产台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbCustomerAssetLedger row) {
        return toAjax(service.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetLedger:remove')")
    @Log(title = "资产台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(service.deleteById(id));
    }
}
