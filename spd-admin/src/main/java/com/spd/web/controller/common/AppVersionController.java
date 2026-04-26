package com.spd.web.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Anonymous;
import com.spd.common.config.SPDConfig;
import com.spd.common.core.domain.AjaxResult;

/**
 * 应用版本（匿名，便于登录页与运维核对生产是否已更新）
 *
 * @author spd
 */
@RestController
@RequestMapping("/common")
public class AppVersionController
{
    @Autowired
    private SPDConfig spdConfig;

    @Anonymous
    @GetMapping("/version")
    public AjaxResult version()
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("name", spdConfig.getName());
        ajax.put("version", spdConfig.getVersion());
        ajax.put("buildTime", spdConfig.getBuildTime());
        return ajax;
    }
}
