package com.spd.his.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * HIS 计费镜像行金额：明细以单价×数量为准。
 * <p>衡水三院等 HIS 组套收费时，视图 {@code total_amount} 常为组套汇总金额，与单行 {@code unit_price × quantity} 不一致。</p>
 */
public final class HisChargeMirrorAmountSupport
{
    /** 与镜像表 {@code total_amount decimal(18,6)} 一致 */
    private static final int LINE_AMOUNT_SCALE = 6;

    private HisChargeMirrorAmountSupport()
    {
    }

    /**
     * 明细行金额：优先 {@code unitPrice × quantity}；单价或数量缺失时回退 HIS 原值。
     */
    public static BigDecimal resolveDetailLineAmount(BigDecimal quantity, BigDecimal unitPrice, BigDecimal hisTotalAmount)
    {
        if (quantity != null && unitPrice != null)
        {
            return quantity.multiply(unitPrice).setScale(LINE_AMOUNT_SCALE, RoundingMode.HALF_UP);
        }
        return hisTotalAmount;
    }
}
