-- 修复采购计划状态字典和记录
-- 1. 将字典中的"未审核"改为"未提交"（如果存在）
-- 2. 更新特定记录的状态为"未提交"（0）

-- 如果字典中存在"未审核"标签，将其改为"未提交"
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    dict_value = '0',
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_label = '未审核';

-- 确保"未提交"状态存在
INSERT INTO sys_dict_data (
    dict_sort, 
    dict_label, 
    dict_value, 
    dict_type, 
    css_class, 
    list_class, 
    is_default, 
    status, 
    create_by, 
    create_time, 
    remark
) 
SELECT 
    0, 
    '未提交', 
    '0', 
    'biz_status', 
    '', 
    'info', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '未提交状态'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data 
    WHERE dict_type = 'biz_status' 
      AND dict_value = '0'
);

-- 确保"待审核"状态存在
INSERT INTO sys_dict_data (
    dict_sort, 
    dict_label, 
    dict_value, 
    dict_type, 
    css_class, 
    list_class, 
    is_default, 
    status, 
    create_by, 
    create_time, 
    remark
) 
SELECT 
    1, 
    '待审核', 
    '1', 
    'biz_status', 
    '', 
    'warning', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '待审核状态'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data 
    WHERE dict_type = 'biz_status' 
      AND dict_value = '1'
);

-- 修改表的默认值为'0'（未提交）
ALTER TABLE purchase_plan 
MODIFY COLUMN plan_status char(1) NOT NULL DEFAULT '0' COMMENT '计划状态（0未提交 1待审核 2已审核 3已执行 4已取消）';

-- 更新特定计划单号的状态为"未提交"（0）
-- 如果该计划只是保存了但未提交，应该设置为"未提交"状态
UPDATE purchase_plan 
SET plan_status = '0',
    update_time = NOW()
WHERE plan_no IN ('JH2025120700001', 'JH2025120700002')
  AND plan_status = '1';

