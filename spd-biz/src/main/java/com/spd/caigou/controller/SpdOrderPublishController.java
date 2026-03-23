package com.spd.caigou.controller;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购订单发布（调用前置机 scminterface）
 */
@RestController
@RequestMapping("/spd/order")
public class SpdOrderPublishController extends BaseController
{
    /** 服务器 interface 接口 URL，沿用耗材档案推送的配置 */
    @Value("${spd.interface.url:http://localhost:8088}")
    private String interfaceUrl;

    /**
     * 发布选中的采购订单到前置机
     */
    @Log(title = "采购订单发布", businessType = BusinessType.OTHER)
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody Map<String, Object> params)
    {
        try
        {
            Object idsObj = params.get("ids");
            if (!(idsObj instanceof List))
            {
                return error("参数错误，ids 必须为数组");
            }

            @SuppressWarnings("unchecked")
            List<Object> rawIds = (List<Object>) idsObj;
            List<Long> ids = new ArrayList<>();
            for (Object o : rawIds)
            {
                if (o instanceof Number)
                {
                    ids.add(((Number) o).longValue());
                }
                else if (o != null)
                {
                    ids.add(Long.parseLong(o.toString()));
                }
            }

            if (ids.isEmpty())
            {
                return error("订单ID列表不能为空");
            }

            // 拼接前置机 scminterface 的订单发布接口地址
            String url = interfaceUrl;
            if (!url.endsWith("/"))
            {
                url += "/";
            }
            url += "api/spd/order/publish";

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("ids", ids);

            String jsonData = com.alibaba.fastjson2.JSON.toJSONString(requestData);
            String result = com.spd.common.utils.http.HttpUtils
                    .sendPost(url, jsonData, "application/json;charset=UTF-8");

            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(result);
            Integer code = jsonResult.getInteger("code");
            String msg = jsonResult.getString("msg");

            if (code != null && code == 200)
            {
                return success(jsonResult.get("data"));
            }
            else
            {
                return error(msg != null ? msg : "发布失败");
            }
        }
        catch (Exception e)
        {
            return error("发布订单失败: " + e.getMessage());
        }
    }
}

