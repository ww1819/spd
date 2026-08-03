package com.spd.foundation.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.foundation.service.IFdJcSettingService;

/**
 * 集采租户配置（报量模式二选一；切换保留旧报量）
 */
@RestController
@RequestMapping("/foundation/jcSetting")
public class FdJcSettingController extends BaseController
{
    @Autowired
    private IFdJcSettingService fdJcSettingService;

    @PreAuthorize("@ss.hasPermi('foundation:jcSetting:query')")
    @GetMapping
    public AjaxResult get()
    {
        return success(fdJcSettingService.getSettingSummary());
    }

    /**
     * body: { "reportMode": "PRODUCT" | "TYPE" }
     * 切换不删除任一模式历史报量；切回后原数据仍可用；新模式需另行维护报量。
     */
    @PreAuthorize("@ss.hasPermi('foundation:jcSetting:edit')")
    @Log(title = "集采报量模式", businessType = BusinessType.UPDATE)
    @PutMapping("/reportMode")
    public AjaxResult saveReportMode(@RequestBody Map<String, String> body)
    {
        String mode = body == null ? null : body.get("reportMode");
        fdJcSettingService.saveReportMode(mode);
        return success(fdJcSettingService.getSettingSummary());
    }
}
