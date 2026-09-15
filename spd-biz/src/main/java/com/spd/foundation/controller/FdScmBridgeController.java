package com.spd.foundation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONArray;
import com.spd.caigou.mapper.SpdScmTenantBindMapper;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.service.bridge.SpdScmBridgeClient;

/**
 * 云端收件箱拉取（二期：院内主动 pull/ack，少开入站）
 */
@RestController
@RequestMapping("/foundation/scmBridge")
public class FdScmBridgeController extends BaseController
{
    @Autowired
    private SpdScmBridgeClient spdScmBridgeClient;

    @Autowired
    private SpdScmTenantBindMapper spdScmTenantBindMapper;

    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:list')")
    @GetMapping("/pull")
    public AjaxResult pull(@RequestParam(value = "limit", required = false) Integer limit)
    {
        String hospitalCode = hospitalCodeOrThrow();
        JSONArray arr = spdScmBridgeClient.pull(hospitalCode, tenantId(), limit != null ? limit : 20);
        return success(arr);
    }

    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:list')")
    @PostMapping("/ack")
    public AjaxResult ack(@RequestBody Map<String, Object> body)
    {
        String hospitalCode = hospitalCodeOrThrow();
        @SuppressWarnings("unchecked")
        List<String> ids = (List<String>) body.get("messageIds");
        if (ids == null || ids.isEmpty())
        {
            Object one = body.get("messageId");
            if (one != null)
            {
                ids = java.util.Collections.singletonList(String.valueOf(one));
            }
        }
        if (ids == null || ids.isEmpty())
        {
            return error("messageIds 不能为空");
        }
        int n = spdScmBridgeClient.ack(hospitalCode, tenantId(), ids);
        return success(n);
    }

    private String tenantId()
    {
        String tid = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid))
        {
            tid = SecurityUtils.requiredScopedTenantIdForSql();
        }
        return tid;
    }

    private String hospitalCodeOrThrow()
    {
        String code = spdScmTenantBindMapper.selectHospitalCodeByTenantId(tenantId());
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("请先在「云平台编码绑定」中维护当前租户的平台医院编码");
        }
        return code.trim();
    }
}
