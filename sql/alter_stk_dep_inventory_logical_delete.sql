-- 为 stk_dep_inventory 增加逻辑删除字段（del_flag/delete_by/delete_time）
-- 注意：请在对应数据库/schema 下执行（如需：use aspt;）

-- 1) 添加字段
ALTER TABLE stk_dep_inventory
  ADD COLUMN del_flag INT NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  ADD COLUMN delete_by VARCHAR(64) NULL COMMENT '删除者',
  ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间';

-- 2) 兜底：历史数据 NULL 置 0
UPDATE stk_dep_inventory SET del_flag = 0 WHERE del_flag IS NULL;

-- 3) 索引（加速常用过滤）
CREATE INDEX idx_stk_dep_inventory_del_flag ON stk_dep_inventory(del_flag);

