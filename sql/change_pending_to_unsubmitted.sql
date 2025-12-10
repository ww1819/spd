-- 将"待审核"状态改为"未提交"
-- 更新字典中所有"待审核"标签为"未提交"

-- 1. 将字典中所有"待审核"标签改为"未提交"（无论状态值是什么）
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_label = '待审核';

-- 2. 将字典中状态值1的标签改为"未提交"（无论原来是什么标签）
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_value = '1'
  AND dict_label != '未提交';

-- 3. 将字典中所有"未审核"标签改为"未提交"
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    dict_value = '0',
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_label = '未审核';

-- 4. 确保状态值0显示为"未提交"
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_value = '0'
  AND dict_label != '未提交';

-- 5. 如果状态值0不存在，则插入"未提交"状态
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
    '未提交状态（仅保存）'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data 
    WHERE dict_type = 'biz_status' 
      AND dict_value = '0'
);

-- 6. 如果状态值1不存在，则插入"未提交"状态（已提交但未审核）
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

-- 7. 删除重复的字典项（保留dict_code最小的）
DELETE d1 FROM sys_dict_data d1
INNER JOIN sys_dict_data d2 
WHERE d1.dict_type = 'biz_status'
  AND d2.dict_type = 'biz_status'
  AND d1.dict_value = d2.dict_value
  AND d1.dict_code > d2.dict_code;

