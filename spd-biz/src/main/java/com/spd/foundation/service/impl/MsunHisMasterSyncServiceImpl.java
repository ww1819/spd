package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.service.IMsunHisMasterSyncService;
import com.spd.foundation.service.IMsunHisProbeProxyService;
import com.spd.foundation.support.MsunHisInterfaceSupport;
import com.spd.foundation.support.MsunHisProbeApiRegistry;
import com.spd.foundation.support.MsunHisTenantSupport;
import java.util.HashMap;
import java.util.Map;
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

    private final MsunHisInterfaceSupport interfaceSupport;
    private final IMsunHisProbeProxyService probeProxyService;
    private final FdMaterialMapper fdMaterialMapper;

    public MsunHisMasterSyncServiceImpl(
            MsunHisInterfaceSupport interfaceSupport,
            IMsunHisProbeProxyService probeProxyService,
            FdMaterialMapper fdMaterialMapper)
    {
        this.interfaceSupport = interfaceSupport;
        this.probeProxyService = probeProxyService;
        this.fdMaterialMapper = fdMaterialMapper;
    }

    @Override
    public AjaxResult sync(String syncType, Map<String, Object> probeParams)
    {
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        MsunHisTenantSupport.assertIntegrated(tenantId);
        String type = StringUtils.trim(syncType);
        if (StringUtils.isEmpty(type))
        {
            return AjaxResult.error("同步类型不能为空");
        }
        if (probeParams != null && !probeParams.isEmpty())
        {
            String probeKey = MsunHisProbeApiRegistry.probeKeyForSyncType(type);
            if (StringUtils.isNotEmpty(probeKey))
            {
                AjaxResult probeResult = probeProxyService.invoke(probeKey, probeParams);
                if (!probeResult.isSuccess())
                {
                    return probeResult;
                }
            }
        }
        String url = interfaceSupport.joinUrl(
            MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + "/sync/" + type);
        try
        {
            log.info("众阳HIS主数据同步请求 tenant={} type={} url={}", tenantId, type, url);
            String body = HttpUtils.sendPost(url, "{}");
            return parseInterfaceResponse(body, "同步失败");
        }
        catch (Exception ex)
        {
            log.warn("众阳HIS主数据同步失败 type={} err={}", type, ex.getMessage(), ex);
            return AjaxResult.error("同步失败: " + ex.getMessage());
        }
    }

    @Override
    public AjaxResult syncSingleMaterial(Long materialId)
    {
        if (materialId == null)
        {
            return AjaxResult.error("产品档案ID不能为空");
        }
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        MsunHisTenantSupport.assertIntegrated(tenantId);
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(materialId);
        if (material == null || !tenantId.equals(material.getTenantId()))
        {
            return AjaxResult.error("产品档案不存在或不属于当前组织机构");
        }
        String hisId = StringUtils.trim(material.getHisId());
        if (StringUtils.isEmpty(hisId))
        {
            return AjaxResult.error("该产品档案未维护 HIS系统ID，无法单条同步");
        }
        Long drugId;
        try
        {
            drugId = Long.valueOf(hisId);
        }
        catch (NumberFormatException ex)
        {
            return AjaxResult.error("HIS系统ID格式无效: " + hisId);
        }
        Map<String, Object> payload = new HashMap<>(4);
        payload.put("drugId", drugId);
        String specPackingId = StringUtils.trim(material.getHisSpecPackingId());
        if (StringUtils.isNotEmpty(specPackingId))
        {
            payload.put("drugSpecPackingId", specPackingId);
        }
        String url = interfaceSupport.joinUrl(
            MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + "/sync/materials/single");
        try
        {
            log.info("众阳HIS单条耗材同步 tenant={} materialId={} drugId={} specPackingId={} url={}",
                    tenantId, materialId, drugId, specPackingId, url);
            String body = HttpUtils.sendPost(url, JSON.toJSONString(payload));
            return parseInterfaceResponse(body, "单条同步失败");
        }
        catch (Exception ex)
        {
            log.warn("众阳HIS单条耗材同步失败 materialId={} err={}", materialId, ex.getMessage(), ex);
            return AjaxResult.error("单条同步失败: " + ex.getMessage());
        }
    }

    private AjaxResult parseInterfaceResponse(String body, String defaultError)
    {
        if (StringUtils.isEmpty(body))
        {
            return AjaxResult.error("前置机无响应，请检查 spd.internal.base_url 及 scminterface 服务");
        }
        JSONObject json = JSON.parseObject(body);
        Integer code = json.getInteger("code");
        String msg = json.getString("msg");
        if (code != null && code == 200)
        {
            return AjaxResult.success(msg != null ? msg : "同步完成", json.get("data"));
        }
        return AjaxResult.error(msg != null ? msg : defaultError);
    }

}
