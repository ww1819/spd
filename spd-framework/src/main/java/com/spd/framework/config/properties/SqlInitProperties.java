package com.spd.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 启动时 SQL 脚本执行配置
 *
 * @author spd
 */
@ConfigurationProperties(prefix = "spd.sql.init")
public class SqlInitProperties
{
    /** 是否启用启动时执行 SQL 脚本 */
    private boolean enabled = true;

    /** 脚本根路径，如 classpath:sql/mysql/ 或 file:./sql/mysql/ */
    private String location = "classpath:sql/mysql/";

    /** 某脚本执行失败是否中断应用启动 */
    private boolean failOnError = false;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public boolean isFailOnError()
    {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }
}
