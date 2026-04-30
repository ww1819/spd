package com.spd.caigou.controller;

import com.spd.caigou.service.IPremiseOrderPublishService;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 采购订单发布（调用前置机 scminterface；具体 ids / payload / auto 见 {@link IPremiseOrderPublishService}）
 */
@RestController
@RequestMapping("/spd/order")
public class SpdOrderPublishController extends BaseController
{
    @Autowired
    private IPremiseOrderPublishService premiseOrderPublishService;

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

            AjaxResult r = premiseOrderPublishService.publish(ids);
            if (r.isSuccess())
            {
                return success(r.get(AjaxResult.DATA_TAG));
            }
            Object msg = r.get(AjaxResult.MSG_TAG);
            return error(msg != null ? msg.toString() : "发布失败");
        }
        catch (Exception e)
        {
            return error("发布订单失败: " + e.getMessage());
        }
    }
}

