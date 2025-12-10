-- 更新采购计划状态字典
-- 添加"未提交"状态，确保"待审核"状态正确

-- 如果字典中存在"未审核"标签，将其改为"未提交"
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    dict_value = '0',
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_label = '未审核';

-- 将状态值1的标签改为"未提交"（无论原来是"待审核"还是"已提交"）
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_value = '1' 
  AND (dict_label = '待审核' OR dict_label = '已提交');

-- 确保状态值1显示为"未提交"（如果不存在则插入）
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
    '未提交', 
    '1', 
    'biz_status', 
    '', 
    'info', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '未提交状态（已提交但未审核）'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data 
    WHERE dict_type = 'biz_status' 
      AND dict_value = '1'
);

-- 如果不存在"未提交"状态，则插入
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

