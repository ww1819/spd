package com.spd.his.support;

import org.apache.commons.lang3.StringUtils;
import com.spd.common.exception.ServiceException;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.his.domain.HisChargeItemMirror;
import com.spd.his.mapper.HisChargeItemMirrorMapper;

/**
 * HIS 计费镜像高低值：以收费项目 {@code his_charge_item_mirror.value_level} 手工维护为准。
 * <ul>
 *   <li>1 = 高值</li>
 *   <li>2 = 低值</li>
 *   <li>0 / 空 = 未设置（不进高/低值通道，禁止核销与自动核销）</li>
 * </ul>
 */
public final class HisMirrorValueLevelSupport
{
    /** 未设置 */
    public static final String LEVEL_UNKNOWN = "0";
    /** 高值 */
    public static final String LEVEL_HIGH = "1";
    /** 低值 */
    public static final String LEVEL_LOW = "2";

    public static final String MSG_UNKNOWN =
        "收费项目未维护高低值标识，请先在「HIS收费项目」维护高低值后再核销";

    private HisMirrorValueLevelSupport()
    {
    }

    /**
     * 归一化为落库/查询口径：仅 1/2 保留，其余（含空）→ 0（未设置）。
     */
    public static String normalizeToCanonical(String level)
    {
        String v = StringUtils.trimToEmpty(level);
        if (LEVEL_HIGH.equals(v))
        {
            return LEVEL_HIGH;
        }
        if (LEVEL_LOW.equals(v))
        {
            return LEVEL_LOW;
        }
        return LEVEL_UNKNOWN;
    }

    /**
     * 列表/导出展示：保留未设置，不再把空/0 视同低值。
     */
    public static String normalizeStoredLevel(String level)
    {
        return normalizeToCanonical(level);
    }

    /** 从收费项镜像解析（手工维护为准）。 */
    public static String resolveFromChargeItem(HisChargeItemMirror item)
    {
        if (item == null)
        {
            return LEVEL_UNKNOWN;
        }
        return normalizeToCanonical(item.getValueLevel());
    }

    public static String resolveFromChargeItem(HisChargeItemMirrorMapper mapper, String tenantId, String chargeItemId)
    {
        if (mapper == null || StringUtils.isBlank(tenantId) || StringUtils.isBlank(chargeItemId))
        {
            return LEVEL_UNKNOWN;
        }
        HisChargeItemMirror item = mapper.selectByTenantAndChargeItemId(tenantId, chargeItemId.trim());
        return resolveFromChargeItem(item);
    }

    /**
     * @deprecated 档案 is_gz 仅作可选「同步到收费项」工具；分流请用 {@link #resolveFromChargeItem}。
     */
    @Deprecated
    public static String resolveFromMaterial(FdMaterial mat)
    {
        if (mat == null)
        {
            return LEVEL_UNKNOWN;
        }
        return fromIsGzFlag(mat.getIsGz());
    }

    /**
     * @deprecated 见 {@link #resolveFromMaterial(FdMaterial)}。
     */
    @Deprecated
    public static String resolveFromMaterial(FdMaterialMapper mapper, String tenantId, String chargeItemId)
    {
        if (mapper == null || StringUtils.isBlank(tenantId) || StringUtils.isBlank(chargeItemId))
        {
            return LEVEL_UNKNOWN;
        }
        int highCount = mapper.countHighValueMaterialByChargeItemId(tenantId, chargeItemId.trim());
        return highCount > 0 ? LEVEL_HIGH : LEVEL_LOW;
    }

    /** 档案 is_gz → 收费项建议值（显式同步工具用；未绑档案不默认低值）。 */
    public static String fromIsGzFlag(String isGz)
    {
        return "1".equals(StringUtils.trimToEmpty(isGz)) ? LEVEL_HIGH : LEVEL_LOW;
    }

    public static void assertKnownForConsume(String level)
    {
        if (isUnknown(level))
        {
            throw new ServiceException(MSG_UNKNOWN);
        }
    }

    public static void assertLowValue(String level)
    {
        assertKnownForConsume(level);
        if (isHighValue(level))
        {
            throw new ServiceException("收费项目为高值属性，请至高值扫描核销处理");
        }
    }

    public static void assertHighValue(String level)
    {
        assertKnownForConsume(level);
        if (!isHighValue(level))
        {
            throw new ServiceException("收费项目为低值属性，请至患者收费查询低值核销");
        }
    }

    /** 仅明确标低值；未设置不算低值。 */
    public static boolean isLowValue(String level)
    {
        return LEVEL_LOW.equals(StringUtils.trimToEmpty(level));
    }

    public static boolean isHighValue(String level)
    {
        return LEVEL_HIGH.equals(StringUtils.trimToEmpty(level));
    }

    public static boolean isUnknown(String level)
    {
        return LEVEL_UNKNOWN.equals(normalizeToCanonical(level));
    }
}
