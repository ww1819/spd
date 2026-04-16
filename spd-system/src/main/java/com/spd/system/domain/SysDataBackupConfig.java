package com.spd.system.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 数据备份配置 sys_data_backup_config
 */
public class SysDataBackupConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 租户ID，空串表示平台 */
    @Excel(name = "租户ID")
    private String tenantId;

    @Excel(name = "备份目录")
    private String backupPath;

    /** mysqldump 可执行文件路径（可选） */
    private String mysqldumpPath;

    /** 每日备份时间 HH:mm */
    @Excel(name = "备份时间")
    private String backupTime;

    /** 0停用 1启用 */
    @Excel(name = "启用")
    private String enabled;

    private Long jobId;

    @Min(0)
    @Max(3650)
    private Integer retainDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date lastBackupTime;

    private String lastBackupStatus;

    private String lastBackupMessage;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    @Size(max = 500, message = "备份目录长度不能超过500个字符")
    public String getBackupPath()
    {
        return backupPath;
    }

    public void setBackupPath(String backupPath)
    {
        this.backupPath = backupPath;
    }

    @Size(max = 500, message = "mysqldump路径长度不能超过500个字符")
    public String getMysqldumpPath()
    {
        return mysqldumpPath;
    }

    public void setMysqldumpPath(String mysqldumpPath)
    {
        this.mysqldumpPath = mysqldumpPath;
    }

    @Size(max = 8, message = "备份时间格式为HH:mm")
    public String getBackupTime()
    {
        return backupTime;
    }

    public void setBackupTime(String backupTime)
    {
        this.backupTime = backupTime;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public Long getJobId()
    {
        return jobId;
    }

    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    public Integer getRetainDays()
    {
        return retainDays;
    }

    public void setRetainDays(Integer retainDays)
    {
        this.retainDays = retainDays;
    }

    public java.util.Date getLastBackupTime()
    {
        return lastBackupTime;
    }

    public void setLastBackupTime(java.util.Date lastBackupTime)
    {
        this.lastBackupTime = lastBackupTime;
    }

    public String getLastBackupStatus()
    {
        return lastBackupStatus;
    }

    public void setLastBackupStatus(String lastBackupStatus)
    {
        this.lastBackupStatus = lastBackupStatus;
    }

    public String getLastBackupMessage()
    {
        return lastBackupMessage;
    }

    public void setLastBackupMessage(String lastBackupMessage)
    {
        this.lastBackupMessage = lastBackupMessage;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("tenantId", getTenantId())
            .append("backupPath", getBackupPath())
            .append("mysqldumpPath", getMysqldumpPath())
            .append("backupTime", getBackupTime())
            .append("enabled", getEnabled())
            .append("jobId", getJobId())
            .append("retainDays", getRetainDays())
            .append("lastBackupTime", getLastBackupTime())
            .append("lastBackupStatus", getLastBackupStatus())
            .append("lastBackupMessage", getLastBackupMessage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
