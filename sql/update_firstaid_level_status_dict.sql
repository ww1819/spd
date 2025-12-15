-- 更新急救类型字典数据
-- 字典类型：firstaid_level_status

-- 1. 确保字典类型存在（如果不存在则创建）
INSERT INTO sys_dict_type (
    dict_name, 
    dict_type, 
    status, 
    create_by, 
    create_time, 
    remark
) 
SELECT 
    '急救类型', 
    'firstaid_level_status', 
    '0', 
    'admin', 
    NOW(), 
    '急救类型列表'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_type WHERE dict_type = 'firstaid_level_status'
);

-- 2. 先删除旧的急救类型字典数据
DELETE FROM sys_dict_data WHERE dict_type = 'firstaid_level_status';

-- 3. 插入新的急救类型字典数据
-- 基础护理类
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
) VALUES (
    1, 
    '基础护理类', 
    '1', 
    'firstaid_level_status', 
    '', 
    'info', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '基础护理类'
);

-- 专科专用类
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
) VALUES (
    2, 
    '专科专用类', 
    '2', 
    'firstaid_level_status', 
    '', 
    'warning', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '专科专用类'
);

-- 监测辅助类
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
) VALUES (
    3, 
    '监测辅助类', 
    '3', 
    'firstaid_level_status', 
    '', 
    'primary', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '监测辅助类'
);

-- 一次性医用包
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
) VALUES (
    4, 
    '一次性医用包', 
    '4', 
    'firstaid_level_status', 
    '', 
    'success', 
    'N', 
    '0', 
    'admin', 
    NOW(), 
    '一次性医用包'
);

-- 4. 查询结果，确认更新是否成功
SELECT dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, remark
FROM sys_dict_data
WHERE dict_type = 'firstaid_level_status'
ORDER BY dict_sort;

