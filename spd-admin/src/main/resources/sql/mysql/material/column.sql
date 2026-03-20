-- ========== 耗材模块 增量字段（含 add_table_column 存储过程） ==========
-- 建议在 table.sql 之后执行；按「/」分段执行。新环境若已执行 table.sql 完整建表，本脚本中与 table 中已存在字段的 CALL 会跳过。
/*
 * 存储过程：add_table_column
 * 功能：安全地为指定数据表添加新字段，避免重复添加
 * 特点：1. 参数合法性校验 2. 字段存在性检查 3. 支持默认值设置 4. 友好的执行结果提示
 */
CREATE PROCEDURE IF NOT EXISTS `add_table_column`(
    IN p_table_name VARCHAR(64),      -- 输入参数：目标表名（必填）
    IN p_column_name VARCHAR(64),     -- 输入参数：要添加的字段名（必填）
    IN p_column_type VARCHAR(64),     -- 输入参数：字段类型（如int, varchar(255)等，必填）
    IN p_column_comment VARCHAR(256), -- 输入参数：字段注释（必填）
    IN p_default_value VARCHAR(256)   -- 输入参数：字段默认值（可选，不传则为NULL）
)
add_column_block:  -- 定义代码块标签，用于提前退出
BEGIN
    -- 声明局部变量：用于存储字段是否存在的标识（0=不存在，1=存在）
    DECLARE v_column_exists INT DEFAULT 0;

    -- 处理默认值参数：如果传入NULL则显式设置为NULL
    SET p_default_value = IFNULL(p_default_value, NULL);

    -- 初始化动态SQL变量
    SET @dynamic_sql = '';

    -- 第一步：参数合法性校验 - 检查必填参数是否为空
    IF p_table_name IS NULL OR p_table_name = ''
        OR p_column_name IS NULL OR p_column_name = ''
        OR p_column_type IS NULL OR p_column_type = ''
        OR p_column_comment IS NULL OR p_column_comment = '' THEN
        -- 抛出自定义异常，提示必填参数不能为空
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：表名、字段名、字段类型、字段注释为必填参数，不能为空！';
END IF;

    -- 第二步：检查字段是否已存在
SELECT COUNT(*) INTO v_column_exists
FROM information_schema.COLUMNS  -- 系统表：存储所有表的字段信息
WHERE TABLE_SCHEMA = DATABASE()  -- 当前数据库
  AND TABLE_NAME = p_table_name  -- 目标表名
  AND COLUMN_NAME = p_column_name; -- 要检查的字段名

-- 如果字段已存在，提示并退出存储过程
IF v_column_exists > 0 THEN
SELECT CONCAT('提示：字段【', p_column_name, '】已存在于表【', p_table_name, '】，无需重复添加') AS 执行结果;
LEAVE add_column_block;  -- 退出代码块，结束存储过程执行
END IF;

    -- 第三步：拼接动态SQL语句（ALTER TABLE ADD COLUMN）
    SET @dynamic_sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD COLUMN `', p_column_name, '` ', p_column_type, ' '
                       );

    -- 如果传入了默认值，拼接DEFAULT子句（使用QUOTE函数防止SQL注入）
    IF p_default_value IS NOT NULL AND p_default_value != '' THEN
        SET @dynamic_sql = CONCAT(@dynamic_sql, 'DEFAULT ', QUOTE(p_default_value), ' ');
END IF;

    -- 拼接字段注释（使用QUOTE函数处理特殊字符）
    SET @dynamic_sql = CONCAT(@dynamic_sql, 'COMMENT ', QUOTE(p_column_comment));

    -- 第四步：执行动态SQL
PREPARE stmt FROM @dynamic_sql;  -- 预处理动态SQL
EXECUTE stmt;                    -- 执行预处理语句
DEALLOCATE PREPARE stmt;         -- 释放预处理资源

-- 提示字段添加成功
SELECT CONCAT('成功：字段【', p_column_name, '】已成功添加到表【', p_table_name, '】') AS 执行结果;

-- 清空动态SQL变量，避免残留
SET @dynamic_sql = '';
END;
/

-- 调用示例：为bas_apply表添加del_flag字段（int类型，注释为删除标志，默认值0）
CALL add_table_column('bas_apply', 'del_flag', 'int', '删除标志', 0);
/

