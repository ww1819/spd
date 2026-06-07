package com.spd.warehouse.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.service.IStkIoStocktakingService;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.department.dto.StocktakingAppendEntriesBody;
import com.spd.department.dto.StocktakingEntryCountedDto;
import com.spd.department.dto.StocktakingPatchSaveDto;
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.warehouse.domain.dto.WhStocktakingProfitImportConfirmRequest;
import com.spd.warehouse.domain.dto.WhStocktakingProfitImportRow;
import com.spd.common.utils.file.FileUtils;

/**
 * 盘点Controller
 *
 * @author spd
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/stocktaking/in")
public class StkIoStocktakingController extends BaseController
{
    @Autowired
    private IStkIoStocktakingService stkIoStocktakingService;

    private static Date parseStocktakingExpectedUpdateTime(JSONObject json)
    {
        if (json == null)
        {
            return null;
        }
        Object raw = json.get("expectedUpdateTime");
        if (raw == null)
        {
            return null;
        }
        return DateUtils.parseDate(raw);
    }

    /**
     * 查询盘点列表
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoStocktaking stkIoStocktaking)
    {
        startPage();
        List<StkIoStocktaking> list = stkIoStocktakingService.selectStkIoStocktakingList(stkIoStocktaking);
        return getDataTable(list);
    }

    /**
     * 导出盘点列表
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:export')")
    @Log(title = "盘点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoStocktaking stkIoStocktaking)
    {
        List<StkIoStocktaking> list = stkIoStocktakingService.selectStkIoStocktakingList(stkIoStocktaking);
        ExcelUtil<StkIoStocktaking> util = new ExcelUtil<StkIoStocktaking>(StkIoStocktaking.class);
        util.exportExcel(response, list, "盘点数据");
    }

    /**
     * 获取盘点详细信息
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoStocktakingService.selectStkIoStocktakingById(id));
    }

    /**
     * 新增盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add')")
    @Log(title = "盘点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        return toAjax(stkIoStocktakingService.insertStkIoStocktaking(stkIoStocktaking));
    }

    /**
     * 仓库盘点初始化：服务端按仓库库存生成并保存主单+明细，成功后返回完整单据（失败不落库）。
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add') or @ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "仓库盘点初始化", businessType = BusinessType.INSERT)
    @PostMapping("/init-from-inventory")
    public AjaxResult initFromWhInventory(@RequestBody StkIoStocktaking body)
    {
        return success(stkIoStocktakingService.initWarehouseStocktakingFromInventory(body));
    }

    /**
     * 修改盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "盘点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        return toAjax(stkIoStocktakingService.updateStkIoStocktaking(stkIoStocktaking));
    }

    /**
     * 精简保存：主表 + 变更明细的实盘/账面/已盘。
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "盘点精简保存", businessType = BusinessType.UPDATE)
    @PutMapping("/patch-save")
    public AjaxResult patchSave(@RequestBody StocktakingPatchSaveDto save)
    {
        return success(stkIoStocktakingService.patchSaveWhStocktaking(save));
    }

    /**
     * 向已保存的仓库盘点单追加明细（新行无 id），返回完整单据
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "盘点明细追加", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/entries")
    public AjaxResult appendEntries(@PathVariable("id") Long id, @RequestBody StocktakingAppendEntriesBody body)
    {
        if (body == null)
        {
            body = new StocktakingAppendEntriesBody();
        }
        List<StkIoStocktakingEntry> entries = body.getEntries() != null ? body.getEntries() : Collections.emptyList();
        stkIoStocktakingService.appendWarehouseStocktakingEntries(id, entries, body.getExpectedUpdateTime());
        return success(stkIoStocktakingService.selectStkIoStocktakingById(id));
    }

    /**
     * 更新盘点明细「是否已盘」（未审核单）
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:edit')")
    @Log(title = "盘点明细已盘", businessType = BusinessType.UPDATE)
    @PutMapping("/entry/counted")
    public AjaxResult updateEntryCounted(@RequestBody StocktakingEntryCountedDto dto)
    {
        return toAjax(stkIoStocktakingService.updateStocktakingEntryCounted(dto));
    }

    /**
     * 删除盘点
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:remove')")
    @Log(title = "盘点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stkIoStocktakingService.deleteStkIoStocktakingByIds(ids));
    }

    /**
     * 审核入库
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:audit')")
    @Log(title = "入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditStocktaking")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        List<StocktakingQtyAdjustDto> adjustList = null;
        JSONArray arr = json.getJSONArray("qtyAdjustList");
        if (arr != null && !arr.isEmpty()) {
            adjustList = arr.toJavaList(StocktakingQtyAdjustDto.class);
        }
        int result = stkIoStocktakingService.auditStkIoBill(json.getString("id"), adjustList,
            parseStocktakingExpectedUpdateTime(json));
        return toAjax(result);
    }

    /** 审核前：仓库盘点（501）明细库存数量与 stk_inventory 是否一致 */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:audit')")
    @PostMapping("/auditStocktaking/checkQty")
    public AjaxResult checkAuditQty(@RequestBody JSONObject json)
    {
        return success(stkIoStocktakingService.checkWhStocktakingQtyMismatch(json.getString("id")));
    }

    /**
     * 盘盈明细导入：上传 Excel 预览（不落库）
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add')")
    @PostMapping("/profit-import/preview")
    public AjaxResult previewProfitImport(@RequestParam("file") MultipartFile file)
    {
        return stkIoStocktakingService.previewWhStocktakingProfitImport(file);
    }

    /**
     * 盘盈明细导入：按 SPD仓库ID 拆分生成未审核盘点单
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add')")
    @Log(title = "仓库盘点盘盈明细导入", businessType = BusinessType.IMPORT)
    @PostMapping("/profit-import/confirm")
    public AjaxResult confirmProfitImport(@RequestBody WhStocktakingProfitImportConfirmRequest request)
    {
        if (request == null || request.getRows() == null)
        {
            return error("导入数据不能为空");
        }
        return stkIoStocktakingService.confirmWhStocktakingProfitImport(request.getRows());
    }

    /**
     * 下载盘盈明细导入模板（权限与盘点单新增一致，复用 stocktaking:in:add）
     */
    @PreAuthorize("@ss.hasPermi('stocktaking:in:add')")
    @PostMapping("/profit-import/importTemplate")
    public void profitImportTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<WhStocktakingProfitImportRow> util = new ExcelUtil<>(WhStocktakingProfitImportRow.class);
        FileUtils.setAttachmentResponseHeader(response, "盘盈明细模板.xlsx");
        util.importTemplateExcel(response, "盘盈明细");
    }
}
