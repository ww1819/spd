package com.spd.system.service;

import org.quartz.SchedulerException;
import com.spd.common.exception.job.TaskException;
import com.spd.system.domain.SysDataBackupConfig;

/**
 * 数据备份配置
 */
public interface ISysDataBackupConfigService
{
    /**
     * 当前租户（或平台空串）下的配置，不存在则插入默认行
     */
    SysDataBackupConfig getOrCreateForCurrentTenant();

    /**
     * 保存配置并同步 sys_job
     */
    void saveConfig(SysDataBackupConfig config) throws SchedulerException, TaskException;

    /**
     * 仅启停
     */
    void changeStatus(String enabled) throws SchedulerException;

    /**
     * 立即执行一次备份（需已存在 job 且启用）
     */
    boolean runNow() throws SchedulerException;
}
