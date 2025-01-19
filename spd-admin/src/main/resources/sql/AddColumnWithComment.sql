DELIMITER $$

CREATE PROCEDURE AddColumnAndComment(IN tableName VARCHAR(100),
                                     IN columnName VARCHAR(50),
                                     IN columnType VARCHAR(50),
                                     IN columnComment TEXT)
BEGIN
    -- 动态构造ALTER TABLE语句来添加字段
    SET @sql = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', columnName, ' ', columnType);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查字段是否添加成功，然后添加注释（MySQL 5.7.6及以上版本支持列注释）
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE TABLE_NAME = tableName AND COLUMN_NAME = columnName) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Failed to add column.';
ELSE
        SET @commentSql = CONCAT('ALTER TABLE ', tableName, ' MODIFY COLUMN ', columnName, ' ', columnType, ' COMMENT ''', columnComment, '''');
PREPARE commentStmt FROM @commentSql;
EXECUTE commentStmt;
DEALLOCATE PREPARE commentStmt;
END IF;
END$$

DELIMITER ;
