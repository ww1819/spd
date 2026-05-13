package com.spd.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Anonymous;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.model.LicenseRegisterBody;
import com.spd.common.utils.StringUtils;
import com.spd.system.service.ISysLicenseService;

/**
 * 登录页匿名导入离线注册码（v2 医院名称绑定）
 */
@RestController
public class LicenseRegisterController
{
    @Autowired
    private ISysLicenseService sysLicenseService;

    @Anonymous
    @PostMapping("/license/register")
    public AjaxResult register(@RequestBody LicenseRegisterBody body)
    {
        if (body == null || StringUtils.isEmpty(body.getLicenseCode()))
        {
            return AjaxResult.error("注册码不能为空");
        }
        String systemType = body.getSystemType();
        if (StringUtils.isEmpty(systemType))
        {
            systemType = "hc";
        }
        sysLicenseService.activateAnonymousFromLogin(body.getLicenseCode().trim(), body.getCustomerId(), systemType);
        return AjaxResult.success("授权已更新，请登录系统");
    }
}
