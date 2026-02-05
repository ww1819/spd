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

    private static final String[] SCRIPT_ORDER = { "table.sql", "column.sql", "view.sql", "trigger.sql", "procedure.sql", "function.sql" };

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

        for (String scriptName : SCRIPT_ORDER)
        {
            String path = location + scriptName;
            Resource resource = resourceLoader.getResource(path);
            if (!resource.exists())
            {
                log.debug("SQL 脚本不存在，跳过: {}", path);
                continue;
            }

            try
            {
                String content = readResource(resource);
                List<String> statements = parseStatements(content);
                executeStatements(statements, scriptName, failOnError);
                log.info("SQL 脚本执行完成: {}", scriptName);
            }
            catch (Exception e)
            {
                log.error("SQL 脚本执行失败: {}", scriptName, e);
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
    private void executeStatements(List<String> statements, String scriptName, boolean failOnError) throws Exception
    {
        Connection conn = getRawConnection();
        Exception firstError = null;
        try
        {
            try (Statement st = conn.createStatement())
            {
                for (String sql : statements)
                {
                    sql = sql.trim();
                    if (sql.isEmpty() || isCommentOrBlankOnly(sql))
                    {
                        continue;
                    }
                    try
                    {
                        st.execute(sql);
                    }
                    catch (Exception e)
                    {
                        log.warn("执行单条 SQL 失败 [{}]: {}", scriptName, sql.substring(0, Math.min(80, sql.length())) + "...", e);
                        if (firstError == null)
                        {
                            firstError = e;
                        }
                        // 不在此处 throw，保证下一段「/」后的语句继续执行
                    }
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
                conn.close();
            }
        }
    }

    private Connection getRawConnection() throws Exception
    {
        if (masterDataSource instanceof DruidDataSource)
        {
            DruidDataSource druid = (DruidDataSource) masterDataSource;
            return DriverManager.getConnection(
                    druid.getUrl(),
                    druid.getUsername(),
                    druid.getPassword());
        }
        return masterDataSource.getConnection();
    }
}
