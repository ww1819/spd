package com.spd.foundation.support;

import com.spd.common.utils.StringUtils;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.system.service.ISysConfigService;
import org.springframework.stereotype.Component;

/**
 * 枣强众阳 HIS 联调/同步/推送：scminterface 基址取自 {@code spd.internal.base_url}（参数设置「SPD内部接口基址」）。
 */
@Component
public class MsunHisInterfaceSupport
{
    private static final String DEFAULT_BASE_URL = "http://127.0.0.1:8088";

    private final ISysConfigService sysConfigService;

    public MsunHisInterfaceSupport(ISysConfigService sysConfigService)
    {
        this.sysConfigService = sysConfigService;
    }

    public String buildInterfaceBaseUrl()
    {
        String baseUrl = StringUtils.trim(
                sysConfigService.selectConfigByKey(HisBillingTenantConstants.CONFIG_SPD_INTERNAL_BASE_URL));
        if (StringUtils.isEmpty(baseUrl))
        {
            return DEFAULT_BASE_URL;
        }
        if (baseUrl.endsWith("/"))
        {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public String joinUrl(String path)
    {
        return MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(), path);
    }
}
