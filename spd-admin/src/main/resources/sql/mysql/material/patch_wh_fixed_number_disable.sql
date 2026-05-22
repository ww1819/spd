-- 仓库定数监测：停用功能字段（在 aspt 库执行；若某列已存在会报错，可跳过该条）
-- 建议逐条执行，已存在的列不要重复 ADD

ALTER TABLE wh_fixed_number
  ADD COLUMN enable_status char(1) NOT NULL DEFAULT '0' COMMENT '启用状态（0启用 1停用）' AFTER delete_time;

ALTER TABLE wh_fixed_number
  ADD COLUMN disable_by varchar(64) DEFAULT NULL COMMENT '停用人' AFTER enable_status;

ALTER TABLE wh_fixed_number
  ADD COLUMN disable_time datetime DEFAULT NULL COMMENT '停用时间' AFTER disable_by;

UPDATE wh_fixed_number SET enable_status = '0' WHERE enable_status IS NULL OR enable_status = '';
