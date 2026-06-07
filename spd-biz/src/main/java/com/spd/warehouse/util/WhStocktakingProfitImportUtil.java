package com.spd.warehouse.util;

import java.util.Date;

import com.spd.common.utils.StringUtils;
import com.spd.warehouse.domain.dto.WhStocktakingProfitImportRow;

/**
 * 仓库盘点盘盈明细导入：文本归一化（去 Excel 前导单引号等）
 */
public final class WhStocktakingProfitImportUtil {

    private WhStocktakingProfitImportUtil() {
    }

    public static void normalizeRow(WhStocktakingProfitImportRow row) {
        if (row == null) {
            return;
        }
        row.setBatchNumber(stripExcelTextPrefix(row.getBatchNumber()));
        row.setThirdPartyBatchNo(stripExcelTextPrefix(row.getThirdPartyBatchNo()));
        row.setHisId(stripExcelTextPrefix(row.getHisId()));
        if (StringUtils.isNotEmpty(row.getEndDateRaw())) {
            row.setEndDateRaw(row.getEndDateRaw().trim());
        }
        if (StringUtils.isNotEmpty(row.getBeginDateRaw())) {
            row.setBeginDateRaw(row.getBeginDateRaw().trim());
        }
    }

    public static String stripExcelTextPrefix(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        while (s.startsWith("'")) {
            s = s.substring(1).trim();
        }
        return s.isEmpty() ? null : s;
    }

    public static Date parseEndDate(WhStocktakingProfitImportRow row) {
        return row == null ? null : InitialImportDateParser.parseToSqlDate(row.getEndDateRaw());
    }

    public static Date parseBeginDate(WhStocktakingProfitImportRow row) {
        return row == null ? null : InitialImportDateParser.parseToSqlDate(row.getBeginDateRaw());
    }
}
