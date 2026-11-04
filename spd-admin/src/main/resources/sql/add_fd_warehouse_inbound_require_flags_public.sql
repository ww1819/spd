-- 仓库：入库明细必填开关（生产日期 / 有效期 / 批号）
-- 1=点亮必填，0=关闭；新增仓库默认全开；已有仓库回填为 1

-- 若库中有 add_table_column 过程可用下面三行；否则用其后的 ALTER 兜底
-- CALL add_table_column('fd_warehouse', 'require_produce_date', 'tinyint', '入库明细是否必填生产日期 0否1是', '1');
-- CALL add_table_column('fd_warehouse', 'require_expiry_date', 'tinyint', '入库明细是否必填有效期 0否1是', '1');
-- CALL add_table_column('fd_warehouse', 'require_batch_no', 'tinyint', '入库明细是否必填批号 0否1是', '1');

SET @db := DATABASE();

SET @exists := (SELECT COUNT(1) FROM information_schema.columns WHERE table_schema=@db AND table_name='fd_warehouse' AND column_name='require_produce_date');
SET @sql := IF(@exists=0,
  'ALTER TABLE fd_warehouse ADD COLUMN require_produce_date tinyint DEFAULT 1 COMMENT ''入库明细是否必填生产日期 0否1是''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(1) FROM information_schema.columns WHERE table_schema=@db AND table_name='fd_warehouse' AND column_name='require_expiry_date');
SET @sql := IF(@exists=0,
  'ALTER TABLE fd_warehouse ADD COLUMN require_expiry_date tinyint DEFAULT 1 COMMENT ''入库明细是否必填有效期 0否1是''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(1) FROM information_schema.columns WHERE table_schema=@db AND table_name='fd_warehouse' AND column_name='require_batch_no');
SET @sql := IF(@exists=0,
  'ALTER TABLE fd_warehouse ADD COLUMN require_batch_no tinyint DEFAULT 1 COMMENT ''入库明细是否必填批号 0否1是''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE fd_warehouse
SET require_produce_date = COALESCE(require_produce_date, 1),
    require_expiry_date = COALESCE(require_expiry_date, 1),
    require_batch_no = COALESCE(require_batch_no, 1)
WHERE del_flag IS NULL OR del_flag != 1;
