package com.spd.foundation.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.service.IMsunHisBillPushService;
import com.spd.foundation.service.IMsunHisProbeProxyService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 枣强众阳 HIS 联调：SPD 后端代理 scminterface（浏览器不直连前置机）。
 */
@RestController
@RequestMapping("/foundation/msunHis/probe")
public class MsunHisProbeProxyController extends BaseController
{
    private final IMsunHisProbeProxyService probeProxyService;
    private final IMsunHisBillPushService msunHisBillPushService;

    public MsunHisProbeProxyController(
            IMsunHisProbeProxyService probeProxyService,
            IMsunHisBillPushService msunHisBillPushService)
    {
        this.probeProxyService = probeProxyService;
        this.msunHisBillPushService = msunHisBillPushService;
    }

    @GetMapping("/env")
    public AjaxResult env()
    {
        msunHisBillPushService.assertMsunIntegratedTenant(SecurityUtils.getCustomerId());
        return probeProxyService.currentEnv();
    }

    @PostMapping("/invoke/{apiKey}")
    public AjaxResult invoke(
            @PathVariable String apiKey,
            @RequestBody(required = false) Map<String, Object> params)
    {
        msunHisBillPushService.assertMsunIntegratedTenant(SecurityUtils.getCustomerId());
        return probeProxyService.invoke(apiKey, params);
    }
}
