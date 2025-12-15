-- 创建货位表
CREATE TABLE IF NOT EXISTS `fd_location` (
  `location_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '货位ID',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父货位ID，0表示顶级货位',
  `location_code` varchar(50) DEFAULT NULL COMMENT '货位编码',
  `location_name` varchar(100) NOT NULL COMMENT '货位名称',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`location_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='货位表';

