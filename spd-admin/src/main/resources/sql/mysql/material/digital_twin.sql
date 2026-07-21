-- ========== 数字孪生监控大屏：货位五区字段 + 菜单 ==========
-- 可单独执行；依赖 add_table_column 存储过程（见 material/column.sql）
-- 注意：menu_id=3890（现网 3401 已占用为「入退货申请」，不可复用）

CALL add_table_column('fd_location', 'zone_type', 'varchar(32)', '五区类型PENDING_CHECK/QUALIFIED/UNQUALIFIED/RETURN/PENDING_SHIP', 'QUALIFIED');
/
CALL add_table_column('fd_location', 'shelf_code', 'varchar(64)', '货架编码', NULL);
/
CALL add_table_column('fd_location', 'layer_no', 'int', '层号', NULL);
/
CALL add_table_column('fd_location', 'slot_no', 'int', '格口号', NULL);
/
CALL add_table_column('fd_location', 'pos_x', 'decimal(12,2)', '平面X坐标米', NULL);
/
CALL add_table_column('fd_location', 'pos_y', 'decimal(12,2)', '平面Y坐标米', NULL);
/
CALL add_table_column('fd_location', 'pos_z', 'decimal(12,2)', '高度Z坐标米', NULL);
/
CALL add_table_column('fd_location', 'capacity', 'decimal(18,4)', '容量', NULL);
/

UPDATE fd_location SET zone_type = 'PENDING_CHECK'
WHERE (zone_type IS NULL OR zone_type = '') AND (location_name LIKE '%待验%' OR location_name LIKE '%验收%');
/
UPDATE fd_location SET zone_type = 'UNQUALIFIED'
WHERE (zone_type IS NULL OR zone_type = '') AND (location_name LIKE '%不合格%' OR location_name LIKE '%隔离%');
/
UPDATE fd_location SET zone_type = 'RETURN'
WHERE (zone_type IS NULL OR zone_type = '') AND location_name LIKE '%退货%';
/
UPDATE fd_location SET zone_type = 'PENDING_SHIP'
WHERE (zone_type IS NULL OR zone_type = '') AND (location_name LIKE '%待发%' OR location_name LIKE '%发货%');
/
UPDATE fd_location SET zone_type = 'QUALIFIED'
WHERE zone_type IS NULL OR zone_type = '';
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3890, '数字孪生监控大屏', 1066, 5, 'digitalTwin', 'datacenter/digitalTwin/index', NULL,
  1, 0, 'C', '0', '0', 'datacenter:digitalTwin:list', 'monitor',
  'admin', NOW(), '', NULL, '五区三色数字孪生监控大屏',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='datacenter:digitalTwin:list')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3890)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);
/

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 3890);
/

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, 3890, '0', '1', 'admin', NOW()
FROM sb_customer c
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = 3890
  );
/

-- 已开通「数据中心」的客户补授权
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT DISTINCT h.tenant_id, 3890, '0', '1', 'admin', NOW()
FROM hc_customer_menu h
WHERE h.menu_id = 1066
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu x
    WHERE x.tenant_id = h.tenant_id AND x.menu_id = 3890
  );
/
