-- 采购计划状态精简为：0未提交 1待审核 2已审核（移除已执行、已取消）
UPDATE purchase_plan SET plan_status = '2' WHERE plan_status IN ('3', '4');

DELETE FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value IN ('3', '4');

UPDATE sys_dict_type
SET remark = '计划状态（0未提交 1待审核 2已审核）'
WHERE dict_type = 'plan_status';

UPDATE sys_dict_data SET remark = '采购计划状态：未提交' WHERE dict_type = 'plan_status' AND dict_value = '0';
UPDATE sys_dict_data SET remark = '采购计划状态：待审核' WHERE dict_type = 'plan_status' AND dict_value = '1';
UPDATE sys_dict_data SET remark = '采购计划状态：已审核' WHERE dict_type = 'plan_status' AND dict_value = '2';
