package com.spd.foundation.service.bridge;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.system.service.ISysConfigService;

/**
 * SPD to hospital gateway bridge client (/api/bridge/v1).
 */
@Component
public class SpdScmBridgeClient
{
    public static final String CONFIG_BRIDGE_ENABLED = "spd.interface.bridge.enabled";

    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String DEFAULT_PORT = "8081";

    @Autowired
    private ISysConfigService sysConfigService;

    public boolean isBridgeEnabled()
    {
        String v = StringUtils.trim(sysConfigService.selectConfigByKey(CONFIG_BRIDGE_ENABLED));
        if (StringUtils.isEmpty(v))
        {
            return true;
        }
        return !"false".equalsIgnoreCase(v) && !"0".equals(v);
    }

    public String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));
        if (StringUtils.isEmpty(ip))
        {
            ip = DEFAULT_IP;
        }
        if (port == null || !port.matches("\\d{1,5}"))
        {
            port = DEFAULT_PORT;
        }
        int portNum = Integer.parseInt(port);
        if (portNum < 1 || portNum > 65535)
        {
            port = DEFAULT_PORT;
        }
        return "http://" + ip + ":" + port;
    }

    public Object invoke(String action, String hospitalCode, String tenantId, Map<String, Object> payload)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("action", action);
        body.put("apiVersion", "1");
        body.put("hospitalCode", hospitalCode);
        body.put("tenantId", tenantId);
        body.put("requestId", UUID.randomUUID().toString());
        body.put("payload", payload != null ? payload : new HashMap<>());
        String url = buildInterfaceBaseUrl() + "/api/bridge/v1/invoke";
        String raw = HttpUtils.sendPost(url, JSON.toJSONString(body), "application/json;charset=UTF-8");
        return unwrapData(raw, "bridge invoke failed");
    }

    public JSONArray pull(String hospitalCode, String tenantId, int limit)
    {
        try
        {
            StringBuilder q = new StringBuilder();
            q.append("hospitalCode=").append(URLEncoder.encode(hospitalCode == null ? "" : hospitalCode, "UTF-8"));
            if (StringUtils.isNotEmpty(tenantId))
            {
                q.append("&tenantId=").append(URLEncoder.encode(tenantId, "UTF-8"));
            }
            q.append("&limit=").append(Math.max(1, Math.min(limit, 100)));
            String url = buildInterfaceBaseUrl() + "/api/bridge/v1/pull";
            String raw = HttpUtils.sendGet(url, q.toString(), "UTF-8");
            Object data = unwrapData(raw, "bridge pull failed");
            if (data instanceof JSONArray)
            {
                return (JSONArray) data;
            }
            if (data == null)
            {
                return new JSONArray();
            }
            return JSON.parseArray(JSON.toJSONString(data));
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("bridge pull failed: " + e.getMessage());
        }
    }

    public int ack(String hospitalCode, String tenantId, List<String> messageIds)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("hospitalCode", hospitalCode);
        body.put("tenantId", tenantId);
        body.put("messageIds", messageIds);
        String url = buildInterfaceBaseUrl() + "/api/bridge/v1/ack";
        String raw = HttpUtils.sendPost(url, JSON.toJSONString(body), "application/json;charset=UTF-8");
        Object data = unwrapData(raw, "bridge ack failed");
        if (data instanceof JSONObject)
        {
            Integer n = ((JSONObject) data).getInteger("acked");
            return n != null ? n : 0;
        }
        return 0;
    }

    private static Object unwrapData(String raw, String errPrefix)
    {
        if (StringUtils.isEmpty(raw))
        {
            throw new ServiceException(errPrefix + ": empty response");
        }
        JSONObject root = JSON.parseObject(raw);
        if (root == null)
        {
            throw new ServiceException(errPrefix + ": parse error");
        }
        if (root.getIntValue("code") != 200)
        {
            String msg = root.getString("msg");
            throw new ServiceException(StringUtils.isNotEmpty(msg) ? msg : errPrefix);
        }
        return root.get("data");
    }
}
