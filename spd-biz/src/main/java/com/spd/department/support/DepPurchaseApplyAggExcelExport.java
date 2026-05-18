package com.spd.department.support;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.StringUtils;
import com.spd.department.domain.DepPurchaseApplyAgg;
import com.spd.department.domain.DepPurchaseApplyAggEntry;
import com.spd.system.mapper.SysUserMapper;

/**
 * 科室汇总申购明细导出
 */
public final class DepPurchaseApplyAggExcelExport {

    private DepPurchaseApplyAggExcelExport() {
    }

    public static void export(HttpServletResponse response, List<DepPurchaseApplyAgg> bills, SysUserMapper sysUserMapper)
        throws IOException
    {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("汇总申购明细");
        String[] headers = new String[] {
            "单据号", "申请科室", "制单时间", "制单人", "审核时间", "审核人", "是否审核", "拆分状态",
            "仓库", "耗材编码", "耗材名称", "规格", "型号", "单位", "数量", "单价", "金额",
            "品牌", "供应商", "申购理由", "备注"
        };
        int colCount = headers.length;
        CellStyle headerStyle = createHeaderCellStyle(wb);
        CellStyle dataStyle = createDataCellStyle(wb);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < colCount; i++) {
            Cell hc = headerRow.createCell(i);
            hc.setCellValue(headers[i]);
            hc.setCellStyle(headerStyle);
        }
        int rowNum = 1;
        boolean hasData = false;
        Map<String, String> auditUserNameCache = new HashMap<>();
        for (DepPurchaseApplyAgg bill : bills) {
            if (bill == null) {
                continue;
            }
            String departmentName = bill.getDepartment() != null ? bill.getDepartment().getName() : "";
            String billNo = safeStr(bill.getPurchaseBillNo());
            String creatorName = bill.getUser() != null ? safeStr(bill.getUser().getUserName()) : "";
            String createTimeStr = formatDateTime(bill.getCreateTime());
            String auditTimeStr = formatDateTime(bill.getUpdateTime());
            String isAudited = (bill.getPurchaseBillStatus() != null && bill.getPurchaseBillStatus() == 2) ? "是" : "否";
            String splitText = (bill.getSplitStatus() != null && bill.getSplitStatus() == 1) ? "已拆分" : "未拆分";
            String auditorName = resolveAuditorName(bill.getUpdateBy(), auditUserNameCache, sysUserMapper);
            List<DepPurchaseApplyAggEntry> entryList = bill.getEntryList();
            if (entryList == null) {
                continue;
            }
            for (DepPurchaseApplyAggEntry e : entryList) {
                if (e == null || e.getMaterialId() == null) {
                    continue;
                }
                hasData = true;
                Row dataRow = sheet.createRow(rowNum++);
                String[] vals = new String[] {
                    billNo, departmentName, createTimeStr, creatorName, auditTimeStr, auditorName, isAudited, splitText,
                    safeStr(e.getWarehouseName()),
                    e.getMaterial() != null ? safeStr(e.getMaterial().getCode()) : "",
                    safeStr(e.getMaterialName()), safeStr(e.getMaterialSpec()), safeStr(e.getModel()), safeStr(e.getUnit()),
                    formatDecimal(e.getQty()), formatDecimal(e.getUnitPrice()), formatDecimal(e.getAmt()),
                    safeStr(e.getBrand()), safeStr(e.getSupplierName()), safeStr(e.getReason()), safeStr(e.getRemark())
                };
                for (int i = 0; i < colCount; i++) {
                    Cell cell = dataRow.createCell(i);
                    cell.setCellValue(i < vals.length ? vals[i] : "");
                    cell.setCellStyle(dataStyle);
                }
            }
        }
        if (!hasData) {
            Row r = sheet.createRow(rowNum);
            r.createCell(0).setCellValue("暂无可导出的数据");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fn = URLEncoder.encode("科室汇总申购明细_" + System.currentTimeMillis(), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fn + ".xlsx");
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static List<String> parseBillIds(String exportBillIds) {
        List<String> ids = new ArrayList<>();
        if (StringUtils.isEmpty(exportBillIds)) {
            return ids;
        }
        for (String p : exportBillIds.split(",")) {
            if (!StringUtils.isEmpty(p)) {
                ids.add(p.trim());
            }
        }
        return ids;
    }

    private static String resolveAuditorName(String updateBy, Map<String, String> cache, SysUserMapper sysUserMapper) {
        if (StringUtils.isEmpty(updateBy)) {
            return "";
        }
        String cached = cache.get(updateBy);
        if (cached != null) {
            return cached;
        }
        String name = updateBy;
        try {
            Long uid = Long.parseLong(updateBy.trim());
            SysUser su = sysUserMapper.selectUserById(uid);
            if (su != null) {
                name = !StringUtils.isEmpty(su.getNickName()) ? su.getNickName() : safeStr(su.getUserName());
            }
        } catch (Exception ignored) {
        }
        cache.put(updateBy, name);
        return name;
    }

    private static String safeStr(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private static String formatDateTime(Object v) {
        if (v == null) {
            return "";
        }
        if (v instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) v);
        }
        return String.valueOf(v);
    }

    private static String formatDecimal(Object v) {
        if (v == null) {
            return "";
        }
        try {
            return new DecimalFormat("0.##########").format(v);
        } catch (Exception e) {
            return String.valueOf(v);
        }
    }

    private static CellStyle createHeaderCellStyle(Workbook wb) {
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

    private static CellStyle createDataCellStyle(Workbook wb) {
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
