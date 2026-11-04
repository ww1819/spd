-- 仓库：入库有效期临近提示天数（天）；默认 60；到货验收保存/审核时若剩余有效期小于该值则弹窗确认后继续

SET @db := DATABASE();

SET @exists := (SELECT COUNT(1) FROM information_schema.columns WHERE table_schema=@db AND table_name='fd_warehouse' AND column_name='inbound_expiry_warn_days');
SET @sql := IF(@exists=0,
  'ALTER TABLE fd_warehouse ADD COLUMN inbound_expiry_warn_days int DEFAULT 60 COMMENT ''入库有效期临近提示天数''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE fd_warehouse
SET inbound_expiry_warn_days = COALESCE(inbound_expiry_warn_days, 60)
WHERE del_flag IS NULL OR del_flag != 1;
