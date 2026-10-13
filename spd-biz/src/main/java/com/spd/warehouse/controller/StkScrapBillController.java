package com.spd.warehouse.controller;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.enums.BusinessType;
import com.github.pagehelper.PageInfo;
import com.spd.warehouse.domain.StkScrapBill;
import com.spd.warehouse.domain.StkScrapQueryVo;
import com.spd.warehouse.service.IStkScrapBillService;

import java.math.BigDecimal;
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
 * 库房报废单
 */
@RestController
@RequestMapping("/warehouse/scrap")
public class StkScrapBillController extends BaseController {

    @Autowired
    private IStkScrapBillService stkScrapBillService;

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply') or @ss.hasPermi('warehouse:scrap:audit') or @ss.hasPermi('warehouse:scrap:query')")
    @GetMapping("/list")
    public TableDataInfo list(StkScrapBill query) {
        startPage();
        List<StkScrapBill> list = stkScrapBillService.selectStkScrapBillList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:query') or @ss.hasPermi('warehouse:scrap:apply') or @ss.hasPermi('warehouse:scrap:audit')")
    @GetMapping("/query/detail")
    public TableDataInfo queryDetail(StkScrapQueryVo query) {
        startPage();
        List<StkScrapQueryVo> list = stkScrapBillService.selectScrapDetailList(query);
        TotalInfo totalInfo = stkScrapBillService.selectScrapDetailTotal(query);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        if (totalInfo.getTotalAmt() == null) {
            totalInfo.setTotalAmt(BigDecimal.ZERO);
        }
        if (totalInfo.getTotalQty() == null) {
            totalInfo.setTotalQty(BigDecimal.ZERO);
        }
        Long total = new PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:query') or @ss.hasPermi('warehouse:scrap:apply') or @ss.hasPermi('warehouse:scrap:audit')")
    @GetMapping("/query/summary")
    public TableDataInfo querySummary(StkScrapQueryVo query) {
        startPage();
        List<StkScrapQueryVo> list = stkScrapBillService.selectScrapSummaryList(query);
        TotalInfo totalInfo = stkScrapBillService.selectScrapSummaryTotal(query);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        if (totalInfo.getTotalAmt() == null) {
            totalInfo.setTotalAmt(BigDecimal.ZERO);
        }
        if (totalInfo.getTotalQty() == null) {
            totalInfo.setTotalQty(BigDecimal.ZERO);
        }
        Long total = new PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply')")
    @Log(title = "库房报废单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkScrapBill bill) {
        StkScrapBill saved = stkScrapBillService.insertStkScrapBill(bill);
        return success(saved);
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply')")
    @Log(title = "库房报废单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkScrapBill bill) {
        return toAjax(stkScrapBillService.updateStkScrapBill(bill));
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply')")
    @Log(title = "库房报废单提交", businessType = BusinessType.UPDATE)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody Map<String, Object> params) {
        Long[] ids = parseIds(params);
        return toAjax(stkScrapBillService.submitStkScrapBillByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:audit')")
    @Log(title = "库房报损单审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody Map<String, Object> params) {
        Long[] ids = parseIds(params);
        return toAjax(stkScrapBillService.auditStkScrapBillByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply') or @ss.hasPermi('warehouse:scrap:audit') or @ss.hasPermi('warehouse:scrap:query')")
    @GetMapping("/{id:\\d+}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(stkScrapBillService.selectStkScrapBillById(id));
    }

    @PreAuthorize("@ss.hasPermi('warehouse:scrap:apply')")
    @Log(title = "库房报废单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(stkScrapBillService.deleteStkScrapBillByIds(ids));
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
