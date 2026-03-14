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
  `planned_disable_time` datetime DEFAULT NULL COMMENT '计划停用时间（设备侧），到达后租户无法使用设备系统',
  `hc_status` char(1) NOT NULL DEFAULT '0' COMMENT '耗材侧状态（0正常 1停用），与设备侧 status 分开',
  `hc_planned_disable_time` datetime DEFAULT NULL COMMENT '计划停用时间（耗材侧），到达后租户无法使用耗材系统',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `uk_sb_customer_code` (`customer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备/耗材共用客户表(SaaS租户)；设备用 status/planned_disable_time，耗材用 hc_status/hc_planned_disable_time';
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
  `end_time` datetime DEFAULT NULL COMMENT '结束时间，NULL表示当前未结束',
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
  `status` char(1) DEFAULT '0' COMMENT '暂停状态（0正常使用 1暂停使用，仅客户菜单功能管理操作）',
  `is_enabled` char(1) DEFAULT '1' COMMENT '是否开启（0关闭 1开启），客户管理取消功能时改为0',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`customer_id`,`menu_id`),
  KEY `idx_sb_customer_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备系统客户菜单权限表';
/

-- 客户菜单功能启停用记录表
CREATE TABLE IF NOT EXISTS `sb_customer_menu_status_log` (
  `log_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID',
  `status` char(1) NOT NULL COMMENT '状态（0启用 1停用）',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `reason` varchar(500) DEFAULT NULL COMMENT '启停用原因',
  PRIMARY KEY (`log_id`),
  KEY `idx_cm_slog_customer_menu` (`customer_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户菜单功能启停用记录表';
/

-- 客户菜单功能启停用时间段表
CREATE TABLE IF NOT EXISTS `sb_customer_menu_period_log` (
  `period_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID',
  `period_type` varchar(20) NOT NULL COMMENT '类型：usage=使用时段，suspend=停用时段',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间，NULL表示未结束',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`period_id`),
  KEY `idx_cm_plog_customer_menu` (`customer_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户菜单功能启停用时间段表';
/

-- 客户68分类表（以 fd_category68 为蓝本，客户可自行维护；主键 UUID7，含删除标志/删除者/删除时间）
CREATE TABLE IF NOT EXISTS `sb_customer_category68` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `ref_category68_id` bigint(20) NOT NULL COMMENT '对应标准68分类ID(fd_category68.category68_id)',
  `parent_id` char(36) DEFAULT NULL COMMENT '父分类ID(本表主键id，对应父记录)',
  `category68_code` varchar(64) DEFAULT NULL COMMENT '68分类编码',
  `category68_name` varchar(200) DEFAULT NULL COMMENT '68分类名称',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_ref68` (`customer_id`,`ref_category68_id`),
  KEY `idx_sb_cc68_customer_id` (`customer_id`),
  KEY `idx_sb_cc68_ref_id` (`ref_category68_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户68分类表(与fd_category68一一对应，客户可维护)';
/

-- 客户68分类操作记录表（主键 UUID7，用于查看修改记录）
CREATE TABLE IF NOT EXISTS `sb_customer_category68_log` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `target_id` char(36) NOT NULL COMMENT '客户68分类表主键(sb_customer_category68.id)',
  `ref_category68_id` bigint(20) DEFAULT NULL COMMENT '对应标准68分类ID',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型: add=新增, update=修改, delete=删除',
  `content_old` text COMMENT '变更前内容(JSON或摘要)',
  `content_new` text COMMENT '变更后内容(JSON或摘要)',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_cc68_log_customer` (`customer_id`),
  KEY `idx_sb_cc68_log_target` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户68分类操作记录表';
/

-- 设备品牌表（主键UUID7，客户id，删除标志/删除者/删除时间，名称+名称拼音简码，支持名称/简码模糊搜索）
CREATE TABLE IF NOT EXISTS `sb_equipment_brand` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `name_pinyin` varchar(200) DEFAULT NULL COMMENT '名称拼音简码',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_eb_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备品牌表';
/

-- 设备生产厂家表（主键UUID7，客户id，删除标志/删除者/删除时间，名称+名称拼音简码）
CREATE TABLE IF NOT EXISTS `sb_equipment_manufacturer` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `name` varchar(200) NOT NULL COMMENT '名称',
  `name_pinyin` varchar(300) DEFAULT NULL COMMENT '名称拼音简码',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_em_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备生产厂家表';
/

-- 设备供应商表（主键UUID7，客户id，删除标志/删除者/删除时间，名称+名称拼音简码）
CREATE TABLE IF NOT EXISTS `sb_equipment_supplier` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `name` varchar(200) NOT NULL COMMENT '名称',
  `name_pinyin` varchar(300) DEFAULT NULL COMMENT '名称拼音简码',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_es_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备供应商表';
/

-- 资产分类表（主键UUID7，客户id，删除标志/删除者/删除时间，编码+名称+拼音简码）
CREATE TABLE IF NOT EXISTS `sb_asset_category` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `category_code` varchar(64) NOT NULL COMMENT '资产分类编码',
  `category_name` varchar(100) NOT NULL COMMENT '资产分类名称',
  `category_pinyin` varchar(200) DEFAULT NULL COMMENT '资产分类拼音简码',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_ac_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分类表';
/

-- 计量器具分类表（主键UUID7，客户id，删除标志/删除者/删除时间，编码+名称+拼音简码+检定周期(天)）
CREATE TABLE IF NOT EXISTS `sb_measuring_category` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `category_code` varchar(64) NOT NULL COMMENT '计量器具分类编码',
  `category_name` varchar(100) NOT NULL COMMENT '计量器具分类名称',
  `category_pinyin` varchar(200) DEFAULT NULL COMMENT '计量器具分类拼音简码',
  `calibration_cycle_days` int(11) DEFAULT NULL COMMENT '计量检定周期(天)',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_mc_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量器具分类表';
/

-- 客户资产台账表（主键UUID7，客户id，删除标志/删除者/删除时间；含设备流水号、68分类档案号、名称拼音简码等）
CREATE TABLE IF NOT EXISTS `sb_customer_asset_ledger` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `customer_id` char(36) NOT NULL COMMENT '客户ID(UUID7)',
  `equipment_serial_no` varchar(64) DEFAULT NULL COMMENT '设备流水号(默认数字序列，后续可大写字母+数字)',
  `category68_id` char(36) DEFAULT NULL COMMENT '客户68分类ID(sb_customer_category68.id)',
  `category68_code` varchar(64) DEFAULT NULL COMMENT '68分类编码',
  `category68_archive_no` varchar(80) DEFAULT NULL COMMENT '68分类档案号(68分类编码-XXXX，同客户同68分类下排序)',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `name_pinyin` varchar(300) DEFAULT NULL COMMENT '名称拼音简码',
  `spec` varchar(200) DEFAULT NULL COMMENT '规格',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `register_cert_no` varchar(100) DEFAULT NULL COMMENT '注册证号',
  `brand_id` char(36) DEFAULT NULL COMMENT '品牌ID(sb_equipment_brand.id)',
  `brand_name` varchar(100) DEFAULT NULL COMMENT '品牌名称',
  `manufacturer_id` char(36) DEFAULT NULL COMMENT '生产厂家ID(sb_equipment_manufacturer.id)',
  `manufacturer_name` varchar(200) DEFAULT NULL COMMENT '生产厂家名称',
  `supplier_id` char(36) DEFAULT NULL COMMENT '供应商ID(sb_equipment_supplier.id)',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `serial_number` varchar(100) DEFAULT NULL COMMENT '机身序列号(SN码)',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `original_value` decimal(18,4) DEFAULT NULL COMMENT '原值',
  `net_value` decimal(18,4) DEFAULT NULL COMMENT '净值',
  `dept_id` varchar(64) DEFAULT NULL COMMENT '所属科室id',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '所属科室名称',
  `storage_place` varchar(200) DEFAULT NULL COMMENT '存放地点',
  `acceptance_date` date DEFAULT NULL COMMENT '验收日期',
  `storage_date` date DEFAULT NULL COMMENT '入库日期',
  `manufacture_date` date DEFAULT NULL COMMENT '出厂日期',
  `scrap_date` date DEFAULT NULL COMMENT '报废日期',
  `expected_scrap_date` date DEFAULT NULL COMMENT '预计报废日期',
  `asset_category_id` char(36) DEFAULT NULL COMMENT '资产分类ID(sb_asset_category.id)',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产分类名称',
  `measuring_category_id` char(36) DEFAULT NULL COMMENT '计量器具分类ID(sb_measuring_category.id)',
  `measuring_category_name` varchar(100) DEFAULT NULL COMMENT '计量器具分类名称',
  `calibration_cycle_days` int(11) DEFAULT NULL COMMENT '计量检定周期(天)',
  `last_calibration_date` date DEFAULT NULL COMMENT '上次计量检定日期',
  `next_calibration_date` date DEFAULT NULL COMMENT '下次计量检定日期',
  `use_status` varchar(32) DEFAULT 'in_use' COMMENT '使用状态(系统字典eq_use_status，默认在用)',
  `use_status_name` varchar(50) DEFAULT NULL COMMENT '使用状态名称',
  `label_print_status` varchar(10) DEFAULT 'N' COMMENT '标签打印状态(Y/N)',
  `label_print_time` datetime DEFAULT NULL COMMENT '最近标签打印时间',
  `repair_status` varchar(32) DEFAULT 'no_fault' COMMENT '维修状态(系统字典eq_repair_status，默认无故障)',
  `repair_status_name` varchar(50) DEFAULT NULL COMMENT '维修状态名称',
  `warranty_days` int(11) DEFAULT NULL COMMENT '保修期限(天)',
  `warranty_start_time` date DEFAULT NULL COMMENT '保修开始时间',
  `warranty_end_time` date DEFAULT NULL COMMENT '保修结束时间',
  `warranty_type` varchar(32) DEFAULT NULL COMMENT '保修类型(系统字典eq_warranty_type: factory/extend/free等)',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sb_cal_customer` (`customer_id`),
  KEY `idx_sb_cal_category68` (`category68_id`),
  KEY `idx_sb_cal_name` (`name`(50)),
  KEY `idx_sb_cal_name_pinyin` (`name_pinyin`(80))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户资产台账表';
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
  `is_platform_only` char(1) DEFAULT '0' COMMENT '是否仅平台管理功能（1是，客户分配/工作组/用户权限中不展示）',
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

-- ========== 工作组表（主键 UUID7） ==========
CREATE TABLE IF NOT EXISTS `sb_work_group` (
  `group_id` char(36) NOT NULL COMMENT '工作组ID(UUID7)',
  `customer_id` char(36) NOT NULL COMMENT '所属客户ID',
  `group_name` varchar(100) NOT NULL COMMENT '工作组名称',
  `group_key` varchar(64) NOT NULL COMMENT '工作组标识如super',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`group_id`),
  KEY `idx_sb_work_group_customer` (`customer_id`),
  KEY `idx_sb_work_group_key` (`customer_id`, `group_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备系统工作组表';
/
CREATE TABLE IF NOT EXISTS `sb_work_group_user` (
  `group_id` char(36) NOT NULL COMMENT '工作组ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID(sys_user.user_id)',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`group_id`, `user_id`),
  KEY `idx_wgu_user` (`user_id`),
  KEY `idx_wgu_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组与用户关联';
/
CREATE TABLE IF NOT EXISTS `sb_work_group_menu` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `group_id` char(36) NOT NULL COMMENT '工作组ID',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wgm_group_menu` (`group_id`, `menu_id`),
  KEY `idx_wgm_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组菜单权限';
/
CREATE TABLE IF NOT EXISTS `sb_work_group_warehouse` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `group_id` char(36) NOT NULL COMMENT '工作组ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wgw_group_wh` (`group_id`, `warehouse_id`),
  KEY `idx_wgw_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组仓库权限';
/
CREATE TABLE IF NOT EXISTS `sb_work_group_dept` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `group_id` char(36) NOT NULL COMMENT '工作组ID',
  `dept_id` bigint(20) NOT NULL COMMENT '科室/部门ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wgd_group_dept` (`group_id`, `dept_id`),
  KEY `idx_wgd_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组科室权限';
/
-- ========== 用户权限表（主键 UUID7） ==========
CREATE TABLE IF NOT EXISTS `sb_user_permission_menu` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `menu_id` char(36) NOT NULL COMMENT '菜单ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upm_user_menu` (`user_id`, `menu_id`),
  KEY `idx_upm_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备用户菜单权限';
/
CREATE TABLE IF NOT EXISTS `sb_user_permission_warehouse` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upw_user_wh` (`user_id`, `warehouse_id`),
  KEY `idx_upw_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备用户仓库权限';
/
CREATE TABLE IF NOT EXISTS `sb_user_permission_dept` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_id` char(36) NOT NULL COMMENT '客户ID',
  `dept_id` bigint(20) NOT NULL COMMENT '科室/部门ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upd_user_dept` (`user_id`, `dept_id`),
  KEY `idx_upd_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备用户科室权限';
/