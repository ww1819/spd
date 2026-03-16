package com.spd.finance.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.finance.domain.SuppSettlementBill;
import com.spd.finance.service.ISuppSettlementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商结算单Controller
 */
@RestController
@RequestMapping("/finance/suppSettlement")
public class SuppSettlementBillController extends BaseController {

    @Autowired
    private ISuppSettlementBillService suppSettlementBillService;

    @PreAuthorize("@ss.hasPermi('finance:suppSettlement:list')")
    @GetMapping("/list")
    public TableDataInfo list(SuppSettlementBill query) {
        startPage();
        List<SuppSettlementBill> list = suppSettlementBillService.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('finance:suppSettlement:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(suppSettlementBillService.getById(id));
    }

    /** 查询该供应商结算单已关联的发票列表 */
    @PreAuthorize("@ss.hasPermi('finance:suppSettlement:query')")
    @GetMapping("/{id}/invoices")
    public AjaxResult listInvoices(@PathVariable String id) {
        return success(suppSettlementBillService.listInvoices(id));
    }

    @PreAuthorize("@ss.hasPermi('finance:suppSettlement:linkInvoice')")
    @PostMapping("/addInvoice")
    public AjaxResult addInvoice(@RequestParam String suppSettlementId, @RequestParam String invoiceId) {
        return toAjax(suppSettlementBillService.addInvoice(suppSettlementId, invoiceId));
    }

    @PreAuthorize("@ss.hasPermi('finance:suppSettlement:linkInvoice')")
    @DeleteMapping("/removeInvoice")
    public AjaxResult removeInvoice(@RequestParam String suppSettlementId, @RequestParam String invoiceId) {
        return toAjax(suppSettlementBillService.removeInvoice(suppSettlementId, invoiceId));
    }
}
