package com.spd.his.config;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 某租户当前生效的 HIS 外联 JDBC 与抓取 SQL 文本。
 */
public class HisTenantDbHandle
{
    private final JdbcTemplate jdbcTemplate;
    /** 非空则须在应用退出或配置变更时 close */
    private final DruidDataSource ownedDataSource;
    private final String inpatientRangeSql;
    private final String outpatientRangeSql;
    private final String dbTypeNormalized;

    public HisTenantDbHandle(
        JdbcTemplate jdbcTemplate,
        DruidDataSource ownedDataSource,
        String inpatientRangeSql,
        String outpatientRangeSql,
        String dbTypeNormalized)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.ownedDataSource = ownedDataSource;
        this.inpatientRangeSql = inpatientRangeSql;
        this.outpatientRangeSql = outpatientRangeSql;
        this.dbTypeNormalized = dbTypeNormalized;
    }

    public JdbcTemplate getJdbcTemplate()
    {
        return jdbcTemplate;
    }

    public String getInpatientRangeSql()
    {
        return inpatientRangeSql;
    }

    public String getOutpatientRangeSql()
    {
        return outpatientRangeSql;
    }

    public String getDbTypeNormalized()
    {
        return dbTypeNormalized;
    }

    public DataSource getOwnedDataSourceIfAny()
    {
        return ownedDataSource;
    }
}
