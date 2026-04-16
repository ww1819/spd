package com.spd.system.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysDataBackupConfig;
import com.spd.system.mapper.SysDataBackupConfigMapper;
import org.apache.commons.io.IOUtils;

/**
 * Quartz 调用：dataBackupTask.execute(配置主键L)<br>
 * 优先使用 mysqldump，需服务器已安装 MySQL 客户端并在 PATH 中可用。
 */
@Component("dataBackupTask")
public class DataBackupTask
{
    private static final Logger log = LoggerFactory.getLogger(DataBackupTask.class);

    private static final Pattern JDBC_MYSQL = Pattern.compile(
        "jdbc:mysql://([^/?:]+)(?::(\\d+))?/([^?]+)", Pattern.CASE_INSENSITIVE);

    @Autowired
    private SysDataBackupConfigMapper dataBackupConfigMapper;

    @Autowired
    private Environment environment;

    public void execute(Long configId)
    {
        if (configId == null)
        {
            log.warn("dataBackupTask: configId is null");
            return;
        }
        SysDataBackupConfig cfg = dataBackupConfigMapper.selectById(configId);
        if (cfg == null)
        {
            log.warn("dataBackupTask: config {} not found", configId);
            return;
        }
        if (!"1".equals(cfg.getEnabled()))
        {
            touchResult(cfg, "skipped", "配置已停用，跳过备份");
            return;
        }
        String dirPath = StringUtils.trim(cfg.getBackupPath());
        if (StringUtils.isEmpty(dirPath))
        {
            touchResult(cfg, "failed", "备份目录为空");
            return;
        }
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs())
        {
            touchResult(cfg, "failed", "无法创建备份目录: " + dirPath);
            return;
        }
        if (!dir.isDirectory() || !dir.canWrite())
        {
            touchResult(cfg, "failed", "备份目录不可写: " + dirPath);
            return;
        }

        DbConn conn = resolveDbConn();
        if (conn == null || StringUtils.isEmpty(conn.database))
        {
            touchResult(cfg, "failed", "无法解析主库 spring.datasource.druid.master.url，请检查配置");
            return;
        }

        // 校验 mysqldump 路径（若配置）
        String dumpPath = StringUtils.trim(cfg.getMysqldumpPath());
        if (StringUtils.isNotEmpty(dumpPath))
        {
            File dumpFile = new File(dumpPath);
            if (!dumpFile.exists() || !dumpFile.isFile())
            {
                touchResult(cfg, "failed", "mysqldump 路径不存在或不是文件: " + dumpPath);
                return;
            }
        }

