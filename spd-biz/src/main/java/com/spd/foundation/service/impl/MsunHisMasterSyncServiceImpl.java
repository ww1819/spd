package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.service.IMsunHisMasterSyncService;
import com.spd.foundation.support.MsunHisTenantSupport;
import com.spd.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 调用 scminterface {@code POST /api/spd/msun/hospitals/{hospitalKey}/sync/{type}} 一键同步众阳 HIS 主数据。
 */
@Service
public class MsunHisMasterSyncServiceImpl implements IMsunHisMasterSyncService
{
    private static final Logger log = LoggerFactory.getLogger(MsunHisMasterSyncServiceImpl.class);

    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    private final ISysConfigService sysConfigService;

    public MsunHisMasterSyncServiceImpl(ISysConfigService sysConfigService)
    {
        this.sysConfigService = sysConfigService;
    }

    @Override
    public AjaxResult sync(String syncType)
    {
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        MsunHisTenantSupport.assertIntegrated(tenantId);
        String type = StringUtils.trim(syncType);
        if (StringUtils.isEmpty(type))
        {
            return AjaxResult.error("同步类型不能为空");
        }
        String url = MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(),
            MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + "/sync/" + type);
        try
        {
            log.info("众阳HIS主数据同步请求 tenant={} type={} url={}", tenantId, type, url);
            String body = HttpUtils.sendPost(url, "{}");
            if (StringUtils.isEmpty(body))
            {
                return AjaxResult.error("前置机无响应，请检查 spd.interface.ip/port 及 scminterface 服务");
            }
            JSONObject json = JSON.parseObject(body);
            Integer code = json.getInteger("code");
            String msg = json.getString("msg");
            if (code != null && code == 200)
            {
                return AjaxResult.success(msg != null ? msg : "同步完成", json.get("data"));
            }
            return AjaxResult.error(msg != null ? msg : "同步失败");
        }
        catch (Exception ex)
        {
            log.warn("众阳HIS主数据同步失败 type={} err={}", type, ex.getMessage(), ex);
            return AjaxResult.error("同步失败: " + ex.getMessage());
        }
    }

    private String buildInterfaceBaseUrl()
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
}
