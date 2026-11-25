package com.spd.framework.runner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 启动时按顺序执行 SQL 脚本：table → column → view → trigger → procedure。
 * 脚本内用单独一行的「/」分隔每条要执行的语句，按「/」分次执行。
 * <p>
 * 业务 Bean（如 SysConfig/SysJob）的 {@code @PostConstruct} 早于 {@link ApplicationRunner}，
 * 因此主路径在 {@code masterDataSource} 创建时由 {@link #runEarlyIfEnabled} 执行；
 * {@link ApplicationRunner} 仅作兜底（已执行则跳过）。
 * </p>
 *
 * @author spd
 */
@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "spd.sql.init", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "spd.sql.init")
public class SqlInitRunner implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(SqlInitRunner.class);

    private static final String[] SCRIPT_ORDER = {
        "table.sql",
        "cleanup_duplicate_fd_supplier_factory.sql",
        "column.sql",
        "mat_scm_archive.sql",
        "view.sql",
        "trigger.sql",
        "procedure.sql",
        "function.sql",
        "menu.sql",
        "menu_dir_restructure.sql",
        "insert.sql",
        "focus18_data.sql",
        "data_integrity.sql"
    };

    /** 全 JVM 只执行一次（早跑成功后 ApplicationRunner 跳过） */
    private static final AtomicBoolean EXECUTED = new AtomicBoolean(false);

    private final DataSource masterDataSource;
    private final ResourceLoader resourceLoader;

    /** 是否启用启动时执行 SQL 脚本（与 {@code spd.sql.init.enabled} 一致，供绑定） */
    private boolean enabled = true;

    /** 脚本根路径，如 classpath:sql/mysql/ 或 file:./sql/mysql/ */
    private String location = "classpath:sql/mysql/";

    /** 某脚本执行失败是否中断应用启动 */
    private boolean failOnError = false;

    public SqlInitRunner(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            ResourceLoader resourceLoader)
    {
        this.masterDataSource = masterDataSource;
        this.resourceLoader = resourceLoader;
    }

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

    /**
     * 在 {@code masterDataSource} 工厂方法内调用（早于业务 {@code @PostConstruct}）。
     * <p>
     * 注意：此时 {@code @ConfigurationProperties} 尚未把 url 绑到 Druid 对象上，
     * 必须从 Environment 读取 {@code spring.datasource.druid.master.*}，否则会出现
     * {@code The url cannot be null} 且 EXECUTED 被置位导致 ApplicationRunner 跳过。
     * </p>
     */
    public static void runEarlyIfEnabled(DataSource masterDataSource, Environment env, ResourceLoader resourceLoader)
    {
        if (env == null || resourceLoader == null)
        {
            return;
        }
        Boolean on = env.getProperty("spd.sql.init.enabled", Boolean.class, true);
        if (!Boolean.TRUE.equals(on))
        {
            return;
        }
        String jdbcUrl = env.getProperty("spring.datasource.druid.master.url");
        String jdbcUser = env.getProperty("spring.datasource.druid.master.username");
        String jdbcPass = env.getProperty("spring.datasource.druid.master.password", "");
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty())
        {
            log.warn("SqlInitRunner early：Environment 尚无 master.url，改由 ApplicationRunner 执行");
            return;
        }
        if (!EXECUTED.compareAndSet(false, true))
        {
            return;
        }
        String loc = normalizeLocation(env.getProperty("spd.sql.init.location", "classpath:sql/mysql/"));
        boolean fail = Boolean.TRUE.equals(env.getProperty("spd.sql.init.fail-on-error", Boolean.class, false));
        log.info("SqlInitRunner：用 Environment JDBC 配置早执行 material 脚本（早于业务 @PostConstruct）");
        try
        {
            runMaterialScripts(masterDataSource, resourceLoader, loc, fail, jdbcUrl.trim(), jdbcUser, jdbcPass);
        }
        catch (RuntimeException ex)
        {
            EXECUTED.set(false);
            throw ex;
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (!enabled)
        {
            return;
        }
        if (!EXECUTED.compareAndSet(false, true))
        {
            log.info("SqlInitRunner：material 脚本已在 DataSource 创建时执行，跳过 ApplicationRunner");
            return;
        }
        String loc = normalizeLocation(location);
        boolean fail = failOnError;
        log.info("SqlInitRunner：ApplicationRunner 兜底执行 material 脚本");
        runMaterialScripts(masterDataSource, resourceLoader, loc, fail, null, null, null);
    }

    private static void runMaterialScripts(DataSource masterDataSource, ResourceLoader resourceLoader,
            String loc, boolean fail, String jdbcUrl, String jdbcUser, String jdbcPass)
            throws IllegalStateException
    {
        for (String scriptName : SCRIPT_ORDER)
        {
            String path = loc + "material/" + scriptName;
            Resource resource = resourceLoader.getResource(path);
            if (!resource.exists())
            {
                log.debug("材料管理SQL 脚本不存在，跳过: {}", path);
                continue;
            }

            try
            {
                String content = readResource(resource);
                List<String> statements = parseStatements(content);
                executeStatements(masterDataSource, statements, scriptName, fail, jdbcUrl, jdbcUser, jdbcPass);
                log.info("材料管理SQL 脚本执行完成: {}", scriptName);
            }
            catch (Exception e)
            {
                log.error("材料管理SQL 脚本执行失败: {}", scriptName, e);
                if (fail)
                {
                    throw new IllegalStateException("材料管理SQL 脚本执行失败: " + scriptName, e);
                }
            }
        }
    }

    private static String normalizeLocation(String location)
    {
        if (location == null || location.isEmpty())
        {
            return "classpath:sql/mysql/";
        }
        return location.endsWith("/") ? location : location + "/";
    }

    private static String readResource(Resource resource) throws IOException
    {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) >= 0)
            {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }

    /**
     * 按单独一行的「/」分隔符解析 SQL，得到多条语句。
     */
    private static List<String> parseStatements(String content)
    {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : content.split("\\r?\\n", -1))
        {
            if (line.trim().equals("/"))
            {
                String stmt = current.toString().trim();
                if (!stmt.isEmpty())
                {
                    statements.add(stmt);
                }
                current.setLength(0);
                continue;
            }
            current.append(line).append("\n");
        }
        String stmt = current.toString().trim();
        if (!stmt.isEmpty())
        {
            statements.add(stmt);
        }
        return statements;
    }

    /** 仅注释或空白的片段不发送给 MySQL */
    private static boolean isCommentOrBlankOnly(String sql)
    {
        String s = sql.trim();
        if (s.isEmpty()) return true;
        boolean inBlock = false;
        StringBuilder sb = new StringBuilder();
        for (String line : s.split("\\r?\\n"))
        {
            String t = line.trim();
            if (inBlock) { if (t.endsWith("*/")) inBlock = false; continue; }
            if (t.startsWith("/*")) { inBlock = !t.contains("*/"); continue; }
            if (t.isEmpty() || t.startsWith("--")) continue;
            sb.append(t).append(' ');
        }
        return sb.toString().trim().isEmpty();
    }

    private static void executeStatements(DataSource masterDataSource, List<String> statements, String scriptName,
            boolean failOnError, String jdbcUrl, String jdbcUser, String jdbcPass) throws Exception
    {
        Connection conn = getRawConnection(masterDataSource, jdbcUrl, jdbcUser, jdbcPass);
        Exception firstError = null;
        try
        {
            for (String sql : statements)
            {
                sql = sql.trim();
                if (sql.isEmpty() || isCommentOrBlankOnly(sql))
                {
                    continue;
                }
                Exception err = executeOne(conn, sql, scriptName);
                if (err != null && isConnectionClosedOrTimeout(err))
                {
                    try { conn.close(); } catch (Exception ignored) { }
                    conn = getRawConnection(masterDataSource, jdbcUrl, jdbcUser, jdbcPass);
                    err = executeOne(conn, sql, scriptName);
                }
                if (err != null && firstError == null)
                {
                    firstError = err;
                }
            }
            if (failOnError && firstError != null)
            {
                throw firstError;
            }
        }
        finally
        {
            if (conn != null)
            {
                try { conn.close(); } catch (Exception ignored) { }
            }
        }
    }

    private static Exception executeOne(Connection conn, String sql, String scriptName)
    {
        try (Statement st = conn.createStatement())
        {
            st.execute(sql);
            consumeAllResults(st);
            return null;
        }
        catch (Exception e)
        {
            log.warn("执行单条 SQL 失败 [{}]: {}", scriptName, sql.substring(0, Math.min(80, sql.length())) + "...", e);
            return e;
        }
    }

    private static boolean isConnectionClosedOrTimeout(Exception e)
    {
        if (e == null) return false;
        String msg = e.getMessage();
        if (msg != null && (msg.contains("connection closed") || msg.contains("Connection closed")
                || msg.contains("No operations allowed after connection closed")
                || msg.contains("Communications link failure") || msg.contains("Read timed out")))
        {
            return true;
        }
        Throwable cause = e.getCause();
        return cause != null && cause != e && isConnectionClosedOrTimeout(cause instanceof Exception ? (Exception) cause : new Exception(cause));
    }

    private static void consumeAllResults(Statement st) throws java.sql.SQLException
    {
        while (true)
        {
            if (st.getMoreResults(Statement.CLOSE_CURRENT_RESULT))
            {
                continue;
            }
            if (st.getUpdateCount() == -1)
            {
                break;
            }
        }
    }

    private static final int SOCKET_TIMEOUT_MS = 300_000;

    private static Connection getRawConnection(DataSource masterDataSource, String jdbcUrl, String jdbcUser,
            String jdbcPass) throws Exception
    {
        // 优先 Environment 传入的 URL（工厂方法阶段 Druid 上 url 常为 null）
        if (jdbcUrl != null && !jdbcUrl.trim().isEmpty())
        {
            String url = appendSocketTimeout(jdbcUrl.trim(), SOCKET_TIMEOUT_MS);
            return DriverManager.getConnection(url, jdbcUser, jdbcPass != null ? jdbcPass : "");
        }
        if (masterDataSource instanceof DruidDataSource)
        {
            DruidDataSource druid = (DruidDataSource) masterDataSource;
            if (druid.getUrl() != null && !druid.getUrl().isEmpty())
            {
                String url = appendSocketTimeout(druid.getUrl(), SOCKET_TIMEOUT_MS);
                return DriverManager.getConnection(url, druid.getUsername(), druid.getPassword());
            }
        }
        if (masterDataSource != null)
        {
            return masterDataSource.getConnection();
        }
        throw new IllegalStateException("SqlInitRunner：无可用 JDBC 连接（url 为空且无 DataSource）");
    }

    private static String appendSocketTimeout(String url, int timeoutMs)
    {
        if (url == null) return url;
        if (url.contains("socketTimeout=")) return url;
        return url + (url.contains("?") ? "&" : "?") + "socketTimeout=" + timeoutMs;
    }
}
