-- ============================================
-- 统一软删除改造：为所有表添加 del_flag 字段
-- 字段类型：INT，默认值：0（0=未删除，1=已删除）
-- ============================================

-- 基础资料表
ALTER TABLE fd_material ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_supplier ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_warehouse ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_factory ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_finance_category ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_material_category ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE fd_equipment_dict ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 库存表
ALTER TABLE stk_inventory ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE stk_dep_inventory ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE gz_dep_inventory ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE gz_depot_inventory ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 设备相关表
ALTER TABLE equipment_return ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE equipment_return_detail ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE equipment_storage ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE equipment_storage_detail ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE equipment_file ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE equipment_category ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 科室申请相关表
ALTER TABLE new_product_apply ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE new_product_apply_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE new_product_apply_detail ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE dep_purchase_apply_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE bas_apply ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE bas_apply_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 盘点表
ALTER TABLE stk_io_stocktaking ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE stk_io_stocktaking_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 高值科室申请表
ALTER TABLE gz_dep_apply ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE gz_dep_apply_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 高值相关明细表（如果还没有字段）
ALTER TABLE gz_order_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE gz_shipment_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE gz_refund_goods_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';
ALTER TABLE stk_io_bill_entry ADD COLUMN IF NOT EXISTS del_flag INT DEFAULT 0 COMMENT '删除标识（0=未删除，1=已删除）';

-- 更新现有记录的 del_flag 为 0（如果为 NULL）
UPDATE fd_material SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_supplier SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_warehouse SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_factory SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_finance_category SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_material_category SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE fd_equipment_dict SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE stk_inventory SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE stk_dep_inventory SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_dep_inventory SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_depot_inventory SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_return SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_return_detail SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_storage SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_storage_detail SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_file SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE equipment_category SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE new_product_apply SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE new_product_apply_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE new_product_apply_detail SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE dep_purchase_apply_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE bas_apply SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE bas_apply_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE stk_io_stocktaking SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE stk_io_stocktaking_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_dep_apply SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_dep_apply_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_order_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_shipment_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE gz_refund_goods_entry SET del_flag = 0 WHERE del_flag IS NULL;
UPDATE stk_io_bill_entry SET del_flag = 0 WHERE del_flag IS NULL;

-- 注意：如果数据库不支持 IF NOT EXISTS，请手动检查字段是否存在后再执行
-- MySQL 5.7 及以下版本不支持 IF NOT EXISTS，需要使用以下方式：
-- 先检查：SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '表名' AND COLUMN_NAME = 'del_flag';
-- 如果返回 0，则执行 ALTER TABLE 语句
