/*
 * 存储过程：add_table_column
 * 功能：安全地为指定数据表添加新字段，避免重复添加
 * 特点：1. 参数合法性校验 2. 字段存在性检查 3. 支持默认值设置 4. 友好的执行结果提示
 */
CREATE PROCEDURE IF NOT EXISTS `add_table_column`(
    IN p_table_name VARCHAR(64),      -- 输入参数：目标表名（必填）
    IN p_column_name VARCHAR(64),     -- 输入参数：要添加的字段名（必填）
    IN p_column_type VARCHAR(64),     -- 输入参数：字段类型（如int, varchar(255)等，必填）
    IN p_column_comment VARCHAR(256), -- 输入参数：字段注释（必填）
    IN p_default_value VARCHAR(256)   -- 输入参数：字段默认值（可选，不传则为NULL）
)
add_column_block:  -- 定义代码块标签，用于提前退出
BEGIN
    -- 声明局部变量：用于存储字段是否存在的标识（0=不存在，1=存在）
    DECLARE v_column_exists INT DEFAULT 0;

    -- 处理默认值参数：如果传入NULL则显式设置为NULL
    SET p_default_value = IFNULL(p_default_value, NULL);

    -- 初始化动态SQL变量
    SET @dynamic_sql = '';

    -- 第一步：参数合法性校验 - 检查必填参数是否为空
    IF p_table_name IS NULL OR p_table_name = ''
        OR p_column_name IS NULL OR p_column_name = ''
        OR p_column_type IS NULL OR p_column_type = ''
        OR p_column_comment IS NULL OR p_column_comment = '' THEN
        -- 抛出自定义异常，提示必填参数不能为空
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：表名、字段名、字段类型、字段注释为必填参数，不能为空！';
END IF;

    -- 第二步：检查字段是否已存在
SELECT COUNT(*) INTO v_column_exists
FROM information_schema.COLUMNS  -- 系统表：存储所有表的字段信息
WHERE TABLE_SCHEMA = DATABASE()  -- 当前数据库
  AND TABLE_NAME = p_table_name  -- 目标表名
  AND COLUMN_NAME = p_column_name; -- 要检查的字段名

-- 如果字段已存在，提示并退出存储过程
IF v_column_exists > 0 THEN
SELECT CONCAT('提示：字段【', p_column_name, '】已存在于表【', p_table_name, '】，无需重复添加') AS 执行结果;
LEAVE add_column_block;  -- 退出代码块，结束存储过程执行
END IF;

    -- 第三步：拼接动态SQL语句（ALTER TABLE ADD COLUMN）
    SET @dynamic_sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD COLUMN `', p_column_name, '` ', p_column_type, ' '
                       );

    -- 如果传入了默认值，拼接DEFAULT子句（使用QUOTE函数防止SQL注入）
    IF p_default_value IS NOT NULL AND p_default_value != '' THEN
        SET @dynamic_sql = CONCAT(@dynamic_sql, 'DEFAULT ', QUOTE(p_default_value), ' ');
END IF;

    -- 拼接字段注释（使用QUOTE函数处理特殊字符）
    SET @dynamic_sql = CONCAT(@dynamic_sql, 'COMMENT ', QUOTE(p_column_comment));

    -- 第四步：执行动态SQL
PREPARE stmt FROM @dynamic_sql;  -- 预处理动态SQL
EXECUTE stmt;                    -- 执行预处理语句
DEALLOCATE PREPARE stmt;         -- 释放预处理资源

-- 提示字段添加成功
SELECT CONCAT('成功：字段【', p_column_name, '】已成功添加到表【', p_table_name, '】') AS 执行结果;

-- 清空动态SQL变量，避免残留
SET @dynamic_sql = '';
END;
/

-- 调用示例：为bas_apply表添加del_flag字段（int类型，注释为删除标志，默认值0）
CALL add_table_column('bas_apply', 'del_flag', 'int', '删除标志', 0);
/


-- 为sys_user表添加customer_id字段
CALL add_table_column('sys_user', 'customer_id', 'char(36)', '客户ID(UUID7)，归属客户/租户', NULL);
/


-- 为sb_customer表添加planned_disable_time字段
CALL add_table_column('sb_customer', 'planned_disable_time', 'datetime', '计划停用时间，到达后租户无法使用', NULL);
/

-- sb_customer 租户枚举键，与代码内 TenantEnum 关联，用于条件分支
CALL add_table_column('sb_customer', 'tenant_key', 'varchar(64)', '租户枚举键(TenantEnum.name)，与代码内租户列表关联', NULL);
/

