-- 科室申购表
DROP TABLE IF EXISTS `dep_purchase_apply`;
CREATE TABLE `dep_purchase_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `purchase_bill_no` varchar(50) NOT NULL COMMENT '申购单号',
  `purchase_bill_date` date DEFAULT NULL COMMENT '申请日期',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint(20) DEFAULT NULL COMMENT '科室ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `purchase_bill_status` int(11) DEFAULT '1' COMMENT '申购状态(1-待审核,2-已审核,3-已拒绝)',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
  `urgency_level` int(11) DEFAULT '1' COMMENT '紧急程度(1-普通,2-紧急,3-特急)',
  `expected_delivery_date` date DEFAULT NULL COMMENT '期望到货日期',
  `del_flag` int(11) DEFAULT '0' COMMENT '删除标识(0-正常,1-删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_purchase_bill_no` (`purchase_bill_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_purchase_bill_date` (`purchase_bill_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='科室申购表';

-- 科室申购明细表
DROP TABLE IF EXISTS `dep_purchase_apply_entry`;
CREATE TABLE `dep_purchase_apply_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` bigint(20) NOT NULL COMMENT '父类ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '耗材ID',
  `material_name` varchar(200) DEFAULT NULL COMMENT '耗材名称',
  `material_spec` varchar(200) DEFAULT NULL COMMENT '规格型号',
  `unit` varchar(50) DEFAULT NULL COMMENT '单位',
  `unit_price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `qty` decimal(10,2) DEFAULT '0.00' COMMENT '申购数量',
  `amt` decimal(10,2) DEFAULT '0.00' COMMENT '金额',
  `reason` varchar(500) DEFAULT NULL COMMENT '申购理由',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '建议供应商',
  `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='科室申购明细表';

-- 插入测试数据
INSERT INTO `dep_purchase_apply` VALUES 
(1, 'SG202501010001', '2025-01-01', 1, 1, 1, 1, 1500.00, 2, '2025-01-15', 0, 'admin', '2025-01-01 09:00:00', NULL, NULL, '急需补充库存'),
(2, 'SG202501010002', '2025-01-01', 1, 2, 1, 1, 2800.00, 1, '2025-01-20', 0, 'admin', '2025-01-01 10:30:00', NULL, NULL, '常规补充');

INSERT INTO `dep_purchase_apply_entry` VALUES 
(1, 1, 1, '一次性注射器', '5ml', '支', 2.50, 200.00, 500.00, '库存不足，需要紧急补充', '医疗器械有限公司', 'BD', 'BD-5ML', 'admin', '2025-01-01 09:00:00', NULL, NULL, NULL),
(2, 1, 2, '医用口罩', '一次性', '只', 1.00, 1000.00, 1000.00, '日常消耗量大', '防护用品公司', '3M', 'N95', 'admin', '2025-01-01 09:00:00', NULL, NULL, NULL),
(3, 2, 3, '输液器', '一次性', '套', 3.50, 300.00, 1050.00, '常规补充库存', '医疗器械有限公司', 'TERUMO', 'T-300', 'admin', '2025-01-01 10:30:00', NULL, NULL, NULL),
(4, 2, 4, '医用纱布', '10cm×10cm', '块', 0.50, 3500.00, 1750.00, '外科手术消耗', '医用纱布厂', '振德', 'ZD-1010', 'admin', '2025-01-01 10:30:00', NULL, NULL, NULL);

-- 菜单权限配置
-- 科室申购菜单 (假设科室管理的父菜单ID为5000)
INSERT INTO `sys_menu` VALUES 
(5001, '科室申购', 5000, 2, '/department/purchase', '', 'C', '0', '1', 'department:purchase:view', 'fa fa-shopping-cart', 'admin', NOW(), '', NULL, '科室申购菜单');

-- 科室申购按钮权限
INSERT INTO `sys_menu` VALUES 
(5101, '科室申购查询', 5001, 1, '#', '', 'F', '0', '1', 'department:purchase:list', '#', 'admin', NOW(), '', NULL, ''),
(5102, '科室申购新增', 5001, 2, '#', '', 'F', '0', '1', 'department:purchase:add', '#', 'admin', NOW(), '', NULL, ''),
(5103, '科室申购修改', 5001, 3, '#', '', 'F', '0', '1', 'department:purchase:edit', '#', 'admin', NOW(), '', NULL, ''),
(5104, '科室申购删除', 5001, 4, '#', '', 'F', '0', '1', 'department:purchase:remove', '#', 'admin', NOW(), '', NULL, ''),
(5105, '科室申购导出', 5001, 5, '#', '', 'F', '0', '1', 'department:purchase:export', '#', 'admin', NOW(), '', NULL, ''),
(5106, '科室申购查看', 5001, 6, '#', '', 'F', '0', '1', 'department:purchase:query', '#', 'admin', NOW(), '', NULL, '');

-- 字典数据配置
-- 申购状态字典
INSERT INTO `sys_dict_type` VALUES (100, '申购状态', 'purchase_status', '0', 'admin', NOW(), '', NULL, '科室申购状态列表');
INSERT INTO `sys_dict_data` VALUES (200, 1, '待审核', '1', 'purchase_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '待审核状态');
INSERT INTO `sys_dict_data` VALUES (201, 2, '已审核', '2', 'purchase_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已审核状态');
INSERT INTO `sys_dict_data` VALUES (202, 3, '已拒绝', '3', 'purchase_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已拒绝状态');

-- 紧急程度字典
INSERT INTO `sys_dict_type` VALUES (101, '紧急程度', 'urgency_level', '0', 'admin', NOW(), '', NULL, '申购紧急程度列表');
INSERT INTO `sys_dict_data` VALUES (203, 1, '普通', '1', 'urgency_level', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '普通优先级');
INSERT INTO `sys_dict_data` VALUES (204, 2, '紧急', '2', 'urgency_level', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '紧急优先级');
INSERT INTO `sys_dict_data` VALUES (205, 3, '特急', '3', 'urgency_level', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '特急优先级');
