package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.service.IMsunHisProbeProxyService;
import com.spd.foundation.support.MsunHisInterfaceSupport;
import com.spd.foundation.support.MsunHisProbeApiRegistry;
import com.spd.foundation.support.MsunHisProbeApiRegistry.ApiDef;
import com.spd.foundation.support.MsunHisTenantSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 入参策略：materialOrDrug=1、不传 limitCount、2.5.44 未填 invalidFlag 时 0+1 双调合并。
 */
@Service
public class MsunHisProbeProxyServiceImpl implements IMsunHisProbeProxyService
{
    private static final Logger log = LoggerFactory.getLogger(MsunHisProbeProxyServiceImpl.class);

    private final MsunHisInterfaceSupport interfaceSupport;

    public MsunHisProbeProxyServiceImpl(MsunHisInterfaceSupport interfaceSupport)
    {
        this.interfaceSupport = interfaceSupport;
    }

    @Override
    public AjaxResult currentEnv()
    {
        return invoke("env", null);
    }

    @Override
    public AjaxResult invoke(String apiKey, Map<String, Object> params)
    {
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        MsunHisTenantSupport.assertIntegrated(tenantId);
        ApiDef def = MsunHisProbeApiRegistry.require(apiKey);
        Map<String, String> query = applyCallPolicy(def, toStringMap(params));
        try
        {
            if (def.isInvalidFlagSweep() && !query.containsKey("invalidFlag"))
            {
                return parseInterfaceResponse(mergeInvalidFlagSweep(tenantId, def, query));
            }
            return parseInterfaceResponse(callOnce(tenantId, def, query));
        }
        catch (IllegalArgumentException ex)
        {
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            log.warn("众阳HIS联调失败 apiKey={} err={}", apiKey, ex.getMessage(), ex);
            return AjaxResult.error("联调调用失败: " + ex.getMessage());
        }
    }

    private String mergeInvalidFlagSweep(String tenantId, ApiDef def, Map<String, String> baseQuery) throws Exception
    {
        Map<String, String> q0 = new LinkedHashMap<>(baseQuery);
        q0.put("invalidFlag", "0");
        Map<String, String> q1 = new LinkedHashMap<>(baseQuery);
        q1.put("invalidFlag", "1");
        String raw0 = callOnce(tenantId, def, q0);
        String raw1 = callOnce(tenantId, def, q1);
        JSONObject j0 = JSON.parseObject(raw0);
        JSONObject j1 = JSON.parseObject(raw1);
        JSONArray items0 = extractHisData(j0);
        JSONArray items1 = extractHisData(j1);
        backfillInvalidFlag(items0, "0");
        backfillInvalidFlag(items1, "1");
        JSONArray merged = new JSONArray(items0.size() + items1.size());
        merged.addAll(items0);
        merged.addAll(items1);
        JSONObject shell = pickShell(j1, j0);
        JSONObject data = shell.getJSONObject("data");
        if (data == null)
        {
            data = new JSONObject();
            shell.put("data", data);
        }
        JSONObject hisBody = data.getJSONObject("hisBody");
        if (hisBody == null)
        {
            hisBody = new JSONObject();
            data.put("hisBody", hisBody);
        }
        hisBody.put("data", merged);
        hisBody.put("success", true);
        JSONObject probeMeta = new JSONObject();
        probeMeta.put("mode", "invalidFlagSweep");
        probeMeta.put("totalRows", merged.size());
        hisBody.put("_probeMerged", probeMeta);
        return shell.toJSONString();
    }

    private static JSONObject pickShell(JSONObject primary, JSONObject fallback)
    {
        if (primary != null && primary.getInteger("code") != null && primary.getInteger("code") == 200)
        {
            return primary;
        }
        return fallback != null ? fallback : new JSONObject();
    }

    private static JSONArray extractHisData(JSONObject root)
    {
        if (root == null)
        {
            return new JSONArray();
        }
        JSONObject data = root.getJSONObject("data");
        if (data == null)
        {
            return new JSONArray();
        }
        JSONObject hisBody = data.getJSONObject("hisBody");
        if (hisBody == null)
        {
            return new JSONArray();
        }
        JSONArray arr = hisBody.getJSONArray("data");
        return arr != null ? arr : new JSONArray();
    }

    private static void backfillInvalidFlag(JSONArray items, String flag)
    {
        if (items == null)
        {
            return;
        }
        for (int i = 0; i < items.size(); i++)
        {
            JSONObject row = items.getJSONObject(i);
            if (row == null)
            {
                continue;
            }
            if (StringUtils.isEmpty(row.getString("invalidFlag")))
            {
                row.put("invalidFlag", flag);
            }
        }
    }

    private String callOnce(String tenantId, ApiDef def, Map<String, String> query) throws Exception
    {
        String url = interfaceSupport.joinUrl(
                MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + def.getPathSuffix());
        if ("POST".equalsIgnoreCase(def.getHttpMethod()))
        {
            String body = buildJsonBody(query);
            log.info("众阳HIS联调 POST tenant={} path={}", tenantId, def.getPathSuffix());
            return HttpUtils.sendPost(url, body, "application/json;charset=UTF-8");
        }
        String qs = buildQueryString(query);
        log.info("众阳HIS联调 GET tenant={} path={} {}", tenantId, def.getPathSuffix(), qs);
        return HttpUtils.sendGet(url, qs);
    }

    private static Map<String, String> applyCallPolicy(ApiDef def, Map<String, String> raw)
    {
        Map<String, String> out = new LinkedHashMap<>();
        if (raw != null)
        {
            raw.forEach((k, v) -> {
                if (StringUtils.isNotEmpty(v))
                {
                    out.put(k, v);
                }
            });
        }
        out.remove("limitCount");
        if (def.isForceMaterialOrDrug())
        {
            out.put("materialOrDrug", "1");
        }
        return out;
    }

    private static Map<String, String> toStringMap(Map<String, Object> params)
    {
        Map<String, String> map = new LinkedHashMap<>();
        if (params == null)
        {
            return map;
        }
        params.forEach((k, v) -> {
            if (v != null && StringUtils.isNotEmpty(String.valueOf(v).trim()))
            {
                map.put(k, String.valueOf(v).trim());
            }
        });
        return map;
    }

    private static String buildQueryString(Map<String, String> params)
    {
        if (params == null || params.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (StringUtils.isEmpty(v))
            {
                return;
            }
            if (sb.length() > 0)
            {
                sb.append('&');
            }
            sb.append(k).append('=').append(v);
        });
        return sb.toString();
    }

    private static String buildJsonBody(Map<String, String> params)
    {
        JSONObject obj = new JSONObject();
        if (params != null)
        {
            params.forEach(obj::put);
        }
        return obj.toJSONString();
    }

    private static AjaxResult parseInterfaceResponse(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return AjaxResult.error("前置机无响应，请检查 spd.interface.ip/port 及 scminterface 服务");
        }
        JSONObject json = JSON.parseObject(raw);
        Integer code = json.getInteger("code");
        String msg = json.getString("msg");
        if (code != null && code == 200)
        {
            Object data = json.get("data");
            AjaxResult ok = AjaxResult.success(msg != null ? msg : "成功", data);
            copyMeta(json, ok, "tenantId", "activeEnv", "hospitalName", "msunBaseUrl", "mirrorSync");
            return ok;
        }
        return AjaxResult.error(msg != null ? msg : "联调失败", json.get("data"));
    }

    private static void copyMeta(JSONObject src, AjaxResult dest, String... keys)
    {
        for (String key : keys)
        {
            if (src.containsKey(key))
            {
                dest.put(key, src.get(key));
            }
        }
    }
}
