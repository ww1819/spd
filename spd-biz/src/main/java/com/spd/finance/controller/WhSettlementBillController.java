package com.spd.finance.controller;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.finance.domain.WhSettlementBill;
import com.spd.finance.domain.WhSettlementBillEntry;
import com.spd.finance.service.IWhSettlementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库结算单Controller
 */
@RestController
@RequestMapping("/finance/whSettlement")
public class WhSettlementBillController extends BaseController {

    @Autowired
    private IWhSettlementBillService whSettlementBillService;

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:list')")
    @GetMapping("/list")
    public TableDataInfo list(WhSettlementBill query) {
        startPage();
        List<WhSettlementBill> list = whSettlementBillService.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(whSettlementBillService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:add')")
    @Log(title = "仓库结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WhSettlementBill row) {
        return toAjax(whSettlementBillService.add(row));
    }

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:edit')")
    @Log(title = "仓库结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WhSettlementBill row) {
        return toAjax(whSettlementBillService.update(row));
    }

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:remove')")
    @Log(title = "仓库结算单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(whSettlementBillService.remove(id));
    }

    /** 提取数据：选仓库、开始/结束时间、结算方式，返回未结算的入库或出库明细 */
    @PreAuthorize("@ss.hasPermi('finance:whSettlement:add')")
    @GetMapping("/extractData")
    public AjaxResult extractData(
            @RequestParam Long warehouseId,
            @RequestParam String settlementMethod,
            @RequestParam String beginTime,
            @RequestParam String endTime) {
        java.util.Date begin = parseDate(beginTime);
        java.util.Date end = parseDate(endTime);
        List<WhSettlementBillEntry> list = whSettlementBillService.extractData(warehouseId, settlementMethod, begin, end);
        return success(list);
    }

    /** 保存明细（覆盖原明细）；审核后不可增删明细 */
    @PreAuthorize("@ss.hasPermi('finance:whSettlement:edit')")
    @PostMapping("/saveEntries")
    public AjaxResult saveEntries(@RequestParam String billId, @RequestBody List<WhSettlementBillEntry> entries) {
        return toAjax(whSettlementBillService.saveEntries(billId, entries));
    }

    /** 删除指定明细（逻辑删除）；审核后不可用 */
    @PreAuthorize("@ss.hasPermi('finance:whSettlement:edit')")
    @DeleteMapping("/removeEntries")
    public AjaxResult removeEntries(@RequestParam String billId, @RequestBody List<String> entryIds) {
        return toAjax(whSettlementBillService.removeEntries(billId, entryIds));
    }

    @PreAuthorize("@ss.hasPermi('finance:whSettlement:audit')")
    @Log(title = "仓库结算单审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable String id) {
        return toAjax(whSettlementBillService.audit(id));
    }

    private java.util.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
