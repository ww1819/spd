package com.spd.his.support;

import org.apache.commons.lang3.StringUtils;

/**
 * HIS 计费核销写入「处理情况」的用户可读文案（避免镜像行、charge_item_id 等技术术语）。
 */
public final class HisMirrorProcessUserMessages
{
    private HisMirrorProcessUserMessages()
    {
    }

    /** 收费项目展示名：优先项目名称，其次编码 */
    public static String chargeItemLabel(String chargeItemId, String itemName)
    {
        if (StringUtils.isNotBlank(itemName))
        {
            return itemName.trim();
        }
        if (StringUtils.isNotBlank(chargeItemId))
        {
            return chargeItemId.trim();
        }
        return "未知收费项目";
    }

    public static String notMapped(String chargeItemId, String itemName)
    {
        return "收费项目【" + chargeItemLabel(chargeItemId, itemName) + "】未对照耗材档案";
    }

    public static String missingChargeItem()
    {
        return "缺少收费项目信息，未对照";
    }

    public static String deptNotMapped()
    {
        return "计费科室未对照";
    }

    public static String stockInsufficient(String productName)
    {
        String name = StringUtils.isNotBlank(productName) ? productName.trim() : "耗材";
        return "【" + name + "】库存不足";
    }

    public static String noUnitPrice(String chargeItemId, String itemName)
    {
        return "收费项目【" + chargeItemLabel(chargeItemId, itemName) + "】缺少单价，无法核销";
    }

    public static String valueLevelUnknown(String chargeItemId, String itemName)
    {
        return "收费项目【" + chargeItemLabel(chargeItemId, itemName) + "】未维护高低值标识";
    }

    public static String valueLevelHigh(String chargeItemId, String itemName)
    {
        return "收费项目【" + chargeItemLabel(chargeItemId, itemName) + "】为高值，请至高值扫描核销";
    }

    public static String valueLevelLow(String chargeItemId, String itemName)
    {
        return "收费项目【" + chargeItemLabel(chargeItemId, itemName) + "】为低值，请至患者收费查询低值核销";
    }

    public static String lowPendingOnly(String processStatusCode)
    {
        return "当前状态为「" + processStatusText(processStatusCode) + "」，无法低值核销";
    }

    public static String highScanNotAllowed(String processStatusCode)
    {
        return "当前状态为「" + processStatusText(processStatusCode) + "」，无法继续高值扫码";
    }

    public static String fullyConsumed()
    {
        return "计费数量已全部核销";
    }

    public static String zeroQuantity()
    {
        return "计费数量为 0，无需核销";
    }

    public static String scanCodeNotFound(String productName)
    {
        return "未找到该院内码对应的【" + (StringUtils.isNotBlank(productName) ? productName.trim() : "耗材") + "】库存";
    }

    public static String priceMismatch(java.math.BigDecimal chargeUnitPrice, java.math.BigDecimal stockUnitPrice)
    {
        return String.format("扫码耗材单价与计费单价不一致（计费单价：%s，库存单价：%s）",
            formatPrice(chargeUnitPrice), formatPrice(stockUnitPrice));
    }

    private static String formatPrice(java.math.BigDecimal price)
    {
        if (price == null)
        {
            return "无";
        }
        return price.setScale(4, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public static String materialMismatch()
    {
        return "扫码耗材与收费项目不一致";
    }

    public static String wrongDeptScan()
    {
        return "院内码不属于当前计费科室";
    }

    public static String qtyExceedRemaining()
    {
        return "本次消耗数量超过剩余计费数量";
    }

    public static String legacyBatchLow()
    {
        return "该记录已按历史方式处理过，无法低值核销";
    }

    public static String alreadyHighPath()
    {
        return "已走高值核销，请在高值扫描核销中继续完成";
    }

    public static String alreadyLowConsumed()
    {
        return "该记录已有低值消耗，无法重复核销";
    }

    public static String legacyBatchHigh()
    {
        return "该记录已按历史方式处理过，无法高值扫码";
    }

    public static String alreadyLowCannotHigh()
    {
        return "该记录已低值核销，无法再高值扫码";
    }

    public static String chargeNotFound(boolean inpatient)
    {
        return inpatient ? "未找到该住院计费记录" : "未找到该门诊计费记录";
    }

    public static String processStatusText(String code)
    {
        if (StringUtils.isBlank(code))
        {
            return "未知";
        }
        switch (code.trim())
        {
            case "PENDING_CONSUME":
                return "待处理";
            case "PARTIALLY_CONSUMED":
                return "部分消耗";
            case "CONSUMED":
                return "已消耗";
            case "REFUNDED":
                return "已退费";
            case "REFUND_RETURNED":
                return "退费已返还";
            default:
                return code.trim();
        }
    }

    public static String highApplyFailed()
    {
        return "核销未完成，请重试或联系信息科";
    }

    public static String lowApplyFailed()
    {
        return "核销未完成，请重试或联系信息科";
    }

    /** 非业务异常写入处理情况时使用通用文案，避免暴露技术细节 */
    public static String safeFailureMessage(Throwable e, String defaultMessage)
    {
        if (e instanceof com.spd.common.exception.ServiceException)
        {
            return StringUtils.defaultIfBlank(e.getMessage(), defaultMessage);
        }
        return defaultMessage;
    }
}
