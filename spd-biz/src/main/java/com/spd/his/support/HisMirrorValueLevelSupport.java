package com.spd.his.support;

import org.apache.commons.lang3.StringUtils;
import com.spd.common.exception.ServiceException;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;

/**
 * HIS 计费镜像行高低值解析：以耗材档案 fd_material.is_gz 为准（通过 his_charge_item_id 对照）。
 */
public final class HisMirrorValueLevelSupport
{
    /** 未识别：无对照耗材或未维护 is_gz */
    public static final String LEVEL_UNKNOWN = "0";
    /** 高值 */
    public static final String LEVEL_HIGH = "1";
    /** 低值 */
    public static final String LEVEL_LOW = "2";

    public static final String MSG_UNKNOWN =
        "收费项目未维护高低值标识，请先在耗材档案维护是否高值后再核销";

    private HisMirrorValueLevelSupport()
    {
    }

    /**
     * 按耗材档案 is_gz 解析：1→高值，2→低值，否则未识别。
     */
    public static String resolveFromMaterial(FdMaterial mat)
    {
        if (mat == null)
        {
            return LEVEL_UNKNOWN;
        }
        String isGz = StringUtils.trimToEmpty(mat.getIsGz());
        if ("1".equals(isGz))
        {
            return LEVEL_HIGH;
        }
        if ("2".equals(isGz))
        {
            return LEVEL_LOW;
        }
        return LEVEL_UNKNOWN;
    }

    public static String resolveFromMaterial(FdMaterialMapper mapper, String tenantId, String chargeItemId)
    {
        if (mapper == null || StringUtils.isBlank(tenantId) || StringUtils.isBlank(chargeItemId))
        {
            return LEVEL_UNKNOWN;
        }
        FdMaterial mat = mapper.selectFdMaterialByTenantAndHisChargeItemId(tenantId, chargeItemId.trim());
        return resolveFromMaterial(mat);
    }

    public static void assertKnownForConsume(String level)
    {
        if (LEVEL_UNKNOWN.equals(StringUtils.trimToEmpty(level)))
        {
            throw new ServiceException(MSG_UNKNOWN);
        }
    }

    public static void assertLowValue(String level)
    {
        assertKnownForConsume(level);
        if (LEVEL_HIGH.equals(level))
        {
            throw new ServiceException("收费项目为高值属性，请至高值扫描核销处理");
        }
    }

    public static void assertHighValue(String level)
    {
        assertKnownForConsume(level);
        if (LEVEL_LOW.equals(level))
        {
            throw new ServiceException("收费项目为低值属性，请至患者收费查询低值核销");
        }
    }

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
        return LEVEL_UNKNOWN.equals(StringUtils.trimToEmpty(level))
            || StringUtils.isBlank(level);
    }
}
