package com.spd.his.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * HIS SQL Server 只读数据源（与主库 MySQL 独立）。
 */
@Configuration
@EnableConfigurationProperties(HisSqlServerProperties.class)
public class HisSqlServerConfiguration
{
    @Bean(name = "hisDataSource", destroyMethod = "close")
    @ConditionalOnProperty(prefix = "spd.his.datasource", name = "enabled", havingValue = "true")
    public DataSource hisDataSource(HisSqlServerProperties properties)
    {
        HisSqlServerProperties.Datasource c = properties.getDatasource();
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(c.getDriverClassName());
        ds.setUrl(c.getUrl());
        ds.setUsername(c.getUsername());
        ds.setPassword(c.getPassword());
        ds.setValidationQuery("SELECT 1");
        ds.setTestWhileIdle(true);
        ds.setMaxActive(8);
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxWait(60000);
        return ds;
    }

    @Bean(name = "hisJdbcTemplate")
    @ConditionalOnProperty(prefix = "spd.his.datasource", name = "enabled", havingValue = "true")
    public JdbcTemplate hisJdbcTemplate(@Qualifier("hisDataSource") DataSource hisDataSource)
    {
        return new JdbcTemplate(hisDataSource);
    }
}
