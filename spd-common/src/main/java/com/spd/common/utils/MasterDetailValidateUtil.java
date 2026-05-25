package com.spd.common.utils;

import com.spd.common.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    /**
     * 已选耗材且数量为空或≤0 时，默认置为 1（保存/草稿入库前规范化，避免空数量落库）。
     */
    public static <T> void normalizeMaterialLineQtyDefaultOne(List<T> entryList, Function<T, ?> materialIdGetter,
        Function<T, BigDecimal> qtyGetter, BiConsumer<T, BigDecimal> qtySetter) {
        if (entryList == null) {
            return;
        }
        for (T e : entryList) {
            if (e == null || materialIdGetter.apply(e) == null) {
                continue;
            }
            BigDecimal qty = qtyGetter.apply(e);
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                qtySetter.accept(e, BigDecimal.ONE);
            }
        }
    }

    /**
     * 已选耗材的明细行数量必填且大于 0（与保存时跳过 materialId 为空的占位行一致）。
     */
    public static <T> void assertMaterialLinesHavePositiveQty(List<T> entryList, Function<T, ?> materialIdGetter,
        Function<T, BigDecimal> qtyGetter) {
        assertMaterialLinesHavePositiveQty(entryList, materialIdGetter, qtyGetter, null);
    }

    public static <T> void assertMaterialLinesHavePositiveQty(List<T> entryList, Function<T, ?> materialIdGetter,
        Function<T, BigDecimal> qtyGetter, String docLabel) {
        if (entryList == null) {
            return;
        }
        String prefix = (docLabel == null || docLabel.isEmpty()) ? "" : docLabel;
        for (T e : entryList) {
            if (e == null || materialIdGetter.apply(e) == null) {
                continue;
            }
            BigDecimal qty = qtyGetter.apply(e);
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException(prefix + "明细中数量不能为空且必须大于0，请检查后保存。");
            }
        }
    }

    /** 明细未逻辑删除：del_flag 为空或不为 1 */
    public static boolean isNotDeletedFlag(Integer delFlag) {
        return delFlag == null || delFlag.intValue() != 1;
    }

    /** 明细未逻辑删除：del_flag 为空、'0' 或其它非删除标记 */
    public static boolean isNotDeletedFlagStr(String delFlag) {
        if (delFlag == null || delFlag.trim().isEmpty()) {
            return true;
        }
        return !"1".equals(delFlag.trim());
    }

    /**
     * 审核前：至少一条未删除的有效明细（isActive 为 null 时仅要求列表非空）。
     */
    public static <T> void assertHasActiveEntryForAudit(List<T> entryList, Predicate<T> isActive, String docLabel) {
        String label = auditDocLabel(docLabel);
        if (entryList == null || entryList.isEmpty()) {
            throw new ServiceException(label + "无有效明细，不允许审核");
        }
        if (isActive == null) {
            return;
        }
        boolean has = false;
        for (T e : entryList) {
            if (e != null && isActive.test(e)) {
                has = true;
                break;
            }
        }
        if (!has) {
            throw new ServiceException(label + "无有效明细（明细均已删除），不允许审核");
        }
    }

    /**
     * 审核前严格校验：每条明细产品档案(materialId)与数量均必填且数量&gt;0；不自动补默认值。
     */
    public static <T> void assertEntriesReadyForAudit(List<T> entryList, Function<T, ?> materialIdGetter,
        Function<T, BigDecimal> qtyGetter, Function<T, String> lineDescGetter, String docLabel) {
        assertEntryListNotEmpty(entryList, docLabel);
        int idx = 0;
        for (T e : entryList) {
            idx++;
            if (e == null) {
                throw new ServiceException(auditLineDesc(null, lineDescGetter, idx, docLabel)
                    + "数据异常，不允许审核");
            }
            if (materialIdGetter.apply(e) == null) {
                throw new ServiceException(auditLineDesc(e, lineDescGetter, idx, docLabel)
                    + "产品档案不能为空，不允许审核");
            }
            BigDecimal qty = qtyGetter.apply(e);
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException(auditLineDesc(e, lineDescGetter, idx, docLabel)
                    + "申购数量不能为空且必须大于0，不允许审核");
            }
        }
    }

    private static <T> String auditLineDesc(T e, Function<T, String> lineDescGetter, int idx, String docLabel) {
        if (lineDescGetter != null && e != null) {
            String s = lineDescGetter.apply(e);
            if (s != null && !s.isEmpty()) {
                return "明细【" + s + "】";
            }
        }
        String prefix = (docLabel == null || docLabel.isEmpty()) ? "" : docLabel;
        return prefix + "第" + idx + "行明细";
    }

    private static String auditDocLabel(String docLabel) {
        if (docLabel == null || docLabel.isEmpty()) {
            return "单据";
        }
        return docLabel;
    }

    private static String message(String docLabel) {
        if (docLabel == null || docLabel.isEmpty()) {
            return DEFAULT_MSG;
        }
        return docLabel + "明细不能为空，请至少添加一条明细";
    }
}
