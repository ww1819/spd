package com.spd.foundation.support;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;

/**
 * 众阳 HIS 租户与 scminterface URL 拼装（{@code hospitalKey} 与 SPD {@code customerId} 对齐）。
 */
public final class MsunHisTenantSupport
{
    private static final String VENDOR_CODE = "msun";

    private MsunHisTenantSupport()
    {
    }

    public static boolean isIntegrated(String tenantId)
    {
        return MsunHisTenantRegistry.resolve(tenantId) != null;
    }

    public static String requireHospitalKey(String tenantId)
    {
        MsunHisTenantRegistry registry = MsunHisTenantRegistry.resolve(tenantId);
        if (registry == null)
        {
            throw new ServiceException("当前租户未接入众阳HIS: " + tenantId);
        }
        return registry.getHospitalKey();
    }

    public static void assertIntegrated(String tenantId)
    {
        if (!isIntegrated(tenantId))
        {
            throw new ServiceException("仅已接入众阳HIS的租户可使用本功能");
        }
    }

    /** {@code /api/spd/msun/hospitals/{hospitalKey}} */
    public static String spdHospitalApiPrefix(String tenantId)
    {
        return "/api/spd/" + VENDOR_CODE + "/hospitals/" + requireHospitalKey(tenantId);
    }

    /** {@code /api/vendor/msun/hospitals/{hospitalKey}} */
    public static String vendorHospitalApiPrefix(String tenantId)
    {
        return "/api/vendor/" + VENDOR_CODE + "/hospitals/" + requireHospitalKey(tenantId);
    }

    public static String joinUrl(String baseUrl, String path)
    {
        String base = StringUtils.trimToEmpty(baseUrl);
        String p = path.startsWith("/") ? path : "/" + path;
        if (base.endsWith("/"))
        {
            base = base.substring(0, base.length() - 1);
        }
        return base + p;
    }
}
