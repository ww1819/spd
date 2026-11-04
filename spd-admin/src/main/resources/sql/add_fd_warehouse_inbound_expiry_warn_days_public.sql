-- 仓库：入库有效期临近提示天数（天）；默认 60
-- 到货验收保存/审核时若剩余有效期小于该值则弹窗确认后继续
--
-- 【生产 / DBeaver 注意】
-- 1. 先选中库 aspt（或当前业务库），确认当前库：SELECT DATABASE();
-- 2. 先单独执行下面「第一步」整段；成功后再执行「第二步」
-- 3. 若第一步报 Duplicate column name，说明列已存在，直接做第二步即可

-- ========== 第一步：加列（只执行本段）==========
ALTER TABLE fd_warehouse
  ADD COLUMN inbound_expiry_warn_days int DEFAULT 60 COMMENT '入库有效期临近提示天数';

-- ========== 第二步：回填默认值（第一步成功后再执行）==========
UPDATE fd_warehouse
SET inbound_expiry_warn_days = COALESCE(inbound_expiry_warn_days, 60)
WHERE del_flag IS NULL OR del_flag != 1;