        String tenantSafe = StringUtils.isEmpty(cfg.getTenantId()) ? "platform" : cfg.getTenantId().replaceAll("[^a-zA-Z0-9_-]", "_");
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        String baseName = tenantSafe + "_" + ts;
        File sqlFile = new File(dir, baseName + ".sql");
        File gzFile = new File(dir, baseName + ".sql.gz");
        File errFile = new File(dir, baseName + ".err");
        Path cnf = null;
        try
        {
            cnf = writeClientCnf(conn);
            List<String> tables = listBaseTables(conn);
            if (tables.isEmpty())
            {
                touchResult(cfg, "failed", "未发现可备份的基表（BASE TABLE），请检查数据库或权限");
                return;
            }
            int code = runMysqldump(cnf, conn.database, tables, sqlFile, errFile, dumpPath);
            String err = readTail(dir, errFile.getName(), 1200);

            // 失败：非0退出码，或输出为空
            if (code != 0 || !sqlFile.exists() || sqlFile.length() == 0)
            {
                try
                {
                    Files.deleteIfExists(sqlFile.toPath());
                }
                catch (Exception ignored)
                {
                }
                String msg = (code != 0 ? ("mysqldump 退出码=" + code) : "mysqldump 输出为空(0KB)");
                if (StringUtils.isNotEmpty(err))
                {
                    msg = msg + "，错误：" + err;
                }
                else
                {
                    msg = msg + "，错误文件为空/无法读取（请检查 mysqldump 是否存在、账号权限、网络连接）";
                }
                touchResult(cfg, "failed", msg);
                return;
            }
            try (FileInputStream in = new FileInputStream(sqlFile);
                 GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(gzFile)))
            {
                IOUtils.copy(in, gz);
            }
            if (!sqlFile.delete())
            {
                log.warn("临时 SQL 文件删除失败: {}", sqlFile.getAbsolutePath());
            }
            try
            {
                Files.deleteIfExists(errFile.toPath());
            }
            catch (Exception ignored)
            {
            }
            applyRetention(cfg, dir, tenantSafe + "_");
            touchResult(cfg, "success", "已生成 " + gzFile.getName());
        }
        catch (Exception e)
        {
            log.error("数据备份失败 configId={}", configId, e);
            touchResult(cfg, "failed", truncate(e.getMessage(), 500));
        }
        finally
        {
            if (cnf != null)
            {
                try
                {
                    Files.deleteIfExists(cnf);
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }

    private void applyRetention(SysDataBackupConfig cfg, File dir, String namePrefix)
    {
        Integer days = cfg.getRetainDays();
        if (days == null || days <= 0)
        {
            return;
        }
        long cutoff = System.currentTimeMillis() - days.longValue() * 86400000L;
        File[] files = dir.listFiles();
        if (files == null)
        {
            return;
        }
        for (File f : files)
        {
            if (f.isFile() && f.getName().endsWith(".sql.gz") && f.getName().startsWith(namePrefix)
                && f.lastModified() < cutoff)
            {
                if (!f.delete())
                {
                    log.warn("清理旧备份失败: {}", f.getAbsolutePath());
                }
            }
        }
    }

    private int runMysqldump(Path cnf, String database, File outSql, File errFile, String dumpPath) throws Exception
    {
        return runMysqldump(cnf, database, new ArrayList<>(), outSql, errFile, dumpPath);
    }

    /**
     * 仅导出基表（不包含 VIEW）。避免库里存在失效视图时 mysqldump 退出码=2。
     */
    private int runMysqldump(Path cnf, String database, List<String> tables, File outSql, File errFile, String dumpPath) throws Exception
    {
        String dumpCmd = StringUtils.isNotEmpty(StringUtils.trim(dumpPath)) ? StringUtils.trim(dumpPath) : "mysqldump";
        List<String> cmd = new ArrayList<>();
        cmd.add(dumpCmd);
        cmd.add("--defaults-extra-file=" + cnf.toAbsolutePath());
        cmd.add("--protocol=tcp");
        cmd.add("--single-transaction");
        cmd.add("--quick");
        cmd.add("--set-charset");
        cmd.add("--default-character-set=utf8mb4");
        cmd.add(database);
        // mysqldump 语法：mysqldump db_name [tables...]
        // 指定表名后不会包含 VIEW，从而规避失效视图导致的 SHOW FIELDS 失败。
        if (tables != null && !tables.isEmpty())
        {
            cmd.addAll(tables);
        }
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectOutput(outSql);
        pb.redirectError(errFile);
        Process p = pb.start();
        int code = p.waitFor();
        return code;
    }

    private List<String> listBaseTables(DbConn conn)
    {
        // 使用 JDBC 获取基表列表，确保 mysqldump 只导出数据表，不触发 VIEW 解析。
        String url = environment.getProperty("spring.datasource.druid.master.url");
        if (StringUtils.isEmpty(url))
        {
            return new ArrayList<>();
        }
        String sql = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";
        List<String> tables = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, conn.user, conn.password);
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, conn.database);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String name = rs.getString(1);
                    if (StringUtils.isNotEmpty(name))
                    {
                        tables.add(name);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.warn("读取基表列表失败，将导致备份无法排除视图: {}", e.getMessage());
            return new ArrayList<>();
        }
        return tables;
    }


    private String readTail(File parent, String name, int maxLen)
    {
        File f = new File(parent, name);
        if (!f.exists())
        {
            return "";
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
            {
                if (sb.length() + line.length() > maxLen)
                {
                    break;
                }
                sb.append(line).append('\n');
            }
            return sb.toString().trim();
        }
        catch (Exception e)
        {
            return "";
        }
        finally
        {
            try
            {
                Files.deleteIfExists(f.toPath());
            }
            catch (Exception ignored)
            {
            }
        }
    }

    private Path writeClientCnf(DbConn c) throws Exception
    {
        Path p = Files.createTempFile("spd-mysqldump-", ".cnf");
        String content = "[client]\nuser=" + escapeCnf(c.user) + "\npassword=" + escapeCnf(c.password) + "\nhost="
            + escapeCnf(c.host) + "\nport=" + c.port + "\n";
        Files.write(p, content.getBytes(StandardCharsets.UTF_8));
        return p;
    }

    private static String escapeCnf(String v)
    {
        if (v == null)
        {
            return "";
        }
        return v.replace("\n", "").replace("\r", "");
    }

    private DbConn resolveDbConn()
    {
        String url = environment.getProperty("spring.datasource.druid.master.url");
        String user = environment.getProperty("spring.datasource.druid.master.username");
        String password = environment.getProperty("spring.datasource.druid.master.password");
        if (StringUtils.isEmpty(url))
        {
            return null;
        }
        Matcher m = JDBC_MYSQL.matcher(url);
        if (!m.find())
        {
            return null;
        }
        DbConn c = new DbConn();
        c.host = m.group(1);
        c.port = m.group(2) != null ? Integer.parseInt(m.group(2)) : 3306;
        c.database = m.group(3);
        c.user = user;
        c.password = password == null ? "" : password;
        return c;
    }

    private void touchResult(SysDataBackupConfig cfg, String status, String message)
    {
        SysDataBackupConfig u = new SysDataBackupConfig();
        u.setId(cfg.getId());
        u.setLastBackupTime(new Date());
        u.setLastBackupStatus(status);
        u.setLastBackupMessage(truncate(message, 500));
        dataBackupConfigMapper.updateLastResult(u);
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static class DbConn
    {
        String host;
        int port = 3306;
        String database;
        String user;
        String password;
    }
}
