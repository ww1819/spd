package com.spd.warehouse.controller;

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
import com.spd.common.enums.BusinessType;
import com.spd.common.core.page.TableDataInfo;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.service.IStkIoProfitLossService;

/**
 * 盈亏单 Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/warehouse/profitLoss")
public class StkIoProfitLossController extends BaseController {

    @Autowired
    private IStkIoProfitLossService stkIoProfitLossService;

    /**
     * 查询盈亏单列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoProfitLoss stkIoProfitLoss) {
        startPage();
        List<StkIoProfitLoss> list = stkIoProfitLossService.selectStkIoProfitLossList(stkIoProfitLoss);
        return getDataTable(list);
    }

    /**
     * 获取盈亏单详情（含明细）
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(stkIoProfitLossService.selectStkIoProfitLossById(id));
    }

    /**
     * 根据已审核盘点单ID加载有盈亏明细，返回盈亏单草稿（不落库）
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:add')")
    @GetMapping("/loadDraft")
    public AjaxResult loadDraft(@RequestParam("stocktakingId") Long stocktakingId) {
        return success(stkIoProfitLossService.loadDraftByStocktakingId(stocktakingId));
    }

    /**
     * 新增盈亏单
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:add')")
    @Log(title = "盈亏单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoProfitLoss stkIoProfitLoss) {
        return toAjax(stkIoProfitLossService.insertStkIoProfitLoss(stkIoProfitLoss));
    }

    /**
     * 修改盈亏单
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:edit')")
    @Log(title = "盈亏单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoProfitLoss stkIoProfitLoss) {
        return toAjax(stkIoProfitLossService.updateStkIoProfitLoss(stkIoProfitLoss));
    }

    /**
     * 删除盈亏单
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:remove')")
    @Log(title = "盈亏单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(stkIoProfitLossService.deleteStkIoProfitLossById(id));
    }

    /**
     * 审核盈亏单
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLoss:audit')")
    @Log(title = "盈亏单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable Long id) {
        return toAjax(stkIoProfitLossService.auditStkIoProfitLoss(id));
    }
}
