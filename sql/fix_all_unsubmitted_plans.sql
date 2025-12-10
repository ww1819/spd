-- 修复所有已保存但未提交的计划状态
-- 将所有状态为'1'（待审核）但实际只是保存未提交的计划改为'0'（未提交）
-- 注意：这个脚本会将所有状态为'1'的计划改为'0'，请根据实际情况调整

-- 修改表的默认值为'0'（未提交）
ALTER TABLE purchase_plan 
MODIFY COLUMN plan_status char(1) NOT NULL DEFAULT '0' COMMENT '计划状态（0未提交 1待审核 2已审核 3已执行 4已取消）';

-- 更新所有状态为'1'且没有审核信息的计划为"未提交"（0）
-- 如果计划有审核信息（audit_by不为空），说明已经提交过，不应该改为未提交
UPDATE purchase_plan 
SET plan_status = '0',
    update_time = NOW()
WHERE plan_status = '1'
  AND (audit_by IS NULL OR audit_by = '')
  AND (audit_date IS NULL);

