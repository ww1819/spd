package com.spd.his.config;

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

    /** 与开发联调常用的本机 SQL Server 默认库名，可按现场在 yml 或环境变量 {@link #ENV_URL} 覆盖 */
    public static final String JDBC_URL =
        "jdbc:sqlserver://127.0.0.1;databaseName=THIS4;encrypt=false;trustServerCertificate=true";

    public static final String USERNAME = "sa";

    public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static final String ENV_URL = "HIS_DB_URL";

    public static final String ENV_USERNAME = "HIS_DB_USERNAME";

    public static final String ENV_PASSWORD = "HIS_DB_PASSWORD";

    public static final String ENV_DRIVER_CLASS_NAME = "HIS_DB_DRIVER_CLASS_NAME";
}
