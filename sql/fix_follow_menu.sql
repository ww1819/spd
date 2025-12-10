-- 修正"跟台管理"菜单配置
-- 菜单ID: 1526
-- 问题：is_frame 为 1（是外链），应该是 0（否）；perms 为空，应该填写权限标识

-- 修正菜单配置
UPDATE sys_menu 
SET 
    is_frame = '0',                      -- 是否外链：否（修正：从 1 改为 0）
    perms = 'gzOrder:follow:list',       -- 权限字符：gzOrder:follow:list（新增）
    update_time = NOW()                  -- 更新时间
WHERE menu_id = 1526;

-- 验证更新结果
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    query AS '路由参数',
    perms AS '权限字符',
    icon AS '菜单图标',
    is_frame AS '是否外链(0否1是)',
    is_cache AS '是否缓存(0缓存1不缓存)',
    menu_type AS '菜单类型(M目录C菜单F按钮)',
    visible AS '显示状态(0显示1隐藏)',
    status AS '菜单状态(0正常1停用)',
    update_time AS '更新时间'
FROM sys_menu 
WHERE menu_id = 1526;

-- 对比其他高值管理子菜单配置，确保一致性
SELECT 
    menu_id,
    menu_name,
    path,
    component,
    perms,
    icon,
    is_frame,
    menu_type,
    order_num
FROM sys_menu 
WHERE parent_id = 1064 
  AND menu_type = 'C'
ORDER BY order_num;

