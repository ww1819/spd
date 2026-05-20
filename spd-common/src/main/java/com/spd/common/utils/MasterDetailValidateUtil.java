package com.spd.common.utils;

import com.spd.common.exception.ServiceException;

import java.util.List;
import java.util.function.Function;

/**
 * 主从单据保存：至少一条有效明细的通用校验。
 */
public final class MasterDetailValidateUtil {

    private static final String DEFAULT_MSG = "单据明细不能为空，请至少添加一条明细";

    private MasterDetailValidateUtil() {
    }

    public static void assertEntryListNotEmpty(List<?> entryList) {
        assertEntryListNotEmpty(entryList, null);
    }

    public static void assertEntryListNotEmpty(List<?> entryList, String docLabel) {
        if (entryList == null || entryList.isEmpty()) {
            throw new ServiceException(message(docLabel));
        }
    }

    /**
     * 至少一条明细且 materialId（或同类主键）非空；适用于保存时跳过 materialId 为空的占位行。
     */
    public static <T> void assertHasMaterialLine(List<T> entryList, Function<T, ?> materialIdGetter) {
        assertHasMaterialLine(entryList, materialIdGetter, null);
    }

    public static <T> void assertHasMaterialLine(List<T> entryList, Function<T, ?> materialIdGetter, String docLabel) {
        assertEntryListNotEmpty(entryList, docLabel);
        boolean has = false;
        for (T e : entryList) {
            if (e != null && materialIdGetter.apply(e) != null) {
                has = true;
                break;
            }
        }
        if (!has) {
            throw new ServiceException(message(docLabel));
        }
    }

    private static String message(String docLabel) {
        if (docLabel == null || docLabel.isEmpty()) {
            return DEFAULT_MSG;
        }
        return docLabel + "明细不能为空，请至少添加一条明细";
    }
}
