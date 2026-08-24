-- 一次性修复：消息提醒权限字段（登录/授权依赖）
-- 在 Navicat / DBeaver / mysql 客户端执行本文件，或重启后端（column.sql 已含相同 CALL）

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
            SET MESSAGE_TEXT = 'table/column/type/comment required';
    END IF;
    SELECT COUNT(*) INTO v_column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;
    IF v_column_exists > 0 THEN
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
    SET @dynamic_sql = '';
END;
/
CALL add_table_column('sys_user', 'message_reminder_keys', 'varchar(128)', 'message reminder keys warehouse department data', NULL);
/
CALL add_table_column('sys_post', 'message_reminder_keys', 'varchar(128)', 'message reminder keys warehouse department data', NULL);
/
CALL add_table_column('sys_user', 'message_reminder_popup_keys', 'varchar(128)', 'message reminder login popup keys warehouse department data', NULL);
/
CALL add_table_column('sys_post', 'message_reminder_popup_keys', 'varchar(128)', 'message reminder login popup keys warehouse department data', NULL);
/
