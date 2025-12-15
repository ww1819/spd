package com.spd.monitoring.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 定数监测Controller
 *
 * @author spd
 * @date 2024-12-13
 */
@RestController
@RequestMapping("/monitoring/fixedNumber")
public class FixedNumberController extends BaseController
{
    /**
     * 查询定数监测列表
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:list')")
    @GetMapping("/list")
    public TableDataInfo list()
    {
        startPage();
        // TODO: 实现实际的数据查询逻辑
        List<Object> list = new ArrayList<>();
        return getDataTable(list);
    }

    /**
     * 新增定数监测
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:add')")
    @Log(title = "定数监测", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Object fixedNumber)
    {
        // TODO: 实现实际的数据保存逻辑
        return AjaxResult.success("保存成功");
    }

    /**
     * 导出定数监测列表
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:export')")
    @Log(title = "定数监测", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response)
    {
        // TODO: 实现实际的导出逻辑
        List<Object> list = new ArrayList<>();
        ExcelUtil<Object> util = new ExcelUtil<Object>(Object.class);
        util.exportExcel(response, list, "定数监测数据");
    }
}

