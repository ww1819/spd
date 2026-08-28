package com.spd.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 租户金额显示格式化（不改变入库精度；仅展示/打印/导出）。
 */
public final class MoneyScaleUtils {

  public static final int DEFAULT_SCALE = 3;
  public static final int STORAGE_SCALE = 6;
  public static final int MIN_SCALE = 0;
  public static final int MAX_SCALE = 6;
  public static final String DEFAULT_ROUND_MODE = "HALF_UP";

  private MoneyScaleUtils() {
  }

  public static int normalizeScale(Integer scale) {
    if (scale == null) {
      return DEFAULT_SCALE;
    }
    if (scale < MIN_SCALE) {
      return MIN_SCALE;
    }
    if (scale > MAX_SCALE) {
      return MAX_SCALE;
    }
    return scale;
  }

  public static RoundingMode resolveRoundingMode(String mode) {
    if (mode == null || mode.trim().isEmpty()) {
      return RoundingMode.HALF_UP;
    }
    String m = mode.trim().toUpperCase();
    if ("HALF_EVEN".equals(m) || "HALF_EVEN_BANKERS".equals(m)) {
      return RoundingMode.HALF_EVEN;
    }
    if ("DOWN".equals(m) || "FLOOR".equals(m)) {
      return RoundingMode.DOWN;
    }
    return RoundingMode.HALF_UP;
  }

  public static BigDecimal format(BigDecimal value, Integer scale, String roundMode) {
    if (value == null) {
      return null;
    }
    return value.setScale(normalizeScale(scale), resolveRoundingMode(roundMode));
  }

  /** 入库精度（与 decimal(18,6) 对齐），不按展示位截断 */
  public static BigDecimal toStorage(BigDecimal value) {
    if (value == null) {
      return null;
    }
    return value.setScale(STORAGE_SCALE, RoundingMode.HALF_UP);
  }

  /**
   * 展示用：按租户小数位舍入后去掉末尾 0（0.020 → 0.02；100 保持 100 而非 1E+2）。
   */
  public static BigDecimal toDisplay(BigDecimal value, Integer scale, String roundMode) {
    BigDecimal formatted = format(value, scale, roundMode);
    if (formatted == null) {
      return null;
    }
    return stripTrailingZerosKeepInt(formatted);
  }

  /**
   * 仅去掉末尾 0，不按展示位再舍入（入库 6 位回传表单时避免把 0.123456 截成 0.123）。
   * 100 保持 100 而非 1E+2。
   */
  public static BigDecimal stripTrailingZerosKeepInt(BigDecimal value) {
    if (value == null) {
      return null;
    }
    BigDecimal stripped = value.stripTrailingZeros();
    if (stripped.scale() < 0) {
      return stripped.setScale(0);
    }
    return stripped;
  }

  public static String toPlainStripZeros(BigDecimal value) {
    BigDecimal stripped = stripTrailingZerosKeepInt(value);
    return stripped == null ? null : stripped.toPlainString();
  }

  public static BigDecimal sumThenFormat(Iterable<? extends Number> values, Integer scale, String roundMode) {
    BigDecimal sum = BigDecimal.ZERO;
    if (values != null) {
      for (Number n : values) {
        if (n == null) {
          continue;
        }
        if (n instanceof BigDecimal) {
          sum = sum.add((BigDecimal) n);
        } else {
          sum = sum.add(BigDecimal.valueOf(n.doubleValue()));
        }
      }
    }
    return format(sum, scale, roundMode);
  }
}
