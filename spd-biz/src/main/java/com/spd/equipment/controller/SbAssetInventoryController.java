package com.spd.equipment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.equipment.domain.SbAssetInventory;
import com.spd.equipment.domain.SbAssetInventoryItem;
import com.spd.common.utils.SecurityUtils;
import com.spd.equipment.domain.SbAssetPrintTask;
import com.spd.equipment.service.ISbAssetInventoryService;
import com.spd.equipment.service.ISbAssetInventoryItemService;
import com.spd.equipment.service.ISbAssetPrintTaskItemService;
import com.spd.equipment.service.ISbCustomerAssetLedgerService;

/**
 * 资产盘点单 Controller（盘点单主表+明细，名下权限默认对客户开放）
 */
@RestController
@RequestMapping("/equipment/assetInventory")
public class SbAssetInventoryController extends BaseController {

    @Autowired
    private ISbAssetInventoryService inventoryService;
    @Autowired
    private ISbAssetInventoryItemService inventoryItemService;
    @Autowired
    private ISbAssetPrintTaskItemService printTaskItemService;
    @Autowired
    private ISbCustomerAssetLedgerService assetLedgerService;

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbAssetInventory q) {
        startPage();
        List<SbAssetInventory> list = inventoryService.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:export')")
    @Log(title = "资产盘点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SbAssetInventory q) {
        List<SbAssetInventory> list = inventoryService.selectList(q);
        ExcelUtil<SbAssetInventory> util = new ExcelUtil<>(SbAssetInventory.class);
        util.exportExcel(response, list, "资产盘点单");
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        SbAssetInventory master = inventoryService.selectById(id);
        if (master != null) {
            List<SbAssetInventoryItem> items = inventoryItemService.selectByInventoryId(id);
            AjaxResult r = success(master);
            r.put("items", items);
            return r;
        }
        return success(master);
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:add')")
    @Log(title = "资产盘点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbAssetInventory row) {
        return toAjax(inventoryService.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:edit')")
    @Log(title = "资产盘点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbAssetInventory row) {
        return toAjax(inventoryService.update(row));
    }

    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:remove')")
    @Log(title = "资产盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(inventoryService.deleteById(id));
    }

    /** 提交审核（草稿→待审核，具体审核流可在此扩展） */
    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:submit')")
    @Log(title = "资产盘点提交审核", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/submit")
    public AjaxResult submit(@PathVariable String id) {
        SbAssetInventory row = inventoryService.selectById(id);
        if (row == null) return error("盘点单不存在");
        if (!"draft".equals(row.getStatus())) return error("仅草稿状态可提交审核");
        row.setStatus("audited");
        return toAjax(inventoryService.update(row));
    }

    /** 盘点明细打印：生成打印任务单（含任务单id、任务单号），并建立明细与打印关联；返回任务单及明细供前端打印 */
    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:list')")
    @Log(title = "资产盘点生成打印任务", businessType = BusinessType.INSERT)
    @PostMapping("/printFromItems")
    public AjaxResult printFromItems(@RequestBody java.util.Map<String, Object> body) {
        String inventoryId = body != null && body.get("inventoryId") != null ? body.get("inventoryId").toString() : null;
        if (inventoryId == null || inventoryId.isEmpty()) return error("盘点单ID不能为空");
        @SuppressWarnings("unchecked")
        List<String> itemIds = body != null && body.get("inventoryItemIds") != null ? (List<String>) body.get("inventoryItemIds") : null;
        SbAssetPrintTask task = inventoryService.createPrintTaskFromInventoryItems(inventoryId, itemIds);
        List<com.spd.equipment.domain.SbAssetPrintTaskItem> taskItems = printTaskItemService.selectByTaskId(task.getId());
        AjaxResult r = success(task);
        r.put("items", taskItems);
        return r;
    }

    /** 审核（待审核→已审核/通过，具体审核流可在此扩展） */
    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:audit')")
    @Log(title = "资产盘点审核", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/audit")
    public AjaxResult audit(@PathVariable String id) {
        SbAssetInventory row = inventoryService.selectById(id);
        if (row == null) return error("盘点单不存在");
        if (!"audited".equals(row.getStatus())) return error("状态不允许审核");
        row.setStatus("in_progress");
        row.setAuditBy(SecurityUtils.getUserIdStr());
        row.setAuditTime(new java.util.Date());
        return toAjax(inventoryService.update(row));
    }

    /** 根据盘点类型与表头范围从台账生成盘点明细（仅草稿可执行） */
    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:edit')")
    @Log(title = "资产盘点生成明细", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/buildItems")
    public AjaxResult buildItems(@PathVariable String id) {
        int count = inventoryService.buildItemsFromLedger(id);
        return success().put("count", count);
    }

    /** 获取当前客户台账中不重复的存放地点列表（用于按存放地点盘点下拉） */
    @PreAuthorize("@ss.hasPermi('equipment:assetInventory:list')")
    @GetMapping("/storagePlaces")
    public AjaxResult storagePlaces() {
        List<String> list = assetLedgerService.listDistinctStoragePlace();
        return success(list);
    }
}