CALL add_table_column('stk_inventory', 'batch_number', 'varchar(100)', '批号', null);
/

CALL add_table_column('stk_dep_inventory', 'batch_number', 'varchar(100)', '批号', null);
/

-- 科室 fd_department 增加 名称简码
CALL add_table_column(
  'fd_department',
  'referred_name',
  'varchar(64)',
  '名称简码',
  NULL
);
/* 分隔符 */
/
/* 工作人员 sys_user 增加 名称简码（拼音简码，用于用户名称） */
CALL add_table_column(
  'sys_user',
  'referred_name',
  'varchar(64)',
  '名称简码',
  NULL
);
/
/* 库房分类 fd_warehouse_category 增加 名称简码 */
CALL add_table_column(
  'fd_warehouse_category',
  'referred_name',
  'varchar(64)',
  '名称简码',
  NULL
);
/
/* 财务分类 fd_finance_category 增加 名称简码 */
CALL add_table_column(
  'fd_finance_category',
  'referred_name',
  'varchar(64)',
  '名称简码',
  NULL
);
/
/* 仓库流水表 t_hc_ck_flow 增加 期初单主表/明细表ID（UUID7 引用） */
CALL add_table_column('t_hc_ck_flow', 'ref_bill_id', 'varchar(36)', '期初单主表ID（UUID7）', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'ref_entry_id', 'varchar(36)', '期初单明细ID（UUID7）', NULL);
/
/* 批次表 stk_batch 增加 期初单主表/明细表ID（UUID7 引用） */
CALL add_table_column('stk_batch', 'ref_bill_id', 'varchar(36)', '期初单主表ID（UUID7）', NULL);
/
CALL add_table_column('stk_batch', 'ref_entry_id', 'varchar(36)', '期初单明细ID（UUID7）', NULL);
/
/* 产品档案 fd_material 增加 第三方系统产品档案ID（用于期初导入匹配） */
CALL add_table_column('fd_material', 'his_id', 'varchar(64)', '第三方系统产品档案ID（HIS等）', NULL);
/
/* 期初库存导入明细表：以下字段已合并到 table.sql 建表语句，此处仅用于已有库的增量升级 */
CALL add_table_column('stk_initial_import_entry', 'third_party_detail_id', 'varchar(64)', '第三方系统库存明细ID', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'third_party_material_id', 'varchar(64)', '第三方系统产品档案ID', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'material_code', 'varchar(64)', '耗材编码', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'speci', 'varchar(255)', '规格', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'model', 'varchar(255)', '型号', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'register_no', 'varchar(128)', '注册证号', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'medical_no', 'varchar(64)', '医保编码', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'medical_name', 'varchar(255)', '医保名称', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'main_barcode', 'varchar(128)', '主条码', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/

CALL add_table_column('stk_io_bill_entry', 'suppler_id', 'varchar(128)', '供应商ID，出退库单明细内的供应商id', NULL);
/

CALL add_table_column('stk_io_profit_loss_entry', 'suppler_id', 'varchar(128)', '供应商ID，出退库单明细内的供应商id', NULL);
/
/* 产品档案 fd_material 增加 入选原因 */
CALL add_table_column('fd_material', 'selection_reason', 'varchar(512)', '入选原因', NULL);
/

/* 产品档案 fd_material 增加 是否计费 */
CALL add_table_column('fd_material', 'is_billing', 'char(4)', '是否计费：1=计费,2=不计费', '2');
/

/* 科室库存表增加收货确认状态：0=未确认 1=已确认；出库单审核即插入科室库存(未确认)，收货确认后更新为已确认 */
CALL add_table_column('stk_dep_inventory', 'receipt_confirm_status', 'TINYINT', '收货确认状态 0未确认 1已确认', '0');
/
/* 科室库存表增加单据关联字段，便于收货确认时精确定位对应出库单及出库明细 */
CALL add_table_column('stk_dep_inventory', 'bill_id', 'BIGINT', '单据主表id(出库单id)', NULL);
/
CALL add_table_column('stk_dep_inventory', 'bill_entry_id', 'BIGINT', '单据明细id(出库单明细id)', NULL);
/
CALL add_table_column('stk_dep_inventory', 'bill_no', 'varchar(64)', '单据号', NULL);
/
CALL add_table_column('stk_dep_inventory', 'bill_type', 'INT', '单据类型 201出库', NULL);
/
/* 科室库存表增加备注（出库单生成的可填写“本库存科室出库业务生成”） */
CALL add_table_column('stk_dep_inventory', 'remark', 'varchar(500)', '备注', NULL);
/
/* 耗材科室列表与租户关联 */
CALL add_table_column('fd_department', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 耗材业务表与租户关联：仓库、出入库单、库存、科室库存、申领单 */
CALL add_table_column('fd_warehouse', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_io_bill', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_io_bill', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_io_bill', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('stk_inventory', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_dep_inventory', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('bas_apply', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 耗材工作组使用 sys_post；岗位表及关联表按租户隔离 */
CALL add_table_column('sys_post', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('sys_post_department', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('sys_post_menu', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('sys_post_warehouse', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 耗材与设备停用状态、计划停用时间分离：sb_customer 增加耗材侧字段 */
CALL add_table_column('sb_customer', 'hc_status', 'char(1)', '耗材侧状态（0正常 1停用）', '0');
/
CALL add_table_column('sb_customer', 'hc_planned_disable_time', 'datetime', '计划停用时间（耗材侧）', NULL);
/
/* ========== 高值耗材主条码、辅条码（便于追溯与数据核对） ========== */
CALL add_table_column('stk_io_bill_entry', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('stk_inventory', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('stk_inventory', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('stk_dep_inventory', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('stk_dep_inventory', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
/* 科室批量消耗明细：高值耗材主条码、辅条码 */
CALL add_table_column('t_hc_ks_xh_entry', 'main_barcode', 'varchar(128)', '高值耗材主条码', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'sub_barcode', 'varchar(128)', '高值耗材辅条码', NULL);
/
/* 批次表：租户隔离与历史追溯 */
CALL add_table_column('stk_batch', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* ========== 耗材业务表与客户(租户)关联（便于多租户隔离与列表过滤） ========== */
CALL add_table_column('stk_initial_import', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('bas_apply_template', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('t_hc_ks_xh', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('wh_fixed_number', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('dept_fixed_number', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_material_import', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 流水与档案日志表：冗余租户ID 便于按客户查流水/日志 */
CALL add_table_column('t_hc_ck_flow', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_material_status_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_material_change_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* ========== 高值耗材相关表：租户关联 + 主条码/辅条码 ========== */
CALL add_table_column('gz_order', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('gz_order_entry', 'master_barcode', 'varchar(128)', '主条码', NULL);
/
CALL add_table_column('gz_order_entry', 'secondary_barcode', 'varchar(128)', '辅条码', NULL);
/
CALL add_table_column('gz_depot_inventory', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* ========== 耗材仓库结算方式：入库/出库/消耗；入库单、明细、仓库库存、科室库存写入结算方式 ========== */
CALL add_table_column('stk_io_bill', 'settlement_type', 'varchar(16)', '结算方式 1入库结算 2出库结算 3消耗结算（来自仓库）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'settlement_type', 'varchar(16)', '结算方式（与主表一致）', NULL);
/
CALL add_table_column('stk_inventory', 'settlement_type', 'varchar(16)', '结算方式（来自入库单）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'settlement_type', 'varchar(16)', '结算方式（来自出库单）', NULL);
/
/* 发票表增加供应商ID */
CALL add_table_column('fin_invoice', 'supplier_id', 'bigint(20)', '供应商ID', NULL);
/
/* ========== 供应商结算单：主表去掉 invoice_id；明细增加仓库结算单主表id/单号；新增发票关联表 ========== */
CALL add_table_column('supp_settlement_bill_entry', 'wh_settlement_id', 'varchar(36)', '仓库结算单主表ID（UUID7）', NULL);
/
CALL add_table_column('supp_settlement_bill_entry', 'wh_settlement_bill_no', 'varchar(64)', '仓库结算单单号', NULL);
/
/* 供应商结算单与发票关联表：删除者、删除时间（逻辑删除）；结算单审核后不得删除、修改关联 */
CALL add_table_column('supp_settlement_invoice', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('supp_settlement_invoice', 'delete_time', 'datetime', '删除时间', NULL);
/
/* ========== 耗材相关表：补充删除者、删除时间（与 del_flag 逻辑删除配套） ========== */
CALL add_table_column('stk_initial_import', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_initial_import', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_initial_import_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('bas_apply_template', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('bas_apply_template', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('wh_settlement_bill_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('wh_settlement_bill_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('supp_settlement_bill_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('supp_settlement_bill_entry', 'delete_time', 'datetime', '删除时间', NULL);
/

-- ============================================================
-- 为指定表补充：创建时间、创建者、更新时间、更新者、删除时间、删除者、租户ID/客户ID
-- 使用 add_table_column 存储过程，已存在的字段会跳过
-- 执行前请确保已执行过 material/column.sql 中的 add_table_column 定义
-- 按「/」分段执行
-- ============================================================

-- ========== 通用字段类型 ==========
-- create_by varchar(64), create_time datetime, update_by varchar(64), update_time datetime
-- delete_by varchar(64), delete_time datetime
-- tenant_id varchar(36) 或 customer_id char(36)（仅 sys_user 等用 customer_id）

-- bas_apply_entry
CALL add_table_column('bas_apply_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('bas_apply_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('bas_apply_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('bas_apply_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('bas_apply_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('bas_apply_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('bas_apply_entry', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/

-- bas_apply_template（表已有 create_by 等；补 delete/tenant 若缺）
CALL add_table_column('bas_apply_template', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('bas_apply_template', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('bas_apply_template', 'delete_time', 'datetime', '删除时间', NULL);
/

-- bas_apply_template_entry
CALL add_table_column('bas_apply_template_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('bas_apply_template_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- dep_inventory_warning
CALL add_table_column('dep_inventory_warning', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('dep_inventory_warning', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('dep_inventory_warning', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('dep_inventory_warning', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('dep_inventory_warning', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('dep_inventory_warning', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('dep_inventory_warning', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- dep_purchase_apply
CALL add_table_column('dep_purchase_apply', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('dep_purchase_apply', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('dep_purchase_apply', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('dep_purchase_apply', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('dep_purchase_apply', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('dep_purchase_apply', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('dep_purchase_apply', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- dep_purchase_apply_entry
CALL add_table_column('dep_purchase_apply_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('dep_purchase_apply_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- fd_material
CALL add_table_column('fd_material', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_material', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_material', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- fd_material_category
CALL add_table_column('fd_material_category', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_material_category', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_material_category', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- fd_supplier
CALL add_table_column('fd_supplier', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_supplier', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_supplier', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- fd_factory 生产厂家
CALL add_table_column('fd_factory', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('fd_factory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_factory', 'delete_time', 'datetime', '删除时间', NULL);
/

-- fd_warehouse_category
CALL add_table_column('fd_warehouse_category', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_warehouse_category', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_warehouse_category', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_dep_apply
CALL add_table_column('gz_dep_apply', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_dep_apply', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_dep_apply', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_dep_apply', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_dep_apply', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_dep_apply', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_dep_apply', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_dep_apply_entry
CALL add_table_column('gz_dep_apply_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_depot_inventory（已有 tenant_id 时跳过）
CALL add_table_column('gz_depot_inventory', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('gz_depot_inventory', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_depot_inventory', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_depot_inventory', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_depot_inventory', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_depot_inventory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_depot_inventory', 'delete_time', 'datetime', '删除时间', NULL);
/

-- gz_order_entry
CALL add_table_column('gz_order_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('gz_order_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_order_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_order_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_order_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_order_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_order_entry', 'delete_time', 'datetime', '删除时间', NULL);
/

-- gz_order_entry_inhospitalcode_list
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_patient_info
CALL add_table_column('gz_patient_info', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_patient_info', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_patient_info', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_patient_info', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_patient_info', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_patient_info', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_patient_info', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_refund_goods
CALL add_table_column('gz_refund_goods', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_refund_goods', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_refund_goods', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_refund_goods', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_refund_goods', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_refund_goods', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_refund_goods', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_refund_goods_entry
CALL add_table_column('gz_refund_goods_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_refund_stock
CALL add_table_column('gz_refund_stock', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_refund_stock', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_refund_stock', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_refund_stock', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_refund_stock', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_refund_stock', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_refund_stock', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_refund_stock_entry
CALL add_table_column('gz_refund_stock_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_shipment
CALL add_table_column('gz_shipment', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_shipment', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_shipment', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_shipment', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_shipment', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_shipment', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_shipment', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_shipment_entry
CALL add_table_column('gz_shipment_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_shipment_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_shipment_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_shipment_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_shipment_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_shipment_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_shipment_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_traceability
CALL add_table_column('gz_traceability', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_traceability', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_traceability', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_traceability', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_traceability', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_traceability', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_traceability', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_traceability_entry
CALL add_table_column('gz_traceability_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_traceability_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_traceability_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_traceability_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('gz_traceability_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_traceability_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_traceability_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- his_hc_info
CALL add_table_column('his_hc_info', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('his_hc_info', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('his_hc_info', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('his_hc_info', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('his_hc_info', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('his_hc_info', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('his_hc_info', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- new_product_apply
CALL add_table_column('new_product_apply', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('new_product_apply', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('new_product_apply', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('new_product_apply', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('new_product_apply', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- new_product_apply_detail
CALL add_table_column('new_product_apply_detail', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('new_product_apply_detail', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('new_product_apply_detail', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('new_product_apply_detail', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('new_product_apply_detail', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('new_product_apply_detail', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('new_product_apply_detail', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- new_product_apply_entry
CALL add_table_column('new_product_apply_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('new_product_apply_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('new_product_apply_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('new_product_apply_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('new_product_apply_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- purchase_order
CALL add_table_column('purchase_order', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('purchase_order', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('purchase_order', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('purchase_order', 'plan_id', 'bigint(20)', '计划单主表ID', NULL);
/

-- purchase_order_entry
CALL add_table_column('purchase_order_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('purchase_order_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('purchase_order_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('purchase_order_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('purchase_order_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('purchase_order_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('purchase_order_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('purchase_order_entry', 'plan_id', 'bigint(20)', '计划单主表ID', NULL);
/
CALL add_table_column('purchase_order_entry', 'plan_no', 'varchar(64)', '计划单号', NULL);
/
CALL add_table_column('purchase_order_entry', 'plan_entry_id', 'bigint(20)', '计划单明细ID', NULL);
/

-- purchase_plan
CALL add_table_column('purchase_plan', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('purchase_plan', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('purchase_plan', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('purchase_plan', 'plan_entry_mode', 'varchar(20)', '计划明细生成方式：1=按产品档案汇总 2=按申购单明细拆分', '1');
/

-- purchase_plan_entry
CALL add_table_column('purchase_plan_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('purchase_plan_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('purchase_plan_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('purchase_plan_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('purchase_plan_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('purchase_plan_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('purchase_plan_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('purchase_plan_entry', 'supplier_id', 'bigint(20)', '供应商ID（指定明细供应商，审核后按此拆单）', NULL);
/
CALL add_table_column('purchase_plan_entry', 'apply_qty', 'decimal(20,4)', '申购数量（引用科室申购单汇总数量）', NULL);
/
CALL add_table_column('purchase_plan_entry', 'apply_department_id', 'bigint(20)', '申请科室ID（按申购单明细拆分时写入）', NULL);
/

-- purchase_plan_entry_dep_apply
CALL add_table_column('purchase_plan_entry_dep_apply', 'dep_purchase_apply_id', 'bigint(20)', '申购单主表ID', NULL);
/
CALL add_table_column('purchase_plan_entry_dep_apply', 'purchase_bill_no', 'varchar(64)', '申购单号', NULL);
/
CALL add_table_column('purchase_plan_entry_dep_apply', 'purchase_plan_id', 'bigint(20)', '采购计划主表ID', NULL);
/
CALL add_table_column('purchase_plan_entry_dep_apply', 'plan_no', 'varchar(64)', '采购计划单号', NULL);
/

-- sb_work_group（设备侧用 customer_id，若表已有则跳过）
CALL add_table_column('sb_work_group', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sb_work_group', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('sb_work_group', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('sb_work_group', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('sb_work_group', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('sb_work_group', 'delete_time', 'datetime', '删除时间', NULL);
/

-- stk_initial_import_entry（补 tenant_id）
CALL add_table_column('stk_initial_import_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- stk_io_profit_loss_entry（补 tenant_id）
CALL add_table_column('stk_io_profit_loss_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'delete_time', 'datetime', '删除时间', NULL);
/

-- stk_io_stocktaking_entry（补 tenant_id）
CALL add_table_column('stk_io_stocktaking_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'supplier_id', 'bigint', '明细对应供应商ID（盘盈时必填）', NULL);
/

-- sys_dept（若需按租户隔离部门则加 tenant_id）
CALL add_table_column('sys_dept', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_dept', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('sys_dept', 'delete_time', 'datetime', '删除时间', NULL);
/

-- sys_logininfor
CALL add_table_column('sys_logininfor', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- sys_notice
CALL add_table_column('sys_notice', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_notice', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_notice', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('sys_notice', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('sys_notice', 'update_time', 'datetime', '更新时间', NULL);
/

-- sys_oper_log
CALL add_table_column('sys_oper_log', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- sys_post（已有 tenant_id 时跳过）
CALL add_table_column('sys_post', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_post', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('sys_post', 'delete_time', 'datetime', '删除时间', NULL);
/

-- sys_sheet_id
CALL add_table_column('sys_sheet_id', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_sheet_id', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('sys_sheet_id', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('sys_sheet_id', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('sys_sheet_id', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('sys_sheet_id', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('sys_sheet_id', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- sys_user_department
CALL add_table_column('sys_user_department', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_user_department', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_user_department', 'create_time', 'datetime', '创建时间', NULL);
/

-- sys_menu：是否仅平台管理（1=是，仅平台显示，不对客户显示）
CALL add_table_column('sys_menu', 'is_platform', 'char(1)', '是否仅平台管理（0否 1是）', '0');
/
-- sys_menu：耗材功能重置时默认对客户/super/super_01 开放（对齐设备 sb_menu.default_open_to_customer）
CALL add_table_column('sys_menu', 'default_open_to_customer', 'char(1)', '耗材默认对客户开放（0否 1是）', '0');
/

-- 可选：将「系统设置」子树下非平台、启用菜单标为默认开放，与旧「递归系统设置」逻辑对齐（执行一次即可）
-- UPDATE sys_menu m
-- INNER JOIN (
--   WITH RECURSIVE tree AS (
--     SELECT menu_id FROM sys_menu WHERE menu_name = '系统设置'
--     UNION ALL
--     SELECT m2.menu_id FROM sys_menu m2 INNER JOIN tree t ON m2.parent_id = t.menu_id
--   )
--   SELECT menu_id FROM tree
-- ) x ON m.menu_id = x.menu_id
-- SET m.default_open_to_customer = '1'
-- WHERE (m.is_platform IS NULL OR m.is_platform != '1') AND IFNULL(m.status,'0') = '0';

-- sys_user_menu
CALL add_table_column('sys_user_menu', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_user_menu', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_user_menu', 'create_time', 'datetime', '创建时间', NULL);
/

-- sys_user_post（已有 create_* 等时跳过）
CALL add_table_column('sys_user_post', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- sys_user_role（设备侧部分用 customer_id，此处补 tenant_id 供耗材侧或统一用 tenant_id 时使用）
CALL add_table_column('sys_user_role', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_user_role', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_user_role', 'create_time', 'datetime', '创建时间', NULL);
/

-- sys_user_warehouse
CALL add_table_column('sys_user_warehouse', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('sys_user_warehouse', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('sys_user_warehouse', 'create_time', 'datetime', '创建时间', NULL);
/


CALL add_table_column('stk_dep_inventory', 'del_flag', 'int', '删除标识', '0');
/
CALL add_table_column('stk_dep_inventory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_dep_inventory', 'delete_time', 'datetime', '删除时间', NULL);
/


CALL add_table_column('stk_inventory', 'del_flag', 'int', '删除标识', '0');
/
CALL add_table_column('stk_inventory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('stk_inventory', 'delete_time', 'datetime', '删除时间', NULL);
/

ALTER TABLE purchase_plan MODIFY COLUMN plan_status char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' NOT NULL COMMENT '计划状态（0未提交1待审核 2已审核 3已执行 4已取消）';