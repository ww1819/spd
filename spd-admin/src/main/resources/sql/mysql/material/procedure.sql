DROP PROCEDURE IF EXISTS `add_table_column`;
/
CREATE PROCEDURE `add_table_column`(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_type VARCHAR(64),
    IN p_column_comment VARCHAR(256),
    IN p_default_value VARCHAR(256)
)
add_column_block:
BEGIN
    DECLARE v_column_exists INT DEFAULT 0;
    SET p_default_value = IFNULL(p_default_value, NULL);
    SET @dynamic_sql = '';

    IF p_table_name IS NULL OR p_table_name = ''
        OR p_column_name IS NULL OR p_column_name = ''
        OR p_column_type IS NULL OR p_column_type = ''
        OR p_column_comment IS NULL OR p_column_comment = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：表名、字段名、字段类型、字段注释不能为空！';
    END IF;

    SELECT COUNT(*) INTO v_column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;

    IF v_column_exists > 0 THEN
        SELECT CONCAT('提示：字段【', p_column_name, '】已存在于表【', p_table_name, '】，无需重复添加，已跳过执行') AS 执行结果;
        LEAVE add_column_block;
    END IF;

    SET @dynamic_sql = CONCAT(
        'ALTER TABLE `', p_table_name, '` ADD COLUMN `', p_column_name, '` ', p_column_type, ' '
    );
    IF p_default_value IS NOT NULL AND p_default_value != '' THEN
        SET @dynamic_sql = CONCAT(@dynamic_sql, 'DEFAULT ', QUOTE(p_default_value), ' ');
    END IF;
    SET @dynamic_sql = CONCAT(@dynamic_sql, 'COMMENT ', QUOTE(p_column_comment));

    PREPARE stmt FROM @dynamic_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT CONCAT('成功：字段【', p_column_name, '】已添加到表【', p_table_name, '】') AS 执行结果;
    SET @dynamic_sql = '';
END;
/

-- =============================================================================
-- sp_hc_merge_sys_menu：合并重复 sys_menu（将 drop 侧关联迁移到 keep 后删除 drop）
-- 使用说明与示例见：spd/sql/maintenance/dedupe_hc_material_sys_menu.sql
-- =============================================================================
DROP PROCEDURE IF EXISTS `sp_hc_merge_sys_menu`;
/
CREATE PROCEDURE `sp_hc_merge_sys_menu`(
    IN p_keep BIGINT,
    IN p_drop BIGINT
)
proc_label:
BEGIN
    DECLARE v_child_cnt INT DEFAULT 0;

    IF p_keep IS NULL OR p_drop IS NULL OR p_keep = p_drop THEN
        LEAVE proc_label;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = p_keep) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'sp_hc_merge_sys_menu: keep menu_id 不存在';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = p_drop) THEN
        LEAVE proc_label;
    END IF;

    INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
    SELECT role_id, p_keep FROM sys_role_menu WHERE menu_id = p_drop;
    DELETE FROM sys_role_menu WHERE menu_id = p_drop;

    INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
    SELECT user_id, p_keep, tenant_id FROM sys_user_menu WHERE menu_id = p_drop;
    DELETE FROM sys_user_menu WHERE menu_id = p_drop;

    INSERT IGNORE INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
    SELECT tenant_id, p_keep, status, is_enabled, create_by, create_time
    FROM hc_customer_menu WHERE menu_id = p_drop;
    DELETE FROM hc_customer_menu WHERE menu_id = p_drop;

    INSERT IGNORE INTO sys_post_menu (post_id, menu_id, tenant_id)
    SELECT post_id, p_keep, tenant_id FROM sys_post_menu WHERE menu_id = p_drop;
    DELETE FROM sys_post_menu WHERE menu_id = p_drop;

    DELETE dup FROM hc_user_permission_menu dup
    INNER JOIN hc_user_permission_menu k
        ON dup.user_id = k.user_id AND dup.tenant_id = k.tenant_id AND k.menu_id = p_keep
    WHERE dup.menu_id = p_drop;
    UPDATE hc_user_permission_menu SET menu_id = p_keep WHERE menu_id = p_drop;

    UPDATE hc_customer_menu_status_log SET menu_id = p_keep WHERE menu_id = p_drop;
    UPDATE hc_customer_menu_period_log SET menu_id = p_keep WHERE menu_id = p_drop;

    SELECT COUNT(*) INTO v_child_cnt FROM sys_menu WHERE parent_id = p_drop;
    IF v_child_cnt > 0 THEN
        UPDATE sys_menu SET parent_id = p_keep WHERE parent_id = p_drop;
    END IF;

    DELETE FROM sys_menu WHERE menu_id = p_drop;
END;
/
