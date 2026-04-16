package com.spd.web.controller.system;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.exception.job.TaskException;
import com.spd.system.domain.SysDataBackupConfig;
import com.spd.system.service.ISysDataBackupConfigService;

/**
 * 数据备份管理
 */
@RestController
@RequestMapping("/system/dataBackup")
public class SysDataBackupController extends BaseController
{
    @Autowired
    private ISysDataBackupConfigService dataBackupConfigService;

    @PreAuthorize("@ss.hasPermi('system:dataBackup:query')")
    @GetMapping
    public AjaxResult get()
    {
        return success(dataBackupConfigService.getOrCreateForCurrentTenant());
    }

    @PreAuthorize("@ss.hasPermi('system:dataBackup:edit')")
    @Log(title = "数据备份配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult save(@Validated @RequestBody SysDataBackupConfig config) throws SchedulerException, TaskException
    {
        dataBackupConfigService.saveConfig(config);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('system:dataBackup:changeStatus')")
    @Log(title = "数据备份启停", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysDataBackupConfig body) throws SchedulerException
    {
        dataBackupConfigService.changeStatus(body.getEnabled());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('system:dataBackup:run')")
    @Log(title = "数据备份立即执行", businessType = BusinessType.OTHER)
    @PostMapping("/runNow")
    public AjaxResult runNow() throws SchedulerException
    {
        boolean ok = dataBackupConfigService.runNow();
        return ok ? success("已触发备份任务") : error("触发失败，请检查定时任务是否存在");
    }
}
