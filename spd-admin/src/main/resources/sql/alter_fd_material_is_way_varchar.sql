-- 耗材档案「储存方式」改为自由文本，放宽字段长度（原 char(4) 过短）
-- 执行库：业务库（如 aspt）

ALTER TABLE fd_material
  MODIFY COLUMN is_way varchar(100) DEFAULT NULL COMMENT '储存方式';
