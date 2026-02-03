package com.spd.department.controller;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.department.service.IConsumeDetailService;
import com.spd.common.core.page.TableDataInfo;

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

    /**
     * 查询领用明细列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        List<Map<String, Object>> list = consumeDetailService.selectConsumeDetailList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 查询领用汇总列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(StkIoBill stkIoBill)
    {
        startPage();
        List<Map<String, Object>> list = consumeDetailService.selectConsumeSummaryList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 查询领用排名列表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/ranking")
    public TableDataInfo ranking(StkIoBill stkIoBill)
    {
        startPage();
        List<Map<String, Object>> list = consumeDetailService.selectConsumeRankingList(stkIoBill);
        return getDataTable(list);
    }


    /**
     * 查询仓库进销存报表
     */
    @PreAuthorize("@ss.hasPermi('department:consumeDetail:list')")
    @GetMapping("/selectWarehousePsiReport")
        public TableDataInfo selectWarehousePsiReport(StkIoBill stkIoBill)
    {
        startPage();
        List<Map<String, Object>> list = consumeDetailService.selectWarehousePsiReport(stkIoBill);
        return getDataTable(list);
    }

    // 注意：导出功能暂时未实现，因为查询返回的是Map类型，ExcelUtil需要实体类
    // 如需导出功能，可以：
    // 1. 创建对应的实体类并添加@Excel注解
    // 2. 或者在前端实现导出功能（使用前端表格导出插件）
}
