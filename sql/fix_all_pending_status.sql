-- 修复所有"待审核"状态为"未提交"
-- 确保字典和记录都正确更新

-- 1. 强制更新：将所有"待审核"标签改为"未提交"（无论状态值是什么）
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_label IN ('待审核', '待审核状态');

-- 2. 强制更新：将状态值1的标签改为"未提交"（无论原来是什么标签，包括"待审核"）
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

-- 5. 确保状态值1显示为"未提交"（已提交但未审核）
UPDATE sys_dict_data 
SET dict_label = '未提交', 
    list_class = 'info',
    update_time = NOW()
WHERE dict_type = 'biz_status' 
  AND dict_value = '1'
  AND dict_label != '未提交';

-- 6. 如果状态值0不存在，则插入"未提交"状态
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

-- 7. 如果状态值1不存在，则插入"未提交"状态（已提交但未审核）
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

-- 8. 删除所有重复的字典项，只保留每个状态值的第一条记录
DELETE d1 FROM sys_dict_data d1
INNER JOIN (
    SELECT MIN(dict_code) as min_code, dict_value
    FROM sys_dict_data
    WHERE dict_type = 'biz_status'
    GROUP BY dict_value
) d2 ON d1.dict_value = d2.dict_value
WHERE d1.dict_type = 'biz_status'
  AND d1.dict_code > d2.min_code;

-- 9. 查询结果，确认更新是否成功
SELECT dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, remark
FROM sys_dict_data
WHERE dict_type = 'biz_status'
ORDER BY dict_value;

