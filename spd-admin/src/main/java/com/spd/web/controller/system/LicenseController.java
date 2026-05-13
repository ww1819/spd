package com.spd.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.system.service.ISysLicenseService;

/**
 * 系统离线授权
 */
@RestController
@RequestMapping("/system/license")
public class LicenseController extends BaseController
{
    @Autowired
    private ISysLicenseService sysLicenseService;

    /**
     * 查询授权状态（任意已登录用户，用于复制实例 ID）
     */
    @GetMapping("/status")
    public AjaxResult status()
    {
        return success(sysLicenseService.getStatus());
    }

    /**
     * 导入离线注册码
     */
    @Log(title = "离线授权", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:license:activate')")
    @PostMapping("/activate")
    public AjaxResult activate(@RequestBody Map<String, String> body)
    {
        String code = body != null ? body.get("licenseCode") : null;
        if (StringUtils.isEmpty(code))
        {
            return error("注册码不能为空");
        }
        String customerId = body != null ? body.get("customerId") : null;
        sysLicenseService.activate(code.trim(), customerId, getUsername());
        return success();
    }
}
