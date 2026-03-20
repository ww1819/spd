package com.spd.system.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.system.service.ITenantDataPurgeService;

/**
 * 使用 information_schema 发现带 tenant_id / customer_id 的表并执行 DELETE，外键检查关闭以保证顺序无关。
 */
@Service
public class TenantDataPurgeServiceImpl implements ITenantDataPurgeService
{
    private static final Logger log = LoggerFactory.getLogger(TenantDataPurgeServiceImpl.class);

    private static final Set<String> FULL_RESET_SKIP = buildFullResetSkip();

    private static Set<String> buildFullResetSkip()
    {
        Set<String> s = new HashSet<>();
        Collections.addAll(s,
            "sys_menu", "sys_dict_type", "sys_dict_data", "sys_config",
            "sys_role", "sys_role_menu", "sys_role_dept",
            "sys_dept", "sys_post",
            "sys_user", "sys_user_role", "sys_user_post",
            "sb_menu",
            "sys_job");
        for (String q : new String[] {
            "qrtz_blob_triggers", "qrtz_calendars", "qrtz_cron_triggers", "qrtz_fired_triggers",
            "qrtz_job_details", "qrtz_locks", "qrtz_paused_trigger_grps", "qrtz_scheduler_state",
            "qrtz_simple_triggers", "qrtz_simprop_triggers", "qrtz_triggers"
        })
        {
            s.add(q);
        }
        return s;
    }

    @Autowired
    private DataSource dataSource;

    private static boolean isSafeIdentifier(String name)
    {
        return name != null && name.matches("^[a-zA-Z0-9_]+$");
    }

    private List<String> tablesWithColumn(Connection conn, String columnName) throws SQLException
    {
        List<String> out = new ArrayList<>();
        String sql = "SELECT DISTINCT TABLE_NAME FROM information_schema.COLUMNS "
            + "WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, columnName);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String t = rs.getString(1);
                    if (isSafeIdentifier(t))
                    {
                        out.add(t);
                    }
                }
            }
        }
        return out;
    }

    private int deleteWhere(Connection conn, String table, String col, String value) throws SQLException
    {
        if (!isSafeIdentifier(table) || !isSafeIdentifier(col))
        {
            return 0;
        }
        String sql = "DELETE FROM `" + table + "` WHERE `" + col + "` = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, value);
            return ps.executeUpdate();
        }
    }

    private void setFkChecks(Connection conn, boolean on) throws SQLException
    {
        try (Statement st = conn.createStatement())
        {
            st.execute("SET FOREIGN_KEY_CHECKS = " + (on ? "1" : "0"));
        }
    }

    @Override
    public int purgeConsumablesDataForTenant(String tenantId)
    {
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("租户ID不能为空");
        }
        int total = 0;
        try (Connection conn = dataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                setFkChecks(conn, false);
                for (String table : tablesWithColumn(conn, "tenant_id"))
                {
                    total += deleteWhere(conn, table, "tenant_id", tenantId);
                }
                total += deleteWhere(conn, "sys_user", "customer_id", tenantId);
                if (tableExists(conn, "sys_user_post"))
                {
                    total += deleteWhere(conn, "sys_user_post", "tenant_id", tenantId);
                }
                setFkChecks(conn, true);
                conn.commit();
            }
            catch (Exception e)
            {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e)
        {
            log.error("purgeConsumablesDataForTenant failed", e);
            throw new ServiceException("清理耗材数据失败：" + e.getMessage());
        }
        return total;
    }

    @Override
    public int purgeEquipmentDataForCustomer(String customerId)
    {
        if (StringUtils.isEmpty(customerId))
        {
            throw new ServiceException("客户ID不能为空");
        }
        int total = 0;
        try (Connection conn = dataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                setFkChecks(conn, false);
                for (String table : tablesWithColumn(conn, "customer_id"))
                {
                    if ("sb_customer".equalsIgnoreCase(table))
                    {
                        continue;
                    }
                    total += deleteWhere(conn, table, "customer_id", customerId);
                }
                total += deleteWhere(conn, "sys_user", "customer_id", customerId);
                setFkChecks(conn, true);
                conn.commit();
            }
            catch (Exception e)
            {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e)
        {
            log.error("purgeEquipmentDataForCustomer failed", e);
            throw new ServiceException("清理设备数据失败：" + e.getMessage());
        }
        return total;
    }

    @Override
    public void purgeAllDataKeepPlatform(String confirmToken)
    {
        if (!FULL_RESET_CONFIRM_TOKEN.equals(confirmToken))
        {
            throw new ServiceException("二次确认口令不正确，拒绝执行全库初始化");
        }
        try (Connection conn = dataSource.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                setFkChecks(conn, false);
                List<String> allTables = allBaseTables(conn);
                for (String table : allTables)
                {
                    String tl = table.toLowerCase(Locale.ROOT);
                    if (FULL_RESET_SKIP.contains(tl) || "sys_user".equals(tl))
                    {
                        continue;
                    }
                    if (!isSafeIdentifier(table))
                    {
                        continue;
                    }
                    try (Statement st = conn.createStatement())
                    {
                        st.executeUpdate("DELETE FROM `" + table + "`");
                    }
                    catch (SQLException ex)
                    {
                        log.warn("跳过表 {}: {}", table, ex.getMessage());
                    }
                }
                try (Statement st = conn.createStatement())
                {
                    st.executeUpdate("DELETE FROM sys_user WHERE LOWER(user_name) <> 'admin'");
                }
                try (Statement st = conn.createStatement())
                {
                    st.executeUpdate("DELETE FROM sys_user_role WHERE user_id NOT IN (SELECT user_id FROM sys_user)");
                }
                if (tableExists(conn, "sys_user_post"))
                {
                    try (Statement st = conn.createStatement())
                    {
                        st.executeUpdate("DELETE FROM sys_user_post WHERE user_id NOT IN (SELECT user_id FROM sys_user)");
                    }
                }
                try (Statement st = conn.createStatement())
                {
                    st.executeUpdate(
                        "INSERT IGNORE INTO sys_user_role(user_id, role_id) SELECT u.user_id, 1 FROM sys_user u WHERE u.user_name = 'admin' AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.user_id AND ur.role_id = 1)");
                }
                setFkChecks(conn, true);
                conn.commit();
            }
            catch (Exception e)
            {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e)
        {
            log.error("purgeAllDataKeepPlatform failed", e);
            throw new ServiceException("全库初始化失败：" + e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String table) throws SQLException
    {
        String sql = "SELECT 1 FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
    }

    private List<String> allBaseTables(Connection conn) throws SQLException
    {
        List<String> out = new ArrayList<>();
        String sql = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                String t = rs.getString(1);
                if (isSafeIdentifier(t))
                {
                    out.add(t);
                }
            }
        }
        return out;
    }
}
