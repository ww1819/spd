package com.spd.his.support;

import org.apache.commons.lang3.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.his.constant.HisMirrorProcessConstants;
import com.spd.his.constants.HisBillingTenantConstants;

/**
 * HIS 计费抓取后自动核销/自动退费的操作人（制单人、审核人、处理人）。
 * 衡水三院默认使用 sys_user.user_id=3105（系统自动核销），可通过租户配置覆盖。
 */
public final class HisAutoWriteOffOperatorSupport
{
    /** 衡水三院默认系统自动核销账号（sys_user.user_id） */
    public static final long DEFAULT_HENGSHUI_OPERATOR_USER_ID = 3105L;

    private HisAutoWriteOffOperatorSupport()
    {
    }

    public static boolean isAutoParty(String processParty)
    {
        return HisMirrorProcessConstants.PARTY_AUTO.equals(processParty);
    }

    public static long resolveOperatorUserId(String tenantId, ISbTenantSettingService settings)
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tenantId))
        {
            Long uid = SecurityUtils.getUserId();
            return uid != null ? uid : 0L;
        }
        String fallback = String.valueOf(DEFAULT_HENGSHUI_OPERATOR_USER_ID);
        String raw = settings == null
            ? fallback
            : settings.getSettingValue(tenantId,
                HisBillingTenantConstants.SETTING_INTERNAL_OPERATOR_USER_ID, fallback);
        try
        {
            long id = Long.parseLong(StringUtils.trimToEmpty(raw));
            return id > 0 ? id : DEFAULT_HENGSHUI_OPERATOR_USER_ID;
        }
        catch (Exception e)
        {
            return DEFAULT_HENGSHUI_OPERATOR_USER_ID;
        }
    }

    public static String resolveOperatorUserIdStr(String tenantId, ISbTenantSettingService settings)
    {
        return String.valueOf(resolveOperatorUserId(tenantId, settings));
    }

    public static String resolveCreateBy(String tenantId, String processParty, ISbTenantSettingService settings)
    {
        if (isAutoParty(processParty))
        {
            return resolveOperatorUserIdStr(tenantId, settings);
        }
        return SecurityUtils.getUserIdStr();
    }

    public static String resolveProcessBy(String tenantId, String processParty, ISbTenantSettingService settings)
    {
        if (isAutoParty(processParty))
        {
            return resolveOperatorUserIdStr(tenantId, settings);
        }
        return SecurityUtils.getUserIdStr();
    }

    public static boolean isAutoRefundRemark(String remark)
    {
        return StringUtils.contains(remark, "自动退费");
    }

    public static String resolveRefundOperator(String tenantId, String remark, ISbTenantSettingService settings)
    {
        if (isAutoRefundRemark(remark))
        {
            return resolveOperatorUserIdStr(tenantId, settings);
        }
        return SecurityUtils.getUserIdStr();
    }
}
