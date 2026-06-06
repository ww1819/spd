package com.spd.foundation.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.service.IMsunHisBillPushService;
import com.spd.system.service.ISysConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理 scminterface 镜像查询（枣强租户 HIS 明细查看）。
 */
@RestController
@RequestMapping("/foundation/msunHis/mirror")
public class MsunHisMirrorProxyController extends BaseController
{
    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    private final IMsunHisBillPushService msunHisBillPushService;
    private final ISysConfigService sysConfigService;

    public MsunHisMirrorProxyController(
            IMsunHisBillPushService msunHisBillPushService,
            ISysConfigService sysConfigService)
    {
        this.msunHisBillPushService = msunHisBillPushService;
        this.sysConfigService = sysConfigService;
    }

    @GetMapping("/bill-his")
    public AjaxResult billHis(
            @RequestParam String billId,
            @RequestParam(required = false) String billType)
    {
        msunHisBillPushService.assertZaoqiangTenant(SecurityUtils.getCustomerId());
        String base = buildInterfaceBaseUrl();
        StringBuilder url = new StringBuilder(base)
            .append("/api/vendor/msun/hospitals/zaoqiang-tcm-001/mirror/bill-his?billId=")
            .append(billId);
        appendParam(url, "billType", billType);
        try
        {
            String raw = HttpUtils.sendGet(url.toString());
            JSONObject json = JSON.parseObject(raw);
            Integer code = json.getInteger("code");
            if (code != null && code == 200)
            {
                return AjaxResult.success(json.get("data"));
            }
            return AjaxResult.error(json.getString("msg") != null ? json.getString("msg") : "查询失败");
        }
        catch (Exception ex)
        {
            throw new ServiceException("HIS单据查询失败: " + ex.getMessage());
        }
    }

    @GetMapping("/entry-his")
    public AjaxResult entryHis(
            @RequestParam(required = false) String pharmacyStockId,
            @RequestParam(required = false) String deptId,
            @RequestParam(required = false) String drugId,
            @RequestParam(required = false) String drugSpecPackingId,
            @RequestParam(required = false) String batchNumber)
    {
        msunHisBillPushService.assertZaoqiangTenant(SecurityUtils.getCustomerId());
        String base = buildInterfaceBaseUrl();
        StringBuilder url = new StringBuilder(base)
            .append("/api/vendor/msun/hospitals/zaoqiang-tcm-001/mirror/entry-his?");
        appendParam(url, "pharmacyStockId", pharmacyStockId);
        appendParam(url, "deptId", deptId);
        appendParam(url, "drugId", drugId);
        appendParam(url, "drugSpecPackingId", drugSpecPackingId);
        appendParam(url, "batchNumber", batchNumber);
        try
        {
            String raw = HttpUtils.sendGet(url.toString());
            JSONObject json = JSON.parseObject(raw);
            Integer code = json.getInteger("code");
            if (code != null && code == 200)
            {
                return AjaxResult.success(json.get("data"));
            }
            return AjaxResult.error(json.getString("msg") != null ? json.getString("msg") : "查询失败");
        }
        catch (Exception ex)
        {
            throw new ServiceException("HIS镜像查询失败: " + ex.getMessage());
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

    private static void appendParam(StringBuilder url, String name, String value)
    {
        if (StringUtils.isNotEmpty(value))
        {
            if (url.charAt(url.length() - 1) != '?')
            {
                url.append('&');
            }
            url.append(name).append('=').append(value);
        }
    }
}
