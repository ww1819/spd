-- 修正"备货退库"菜单配置
-- 菜单ID: 1197
-- 问题：当前配置使用了错误的 path、component 和 perms（使用了 goodsApply 而不是 goodsAudit）

-- 修正菜单配置
UPDATE sys_menu 
SET 
    path = 'goodsAudit',                      -- 路由地址：goodsAudit（修正：从 goodsApply 改为 goodsAudit）
    component = 'gzOrder/goodsAudit/index',   -- 组件路径：gzOrder/goodsAudit/index（修正：从 gzOrder/goodsApply/index 改为 gzOrder/goodsAudit/index）
    perms = 'gzOrder:goodsAudit:list',        -- 权限字符：gzOrder:goodsAudit:list（修正：从 gzOrder:goodsApply:list 改为 gzOrder:goodsAudit:list）
    update_time = NOW()                       -- 更新时间
WHERE menu_id = 1197;

-- 验证更新结果
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    query,
    perms,
    icon,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    update_time
FROM sys_menu 
WHERE menu_id = 1197;

-- 对比"备货出库"和"备货退库"菜单配置，确保一致性
SELECT 
    menu_id,
    menu_name,
    path,
    component,
    perms,
    icon,
    order_num
FROM sys_menu 
WHERE menu_id IN (1196, 1197)
ORDER BY menu_id;

