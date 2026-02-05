package com.spd.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.spd.framework.config.properties.SqlInitProperties;

/**
 * SQL 启动脚本配置
 *
 * @author spd
 */
@Configuration
@ConditionalOnProperty(prefix = "spd.sql.init", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SqlInitProperties.class)
public class SqlInitConfig
{
}
