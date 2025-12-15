-- ============================================
-- 批量更新基础字典内所有产品的启用标识为"是"
-- ============================================
-- 将 fd_material 表中所有记录的 is_use 字段更新为 '1'（启用）
-- ============================================

-- 更新所有未删除的产品为启用状态
UPDATE fd_material 
SET is_use = '1' 
WHERE del_flag != 1;

-- 如果需要更新所有产品（包括已删除的），可以使用以下 SQL：
-- UPDATE fd_material SET is_use = '1';

-- 验证更新结果：查看更新后的启用状态统计
SELECT 
    is_use,
    CASE 
        WHEN is_use = '1' THEN '启用'
        WHEN is_use = '2' THEN '停用'
        ELSE '未知'
    END AS status_name,
    COUNT(*) AS count
FROM fd_material
WHERE del_flag != 1
GROUP BY is_use;

-- 查看更新后的总记录数
SELECT 
    COUNT(*) AS total_count,
    SUM(CASE WHEN is_use = '1' THEN 1 ELSE 0 END) AS enabled_count,
    SUM(CASE WHEN is_use = '2' THEN 1 ELSE 0 END) AS disabled_count
FROM fd_material
WHERE del_flag != 1;

-- ============================================
-- 执行完 SQL 后，刷新页面即可看到所有产品都显示为"是"
-- ============================================

