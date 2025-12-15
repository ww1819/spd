package com.spd.settlement.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 结算Controller
 *
 * @author spd
 * @date 2024-12-12
 */
@RestController
@RequestMapping("/settlement/settlement")
public class SettlementController extends BaseController
{
    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询结算列表
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        // 设置结算类型
        stkIoBill.setBillType(501);
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 导出结算列表
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:export')")
    @Log(title = "结算", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        // 设置结算类型
        stkIoBill.setBillType(501);
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "结算数据");
    }

    /**
     * 获取结算详细信息
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 新增结算
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:add')")
    @Log(title = "结算", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoBill stkIoBill)
    {
        // 设置结算类型
        stkIoBill.setBillType(501);
        return toAjax(stkIoBillService.insertStkIoBill(stkIoBill));
    }

    /**
     * 修改结算
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:edit')")
    @Log(title = "结算", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoBill stkIoBill)
    {
        // 设置结算类型
        stkIoBill.setBillType(501);
        return toAjax(stkIoBillService.updateStkIoBill(stkIoBill));
    }

    /**
     * 审核结算
     */
    @PreAuthorize("@ss.hasPermi('settlement:audit:audit')")
    @Log(title = "结算", businessType = BusinessType.UPDATE)
    @PutMapping("/auditSettlement")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }

    /**
     * 删除结算
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:remove')")
    @Log(title = "结算", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }

    /**
     * 查询结算明细：根据供应商、日期范围、仓库结算类型查询出库明细
     * 如果不传供应商ID，则查询所有供应商的明细
     */
    @PreAuthorize("@ss.hasPermi('settlement:apply:list')")
    @GetMapping("/details")
    public AjaxResult getSettlementDetails(StkIoBill stkIoBill)
    {
        List<com.spd.warehouse.domain.StkIoBillEntry> list = stkIoBillService.selectSettlementDetails(stkIoBill);
        return success(list);
    }
}

