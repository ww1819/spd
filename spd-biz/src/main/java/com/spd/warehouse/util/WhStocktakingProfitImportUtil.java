package com.spd.warehouse.util;

import java.time.LocalDate;
import java.util.Date;

import com.spd.common.utils.StringUtils;
import com.spd.warehouse.domain.dto.WhStocktakingProfitImportRow;

/**
 * 仓库盘点盘盈明细导入：文本归一化（去 Excel 前导单引号等）
 */
public final class WhStocktakingProfitImportUtil {

    /** 有效期为空时的默认值 */
    public static final String DEFAULT_END_DATE_RAW = "2099-01-01";

    private static final Date DEFAULT_END_DATE = java.sql.Date.valueOf(LocalDate.of(2099, 1, 1));

    private WhStocktakingProfitImportUtil() {
    }

    public static void normalizeRow(WhStocktakingProfitImportRow row) {
        if (row == null) {
            return;
        }
        row.setBatchNumber(stripExcelTextPrefix(row.getBatchNumber()));
        row.setThirdPartyBatchNo(stripExcelTextPrefix(row.getThirdPartyBatchNo()));
        row.setHisId(stripExcelTextPrefix(row.getHisId()));
        if (StringUtils.isEmpty(StringUtils.trim(row.getEndDateRaw()))) {
            row.setEndDateRaw(DEFAULT_END_DATE_RAW);
        } else {
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
        if (row == null) {
            return null;
        }
        if (StringUtils.isEmpty(StringUtils.trim(row.getEndDateRaw()))) {
            return DEFAULT_END_DATE;
        }
        return InitialImportDateParser.parseToSqlDate(row.getEndDateRaw());
    }

    public static Date parseBeginDate(WhStocktakingProfitImportRow row) {
        return row == null ? null : InitialImportDateParser.parseToSqlDate(row.getBeginDateRaw());
    }
}
