package com.spd.his.config;

import java.util.Locale;

/**
 * HIS（SQL Server）只读库连接默认值：与 {@code spd.his.datasource} 配合使用。
 * <p>
 * 解析顺序（在 {@link HisSqlServerConfiguration} 中）：配置文件非空值 → 环境变量 → 本类常量。
 * 生产环境口令请使用环境变量 {@code HIS_DB_PASSWORD}，勿写入 yml。
 */
public final class HisSqlServerConnectionDefaults
{
    private HisSqlServerConnectionDefaults()
    {
    }

    /**
     * 与开发联调常用的本机 SQL Server 默认库名，可按现场在 yml 或环境变量 {@link #ENV_URL} 覆盖。
     * 命名实例示例：{@code jdbc:sqlserver://host;instanceName=HISSVR;databaseName=THIS4;encrypt=false;trustServerCertificate=true;loginTimeout=60}
     */
    public static final String JDBC_URL =
        "jdbc:sqlserver://127.0.0.1;databaseName=THIS4;encrypt=false;trustServerCertificate=true;loginTimeout=60";

    public static final String USERNAME = "sa";

    public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static final String ENV_URL = "HIS_DB_URL";

    public static final String ENV_USERNAME = "HIS_DB_USERNAME";

    public static final String ENV_PASSWORD = "HIS_DB_PASSWORD";

    public static final String ENV_DRIVER_CLASS_NAME = "HIS_DB_DRIVER_CLASS_NAME";

    private static boolean jdbcParamPresent(String lowerUrl, String paramName)
    {
        return lowerUrl.contains(paramName.toLowerCase(Locale.ROOT) + "=");
    }

    /**
     * 规范化 SQL Server JDBC URL，适配 SQL Server 2012 及较新 mssql-jdbc（未写 encrypt 时驱动可能默认 encrypt=true，与旧实例不兼容）。
     * <p>
     * 规则：若已显式 {@code encrypt=true}，仅按需补 {@code loginTimeout}，不改加密相关参数；否则补
     * {@code encrypt=false}、{@code trustServerCertificate=true}、{@code loginTimeout=60}（已存在则跳过）。
     * <p>
     * 错误 18456「登录失败」表示已到实例并完成协议握手，多为 SQL 账号/密码或服务器身份验证模式问题，与 URL 主机/实例名是否正确无直接关系。
     */
    public static String normalizeSqlServerJdbcUrl(String url)
    {
        if (url == null || url.isEmpty())
        {
            return url;
        }
        String u = url.trim();
        if (!u.regionMatches(true, 0, "jdbc:sqlserver:", 0, "jdbc:sqlserver:".length()))
        {
            return u;
        }
        while (u.endsWith(";"))
        {
            u = u.substring(0, u.length() - 1);
        }
        String lower = u.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(u);
        if (jdbcParamPresent(lower, "encrypt") && lower.contains("encrypt=true"))
        {
            if (!jdbcParamPresent(lower, "loginTimeout"))
            {
                out.append(";loginTimeout=60");
            }
            return out.toString();
        }
        if (!jdbcParamPresent(lower, "encrypt"))
        {
            out.append(";encrypt=false");
            lower = out.toString().toLowerCase(Locale.ROOT);
        }
        if (!jdbcParamPresent(lower, "trustServerCertificate"))
        {
            out.append(";trustServerCertificate=true");
            lower = out.toString().toLowerCase(Locale.ROOT);
        }
        if (!jdbcParamPresent(lower, "loginTimeout"))
        {
            out.append(";loginTimeout=60");
        }
        return out.toString();
    }
}
