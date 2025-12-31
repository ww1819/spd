-- 创建用户菜单关联表
CREATE TABLE IF NOT EXISTS `sys_user_menu` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`user_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和菜单关联表';

