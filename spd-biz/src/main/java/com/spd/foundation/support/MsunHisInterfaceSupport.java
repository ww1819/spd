package com.spd.foundation.support;

import com.spd.common.utils.StringUtils;
import com.spd.system.service.ISysConfigService;
import org.springframework.stereotype.Component;

/**
 * scminterface 前置机基址（{@code spd.interface.ip/port}）。
 */
@Component
public class MsunHisInterfaceSupport
{
    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    private final ISysConfigService sysConfigService;

    public MsunHisInterfaceSupport(ISysConfigService sysConfigService)
    {
        this.sysConfigService = sysConfigService;
    }

    public String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));
        if (StringUtils.isEmpty(ip))
        {
            ip = DEFAULT_INTERFACE_IP;
        }
        if (StringUtils.isEmpty(port))
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        return "http://" + ip + ":" + port;
    }

    public String joinUrl(String path)
    {
        return MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(), path);
    }
}
