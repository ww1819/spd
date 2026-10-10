package com.spd.caigou.controller;

import com.spd.caigou.domain.PurchasePriceAdjust;
import com.spd.caigou.domain.PurchasePriceAdjustEntry;
import com.spd.caigou.service.IPurchasePriceAdjustService;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 采购调价单
 */
@RestController
@RequestMapping("/caigou/priceAdjust")
public class PurchasePriceAdjustController extends BaseController {

    @Autowired
    private IPurchasePriceAdjustService purchasePriceAdjustService;

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list') or @ss.hasPermi('caigou:priceAdjust:audit') or @ss.hasPermi('caigou:priceAdjust:report')")
    @GetMapping("/list")
    public TableDataInfo list(PurchasePriceAdjust query) {
        startPage();
        List<PurchasePriceAdjust> list = purchasePriceAdjustService.selectPurchasePriceAdjustList(query);
        return getDataTable(list);
    }

    /** 调价报表明细表 */
    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list') or @ss.hasPermi('caigou:priceAdjust:report')")
    @GetMapping("/report/detail")
    public TableDataInfo reportDetail(PurchasePriceAdjust query) {
        if (query.getBillStatus() == null || "".equals(query.getBillStatus())) {
            query.setBillStatus("2");
        }
        startPage();
        List<PurchasePriceAdjustEntry> list = purchasePriceAdjustService.selectReportEntryList(query);
        return getDataTable(list);
    }

    /** 调价报表汇总表（按产品） */
    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list') or @ss.hasPermi('caigou:priceAdjust:report')")
    @GetMapping("/report/summary")
    public TableDataInfo reportSummary(PurchasePriceAdjust query) {
        if (query.getBillStatus() == null || "".equals(query.getBillStatus())) {
            query.setBillStatus("2");
        }
        startPage();
        List<PurchasePriceAdjustEntry> list = purchasePriceAdjustService.selectReportSummaryList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list')")
    @Log(title = "采购调价单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PurchasePriceAdjust bill) {
        PurchasePriceAdjust saved = purchasePriceAdjustService.insertPurchasePriceAdjust(bill);
        return success(saved);
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list')")
    @Log(title = "采购调价单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PurchasePriceAdjust bill) {
        return toAjax(purchasePriceAdjustService.updatePurchasePriceAdjust(bill));
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list')")
    @Log(title = "采购调价单提交", businessType = BusinessType.UPDATE)
    @PutMapping("/submit")
    public AjaxResult submit(@RequestBody Map<String, Object> params) {
        Long[] ids = parseIds(params);
        return toAjax(purchasePriceAdjustService.submitPurchasePriceAdjustByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list') or @ss.hasPermi('caigou:priceAdjust:audit')")
    @Log(title = "采购调价单审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody Map<String, Object> params) {
        Long[] ids = parseIds(params);
        return toAjax(purchasePriceAdjustService.auditPurchasePriceAdjustByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list') or @ss.hasPermi('caigou:priceAdjust:audit') or @ss.hasPermi('caigou:priceAdjust:report')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(purchasePriceAdjustService.selectPurchasePriceAdjustById(id));
    }

    @PreAuthorize("@ss.hasPermi('caigou:priceAdjust:list')")
    @Log(title = "采购调价单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(purchasePriceAdjustService.deletePurchasePriceAdjustByIds(ids));
    }

    private Long[] parseIds(Map<String, Object> params) {
        if (params == null || params.get("ids") == null) {
            return new Long[0];
        }
        Object idsObj = params.get("ids");
        if (idsObj instanceof List) {
            List<?> list = (List<?>) idsObj;
            Long[] ids = new Long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                ids[i] = Long.valueOf(String.valueOf(list.get(i)));
            }
            return ids;
        }
        if (idsObj instanceof Long[]) {
            return (Long[]) idsObj;
        }
        if (idsObj.getClass().isArray()) {
            Object[] arr = (Object[]) idsObj;
            Long[] ids = new Long[arr.length];
            for (int i = 0; i < arr.length; i++) {
                ids[i] = Long.valueOf(String.valueOf(arr[i]));
            }
            return ids;
        }
        return new Long[] { Long.valueOf(String.valueOf(idsObj)) };
    }
}
