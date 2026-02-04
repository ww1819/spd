-- ============================================
-- 在远程数据库中创建打印设置表
-- 数据库: aspt
-- 地址: rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com:3306
-- 用户名: spd
-- ============================================
-- 注意：请在远程数据库中执行此脚本，不是本地数据库！

-- 如果表已存在，先删除（可选，如果表有数据请谨慎操作）
-- DROP TABLE IF EXISTS sys_print_setting;

-- 创建打印设置表
CREATE TABLE IF NOT EXISTS sys_print_setting (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
  bill_type INT(4) COMMENT '入库单类型（101普通入库，501调拨等，NULL表示通用）',
  page_width DECIMAL(10,2) DEFAULT 210 COMMENT '页面宽度（mm）',
  page_height DECIMAL(10,2) DEFAULT 297 COMMENT '页面高度（mm）',
  orientation VARCHAR(20) DEFAULT 'portrait' COMMENT '页面方向（portrait纵向，landscape横向）',
  margin_top DECIMAL(10,2) DEFAULT 0 COMMENT '上边距（mm）',
  margin_bottom DECIMAL(10,2) DEFAULT 0 COMMENT '下边距（mm）',
  margin_left DECIMAL(10,2) DEFAULT 0 COMMENT '左边距（mm）',
  margin_right DECIMAL(10,2) DEFAULT 0 COMMENT '右边距（mm）',
  font_size INT(4) DEFAULT 14 COMMENT '字体大小（px）',
  table_font_size INT(4) DEFAULT 12 COMMENT '表格字体大小（px）',
  column_spacing DECIMAL(10,2) DEFAULT 0 COMMENT '列间距（mm）',
  show_purchaser TINYINT(1) DEFAULT 0 COMMENT '显示采购人（0否，1是）',
  show_creator TINYINT(1) DEFAULT 1 COMMENT '显示制单人（0否，1是）',
  show_auditor TINYINT(1) DEFAULT 1 COMMENT '显示复核人（0否，1是）',
  show_receiver TINYINT(1) DEFAULT 0 COMMENT '显示验收人（0否，1是）',
  purchaser_label VARCHAR(50) DEFAULT '采购人' COMMENT '采购人标签',
  creator_label VARCHAR(50) DEFAULT '制单人' COMMENT '制单人标签',
  auditor_label VARCHAR(50) DEFAULT '复核人' COMMENT '复核人标签',
  receiver_label VARCHAR(50) DEFAULT '验收人' COMMENT '验收人标签',
  column_config TEXT COMMENT '列配置（JSON格式，存储列宽度、是否显示等）',
  is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认模板（0否，1是）',
  status CHAR(1) DEFAULT '0' COMMENT '状态（0正常，1停用）',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_bill_type (bill_type),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印设置表';

-- 验证表是否创建成功
SELECT 
  '表创建验证' as '检查项',
  TABLE_NAME as '表名',
  TABLE_TYPE as '类型',
  ENGINE as '引擎',
  TABLE_COLLATION as '字符集'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'sys_print_setting';

-- 显示表结构
DESCRIBE sys_print_setting;

-- 测试查询
SELECT COUNT(*) as '记录数' FROM sys_print_setting;