-- 客户启停用时间段：end_time 允许 NULL，表示当前未结束的时段
CALL add_table_column('sb_customer_period_log', 'end_time', 'datetime', '结束时间，NULL表示当前未结束', NULL);
/

-- 客户菜单功能管理：sb_customer_menu 增加 status（0正常 1暂停）、is_enabled（0关闭 1开启）
CALL add_table_column('sb_customer_menu', 'status', 'char(1)', '暂停状态（0正常 1暂停，仅客户菜单功能管理操作）', '0');
/
CALL add_table_column('sb_customer_menu', 'is_enabled', 'char(1)', '是否开启（0关闭 1开启），客户管理取消功能时改为0', '1');
/

-- 设备菜单表：是否仅平台管理功能（1则客户分配/工作组/用户权限中不展示）
CALL add_table_column('sb_menu', 'is_platform_only', 'char(1)', '是否仅平台管理功能（1是，客户分配/工作组/用户权限中不展示）', '0');
/
-- 设备菜单表：是否默认对客户开放、逻辑删除标志
CALL add_table_column('sb_menu', 'default_open_to_customer', 'char(1)', '是否默认对客户开放（1是，设备功能重置时授权给客户、管理员组、管理员用户）', '0');
/
CALL add_table_column('sb_menu', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/

-- 设备角色与菜单关联表：客户ID（归属租户，平台角色可为空）
CALL add_table_column('sb_role_menu', 'customer_id', 'char(36)', '客户ID(UUID7)，归属客户/租户', NULL);
/

-- sb_work_group_menu 逻辑删除标志（已有表若缺列可执行）
CALL add_table_column('sb_work_group_menu', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('sb_work_group_menu', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('sb_work_group_menu', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/

-- sb_work_group_user 逻辑删除标志
CALL add_table_column('sb_work_group_user', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/
-- 已有数据：将曾通过 delete_time 标记删除的记录同步为 del_flag='1'
UPDATE sb_work_group_user SET del_flag = '1' WHERE delete_time IS NOT NULL AND (del_flag IS NULL OR del_flag = '0');
/

-- sb_user_permission_* 三表：逻辑删除标志 del_flag（与 delete_by/delete_time 一并使用）
CALL add_table_column('sb_user_permission_menu', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/
CALL add_table_column('sb_user_permission_warehouse', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/
CALL add_table_column('sb_user_permission_dept', 'del_flag', 'char(1)', '删除标志（0正常 1删除）', '0');
/
-- 已有数据：将曾通过 delete_time 标记删除的记录同步为 del_flag='1'
UPDATE sb_user_permission_menu SET del_flag = '1' WHERE delete_time IS NOT NULL;
/
UPDATE sb_user_permission_warehouse SET del_flag = '1' WHERE delete_time IS NOT NULL;
/
UPDATE sb_user_permission_dept SET del_flag = '1' WHERE delete_time IS NOT NULL;
/

-- 数据：将客户 hengsui-third-001 名下在 sb_customer_menu 中的菜单全部设为「默认对客户开放」
UPDATE sb_menu SET default_open_to_customer = '1'
WHERE menu_id IN (
  SELECT menu_id FROM sb_customer_menu
  WHERE customer_id = (SELECT customer_id FROM sb_customer WHERE customer_code = 'hengsui-third-001' AND delete_time IS NULL LIMIT 1)
    AND delete_time IS NULL
);
/

-- 客户68分类表 parent_id 改为本表主键id（仅当表中 parent_id 当前为 bigint 时执行以下一段）
-- 若表是按新 table.sql 建的（parent_id 已是 char(36)），请跳过下面三条语句
-- ALTER TABLE sb_customer_category68 ADD COLUMN parent_id_new CHAR(36) DEFAULT NULL COMMENT '父分类ID(本表主键)';
-- UPDATE sb_customer_category68 c1 INNER JOIN sb_customer_category68 c2 ON c2.customer_id = c1.customer_id AND c2.ref_category68_id = c1.parent_id SET c1.parent_id_new = c2.id WHERE c1.parent_id IS NOT NULL AND c1.parent_id != 0;
-- ALTER TABLE sb_customer_category68 DROP COLUMN parent_id, CHANGE parent_id_new parent_id CHAR(36) DEFAULT NULL COMMENT '父分类ID(本表主键id，对应父记录)';
/