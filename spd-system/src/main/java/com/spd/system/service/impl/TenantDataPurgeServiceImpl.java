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
 * <p>全库初始化 {@link #purgeAllDataKeepPlatform}：
 * <ul>
 *   <li>{@link #shouldSkipFullResetTable(String)} — 整表保留（不执行 DELETE）；</li>
 *   <li>{@link #FULL_RESET_TENANT_NULL_PRESERVE_WHITELIST} — 仅删除「带租户/客户维度」的行，保留 tenant_id、customer_id 为空或空白的行；</li>
 *   <li>其余表 — 整表 {@code DELETE}。</li>
 * </ul>
 */
@Service
public class TenantDataPurgeServiceImpl implements ITenantDataPurgeService
{
    private static final Logger log = LoggerFactory.getLogger(TenantDataPurgeServiceImpl.class);

    private static final Set<String> FULL_RESET_SKIP = buildFullResetSkip();

    /**
     * <p><b>全库初始化 — 租户维度「白名单」</b>（表名一律小写，与 {@link #shouldSkipFullResetTable(String)} 入参一致）。</p>
     *
     * <p><b>语义（与「整表跳过」不同）：</b></p>
     * <ul>
     *   <li>对<b>不在</b> {@link #FULL_RESET_SKIP} / {@link #shouldSkipFullResetTable(String)} 中的表，默认是 {@code DELETE FROM 表} 清空整表；</li>
     *   <li>若表加入<b>本白名单</b>，则改为<b>只删除「存在租户/客户归属」的行</b>，即：
     *     <ul>
     *       <li>若表含 {@code tenant_id}：删除 {@code tenant_id IS NOT NULL} 且去掉首尾空白后非空的行；</li>
     *       <li>若表含 {@code customer_id}（可与 tenant_id 同时存在）：同样规则；若两列均存在，则满足任一列「有非空归属」即删除该行（平台侧记录通常两列均为 NULL 或空白）；</li>
     *       <li><b>保留</b>：{@code tenant_id} / {@code customer_id} 为 {@code NULL}，或仅含空白字符的行 —— 常见于平台管理员产生的与租户无关的操作记录、全局配置痕迹等。</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p><b>扩展方式：</b>在 {@link #buildFullResetTenantNullPreserveWhitelist()} 中向集合添加小写表名即可，例如 {@code Collections.addAll(s, "your_audit_log");}。</p>
     *
     * <p><b>注意：</b></p>
     * <ul>
     *   <li>若白名单表<b>既没有</b> {@code tenant_id} 也<b>没有</b> {@code customer_id} 列，则无法按上述语义清理，将打日志并<b>跳过</b>该表（避免误整表删除）；请从白名单移除或增加列后再使用白名单；</li>
     *   <li>本白名单仅作用于 {@link #purgeAllDataKeepPlatform}，不影响按租户/客户单点清理 {@link #purgeConsumablesDataForTenant} / {@link #purgeEquipmentDataForCustomer}。</li>
     * </ul>
     *
     * <p>当前策略：<b>白名单为空</b>，所有非跳过表仍走整表 {@code DELETE}。</p>
     */
    private static final Set<String> FULL_RESET_TENANT_NULL_PRESERVE_WHITELIST = buildFullResetTenantNullPreserveWhitelist();

    private static Set<String> buildFullResetTenantNullPreserveWhitelist()
    {
        Set<String> s = new HashSet<>();
        // 后续在此添加需「保留 NULL/空白 租户或客户键」的表名（小写），例如：
        // Collections.addAll(s, "biz_operation_log");
        return s;
    }

    /** @return 是否在全库初始化中按「仅删有租户/客户维度的行」处理 */
    private static boolean isFullResetTenantNullPreserveWhitelist(String tl)
    {
        return tl != null && FULL_RESET_TENANT_NULL_PRESERVE_WHITELIST.contains(tl);
    }

    /** 表名已统一为小写 */
    private static boolean shouldSkipFullResetTable(String tl)
    {
        if (tl == null)
        {
            return false;
        }
        if (FULL_RESET_SKIP.contains(tl) || "sys_user".equals(tl))
        {
            return true;
        }
        if ("fd_category68".equals(tl))
        {
            return true;
        }
        if (tl.startsWith("scm_") || tl.startsWith("spd_"))
        {
            return true;
        }
        return false;
    }

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
                    if (shouldSkipFullResetTable(tl))
                    {
                        continue;
                    }
                    if (!isSafeIdentifier(table))
                    {
                        continue;
                    }
                    try
                    {
                        if (isFullResetTenantNullPreserveWhitelist(tl))
                        {
                            deleteTenantScopedRowsOnly(conn, table);
                        }
                        else
                        {
                            try (Statement st = conn.createStatement())
                            {
                                st.executeUpdate("DELETE FROM `" + table + "`");
                            }
                        }
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

    /**
     * 判断当前库中指定表是否包含某列（用于白名单删除前检测 tenant_id / customer_id）。
     */
    private boolean tableHasColumn(Connection conn, String tableName, String columnName) throws SQLException
    {
        if (!isSafeIdentifier(tableName) || !isSafeIdentifier(columnName))
        {
            return false;
        }
        String sql = "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
    }

    /**
     * 全库初始化 — 白名单表：仅删除「可识别为租户/客户数据」的行。
     * <p>使用 {@code TRIM(CAST(col AS CHAR))} 以同时兼容字符型与数值型主键列；NULL 与空串、纯空白均不删。</p>
     *
     * @return true 已执行 DELETE；false 表上无 tenant_id/customer_id，未执行任何删除
     */
    private boolean deleteTenantScopedRowsOnly(Connection conn, String table) throws SQLException
    {
        boolean hasTenant = tableHasColumn(conn, table, "tenant_id");
        boolean hasCustomer = tableHasColumn(conn, table, "customer_id");
        if (!hasTenant && !hasCustomer)
        {
            log.warn("全库初始化白名单表「{}」既无 tenant_id 也无 customer_id，跳过删除以免整表误删", table);
            return false;
        }
        String sql;
        if (hasTenant && hasCustomer)
        {
            sql = "DELETE FROM `" + table + "` WHERE (tenant_id IS NOT NULL AND TRIM(CAST(tenant_id AS CHAR)) <> '')"
                + " OR (customer_id IS NOT NULL AND TRIM(CAST(customer_id AS CHAR)) <> '')";
        }
        else if (hasTenant)
        {
            sql = "DELETE FROM `" + table + "` WHERE tenant_id IS NOT NULL AND TRIM(CAST(tenant_id AS CHAR)) <> ''";
        }
        else
        {
            sql = "DELETE FROM `" + table + "` WHERE customer_id IS NOT NULL AND TRIM(CAST(customer_id AS CHAR)) <> ''";
        }
        try (Statement st = conn.createStatement())
        {
            int n = st.executeUpdate(sql);
            log.info("全库初始化白名单表「{}」按租户/客户维度删除 {} 行", table, n);
        }
        return true;
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
