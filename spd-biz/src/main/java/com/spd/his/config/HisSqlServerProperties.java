package com.spd.his.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HIS 外联与抓取策略配置，前缀 {@code spd.his}。
 * <p>
 * 优先使用主库表 {@code sys_his_external_db} 按租户配置连接（见 {@link HisTenantJdbcAccess}）。
 * 全局单数据源（{@link HisSqlServerConfiguration}）仅在 {@link #useGlobalDatasourceFallback} 为 true 且无租户表记录时作为回退。
 */
@ConfigurationProperties(prefix = "spd.his")
public class HisSqlServerProperties
{
    /** 允许使用抓取功能的租户；为空表示不限制（仅建议生产配置具体租户） */
    private List<String> allowedTenantIds = new ArrayList<>();

    /**
     * 主库无该租户 {@code sys_his_external_db} 记录时，是否回退到 {@code spd.his.datasource} 全局单数据源（兼容旧部署）。
     */
    private boolean useGlobalDatasourceFallback = true;

    private final Datasource datasource = new Datasource();
    private final Fetch fetch = new Fetch();

    public boolean isUseGlobalDatasourceFallback()
    {
        return useGlobalDatasourceFallback;
    }

    public void setUseGlobalDatasourceFallback(boolean useGlobalDatasourceFallback)
    {
        this.useGlobalDatasourceFallback = useGlobalDatasourceFallback;
    }

    public List<String> getAllowedTenantIds()
    {
        return allowedTenantIds;
    }

    public void setAllowedTenantIds(List<String> allowedTenantIds)
    {
        this.allowedTenantIds = allowedTenantIds;
    }

    public Datasource getDatasource()
    {
        return datasource;
    }

    public Fetch getFetch()
    {
        return fetch;
    }

    public static class Datasource
    {
        private boolean enabled = false;
        private String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        private String url;
        private String username;
        private String password;

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public String getDriverClassName()
        {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName)
        {
            this.driverClassName = driverClassName;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
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
    }

    public static class Fetch
    {
        /** 单次抓取最大跨度（天） */
        private int maxRangeDays = 31;
        /** 拆分为每段查询的天数 */
        private int chunkDays = 7;

        public int getMaxRangeDays()
        {
            return maxRangeDays;
        }

        public void setMaxRangeDays(int maxRangeDays)
        {
            this.maxRangeDays = maxRangeDays;
        }

        public int getChunkDays()
        {
            return chunkDays;
        }

        public void setChunkDays(int chunkDays)
        {
            this.chunkDays = chunkDays;
        }
    }
}
