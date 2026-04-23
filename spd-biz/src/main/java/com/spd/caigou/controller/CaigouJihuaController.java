package com.spd.caigou.controller;

import com.alibaba.fastjson2.JSONObject;
import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.vo.PurchaseRecordExportVO;
import com.spd.caigou.service.IPurchasePlanEntryApplyService;
import com.spd.caigou.service.IPurchasePlanService;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购计划Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/caigou/jihua")
public class CaigouJihuaController extends BaseController
{
    @Autowired
    private IPurchasePlanService purchasePlanService;
    @Autowired
    private IPurchasePlanEntryApplyService purchasePlanEntryApplyService;

    /**
     * 查询采购计划列表
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:list')")
    @GetMapping("/list")
    public TableDataInfo list(PurchasePlan purchasePlan)
    {
        startPage();
        List<PurchasePlan> list = purchasePlanService.selectPurchasePlanList(purchasePlan);
        return getDataTable(list);
    }

    /**
     * 导出采购计划列表
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:export')")
    @Log(title = "采购计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurchasePlan purchasePlan)
    {
        List<PurchasePlan> list = purchasePlanService.selectPurchasePlanList(purchasePlan);
        ExcelUtil<PurchasePlan> util = new ExcelUtil<PurchasePlan>(PurchasePlan.class);
        util.exportExcel(response, list, "采购计划数据");
    }

    /**
     * 获取采购计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(purchasePlanService.selectPurchasePlanById(id));
    }

    /**
     * 新增采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:add')")
    @Log(title = "采购计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PurchasePlan purchasePlan)
    {
        int rows = purchasePlanService.insertPurchasePlan(purchasePlan);
        if (rows <= 0)
        {
            return error();
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("id", purchasePlan.getId());
        data.put("planNo", purchasePlan.getPlanNo());
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 修改采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:edit')")
    @Log(title = "采购计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PurchasePlan purchasePlan)
    {
        return toAjax(purchasePlanService.updatePurchasePlan(purchasePlan));
    }

    /**
     * 审核采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:audit')")
    @Log(title = "采购计划", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        Long id = json.getLong("id");
        String auditBy = getUserIdStr();
        String auditOpinion = json.getString("auditOpinion");
        int result = purchasePlanService.auditPurchasePlan(id, auditBy, auditOpinion);
        return toAjax(result);
    }

    /**
     * 删除采购计划
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:remove')")
    @Log(title = "采购计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(purchasePlanService.deletePurchasePlanById(ids));
    }

    /**
     * 根据采购计划明细ID查询关联的申购明细（科室申购单单号、申购科室、申购数量、制单人、制单时间、审核人、审核时间）
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:query')")
    @GetMapping("/applyDetails")
    public AjaxResult applyDetails(@RequestParam Long entryId)
    {
        return success(purchasePlanEntryApplyService.listApplyDetailsByEntryId(entryId));
    }

    /**
     * 根据采购计划ID查询关联的申购单号列表（表头「引用申购单号」弹窗用）
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:query')")
    @GetMapping("/applyBillNoList")
    public AjaxResult applyBillNoList(@RequestParam Long planId)
    {
        return success(purchasePlanEntryApplyService.listApplyBillNosByPlanId(planId));
    }

    /**
     * 根据采购计划ID查询关联的申购单表头列表（科室申购单号、仓库、制单人、制单时间、提交人、提交时间、审核人、审核时间）
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:query')")
    @GetMapping("/applyBillHeaderList")
    public AjaxResult applyBillHeaderList(@RequestParam Long planId)
    {
        return success(purchasePlanEntryApplyService.listApplyBillHeaderListByPlanId(planId));
    }

    /**
     * 导出采购记录：按选中的计划单汇总，生成「年份月份耗材采购记录」Excel，首行为标题
     * 列：物资名称，物资规格，数量，单位，供货单位，收货人，收货日期（收货人、收货日期为空）；数量为明细汇总
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:export')")
    @Log(title = "采购计划", businessType = BusinessType.EXPORT)
    @PostMapping("/exportPurchaseRecord")
    public void exportPurchaseRecord(HttpServletResponse response,
                                     @RequestParam(name = "ids", required = false) String idsStr)
    {
        Long[] ids = parseIds(idsStr);
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("请先选择要导出的采购计划");
        }
        List<PurchaseRecordExportVO> list = purchasePlanService.listPurchaseRecordForExportByIds(ids);
        LocalDate now = LocalDate.now();
        String yearMonth = now.getYear() + "年" + now.getMonthValue() + "月";
        String sheetName = yearMonth + "耗材采购记录";
        String title = sheetName;
        ExcelUtil<PurchaseRecordExportVO> util = new ExcelUtil<>(PurchaseRecordExportVO.class);
        util.exportExcel(response, list, sheetName, title);
    }

    /** 解析逗号分隔的 id 字符串为 Long 数组，用于 form 表单提交的 ids */
    private static Long[] parseIds(String idsStr) {
        if (!StringUtils.hasText(idsStr)) {
            return new Long[0];
        }
        String[] parts = idsStr.split(",");
        List<Long> list = new ArrayList<>();
        for (String s : parts) {
            String t = s != null ? s.trim() : "";
            if (t.isEmpty()) continue;
            try {
                list.add(Long.valueOf(t));
            } catch (NumberFormatException ignored) { }
        }
        return list.toArray(new Long[0]);
    }
}
