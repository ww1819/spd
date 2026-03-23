package com.spd.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.service.ISysUserUiConfigService;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前登录用户的界面配置（列显隐等）
 */
@RestController
@RequestMapping("/system/userUiConfig")
public class SysUserUiConfigController extends BaseController
{
    @Autowired
    private ISysUserUiConfigService sysUserUiConfigService;

    @GetMapping("/get")
    public AjaxResult get(@RequestParam("configKey") String configKey)
    {
        if (StringUtils.isEmpty(configKey) || configKey.length() > 100)
        {
            return error("configKey无效");
        }
        Long userId = SecurityUtils.getUserId();
        String configValue = sysUserUiConfigService.getConfigValue(userId, configKey);
        Map<String, Object> data = new HashMap<>();
        data.put("configKey", configKey);
        data.put("configValue", configValue);
        return AjaxResult.success(data);
    }

    @PostMapping("/save")
    public AjaxResult save(@RequestBody Map<String, String> body)
    {
        String configKey = body != null ? body.get("configKey") : null;
        String configValue = body != null ? body.get("configValue") : null;
        if (StringUtils.isEmpty(configKey) || configKey.length() > 100)
        {
            return error("configKey无效");
        }
        if (configValue != null && configValue.length() > 65535)
        {
            return error("配置内容过长");
        }
        Long userId = SecurityUtils.getUserId();
        sysUserUiConfigService.saveConfig(userId, configKey, configValue != null ? configValue : "");
        return success();
    }
}
