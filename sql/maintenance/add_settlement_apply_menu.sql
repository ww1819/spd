-- ========== 结算申请（settlement/apply）菜单与按钮权限 ==========
-- 原因：结算申请页「生成结算单」调用 POST /settlement/settlement，需 settlement:apply:add；
--       仓库脚本 material/menu.sql 中未包含 settlement:apply:*，角色无法分配导致后端 403「没有权限」。
-- 依赖：parent_id=1065「财务管理」已存在（与 material/menu.sql 一致）。
-- 执行后：在「系统管理 → 角色管理」中为相关角色勾选本菜单及按钮；若租户使用 hc_customer_menu 请同步。

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(3180, '结算申请', 1065, 5, 'apply', 'settlement/apply/index', NULL,
 1, 0, 'C', '0', '0', 'settlement:apply:list', 'documentation',
 'admin', NOW(), '1', NOW(), '耗材结算申请（生成结算单等）',
 '0', '1'),
(3181, '结算申请查询', 3180, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'settlement:apply:query', '#',
 'admin', NOW(), '1', NOW(), '详情/查询',
 '0', '1'),
(3182, '结算申请新增', 3180, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'settlement:apply:add', '#',
 'admin', NOW(), '1', NOW(), '新增与「生成结算单」提交',
 '0', '1'),
(3183, '结算申请修改', 3180, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'settlement:apply:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(3184, '结算申请删除', 3180, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'settlement:apply:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(3185, '结算申请导出', 3180, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'settlement:apply:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
