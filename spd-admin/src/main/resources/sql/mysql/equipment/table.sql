-- mysql 追加表（按「/」分段，每段一条语句执行）

-- 客户表（SaaS 租户，主键 UUID7）
CREATE TABLE IF NOT EXISTS `sb_customer` (
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `customer_name` varchar(100) NOT NULL COMMENT '客户名称',
  `customer_code` varchar(64) DEFAULT NULL COMMENT '客户编码',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `planned_disable_time` datetime DEFAULT NULL COMMENT '计划停用时间，到达后租户无法使用',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `uk_sb_customer_code` (`customer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备系统客户表(SaaS租户)';
/

-- 客户启停用记录表（时间、操作人、启停用原因）
CREATE TABLE IF NOT EXISTS `sb_customer_status_log` (
  `log_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `status` char(1) NOT NULL COMMENT '状态（0启用 1停用）',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `reason` varchar(500) DEFAULT NULL COMMENT '启停用原因',
  PRIMARY KEY (`log_id`),
  KEY `idx_sb_customer_status_log_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户启停用记录表';
/

-- 客户实际使用/停用时间段记录表
CREATE TABLE IF NOT EXISTS `sb_customer_period_log` (
  `period_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `period_type` varchar(20) NOT NULL COMMENT '类型：usage=实际使用时段，suspend=实际停用时段',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`period_id`),
  KEY `idx_sb_customer_period_log_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户实际使用/停用时间段记录表';
/

-- 设备系统客户菜单权限表：控制每个客户可用的设备菜单
CREATE TABLE IF NOT EXISTS `sb_customer_menu` (
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID(UUID7)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`customer_id`,`menu_id`),
  KEY `idx_sb_customer_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备系统客户菜单权限表';
/

-- 设备前端独立菜单权限表：设备专用菜单（主键 UUID7）
CREATE TABLE IF NOT EXISTS `sb_menu` (
  `menu_id` char(36) NOT NULL COMMENT '菜单ID(UUID7)',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` varchar(36) NOT NULL DEFAULT '0' COMMENT '父菜单ID(UUID7，根为0)',
  `order_num` int(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `is_frame` char(1) DEFAULT '1' COMMENT '是否外链（0是 1否）',
  `is_cache` char(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT 'C' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`menu_id`),
  KEY `idx_sb_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备前端独立菜单表';
/

-- 设备前端独立菜单权限表：设备角色（主键 UUID7）
CREATE TABLE IF NOT EXISTS `sb_role` (
  `role_id` char(36) NOT NULL COMMENT '角色ID(UUID7)',
  `customer_id` char(36) DEFAULT NULL COMMENT '客户ID(UUID7)，归属客户/租户',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1全部数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`role_id`),
  KEY `idx_sb_role_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备前端角色表';
/

-- 设备前端独立菜单权限表：用户与设备角色关联
CREATE TABLE IF NOT EXISTS `sb_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（关联sys_user.user_id）',
  `role_id` char(36) NOT NULL COMMENT '角色ID（关联sb_role.role_id）',
  `customer_id` char(36) DEFAULT NULL COMMENT '客户ID(UUID7)，归属客户/租户',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `idx_sb_user_role_role_id` (`role_id`),
  KEY `idx_sb_user_role_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备前端用户和角色关联表';
/

-- 设备前端独立菜单权限表：角色与设备菜单关联
CREATE TABLE IF NOT EXISTS `sb_role_menu` (
  `role_id` char(36) NOT NULL COMMENT '角色ID（关联sb_role.role_id）',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID（关联sb_menu.menu_id）',
  `customer_id` char(36) DEFAULT NULL COMMENT '客户ID(UUID7)，归属客户/租户',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`role_id`,`menu_id`),
  KEY `idx_sb_role_menu_menu_id` (`menu_id`),
  KEY `idx_sb_role_menu_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备前端角色和菜单关联表';
/

-- 客户启停用记录表
CREATE TABLE IF NOT EXISTS `sb_customer_status_log` (
  `log_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `status` char(1) NOT NULL COMMENT '状态（0启用 1停用）',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `reason` varchar(500) DEFAULT NULL COMMENT '启停用原因',
  PRIMARY KEY (`log_id`),
  KEY `idx_sb_customer_status_log_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户启停用记录表';
/

-- 客户实际使用/停用时间段记录表
CREATE TABLE IF NOT EXISTS `sb_customer_period_log` (
  `period_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `period_type` varchar(20) NOT NULL COMMENT '类型：usage=实际使用时段，suspend=实际停用时段',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`period_id`),
  KEY `idx_sb_customer_period_log_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户实际使用/停用时间段记录表';
/
