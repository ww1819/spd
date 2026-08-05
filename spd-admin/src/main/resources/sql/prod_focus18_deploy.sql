-- =============================================================================
-- 18类重点耗材维护 - 生产环境一次性脚本（可重复执行）
-- 用途：
--   1) 建表 fd_focus18（不存在才建）
--   2) 将重复菜单「财务分类」(原指向 financeCategory) 改为「18类重点耗材维护」
--   3) 同步按钮权限；停用本菜单下原财务分类专属按钮
--   4) 若库中没有可转换的重复菜单，则新建菜单及按钮，并把权限授给拥有「财务分类维护」的角色
-- 注意：
--   - 仅处理数据库；后端/前端代码仍需单独发布，否则接口仍会 404
--   - 「财务分类维护」菜单（component=foundation/financeCategory/index 且名称含维护）保持不变
--   - 明细数据请执行 mysql/material/focus18_data.sql（或依赖启动 SqlInitRunner 自动执行）
-- =============================================================================

-- ---------- 1. 业务表 ----------
CREATE TABLE IF NOT EXISTS fd_focus18 (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  parent_id bigint(20) DEFAULT 0 COMMENT '上级ID(0为根)',
  category varchar(100) DEFAULT NULL COMMENT '耗材类别',
  class_code varchar(100) DEFAULT NULL COMMENT '耗材分类代码',
  level1 varchar(200) DEFAULT NULL COMMENT '一级分类(学科/品类)',
  level2 varchar(200) DEFAULT NULL COMMENT '二级分类(用途/品目)',
  level3 varchar(200) DEFAULT NULL COMMENT '三级分类(部位/功能/品种)',
  generic_code varchar(100) DEFAULT NULL COMMENT '通用名代码',
  medical_generic_name varchar(200) DEFAULT NULL COMMENT '医保通用名',
  material_code varchar(100) DEFAULT NULL COMMENT '材质代码',
  material varchar(200) DEFAULT NULL COMMENT '材质',
  feature_code varchar(100) DEFAULT NULL COMMENT '特征代码',
  feature_param varchar(500) DEFAULT NULL COMMENT '特征参数',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  del_flag int(1) DEFAULT 0 COMMENT '删除标识(0正常 1删除)',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  delete_by varchar(64) DEFAULT NULL COMMENT '删除者',
  delete_time datetime DEFAULT NULL COMMENT '删除时间',
  tenant_id varchar(64) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_fd_focus18_tenant (tenant_id),
  KEY idx_fd_focus18_parent (tenant_id, parent_id),
  KEY idx_fd_focus18_class_code (tenant_id, class_code),
  KEY idx_fd_focus18_generic (tenant_id, generic_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='18类重点耗材维护';
-- ---------- 2. 定位基础资料父菜单 / 财务分类维护菜单 ----------
SET @foundation_root := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'M' AND (menu_name = '基础资料' OR menu_name LIKE '%基础资料%')
  ORDER BY menu_id LIMIT 1
);
SET @foundation_root := IFNULL(@foundation_root, 1);

SET @finance_keep_menu := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C'
    AND component = 'foundation/financeCategory/index'
    AND (menu_name LIKE '%维护%' OR menu_name = '财务分类维护')
  ORDER BY menu_id
  LIMIT 1
);

-- 可转换的重复菜单：同 component，且不是上面保留的「财务分类维护」
SET @focus18_menu := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C'
    AND (
      component = 'foundation/focus18/index'
      OR (
        component = 'foundation/financeCategory/index'
        AND (@finance_keep_menu IS NULL OR menu_id <> @finance_keep_menu)
        AND (menu_name = '财务分类' OR menu_name = '18类重点耗材维护')
      )
    )
  ORDER BY CASE WHEN component = 'foundation/focus18/index' THEN 0 ELSE 1 END, menu_id
  LIMIT 1
);

-- ---------- 3. 转换已有重复菜单，或新建菜单 ----------
UPDATE sys_menu
SET menu_name = '18类重点耗材维护',
    path = 'focus18',
    component = 'foundation/focus18/index',
    perms = 'foundation:focus18:list',
    icon = IFNULL(NULLIF(icon, ''), 'list'),
    menu_type = 'C',
    visible = '0',
    status = '0',
    remark = '18类重点耗材字典维护',
    update_by = 'admin',
    update_time = NOW()
WHERE @focus18_menu IS NOT NULL
  AND menu_id = @focus18_menu;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  '18类重点耗材维护',
  @foundation_root,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @foundation_root),
  'focus18',
  'foundation/focus18/index',
  NULL, 1, 0,
  'C', '0', '0',
  'foundation:focus18:list',
  'list',
  'admin', NOW(), 'admin', NOW(),
  '18类重点耗材字典维护'
FROM DUAL
WHERE @focus18_menu IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE menu_type = 'C' AND component = 'foundation/focus18/index'
  );

SET @focus18_menu := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND component = 'foundation/focus18/index'
  ORDER BY menu_id DESC
  LIMIT 1
);

