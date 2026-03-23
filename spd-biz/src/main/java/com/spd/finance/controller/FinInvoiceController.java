package com.spd.finance.controller;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.finance.domain.FinInvoice;
import com.spd.finance.service.IFinInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票管理Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/finance/invoice")
public class FinInvoiceController extends BaseController {

    @Autowired
    private IFinInvoiceService finInvoiceService;

    @PreAuthorize("@ss.hasPermi('finance:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinInvoice query) {
        startPage();
        List<FinInvoice> list = finInvoiceService.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('finance:invoice:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(finInvoiceService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('finance:invoice:add')")
    @Log(title = "发票管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FinInvoice row) {
        return toAjax(finInvoiceService.add(row));
    }

    @PreAuthorize("@ss.hasPermi('finance:invoice:edit')")
    @Log(title = "发票管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FinInvoice row) {
        return toAjax(finInvoiceService.update(row));
    }

    @PreAuthorize("@ss.hasPermi('finance:invoice:remove')")
    @Log(title = "发票管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(finInvoiceService.remove(id));
    }

    @PreAuthorize("@ss.hasPermi('finance:invoice:audit')")
    @Log(title = "发票审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable String id) {
        return toAjax(finInvoiceService.audit(id));
    }
}
