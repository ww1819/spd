-- ============================================
-- 科室批量消耗功能 - 数据库表结构和菜单配置
-- 创建时间：2025-01-15
-- ============================================

-- ============================================
-- 一、创建数据库表结构
-- ============================================

-- 1. 创建主表 t_hc_ks_xh（科室批量消耗主表）
DROP TABLE IF EXISTS `t_hc_ks_xh`;
CREATE TABLE `t_hc_ks_xh` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `consume_bill_no` varchar(50) DEFAULT NULL COMMENT '消耗单号',
  `consume_bill_date` date DEFAULT NULL COMMENT '消耗日期',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint(20) DEFAULT NULL COMMENT '科室ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `consume_bill_status` int(11) DEFAULT 1 COMMENT '单据状态（1=待审核，2=已审核）',
  `total_amount` decimal(18,2) DEFAULT 0.00 COMMENT '总金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核日期',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0=正常，1=已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_consume_bill_no` (`consume_bill_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_consume_bill_date` (`consume_bill_date`),
  KEY `idx_consume_bill_status` (`consume_bill_status`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗主表';

-- 2. 创建明细表 t_hc_ks_xh_entry（科室批量消耗明细表）
DROP TABLE IF EXISTS `t_hc_ks_xh_entry`;
CREATE TABLE `t_hc_ks_xh_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paren_id` bigint(20) NOT NULL COMMENT '父表ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '耗材ID',
  `unit_price` decimal(18,2) DEFAULT 0.00 COMMENT '单价',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `price` decimal(18,2) DEFAULT 0.00 COMMENT '价格',
  `amt` decimal(18,2) DEFAULT 0.00 COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_numer` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0=正常，1=已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗明细表';

-- ============================================
-- 二、查询科室消耗菜单ID（作为父菜单）
-- ============================================
-- 执行以下SQL查询科室消耗菜单的ID
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name LIKE '%科室消耗%' OR menu_name LIKE '%消耗%';
-- 假设查询到的菜单ID为：1410（根据实际情况修改）

-- ============================================
-- 三、配置菜单和权限
-- ============================================

-- 3.1 插入主菜单（科室批量消耗）
-- 注意：需要先查询科室消耗菜单的ID，假设为1410，order_num根据实际情况调整
INSERT INTO `sys_menu` (
  `menu_id`, 
  `menu_name`, 
  `parent_id`, 
  `order_num`, 
  `path`, 
  `component`, 
  `is_frame`, 
  `is_cache`, 
  `menu_type`, 
  `visible`, 
  `status`, 
  `perms`, 
  `icon`, 
  `create_by`, 
  `create_time`, 
  `update_by`, 
  `update_time`, 
  `remark`
) VALUES (
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗',
  (SELECT menu_id FROM sys_menu WHERE menu_name LIKE '%科室消耗%' LIMIT 1),
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name LIKE '%科室消耗%' LIMIT 1)),
  'batchConsume',
  'department/batchConsume/index',
  1,
  0,
  'C',
  '0',
  '0',
  'department:batchConsume:list',
  'fa fa-list',
  'admin',
  NOW(),
  '',
  NULL,
  '科室批量消耗菜单'
);

-- 3.2 插入按钮权限
-- 查询按钮权限
INSERT INTO `sys_menu` (
  `menu_id`, 
  `menu_name`, 
  `parent_id`, 
  `order_num`, 
  `path`, 
  `component`, 
  `is_frame`, 
  `is_cache`, 
  `menu_type`, 
  `visible`, 
  `status`, 
  `perms`, 
  `icon`, 
  `create_by`, 
  `create_time`, 
  `update_by`, 
  `update_time`, 
  `remark`
) 
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗查询',
  menu_id,
  1,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:list',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 2 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗新增',
  menu_id,
  2,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:add',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 3 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗修改',
  menu_id,
  3,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:edit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 4 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗删除',
  menu_id,
  4,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:remove',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 5 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗导出',
  menu_id,
  5,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:export',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 6 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗查看',
  menu_id,
  6,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:query',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 7 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '科室批量消耗审核',
  menu_id,
  7,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:batchConsume:audit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '科室批量消耗';

-- ============================================
-- 四、配置单号生成规则（FillRuleUtil）
-- ============================================
-- 单号前缀：KSXH
-- 格式：KSXH-YYYYMMDD-XXXXXX
-- 示例：KSXH-20250115-000001
-- 注意：需要在系统配置中配置单号生成规则，或使用FillRuleUtil自动生成

-- ============================================
-- 五、验证SQL（可选执行）
-- ============================================
-- 验证表是否创建成功
-- SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN ('t_hc_ks_xh', 't_hc_ks_xh_entry');

-- 验证菜单是否添加成功
-- SELECT menu_id, menu_name, parent_id, order_num, path, component, perms, icon, menu_type, visible, status
-- FROM sys_menu 
-- WHERE menu_name LIKE '%科室批量消耗%'
-- ORDER BY menu_id;

-- ============================================
-- 六、注意事项
-- ============================================
-- 1. 执行本脚本前，请先查询科室消耗菜单的ID，并修改parent_id
-- 2. 如果数据库不支持子查询中的SELECT MAX，请手动指定menu_id
-- 3. 单号生成规则需要在系统配置中设置，或使用FillRuleUtil
-- 4. 所有删除操作使用软删除（del_flag = 1）
-- 5. 查询时自动过滤已删除记录（del_flag != 1）
