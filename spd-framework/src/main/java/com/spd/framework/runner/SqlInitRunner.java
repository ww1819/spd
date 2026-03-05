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
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.alibaba.druid.pool.DruidDataSource;
import com.spd.framework.config.properties.SqlInitProperties;

/**
 * 启动时按顺序执行 SQL 脚本：table → column → view → trigger → procedure。
 * 脚本内用单独一行的「/」分隔每条要执行的语句，按「/」分次执行。
 *
 * @author spd
 */
@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "spd.sql.init", name = "enabled", havingValue = "true")
public class SqlInitRunner implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(SqlInitRunner.class);

    private static final String[] SCRIPT_ORDER = { "table.sql", "column.sql", "view.sql", "trigger.sql", "procedure.sql", "function.sql", "menu.sql", "data_integrity.sql" };

    private final DataSource masterDataSource;
    private final SqlInitProperties properties;
    private final ResourceLoader resourceLoader;

    public SqlInitRunner(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            SqlInitProperties properties,
            ResourceLoader resourceLoader)
    {
        this.masterDataSource = masterDataSource;
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (!properties.isEnabled())
        {
            return;
        }
        String location = normalizeLocation(properties.getLocation());
        boolean failOnError = properties.isFailOnError();

        // 材料管理
        for (String scriptName : SCRIPT_ORDER)
        {
            String path = location + "material/" + scriptName;
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
                executeStatements(statements, scriptName, failOnError);
                log.info("材料管理SQL 脚本执行完成: {}", scriptName);
            }
            catch (Exception e)
            {
                log.error("材料管理SQL 脚本执行失败: {}", scriptName, e);
                if (failOnError)
                {
                    throw e;
                }
            }
        }

        // 设备管理
        for (String scriptName : SCRIPT_ORDER)
            {
                String path = location + "equipment/" + scriptName;
                Resource resource = resourceLoader.getResource(path);
                if (!resource.exists())
                {
                    log.debug("设备管理SQL 脚本不存在，跳过: {}", path);
                    continue;
                }
    
                try
                {
                    String content = readResource(resource);
                    List<String> statements = parseStatements(content);
                    executeStatements(statements, scriptName, failOnError);
                    log.info("设备管理SQL 脚本执行完成: {}", scriptName);
                }
                catch (Exception e)
                {
                    log.error("设备管理SQL 脚本执行失败: {}", scriptName, e);
                    if (failOnError)
                    {
                        throw e;
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
    private List<String> parseStatements(String content)
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

    /**
     * 使用原始 JDBC 连接执行，绕过 Druid StatFilter 对存储过程等语法的解析，避免 ParserException。
     * 分隔符「/」的另一作用：单条语句报错不影响后续语句，每条之间独立执行，出错只记日志并继续执行下一段。
     */
    /**
     * 每条 SQL 使用独立 Statement 执行，避免 CALL 存储过程返回结果集后复用同一 Statement 导致
     * "No operations allowed after statement closed"；同时消费掉可能的结果集与更新计数。
     */
    private void executeStatements(List<String> statements, String scriptName, boolean failOnError) throws Exception
    {
        Connection conn = getRawConnection();
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
                    conn = getRawConnection();
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

    /** 执行单条 SQL，成功返回 null，失败记日志并返回异常 */
    private Exception executeOne(Connection conn, String sql, String scriptName)
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

    /** 消费 Statement 的所有结果集与更新计数，避免影响后续执行 */
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

    /** SQL 初始化执行时 socket 读超时（毫秒），避免大表/存储过程执行时 Read timed out 导致连接关闭 */
    private static final int SOCKET_TIMEOUT_MS = 300_000;

    private Connection getRawConnection() throws Exception
    {
        if (masterDataSource instanceof DruidDataSource)
        {
            DruidDataSource druid = (DruidDataSource) masterDataSource;
            String url = druid.getUrl();
            url = appendSocketTimeout(url, SOCKET_TIMEOUT_MS);
            return DriverManager.getConnection(url, druid.getUsername(), druid.getPassword());
        }
        return masterDataSource.getConnection();
    }

    private static String appendSocketTimeout(String url, int timeoutMs)
    {
        if (url == null) return url;
        String param = "socketTimeout=" + timeoutMs;
        if (url.contains("socketTimeout=")) return url;
        return url + (url.contains("?") ? "&" : "?") + param;
    }
}
