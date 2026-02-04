-- 核心逻辑：存在则删除存储过程，无则直接执行后续创建（避免重复创建报错）
DROP PROCEDURE IF EXISTS `add_table_column`;

-- 定义存储过程（保留原所有逻辑）
DELIMITER //  -- 临时修改语句结束符，避免存储过程内;提前终止定义
CREATE PROCEDURE `add_table_column`(
    IN p_table_name VARCHAR(64),  -- 入参：表名（MySQL标识符最大64字符）
    IN p_column_name VARCHAR(64), -- 入参：字段名
    IN p_column_type VARCHAR(64), -- 入参：字段类型（如VARCHAR(50)、INT、DATETIME）
    IN p_column_comment VARCHAR(256), -- 入参：字段注释
    IN p_default_value VARCHAR(256) -- 可选入参：字段默认值，低版本兼容写法
)
add_column_block:
BEGIN
    -- 声明变量：存储字段存在性标识（0=不存在，1=存在）
    DECLARE v_column_exists INT DEFAULT 0;
    -- 初始化默认值为NULL，实现可选参数效果
    SET p_default_value = IFNULL(p_default_value, NULL);
    -- 声明动态SQL变量（会话级变量，避免存储过程内变量作用域问题）
    SET @dynamic_sql = '';

    -- 1. 基础非空校验：核心参数不能为空
    IF p_table_name IS NULL OR p_table_name = ''
        OR p_column_name IS NULL OR p_column_name = ''
        OR p_column_type IS NULL OR p_column_type = ''
        OR p_column_comment IS NULL OR p_column_comment = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：表名、字段名、字段类型、字段注释不能为空！';
    END IF;

    -- 2. 查询字段是否已存在（基于MySQL系统表，匹配当前会话数据库）
    SELECT COUNT(*) INTO v_column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;

    -- 字段已存在则返回提示信息，跳过后续执行（无异常）
    IF v_column_exists > 0 THEN
        SELECT CONCAT('提示：字段【', p_column_name, '】已存在于表【', p_table_name, '】，无需重复添加，已跳过执行') AS 执行结果;
        LEAVE add_column_block; -- 合法跳出BEGIN...END块
    END IF;

-- 3. 拼接动态添加字段的SQL语句（保留`反引号`防标识符冲突，QUOTE()防SQL注入）
    SET @dynamic_sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD COLUMN `', p_column_name, '` ', p_column_type, ' '
                       );

-- 拼接默认值：非空则添加DEFAULT子句，QUOTE()自动转义单引号/特殊字符
    IF p_default_value IS NOT NULL AND p_default_value != '' THEN
        SET @dynamic_sql = CONCAT(@dynamic_sql, 'DEFAULT ', QUOTE(p_default_value), ' ');
    END IF;

-- 拼接字段注释
    SET @dynamic_sql = CONCAT(
            @dynamic_sql, 'COMMENT ', QUOTE(p_column_comment)
                       );

-- 4. 预处理并执行动态SQL（MySQL标准方式，避免动态语法解析问题）
    PREPARE stmt FROM @dynamic_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

-- 执行成功提示
    SELECT CONCAT('成功：字段【', p_column_name, '】已添加到表【', p_table_name, '】') AS 执行结果;

-- 清空会话变量，避免残留影响后续执行
    SET @dynamic_sql = '';
END add_column_block //
DELIMITER ;  -- 恢复MySQL默认的语句结束符为;
