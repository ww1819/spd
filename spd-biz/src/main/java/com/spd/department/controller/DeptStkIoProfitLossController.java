package com.spd.department.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.service.IStkIoProfitLossService;
import com.spd.warehouse.vo.StkProfitLossEntryVo;

/**
 * 科室盈亏处理单（业务域 biz_scope=DEP），接口与仓库盈亏单一致，强制科室域过滤。
 *
 * @author spd
 */
@RestController
@RequestMapping("/department/deptProfitLoss")
public class DeptStkIoProfitLossController extends BaseController {

    private static final String BIZ_SCOPE_DEP = "DEP";

    @Autowired
    private IStkIoProfitLossService stkIoProfitLossService;

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoProfitLoss stkIoProfitLoss) {
        stkIoProfitLoss.setBizScope(BIZ_SCOPE_DEP);
        startPage();
        List<StkIoProfitLoss> list = stkIoProfitLossService.selectStkIoProfitLossList(stkIoProfitLoss);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        StkIoProfitLoss bill = stkIoProfitLossService.selectStkIoProfitLossById(id);
        if (bill != null && !BIZ_SCOPE_DEP.equals(bill.getBizScope())) {
            return error("非科室盈亏单，请使用库房菜单操作");
        }
        return success(bill);
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:add')")
    @GetMapping("/loadDraft")
    public AjaxResult loadDraft(@RequestParam("stocktakingId") Long stocktakingId) {
        return success(stkIoProfitLossService.loadDraftByStocktakingId(stocktakingId));
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:add')")
    @Log(title = "科室盈亏单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoProfitLoss stkIoProfitLoss) {
        if (StringUtils.isEmpty(stkIoProfitLoss.getBizScope())) {
            stkIoProfitLoss.setBizScope(BIZ_SCOPE_DEP);
        }
        if (!BIZ_SCOPE_DEP.equals(stkIoProfitLoss.getBizScope())) {
            return error("科室盈亏单 bizScope 必须为 DEP");
        }
        return toAjax(stkIoProfitLossService.insertStkIoProfitLoss(stkIoProfitLoss));
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:edit')")
    @Log(title = "科室盈亏单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoProfitLoss stkIoProfitLoss) {
        stkIoProfitLoss.setBizScope(BIZ_SCOPE_DEP);
        return toAjax(stkIoProfitLossService.updateStkIoProfitLoss(stkIoProfitLoss));
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:remove')")
    @Log(title = "科室盈亏单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(stkIoProfitLossService.deleteStkIoProfitLossById(id));
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:audit')")
    @Log(title = "科室盈亏单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable Long id) {
        return toAjax(stkIoProfitLossService.auditStkIoProfitLoss(id));
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:list')")
    @GetMapping("/entry/list")
    public TableDataInfo listEntry(StkIoProfitLoss stkIoProfitLoss) {
        stkIoProfitLoss.setBizScope(BIZ_SCOPE_DEP);
        startPage();
        List<StkProfitLossEntryVo> list = stkIoProfitLossService.selectProfitLossEntryList(stkIoProfitLoss);
        TotalInfo totalInfo = stkIoProfitLossService.selectProfitLossEntryListTotal(stkIoProfitLoss);
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotalInfo(totalInfo);
        return dataTable;
    }

    @PreAuthorize("@ss.hasPermi('department:deptProfitLoss:list')")
    @GetMapping("/entry/summary")
    public TableDataInfo listEntrySummary(StkIoProfitLoss stkIoProfitLoss) {
        stkIoProfitLoss.setBizScope(BIZ_SCOPE_DEP);
        startPage();
        List<StkProfitLossEntryVo> list = stkIoProfitLossService.selectProfitLossEntrySummaryList(stkIoProfitLoss);
        TotalInfo totalInfo = stkIoProfitLossService.selectProfitLossEntrySummaryListTotal(stkIoProfitLoss);
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotalInfo(totalInfo);
        return dataTable;
    }
}
