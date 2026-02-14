package com.spd.warehouse.controller;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkInitialImport;
import com.spd.warehouse.domain.dto.InitialImportExcelRow;
import com.spd.warehouse.service.IStkInitialImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 期初库存导入 Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/warehouse/initialStockImport")
public class StkInitialImportController extends BaseController {

    @Autowired
    private IStkInitialImportService stkInitialImportService;

    /**
     * 预览：上传文件解析，不落库
     */
    @PreAuthorize("@ss.hasPermi('warehouse:initialStockImport:import')")
    @PostMapping("/preview")
    public AjaxResult preview(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        return stkInitialImportService.preview(file, warehouseId);
    }

    /**
     * 确认导入：生成期初处理单（主表+明细），自动生成批次号
     */
    @PreAuthorize("@ss.hasPermi('warehouse:initialStockImport:import')")
    @Log(title = "期初库存导入", businessType = BusinessType.IMPORT)
    @PostMapping("/confirmImport")
    public AjaxResult confirmImport(@RequestBody java.util.Map<String, Object> body) {
        Long warehouseId = body.get("warehouseId") != null ? Long.valueOf(body.get("warehouseId").toString()) : null;
        @SuppressWarnings("unchecked")
        List<InitialImportExcelRow> rows = (List<InitialImportExcelRow>) body.get("rows");
        return stkInitialImportService.confirmImport(warehouseId, rows);
    }

    /**
     * 期初单列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:initialStockImport:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkInitialImport query) {
        startPage();
        List<StkInitialImport> list = stkInitialImportService.list(query);
        return getDataTable(list);
    }

    /**
     * 期初单详情（含明细）
     */
    @PreAuthorize("@ss.hasPermi('warehouse:initialStockImport:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(stkInitialImportService.getDetail(id));
    }

    /**
     * 审核：生成批次、库存、流水（QC）
     */
    @PreAuthorize("@ss.hasPermi('warehouse:initialStockImport:audit')")
    @Log(title = "期初库存导入审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable String id) {
        return toAjax(stkInitialImportService.audit(id));
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception {
        com.spd.common.utils.poi.ExcelUtil<InitialImportExcelRow> util = new com.spd.common.utils.poi.ExcelUtil<>(InitialImportExcelRow.class);
        com.spd.common.utils.file.FileUtils.setAttachmentResponseHeader(response, "期初库存导入模板.xlsx");
        util.importTemplateExcel(response, "期初库存");
    }
}
