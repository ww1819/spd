package com.spd.department.controller;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.service.IDepPurchaseApplyService;

/**
 * 科室采购表（明细 / 汇总），结构对齐科室领用明细表。
 */
@RestController
@RequestMapping("/department/purchaseDetail")
public class PurchaseDetailController extends BaseController
{
    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    /**
     * 采购明细表（已审核科室申购分录）
     */
    @PreAuthorize("@ss.hasPermi('department:purchaseDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepPurchaseApply query)
    {
        depPurchaseApplyService.applyDepartmentScopeToQuery(query);
        startPage();
        List<Map<String, Object>> list = depPurchaseApplyService.selectPurchaseDetailList(query);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = depPurchaseApplyService.selectPurchaseDetailListTotal(query);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 采购汇总表（按科室+耗材+仓库）
     */
    @PreAuthorize("@ss.hasPermi('department:purchaseDetail:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(DepPurchaseApply query)
    {
        depPurchaseApplyService.applyDepartmentScopeToQuery(query);
        startPage();
        List<Map<String, Object>> list = depPurchaseApplyService.selectPurchaseSummaryList(query);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = depPurchaseApplyService.selectPurchaseSummaryListTotal(query);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }
}
