package com.spd.warehouse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkProfitLossPending;
import com.spd.warehouse.service.IStkProfitLossPendingService;

/**
 * 盘盈待入账明细 Controller
 */
@RestController
@RequestMapping("/warehouse/profitLossPending")
public class StkProfitLossPendingController extends BaseController
{
    @Autowired
    private IStkProfitLossPendingService stkProfitLossPendingService;

    /**
     * 查询待入账明细列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLossPending:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkProfitLossPending pending)
    {
        startPage();
        List<StkProfitLossPending> list = stkProfitLossPendingService.selectStkProfitLossPendingList(pending);
        return getDataTable(list);
    }

    /**
     * 查询待入账明细详情
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLossPending:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkProfitLossPendingService.selectStkProfitLossPendingById(id));
    }

    /**
     * 更新待入账状态
     */
    @PreAuthorize("@ss.hasPermi('warehouse:profitLossPending:edit')")
    @Log(title = "盘盈待入账明细", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{id}")
    public AjaxResult updateStatus(@PathVariable("id") Long id, @RequestBody StkProfitLossPending pending)
    {
        return toAjax(stkProfitLossPendingService.updatePendingStatusById(
            id,
            pending.getApplyStatus(),
            pending.getSettlementEffectStatus()
        ));
    }
}

