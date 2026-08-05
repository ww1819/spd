-- 18类重点耗材：增加上级菜单（parent_id）
-- 可重复执行

SET @db := DATABASE();
SET @exists := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_focus18' AND COLUMN_NAME = 'parent_id'
);
SET @sql := IF(@exists = 0,
  'ALTER TABLE fd_focus18 ADD COLUMN parent_id bigint(20) DEFAULT 0 COMMENT ''上级ID(0为根)'' AFTER id, ADD KEY idx_fd_focus18_parent (tenant_id, parent_id)',
  'SELECT ''parent_id already exists'' AS msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
