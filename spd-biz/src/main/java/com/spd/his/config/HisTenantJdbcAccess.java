package com.spd.his.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.alibaba.druid.pool.DruidDataSource;
import com.spd.common.exception.ServiceException;
import com.spd.his.domain.HisExternalDb;
import com.spd.his.mapper.HisExternalDbMapper;

/**
 * 按租户解析 HIS 外联 JDBC（主库 sys_his_external_db）；支持 SQLSERVER / MYSQL（MYSQL 须在表中配置自定义区间 SQL）。
 */
@Component
public class HisTenantJdbcAccess
{
    private static final String DB_SQLSERVER = "SQLSERVER";
    private static final String DB_MYSQL = "MYSQL";

    private final ConcurrentHashMap<String, CachedHandle> cache = new ConcurrentHashMap<>();

    @Autowired
    private HisExternalDbMapper hisExternalDbMapper;

    @Autowired(required = false)
    private JdbcTemplate hisJdbcTemplate;

    @Autowired
    private HisSqlServerProperties hisSqlServerProperties;

    /**
     * 解析当前租户的 HIS 连接与区间抓取 SQL；无表配置且不允许全局回退时抛错。
     */
    public HisTenantDbHandle obtainHandle(String tenantId)
    {
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("租户ID为空，无法连接 HIS");
        }
        HisExternalDb row = hisExternalDbMapper.selectEnabledByTenantId(tenantId);
        if (row != null)
        {
            String sig = signature(row);
            CachedHandle cached = cache.get(tenantId);
            if (cached != null && sig.equals(cached.signature))
            {
                return cached.handle;
            }
            synchronized (cache)
            {
                cached = cache.get(tenantId);
                if (cached != null && sig.equals(cached.signature))
                {
                    return cached.handle;
                }
                if (cached != null && cached.handle.getOwnedDataSourceIfAny() instanceof DruidDataSource)
                {
                    ((DruidDataSource) cached.handle.getOwnedDataSourceIfAny()).close();
                }
                HisTenantDbHandle handle = buildFromRow(row);
                cache.put(tenantId, new CachedHandle(sig, handle));
                return handle;
            }
        }
        if (hisSqlServerProperties.isUseGlobalDatasourceFallback()
            && hisJdbcTemplate != null
            && hisSqlServerProperties.getDatasource().isEnabled())
        {
            return new HisTenantDbHandle(
                hisJdbcTemplate,
                null,
                HisChargeMirrorFetchSql.SQLSERVER_INPATIENT_RANGE,
                HisChargeMirrorFetchSql.SQLSERVER_OUTPATIENT_RANGE,
                DB_SQLSERVER);
        }
        throw new ServiceException("当前租户未配置 HIS 外联库：请在主库 sys_his_external_db 中维护 tenant_id="
            + tenantId + " 的记录，或开启 spd.his.use-global-datasource-fallback 并配置全局 HIS 数据源");
    }

    private static HisTenantDbHandle buildFromRow(HisExternalDb row)
    {
        String dbType = normalizeDbType(row.getDbType());
        String driver = StringUtils.isNotEmpty(row.getDriverClass()) ? row.getDriverClass().trim() : defaultDriver(dbType);
        if (StringUtils.isAnyEmpty(driver, row.getJdbcUrl(), row.getUsername()))
        {
            throw new ServiceException("租户 " + row.getTenantId() + " 的 HIS 外联配置不完整（driver/jdbc_url/username）");
        }
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(row.getJdbcUrl().trim());
        ds.setUsername(row.getUsername().trim());
        ds.setPassword(row.getPassword() != null ? row.getPassword() : "");
        ds.setValidationQuery("SELECT 1");
        ds.setTestWhileIdle(true);
        ds.setMaxActive(8);
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxWait(60000);
        JdbcTemplate jt = new JdbcTemplate(ds);
        String inSql;
        String outSql;
        if (DB_SQLSERVER.equals(dbType))
        {
            inSql = StringUtils.isNotBlank(row.getSqlInpatientRange())
                ? row.getSqlInpatientRange().trim() : HisChargeMirrorFetchSql.SQLSERVER_INPATIENT_RANGE;
            outSql = StringUtils.isNotBlank(row.getSqlOutpatientRange())
                ? row.getSqlOutpatientRange().trim() : HisChargeMirrorFetchSql.SQLSERVER_OUTPATIENT_RANGE;
        }
        else if (DB_MYSQL.equals(dbType))
        {
            if (StringUtils.isBlank(row.getSqlInpatientRange()) || StringUtils.isBlank(row.getSqlOutpatientRange()))
            {
                throw new ServiceException("租户 " + row.getTenantId() + " db_type=MYSQL 时须在 sys_his_external_db 配置 sql_inpatient_range、sql_outpatient_range");
            }
            inSql = row.getSqlInpatientRange().trim();
            outSql = row.getSqlOutpatientRange().trim();
        }
        else
        {
            throw new ServiceException("不支持的 HIS db_type: " + row.getDbType() + "，请使用 SQLSERVER 或 MYSQL");
        }
        return new HisTenantDbHandle(jt, ds, inSql, outSql, dbType);
    }

    private static String defaultDriver(String dbType)
    {
        if (DB_MYSQL.equals(dbType))
        {
            return "com.mysql.cj.jdbc.Driver";
        }
        return HisSqlServerConnectionDefaults.DRIVER_CLASS_NAME;
    }

    private static String normalizeDbType(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return DB_SQLSERVER;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("SQLSERVER") || "MSSQL".equals(u))
        {
            return DB_SQLSERVER;
        }
        if ("MYSQL".equals(u) || "MARIADB".equals(u))
        {
            return DB_MYSQL;
        }
        return u;
    }

    private static String signature(HisExternalDb row)
    {
        String s = row.getDbType() + "|" + row.getDriverClass() + "|" + row.getJdbcUrl() + "|" + row.getUsername() + "|"
            + (row.getPassword() != null ? row.getPassword() : "") + "|"
            + StringUtils.defaultString(row.getSqlInpatientRange()) + "|"
            + StringUtils.defaultString(row.getSqlOutpatientRange());
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            return Integer.toHexString(s.hashCode());
        }
    }

    private static final class CachedHandle
    {
        private final String signature;
        private final HisTenantDbHandle handle;

        private CachedHandle(String signature, HisTenantDbHandle handle)
        {
            this.signature = signature;
            this.handle = handle;
        }
    }

    /**
     * 配置变更后丢弃该租户的连接缓存，下次抓取时按新配置重建。
     */
    public void evictTenant(String tenantId)
    {
        if (StringUtils.isEmpty(tenantId))
        {
            return;
        }
        synchronized (cache)
        {
            CachedHandle removed = cache.remove(tenantId);
            if (removed != null && removed.handle.getOwnedDataSourceIfAny() instanceof DruidDataSource)
            {
                ((DruidDataSource) removed.handle.getOwnedDataSourceIfAny()).close();
            }
        }
    }

    @PreDestroy
    public void destroy()
    {
        for (Map.Entry<String, CachedHandle> e : cache.entrySet())
        {
            DataSource ds = e.getValue().handle.getOwnedDataSourceIfAny();
            if (ds instanceof DruidDataSource)
            {
                ((DruidDataSource) ds).close();
            }
        }
        cache.clear();
    }
}
