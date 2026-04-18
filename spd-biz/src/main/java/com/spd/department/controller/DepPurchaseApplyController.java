package com.spd.department.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.common.core.page.TableDataInfo;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.StringUtils;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.system.mapper.SysUserMapper;

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

import org.springframework.web.bind.annotation.RequestParam;

/**
 * 科室申购Controller
 * 
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/department/purchase")
public class DepPurchaseApplyController extends BaseController
{
    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询科室申购列表
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:list') || @ss.hasPermi('department:purchaseAudit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepPurchaseApply depPurchaseApply)
    {
        depPurchaseApplyService.applyDepartmentScopeToQuery(depPurchaseApply);
        startPage();
        List<DepPurchaseApply> list = depPurchaseApplyService.selectDepPurchaseApplyList(depPurchaseApply);
        return getDataTable(list);
    }

    /**
     * 导出科室申购列表
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:export') || @ss.hasPermi('department:purchaseAudit:export')")
    @Log(title = "科室申购", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DepPurchaseApply depPurchaseApply, @RequestParam(required = false) String exportBillIds)
        throws IOException
    {
        depPurchaseApplyService.applyDepartmentScopeToQuery(depPurchaseApply);
        List<Long> billIds = parseBillIds(exportBillIds);
        List<DepPurchaseApply> bills = new ArrayList<>();

        if (billIds != null && !billIds.isEmpty())
        {
            for (Long id : billIds)
            {
                DepPurchaseApply b = depPurchaseApplyService.selectDepPurchaseApplyById(id);
                if (b != null)
                {
                    bills.add(b);
                }
            }
        }
        else
        {
            // 兼容：未传勾选单据时，按查询条件导出明细
            List<DepPurchaseApply> list = depPurchaseApplyService.selectDepPurchaseApplyList(depPurchaseApply);
            for (DepPurchaseApply b : list)
            {
                if (b != null && b.getId() != null)
                {
                    DepPurchaseApply detail = depPurchaseApplyService.selectDepPurchaseApplyById(b.getId());
                    if (detail != null)
                    {
                        bills.add(detail);
                    }
                }
            }
        }

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("科室申购明细");

        // 将单据头信息并入明细：一个统一的明细列表导出（不再分单据重复表头/标题）
        String[] headers = new String[] {
            "单据号", "申请科室", "制单时间", "制单人", "审核时间", "审核人", "是否审核",
            "耗材编码", "耗材名称", "规格", "型号", "单位", "数量", "单价", "金额",
            "品牌", "供应商", "生产厂家", "申购理由", "备注"
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

        sheet.setColumnWidth(6, 10 * 256); // 是否审核

        // 明细列宽（整体右移一列）
        sheet.setColumnWidth(7, 14 * 256);  // 耗材编码
        sheet.setColumnWidth(8, 20 * 256);  // 耗材名称
        sheet.setColumnWidth(9, 16 * 256);  // 规格
        sheet.setColumnWidth(10, 16 * 256); // 型号
        sheet.setColumnWidth(11, 10 * 256); // 单位
        sheet.setColumnWidth(12, 10 * 256); // 数量
        sheet.setColumnWidth(13, 10 * 256); // 单价
        sheet.setColumnWidth(14, 12 * 256); // 金额
        sheet.setColumnWidth(15, 12 * 256); // 品牌
        sheet.setColumnWidth(16, 16 * 256); // 供应商
        sheet.setColumnWidth(17, 18 * 256); // 生产厂家
        sheet.setColumnWidth(18, 18 * 256); // 申购理由
        sheet.setColumnWidth(19, 20 * 256); // 备注

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
        java.util.Map<String, String> auditUserNameCache = new java.util.HashMap<>();

        for (DepPurchaseApply bill : bills)
        {
            if (bill == null)
            {
                continue;
            }

            String departmentName = bill.getDepartment() != null ? bill.getDepartment().getName() : "";
            String billNo = safeStr(bill.getPurchaseBillNo());
            String creatorName = bill.getUser() != null ? safeStr(bill.getUser().getUserName()) : "";
            String createTimeStr = formatDateTime(bill.getCreateTime());
            String auditTimeStr = formatDateTime(bill.getUpdateTime());
            String isAudited = (bill.getPurchaseBillStatus() != null && bill.getPurchaseBillStatus() == 2) ? "是" : "否";

            // 审核人：auditPurchaseApply 时把审核人写到 update_by/update_time
            String updateByStr = bill.getUpdateBy();
            String auditorName = "";
            if (!StringUtils.isEmpty(updateByStr))
            {
                auditorName = auditUserNameCache.get(updateByStr);
                if (auditorName == null)
                {
                    auditorName = updateByStr;
                    try
                    {
                        Long auditUserId = Long.parseLong(updateByStr.trim());
                        SysUser su = sysUserMapper.selectUserById(auditUserId);
                        if (su != null)
                        {
                            auditorName = !StringUtils.isEmpty(su.getNickName()) ? su.getNickName() : safeStr(su.getUserName());
                        }
                    }
                    catch (Exception ignored)
                    {
                        // 如果update_by不是数字，则直接使用原值
                    }
                    auditUserNameCache.put(updateByStr, auditorName);
                }
            }

            List<com.spd.department.domain.DepPurchaseApplyEntry> entryList = bill.getDepPurchaseApplyEntryList();
            if (entryList == null)
            {
                continue;
            }

            for (com.spd.department.domain.DepPurchaseApplyEntry e : entryList)
            {
                if (e == null)
                {
                    continue;
                }

                hasData = true;
                Row dataRow = sheet.createRow(rowNum++);

                String materialCode = e.getMaterial() != null ? safeStr(e.getMaterial().getCode()) : "";
                String materialName = safeStr(e.getMaterialName());
                String materialSpec = safeStr(e.getMaterialSpec());
                String model = safeStr(e.getModel());
                String unit = safeStr(e.getUnit());

                String brand = safeStr(e.getBrand());
                String supplierName = safeStr(e.getSupplierName());
                String factoryName = (e.getMaterial() != null && e.getMaterial().getFdFactory() != null)
                    ? safeStr(e.getMaterial().getFdFactory().getFactoryName()) : "";

                String reason = safeStr(e.getReason());
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
                colValues.add(materialCode);
                colValues.add(materialName);
                colValues.add(materialSpec);
                colValues.add(model);
                colValues.add(unit);
                colValues.add(formatBigDecimal(e.getQty()));
                colValues.add(formatBigDecimal(e.getUnitPrice()));
                colValues.add(formatBigDecimal(e.getAmt()));
                colValues.add(brand);
                colValues.add(supplierName);
                colValues.add(factoryName);
                colValues.add(reason);
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
        String fn = URLEncoder.encode("科室申购明细_" + System.currentTimeMillis(), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fn + ".xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    /**
     * 获取科室申购详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:query') || @ss.hasPermi('department:purchaseAudit:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(depPurchaseApplyService.selectDepPurchaseApplyById(id));
    }

    /**
     * 新增科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:add')")
    @Log(title = "科室申购", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DepPurchaseApply depPurchaseApply)
    {
        int result = depPurchaseApplyService.insertDepPurchaseApply(depPurchaseApply);
        if (result > 0) {
            // 插入成功后返回depPurchaseApply对象，此时id已被自动填充
            return success(depPurchaseApply);
        }
        return toAjax(result);
    }

    /**
     * 修改科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:edit')")
    @Log(title = "科室申购", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DepPurchaseApply depPurchaseApply)
    {
        return toAjax(depPurchaseApplyService.updateDepPurchaseApply(depPurchaseApply));
    }

    /**
     * 删除科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:remove')")
    @Log(title = "科室申购", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(depPurchaseApplyService.deleteDepPurchaseApplyByIds(ids));
    }

    /**
     * 审核科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:audit') || @ss.hasPermi('department:purchaseAudit:audit')")
    @Log(title = "科室申购审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = depPurchaseApplyService.auditPurchaseApply(json.getString("id"), getUserIdStr());
        return toAjax(result);
    }

    /**
     * 驳回科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:reject') || @ss.hasPermi('department:purchaseAudit:reject')")
    @Log(title = "科室申购驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody JSONObject json)
    {
        int result = depPurchaseApplyService.rejectPurchaseApply(json.getString("id"), json.getString("rejectReason"));
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
