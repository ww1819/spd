package com.spd.common.utils;

import com.spd.common.enums.TenantEnum;
import com.spd.common.exception.ServiceException;

/**
 * 枣强县中医院（zaoqiang-tcm-001）基础档案手工新增限制：仅允许从 HIS 同步。
 */
public final class ZqTcmMasterDataGuard
{
    public static final String MANUAL_CREATE_DENIED_MSG = "枣强县中医院不允许手工新增，请从HIS系统同步";

    private ZqTcmMasterDataGuard()
    {
    }

    public static boolean isZqTcmTenant(String tenantId)
    {
        return TenantEnum.ZQ_TCM == TenantEnum.fromCustomerId(StringUtils.trimToEmpty(tenantId));
    }

    public static boolean isCurrentZqTcmTenant()
    {
        return isZqTcmTenant(SecurityUtils.getCustomerId());
    }

    public static void assertManualCreateAllowed()
    {
        if (isCurrentZqTcmTenant())
        {
            throw new ServiceException(MANUAL_CREATE_DENIED_MSG);
        }
    }
}
