-- 创建工作组菜单权限关联表
CREATE TABLE IF NOT EXISTS `sys_post_menu` (
  `post_id` bigint(20) NOT NULL COMMENT '工作组ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`post_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组和菜单关联表';

-- 创建工作组科室权限关联表
CREATE TABLE IF NOT EXISTS `sys_post_department` (
  `post_id` bigint(20) NOT NULL COMMENT '工作组ID',
  `department_id` bigint(20) NOT NULL COMMENT '科室ID',
  PRIMARY KEY (`post_id`, `department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组和科室关联表';

-- 创建工作组仓库权限关联表
CREATE TABLE IF NOT EXISTS `sys_post_warehouse` (
  `post_id` bigint(20) NOT NULL COMMENT '工作组ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  PRIMARY KEY (`post_id`, `warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作组和仓库关联表';

