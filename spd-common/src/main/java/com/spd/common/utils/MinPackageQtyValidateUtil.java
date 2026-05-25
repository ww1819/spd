package com.spd.common.utils;

import java.math.BigDecimal;

/**
 * 最小包装数倍数校验：仅当产品档案维护了有效最小包装数时，数量须为其整数倍。
 */
public final class MinPackageQtyValidateUtil {

    private MinPackageQtyValidateUtil() {
    }

    /** 是否已维护有效最小包装数（&gt;0） */
    public static boolean isEffectiveMinPackageQty(BigDecimal minPackageQty) {
        return minPackageQty != null && minPackageQty.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 数量是否为最小包装数的整数倍；未维护最小包装数时不校验（视为通过）。
     */
    public static boolean isQtyMultipleOfMinPackage(BigDecimal qty, BigDecimal minPackageQty) {
        if (!isEffectiveMinPackageQty(minPackageQty)) {
            return true;
        }
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return qty.remainder(minPackageQty).stripTrailingZeros().compareTo(BigDecimal.ZERO) == 0;
    }

    public static String buildMismatchMessage(String docLabel, String materialName, BigDecimal qty, BigDecimal minPackageQty) {
        String prefix = (docLabel == null || docLabel.isEmpty()) ? "" : docLabel;
        String name = (materialName == null || materialName.trim().isEmpty()) ? "耗材" : materialName.trim();
        return prefix + "明细【" + name + "】数量 " + qty.stripTrailingZeros().toPlainString()
            + " 须为最小包装数 " + minPackageQty.stripTrailingZeros().toPlainString()
            + " 的整数倍，请修改后保存。";
    }
}
