package com.spd.department.controller;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import com.github.pagehelper.PageInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.department.service.IConsumeDetailService;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.service.ITenantScopeService;

/**
 * 科室领用明细Controller
 * 
 * @author spd
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/department/consumeDetail")
public class ConsumeDetailController extends BaseController
{
    @Autowired
    private IConsumeDetailService consumeDetailService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    private void applyDepartmentScopeOrDeny(StkIoBill q) {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        List<Long> deptIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
        // null 表示租户管理员/不限制
        if (deptIds == null) {
            return;
        }
        if (deptIds.isEmpty()) {
            deptIds = Collections.emptyList();
        }
        q.getParams().put("deptIds", deptIds);
    }

    /**
     * 查询领用明细列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeDetailList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeDetailListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询领用汇总列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeSummaryList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeSummaryListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询领用排名列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/ranking")
    public TableDataInfo ranking(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectConsumeRankingList(stkIoBill);
        Long total = new PageInfo<>(list).getTotal();
        clearPage();
        TotalInfo totalInfo = consumeDetailService.selectConsumeRankingListTotal(stkIoBill);
        if (totalInfo == null)
        {
            totalInfo = new TotalInfo();
        }
        return getDataTable(list, totalInfo, total);
    }


    /**
     * 查询仓库进销存报表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/selectWarehousePsiReport")
        public TableDataInfo selectWarehousePsiReport(StkIoBill stkIoBill)
    {
        startPage();
        applyDepartmentScopeOrDeny(stkIoBill);
        List<Map<String, Object>> list = consumeDetailService.selectWarehousePsiReport(stkIoBill);
        return getDataTable(list);
    }

    // 注意：导出功能暂时未实现，因为查询返回的是Map类型，ExcelUtil需要实体类
    // 如需导出功能，可以：
    // 1. 创建对应的实体类并添加@Excel注解
    // 2. 或者在前端实现导出功能（使用前端表格导出插件）
}
