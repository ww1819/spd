package com.spd.system.service.impl;

import java.util.Locale;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.constant.ScheduleConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.exception.job.TaskException;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.quartz.domain.SysJob;
import com.spd.quartz.service.ISysJobService;
import com.spd.system.domain.SysDataBackupConfig;
import com.spd.system.mapper.SysDataBackupConfigMapper;
import com.spd.system.service.ISysDataBackupConfigService;

/**
 * 数据备份配置与 sys_job 同步
 */
@Service
public class SysDataBackupConfigServiceImpl implements ISysDataBackupConfigService
{
    private static final String JOB_GROUP = "DEFAULT";

    @Autowired
    private SysDataBackupConfigMapper dataBackupConfigMapper;

    @Autowired
    private ISysJobService sysJobService;

    private String tenantKey()
    {
        String t = SecurityUtils.getCustomerId();
        return t == null ? "" : t.trim();
    }

    @Override
    public SysDataBackupConfig getOrCreateForCurrentTenant()
    {
        String tid = tenantKey();
        SysDataBackupConfig row = dataBackupConfigMapper.selectByTenantId(tid);
        if (row != null)
        {
            return row;
        }
        SysDataBackupConfig ins = new SysDataBackupConfig();
        ins.setTenantId(tid);
        ins.setBackupPath("D:/backup");
        ins.setMysqldumpPath("");
        ins.setBackupTime("02:00");
        ins.setEnabled("0");
        ins.setRetainDays(7);
        ins.setJobId(null);
        ins.setCreateBy(SecurityUtils.getUsername());
        ins.setRemark("数据备份默认配置");
        dataBackupConfigMapper.insert(ins);
        return dataBackupConfigMapper.selectByTenantId(tid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(SysDataBackupConfig config) throws SchedulerException, TaskException
    {
        SysDataBackupConfig db = getOrCreateForCurrentTenant();
        if (!db.getId().equals(config.getId()))
        {
            throw new ServiceException("非法的配置ID");
        }
        if (StringUtils.isEmpty(StringUtils.trim(config.getBackupTime())))
        {
            throw new ServiceException("备份时间不能为空");
        }
        String cron = toDailyCron(config.getBackupTime());
        if (!sysJobService.checkCronExpressionIsValid(cron))
        {
            throw new ServiceException("无效的每日备份时间，请使用 HH:mm 格式");
        }
        if ("1".equals(config.getEnabled()) && StringUtils.isEmpty(StringUtils.trim(config.getBackupPath())))
        {
            throw new ServiceException("启用备份前请填写备份目录");
        }
        db.setBackupPath(StringUtils.trim(config.getBackupPath()));
        db.setMysqldumpPath(StringUtils.trim(config.getMysqldumpPath()));
        db.setBackupTime(normalizeTime(config.getBackupTime()));
        db.setEnabled("1".equals(config.getEnabled()) ? "1" : "0");
        db.setRetainDays(config.getRetainDays() == null ? 7 : config.getRetainDays());
        db.setRemark(config.getRemark());
        db.setUpdateBy(SecurityUtils.getUsername());
        dataBackupConfigMapper.update(db);

        syncSysJob(db, cron);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(String enabled) throws SchedulerException
    {
        SysDataBackupConfig db = getOrCreateForCurrentTenant();
        db.setEnabled("1".equals(enabled) ? "1" : "0");
        if ("1".equals(db.getEnabled()) && StringUtils.isEmpty(StringUtils.trim(db.getBackupPath())))
        {
            throw new ServiceException("启用备份前请填写备份目录并保存");
        }
        db.setUpdateBy(SecurityUtils.getUsername());
        dataBackupConfigMapper.update(db);
        if (db.getJobId() == null)
        {
            throw new ServiceException("请先保存备份时间与目录以生成定时任务");
        }
        SysJob job = sysJobService.selectJobById(db.getJobId());
        if (job == null)
        {
            throw new ServiceException("关联的定时任务不存在，请重新保存配置");
        }
        if ("1".equals(db.getEnabled()))
        {
            job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
            sysJobService.resumeJob(job);
        }
        else
        {
            job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
            sysJobService.pauseJob(job);
        }
    }

    @Override
    public boolean runNow() throws SchedulerException
    {
        SysDataBackupConfig db = getOrCreateForCurrentTenant();
        if (!"1".equals(db.getEnabled()) || db.getJobId() == null)
        {
            throw new ServiceException("请先启用备份并确保已关联定时任务");
        }
        SysJob job = new SysJob();
        job.setJobId(db.getJobId());
        job.setJobGroup(JOB_GROUP);
        return sysJobService.run(job);
    }

    private void syncSysJob(SysDataBackupConfig cfg, String cron) throws SchedulerException, TaskException
    {
        String invoke = "dataBackupTask.execute(" + cfg.getId() + "L)";
        if (cfg.getJobId() == null)
        {
            SysJob job = new SysJob();
            job.setJobName(buildJobName(cfg));
            job.setJobGroup(JOB_GROUP);
            job.setInvokeTarget(invoke);
            job.setCronExpression(cron);
            job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
            job.setConcurrent("1");
            job.setCreateBy(SecurityUtils.getUsername());
            job.setRemark("数据备份调度");
            sysJobService.insertJob(job);
            cfg.setJobId(job.getJobId());
            dataBackupConfigMapper.updateJobId(cfg);
            if ("1".equals(cfg.getEnabled()))
            {
                SysJob loaded = sysJobService.selectJobById(job.getJobId());
                sysJobService.resumeJob(loaded);
            }
        }
        else
        {
            SysJob job = sysJobService.selectJobById(cfg.getJobId());
            if (job == null)
            {
                throw new ServiceException("关联的定时任务不存在");
            }
            job.setJobName(buildJobName(cfg));
            job.setInvokeTarget(invoke);
            job.setCronExpression(cron);
            job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
            job.setConcurrent("1");
            job.setUpdateBy(SecurityUtils.getUsername());
            sysJobService.updateJob(job);
            SysJob latest = sysJobService.selectJobById(cfg.getJobId());
            if ("1".equals(cfg.getEnabled()))
            {
                if (ScheduleConstants.Status.PAUSE.getValue().equals(latest.getStatus()))
                {
                    sysJobService.resumeJob(latest);
                }
            }
            else
            {
                if (ScheduleConstants.Status.NORMAL.getValue().equals(latest.getStatus()))
                {
                    sysJobService.pauseJob(latest);
                }
            }
        }
    }

    private static String buildJobName(SysDataBackupConfig cfg)
    {
        String suffix = StringUtils.isEmpty(cfg.getTenantId()) ? "platform" : cfg.getTenantId();
        String base = "数据备份-" + suffix;
        return base.length() <= 64 ? base : base.substring(0, 64);
    }

    /**
     * Quartz 6 域：秒 分 时 日 月 周
     */
    public static String toDailyCron(String backupTime)
    {
        String t = normalizeTime(backupTime);
        String[] parts = t.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
        {
            throw new ServiceException("备份时间超出范围");
        }
        return String.format(Locale.ROOT, "0 %d %d * * ?", minute, hour);
    }

    private static String normalizeTime(String backupTime)
    {
        if (backupTime == null)
        {
            return "02:00";
        }
        String s = backupTime.trim();
        if (s.length() == 4 && s.indexOf(':') < 0)
        {
            // "0200" unlikely
            return s;
        }
        String[] p = s.split(":");
        if (p.length < 2)
        {
            throw new ServiceException("备份时间格式应为 HH:mm");
        }
        int h = Integer.parseInt(p[0].trim());
        int m = Integer.parseInt(p[1].trim());
        return String.format(Locale.ROOT, "%02d:%02d", h, m);
    }
}
