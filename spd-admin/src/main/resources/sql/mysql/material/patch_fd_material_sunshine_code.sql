-- fd_material 增加阳光平台编码列（追溯明细等查询依赖）
-- 可重复执行：列已存在则跳过
SET @__db := DATABASE();
SET @__exist := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'sunshine_code'
);
SET @__sql := IF(@__exist = 0,
  'ALTER TABLE fd_material ADD COLUMN sunshine_code varchar(100) DEFAULT NULL COMMENT ''阳光平台编码'' AFTER is_sunshine_procurement',
  'SELECT ''skip_fd_material_sunshine_code'' AS msg'
);
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;