-- ---------- 4. 按钮权限（存在则更新，不存在则新增） ----------
-- 查询
UPDATE sys_menu
SET menu_name = '18类重点查询', perms = 'foundation:focus18:query', status = '0', visible = '0', update_time = NOW()
WHERE parent_id = @focus18_menu AND (perms IN ('foundation:focus18:query', 'foundation:financeCategory:query') OR menu_name LIKE '%查询%');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '18类重点查询', @focus18_menu, 1, '#', '', 1, 0, 'F', '0', '0', 'foundation:focus18:query', '#', 'admin', NOW(), ''
FROM DUAL
WHERE @focus18_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @focus18_menu AND perms = 'foundation:focus18:query');

-- 新增
UPDATE sys_menu
SET menu_name = '18类重点新增', perms = 'foundation:focus18:add', status = '0', visible = '0', update_time = NOW()
WHERE parent_id = @focus18_menu AND (perms IN ('foundation:focus18:add', 'foundation:financeCategory:add') OR menu_name LIKE '%新增%');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '18类重点新增', @focus18_menu, 2, '#', '', 1, 0, 'F', '0', '0', 'foundation:focus18:add', '#', 'admin', NOW(), ''
FROM DUAL
WHERE @focus18_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @focus18_menu AND perms = 'foundation:focus18:add');

-- 修改
UPDATE sys_menu
SET menu_name = '18类重点修改', perms = 'foundation:focus18:edit', status = '0', visible = '0', update_time = NOW()
WHERE parent_id = @focus18_menu AND (perms IN ('foundation:focus18:edit', 'foundation:financeCategory:edit') OR menu_name LIKE '%修改%');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '18类重点修改', @focus18_menu, 3, '#', '', 1, 0, 'F', '0', '0', 'foundation:focus18:edit', '#', 'admin', NOW(), ''
FROM DUAL
WHERE @focus18_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @focus18_menu AND perms = 'foundation:focus18:edit');

-- 删除
UPDATE sys_menu
SET menu_name = '18类重点删除', perms = 'foundation:focus18:remove', status = '0', visible = '0', update_time = NOW()
WHERE parent_id = @focus18_menu AND (perms IN ('foundation:focus18:remove', 'foundation:financeCategory:remove') OR menu_name LIKE '%删除%');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '18类重点删除', @focus18_menu, 4, '#', '', 1, 0, 'F', '0', '0', 'foundation:focus18:remove', '#', 'admin', NOW(), ''
FROM DUAL
WHERE @focus18_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @focus18_menu AND perms = 'foundation:focus18:remove');

-- 导出
UPDATE sys_menu
SET menu_name = '18类重点导出', perms = 'foundation:focus18:export', status = '0', visible = '0', update_time = NOW()
WHERE parent_id = @focus18_menu AND (perms IN ('foundation:focus18:export', 'foundation:financeCategory:export') OR menu_name LIKE '%导出%');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '18类重点导出', @focus18_menu, 5, '#', '', 1, 0, 'F', '0', '0', 'foundation:focus18:export', '#', 'admin', NOW(), ''
FROM DUAL
WHERE @focus18_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @focus18_menu AND perms = 'foundation:focus18:export');

-- 停用本菜单下残留的财务分类专属按钮（更新简码/导入）
UPDATE sys_menu
SET status = '1', visible = '1', update_by = 'admin', update_time = NOW()
WHERE parent_id = @focus18_menu
  AND perms IN ('foundation:financeCategory:updateReferred', 'foundation:financeCategory:import');

-- ---------- 5. 角色授权：把拥有「财务分类维护」的角色同步授权到本菜单及按钮 ----------
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, m.menu_id
FROM sys_role_menu rm
INNER JOIN sys_menu keep_m ON keep_m.menu_id = rm.menu_id
INNER JOIN sys_menu m ON m.menu_id = @focus18_menu OR m.parent_id = @focus18_menu
WHERE @finance_keep_menu IS NOT NULL
  AND keep_m.menu_id = @finance_keep_menu
  AND @focus18_menu IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id
  );

-- 若没有「财务分类维护」参照，则给超级管理员角色(role_id=1)授权（存在才插）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE @focus18_menu IS NOT NULL
  AND EXISTS (SELECT 1 FROM sys_role WHERE role_id = 1)
  AND (m.menu_id = @focus18_menu OR m.parent_id = @focus18_menu)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu x WHERE x.role_id = 1 AND x.menu_id = m.menu_id
  );

-- ---------- 6. 校验查询（执行后可手工查看） ----------
-- SELECT menu_id, menu_name, parent_id, path, component, perms, status, visible
-- FROM sys_menu
-- WHERE component IN ('foundation/focus18/index', 'foundation/financeCategory/index')
--    OR parent_id = @focus18_menu
-- ORDER BY parent_id, order_num, menu_id;
--
-- SHOW CREATE TABLE fd_focus18;

-- ========== 附：上级菜单字段（若表已建可单独执行）==========
-- SET @db := DATABASE();
-- SET @exists := (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='fd_focus18' AND COLUMN_NAME='parent_id');
-- SET @sql := IF(@exists=0, 'ALTER TABLE fd_focus18 ADD COLUMN parent_id bigint(20) DEFAULT 0 COMMENT ''上级ID(0为根)'' AFTER id, ADD KEY idx_fd_focus18_parent (tenant_id, parent_id)', 'SELECT 1');
-- PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

