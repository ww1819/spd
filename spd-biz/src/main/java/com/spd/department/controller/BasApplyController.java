package com.spd.department.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.department.domain.BasApply;
import com.spd.department.service.IBasApplyService;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.StringUtils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 科室申领Controller
 *
 * @author spd
 * @date 2024-02-26
 */
@RestController
@RequestMapping("/department/apply")
public class BasApplyController extends BaseController
{
    @Autowired
    private IBasApplyService basApplyService;

    /**
     * 查询科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasApply basApply)
    {
        basApplyService.applyDepartmentScopeToQuery(basApply);
        startPage();
        List<BasApply> list = basApplyService.selectBasApplyList(basApply);
        return getDataTable(list);
    }

    /**
     * 导出科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:export') || @ss.hasPermi('department:dApplyAudit:export')")
    @Log(title = "科室申领", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasApply basApply, @RequestParam(required = false) String exportBillIds)
        throws IOException
    {
        basApplyService.applyDepartmentScopeToQuery(basApply);
        List<Long> billIds = parseBillIds(exportBillIds);
        List<BasApply> bills = new ArrayList<>();

        if (billIds != null && !billIds.isEmpty())
        {
            for (Long id : billIds)
            {
                BasApply b = basApplyService.selectBasApplyById(id);
                if (b != null)
                {
                    bills.add(b);
                }
            }
        }
        else
        {
            // 兼容：未传勾选单据时，按查询条件导出明细
            List<BasApply> list = basApplyService.selectBasApplyList(basApply);
            for (BasApply b : list)
            {
                if (b != null && b.getId() != null)
                {
                    BasApply detail = basApplyService.selectBasApplyById(b.getId());
                    if (detail != null)
                    {
                        bills.add(detail);
                    }
                }
            }
        }

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("科室申请明细");

        // 将单据头信息并入明细：一个统一的明细列表导出（不再分单据重复表头/标题）
        String[] headers = new String[] {
            "单据号", "申请科室", "制单时间", "制单人", "审核时间", "审核人", "是否审核",
            "名称", "规格", "型号", "单位", "单价", "数量", "金额", "生产厂家", "包装规格",
            "库房分类", "财务分类", "注册证号", "储存方式", "备注"
        };
        int colCount = headers.length;

        CellStyle titleCellStyle = createTitleCellStyle(wb);
        CellStyle headerCellStyle = createHeaderCellStyle(wb);
        CellStyle dataCellStyle = createDataCellStyle(wb);

        // 列宽设置：根据列内容粗略设置（单位：1/256 字符宽度）
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 18 * 256);
        sheet.setColumnWidth(3, 12 * 256);
        sheet.setColumnWidth(4, 18 * 256);
        sheet.setColumnWidth(5, 12 * 256);
        sheet.setColumnWidth(6, 10 * 256);  // 是否审核

        // 明细列宽（整体右移一列）
        sheet.setColumnWidth(7, 18 * 256);  // 名称
        sheet.setColumnWidth(8, 16 * 256);  // 规格
        sheet.setColumnWidth(9, 16 * 256);  // 型号
        sheet.setColumnWidth(10, 10 * 256); // 单位
        sheet.setColumnWidth(11, 10 * 256); // 单价
        sheet.setColumnWidth(12, 10 * 256); // 数量
        sheet.setColumnWidth(13, 12 * 256); // 金额
        sheet.setColumnWidth(14, 18 * 256); // 生产厂家
        sheet.setColumnWidth(15, 14 * 256); // 包装规格
        sheet.setColumnWidth(16, 14 * 256); // 库房分类
        sheet.setColumnWidth(17, 14 * 256); // 财务分类
        sheet.setColumnWidth(18, 12 * 256); // 注册证号
        sheet.setColumnWidth(19, 10 * 256); // 储存方式
        sheet.setColumnWidth(20, 18 * 256); // 备注

        int rowNum = 0;
        // 表头只写一次
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < colCount; i++)
        {
            Cell hc = headerRow.createCell(i);
            hc.setCellValue(headers[i]);
            hc.setCellStyle(headerCellStyle);
        }

        boolean hasData = false;
        for (BasApply bill : bills)
        {
            if (bill == null)
            {
                continue;
            }

            String departmentName = bill.getDepartment() != null ? bill.getDepartment().getName() : "";
            String billNo = safeStr(bill.getApplyBillNo());
            String creatorName = safeStr(bill.getCreaterNmae());
            String auditorName = safeStr(bill.getAuditPersonName());
            String isAudited = (bill.getApplyBillStatus() != null && bill.getApplyBillStatus() == 2) ? "是" : "否";
            String createTimeStr = formatDateTime(bill.getCreateTime());
            String auditTimeStr = formatDateTime(bill.getAuditDate());

            List<com.spd.department.domain.BasApplyEntry> entryList = bill.getBasApplyEntryList();
            if (entryList == null)
            {
                continue;
            }

            for (com.spd.department.domain.BasApplyEntry e : entryList)
            {
                if (e == null)
                {
                    continue;
                }

                hasData = true;
                Row dataRow = sheet.createRow(rowNum++);

                String name = e.getMaterial() != null ? safeStr(e.getMaterial().getName()) : "";
                String speci = e.getMaterial() != null ? safeStr(e.getMaterial().getSpeci()) : "";
                String model = e.getMaterial() != null ? safeStr(e.getMaterial().getModel()) : "";
                String unitName = (e.getMaterial() != null && e.getMaterial().getFdUnit() != null)
                    ? safeStr(e.getMaterial().getFdUnit().getUnitName()) : "";

                String factoryName = (e.getMaterial() != null && e.getMaterial().getFdFactory() != null)
                    ? safeStr(e.getMaterial().getFdFactory().getFactoryName()) : "";
                String packageSpeci = e.getMaterial() != null ? safeStr(e.getMaterial().getPackageSpeci()) : "";
                String warehouseCategoryName =
                    (e.getMaterial() != null && e.getMaterial().getFdWarehouseCategory() != null)
                        ? safeStr(e.getMaterial().getFdWarehouseCategory().getWarehouseCategoryName()) : "";
                String financeCategoryName =
                    (e.getMaterial() != null && e.getMaterial().getFdFinanceCategory() != null)
                        ? safeStr(e.getMaterial().getFdFinanceCategory().getFinanceCategoryName()) : "";
                String registerNo = e.getMaterial() != null ? safeStr(e.getMaterial().getRegisterNo()) : "";
                String isWay = e.getMaterial() != null ? safeStr(String.valueOf(e.getMaterial().getIsWay())) : "";
                String remark = safeStr(e.getRemark());

                List<String> colValues = new ArrayList<>();
                // 头信息
                colValues.add(billNo);
                colValues.add(departmentName);
                colValues.add(createTimeStr);
                colValues.add(creatorName);
                colValues.add(auditTimeStr);
                colValues.add(auditorName);
                colValues.add(isAudited);
                // 明细信息
                colValues.add(name);
                colValues.add(speci);
                colValues.add(model);
                colValues.add(unitName);
                colValues.add(formatBigDecimal(e.getUnitPrice()));
                colValues.add(formatBigDecimal(e.getQty()));
                colValues.add(formatBigDecimal(e.getAmt()));
                colValues.add(factoryName);
                colValues.add(packageSpeci);
                colValues.add(warehouseCategoryName);
                colValues.add(financeCategoryName);
                colValues.add(registerNo);
                colValues.add(isWay);
                colValues.add(remark);

                int maxLines = 1;
                for (int i = 0; i < colCount; i++)
                {
                    String v = colValues.size() > i ? colValues.get(i) : "";
                    Cell cell = dataRow.createCell(i);
                    cell.setCellValue(v);
                    cell.setCellStyle(dataCellStyle);
                    int lineCount = estimateLinesForCell(sheet, i, v);
                    if (lineCount > maxLines)
                    {
                        maxLines = lineCount;
                    }
                }
                dataRow.setHeightInPoints(Math.min(409f, Math.max(18f, 15f * maxLines + 4f)));
            }
        }

        if (!hasData)
        {
            Row r = sheet.createRow(rowNum);
            Cell c = r.createCell(0);
            c.setCellValue("暂无可导出的数据");
            c.setCellStyle(titleCellStyle);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fn = URLEncoder.encode("科室申请明细_" + System.currentTimeMillis(), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fn + ".xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    /**
     * 获取科室申领详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(basApplyService.selectBasApplyById(id));
    }

    /**
     * 新增科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:add')")
    @Log(title = "科室申领", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BasApply basApply)
    {
        int result = basApplyService.insertBasApply(basApply);
        if (result > 0) {
            // 插入成功后返回basApply对象，此时id已被自动填充
            return success(basApply);
        }
        return toAjax(result);
    }

    /**
     * 修改科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:edit')")
    @Log(title = "科室申领", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BasApply basApply)
    {
        return toAjax(basApplyService.updateBasApply(basApply));
    }

    /**
     * 删除科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:remove')")
    @Log(title = "科室申领", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(basApplyService.deleteBasApplyByIds(ids));
    }


    /**
     * 审核科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:audit')")
    @Log(title = "科室申领审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = basApplyService.auditApply(json.getString("id"), getUserIdStr());
        return toAjax(result);
    }

    /**
     * 驳回科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:reject')")
    @Log(title = "科室申领驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody JSONObject json)
    {
        int result = basApplyService.rejectApply(json.getString("id"), json.getString("rejectReason"));
        return toAjax(result);
    }

    private static List<Long> parseBillIds(String exportBillIds)
    {
        List<Long> ids = new ArrayList<>();
        if (StringUtils.isEmpty(exportBillIds))
        {
            return ids;
        }
        String[] parts = exportBillIds.split(",");
        for (String p : parts)
        {
            if (StringUtils.isEmpty(p))
            {
                continue;
            }
            try
            {
                ids.add(Long.parseLong(p.trim()));
            }
            catch (Exception ignored)
            {
            }
        }
        return ids;
    }

    private static String safeStr(Object v)
    {
        return v == null ? "" : String.valueOf(v);
    }

    private static String formatDateTime(Object v)
    {
        if (v == null)
        {
            return "";
        }
        if (v instanceof java.util.Date)
        {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) v);
        }
        return String.valueOf(v);
    }

    private static String formatBigDecimal(Object v)
    {
        if (v == null)
        {
            return "";
        }
        try
        {
            return new java.text.DecimalFormat("0.##########").format(v);
        }
        catch (Exception e)
        {
            return String.valueOf(v);
        }
    }

    private static int estimateLinesForCell(Sheet sheet, int colIndex, String text)
    {
        if (text == null)
        {
            return 1;
        }
        String s = text.trim();
        if (s.isEmpty())
        {
            return 1;
        }
        int units = textDisplayUnits(s);
        int colWidthPx = sheet.getColumnWidth(colIndex) / 256;
        double lineCap = Math.max(6d, colWidthPx * 1.2d);
        return (int) Math.max(1, Math.ceil(units / lineCap));
    }

    private static int textDisplayUnits(String s)
    {
        int units = 0;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            units += c > 127 ? 2 : 1;
        }
        return units;
    }

    private static CellStyle createTitleCellStyle(Workbook wb)
    {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createHeaderCellStyle(Workbook wb)
    {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createDataCellStyle(Workbook wb)
    {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
