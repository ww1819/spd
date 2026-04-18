package com.spd.his.domain;

import java.util.Date;

/**
 * 租户级 HIS 外联库配置（主库表 sys_his_external_db）。
 */
public class HisExternalDb
{
    private String tenantId;
    /** SQLSERVER、MYSQL 等，大写存储 */
    private String dbType;
    private String driverClass;
    private String jdbcUrl;
    private String username;
    private String password;
    /** 0/1 */
    private String enabled;
    /** 非 SQLServer 内置抓取时必填：须含两个时间参数 ? ?，且列别名与内置 SQL 一致 */
    private String sqlInpatientRange;
    private String sqlOutpatientRange;
    private String remark;

    private Date createTime;

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getDbType()
    {
        return dbType;
    }

    public void setDbType(String dbType)
    {
        this.dbType = dbType;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl)
    {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getSqlInpatientRange()
    {
        return sqlInpatientRange;
    }

    public void setSqlInpatientRange(String sqlInpatientRange)
    {
        this.sqlInpatientRange = sqlInpatientRange;
    }

    public String getSqlOutpatientRange()
    {
        return sqlOutpatientRange;
    }

    public void setSqlOutpatientRange(String sqlOutpatientRange)
    {
        this.sqlOutpatientRange = sqlOutpatientRange;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
