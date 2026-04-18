package com.spd.his.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * HIS 全局只读数据源（与主库 MySQL 独立）。多租户场景优先使用主库 {@code sys_his_external_db}（{@link HisTenantJdbcAccess}），
 * 本 Bean 仅在 {@code spd.his.datasource.enabled=true} 且作为回退时使用。
 */
@Configuration
@EnableConfigurationProperties(HisSqlServerProperties.class)
public class HisSqlServerConfiguration
{
    @Bean(name = "hisDataSource", destroyMethod = "close")
    @ConditionalOnProperty(prefix = "spd.his.datasource", name = "enabled", havingValue = "true")
    public DataSource hisDataSource(HisSqlServerProperties properties, Environment environment)
    {
        HisSqlServerProperties.Datasource c = properties.getDatasource();
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(resolveDriverClassName(c, environment));
        ds.setUrl(resolveJdbcUrl(c, environment));
        ds.setUsername(resolveUsername(c, environment));
        ds.setPassword(resolvePassword(c, environment));
        ds.setValidationQuery("SELECT 1");
        ds.setTestWhileIdle(true);
        ds.setMaxActive(8);
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxWait(60000);
        return ds;
    }

    private static String resolveDriverClassName(HisSqlServerProperties.Datasource c, Environment environment)
    {
        return firstNonBlank(
            c.getDriverClassName(),
            environment.getProperty(HisSqlServerConnectionDefaults.ENV_DRIVER_CLASS_NAME),
            HisSqlServerConnectionDefaults.DRIVER_CLASS_NAME);
    }

    private static String resolveJdbcUrl(HisSqlServerProperties.Datasource c, Environment environment)
    {
        String raw = firstNonBlank(
            c.getUrl(),
            environment.getProperty(HisSqlServerConnectionDefaults.ENV_URL),
            HisSqlServerConnectionDefaults.JDBC_URL);
        return HisSqlServerConnectionDefaults.normalizeSqlServerJdbcUrl(raw);
    }

    private static String resolveUsername(HisSqlServerProperties.Datasource c, Environment environment)
    {
        return firstNonBlank(
            c.getUsername(),
            environment.getProperty(HisSqlServerConnectionDefaults.ENV_USERNAME),
            HisSqlServerConnectionDefaults.USERNAME);
    }

    private static String resolvePassword(HisSqlServerProperties.Datasource c, Environment environment)
    {
        return firstNonBlank(
            c.getPassword(),
            environment.getProperty(HisSqlServerConnectionDefaults.ENV_PASSWORD),
            "");
    }

    /**
     * 配置值优先，其次环境变量，最后默认值（defaults 可为 null 时当作空串）。
     */
    private static String firstNonBlank(String configured, String fromEnv, String defaults)
    {
        if (StringUtils.hasText(configured))
        {
            return configured.trim();
        }
        if (StringUtils.hasText(fromEnv))
        {
            return fromEnv.trim();
        }
        return defaults != null ? defaults : "";
    }

    @Bean(name = "hisJdbcTemplate")
    @ConditionalOnProperty(prefix = "spd.his.datasource", name = "enabled", havingValue = "true")
    public JdbcTemplate hisJdbcTemplate(@Qualifier("hisDataSource") DataSource hisDataSource)
    {
        return new JdbcTemplate(hisDataSource);
    }
}
