-- ========== 耗材模块 增量字段（含 add_table_column 存储过程） ==========
-- 建议在 table.sql 之后执行；按「/」分段执行。全量建表仅在 material/table.sql，本脚本不含 CREATE TABLE；新环境执行 table.sql 后，本脚本中 CALL add_table_column 对已存在字段会自动跳过。
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

/* 科室库存表增加 批次对象表ID（stk_batch.id）用于追溯 */
CALL add_table_column('stk_dep_inventory', 'batch_id', 'bigint', '批次对象表ID（stk_batch.id）', NULL);
/

-- 科室库存：归属仓库、效期、厂家（与仓库库存/流水对齐）
CALL add_table_column('stk_dep_inventory', 'warehouse_id', 'bigint', '库存归属仓库ID（与出库来源仓库一致）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'begin_time', 'date', '生产日期', NULL);
/
CALL add_table_column('stk_dep_inventory', 'end_time', 'date', '有效期', NULL);
/
CALL add_table_column('stk_dep_inventory', 'factory_id', 'bigint', '生产厂家ID（fd_factory.factory_id）', NULL);
/

-- 仓库库存：生产厂家冗余
CALL add_table_column('stk_inventory', 'factory_id', 'bigint', '生产厂家ID（冗余，与批次/档案一致）', NULL);
/

-- 仓库流水：生产厂家
CALL add_table_column('t_hc_ck_flow', 'factory_id', 'bigint', '生产厂家ID（fd_factory.factory_id）', NULL);
/

-- 科室流水：归属仓库、生产厂家
CALL add_table_column('t_hc_ks_flow', 'warehouse_id', 'bigint', '库存归属仓库ID（出库来源仓库）', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'factory_id', 'bigint', '生产厂家ID（fd_factory.factory_id）', NULL);
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
CALL add_table_column('fd_finance_category', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_finance_category', 'remark', 'varchar(512)', '备注', NULL);
/
CALL add_table_column('fd_finance_category', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_finance_category', 'delete_time', 'datetime', '删除时间', NULL);
/
/* 仓库流水表 t_hc_ck_flow 增加 期初单主表/明细表ID（UUID7 引用） */
CALL add_table_column('t_hc_ck_flow', 'ref_bill_id', 'varchar(36)', '期初单主表ID（UUID7）', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'ref_entry_id', 'varchar(36)', '期初单明细ID（UUID7）', NULL);
/

/* 仓库流水表 t_hc_ck_flow 增加：批次对象表ID与来源业务类型（追溯展示） */
CALL add_table_column('t_hc_ck_flow', 'batch_id', 'BIGINT', '批次对象表ID（stk_batch.id）', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'origin_business_type', 'varchar(64)', '来源业务类型中文（便于追溯展示）', NULL);
/
/* 批次表 stk_batch 增加 期初单主表/明细表ID（UUID7 引用） */
CALL add_table_column('stk_batch', 'ref_bill_id', 'varchar(36)', '期初单主表ID（UUID7）', NULL);
/
CALL add_table_column('stk_batch', 'ref_entry_id', 'varchar(36)', '期初单明细ID（UUID7）', NULL);
/
/* 产品档案 fd_material 增加 第三方系统产品档案ID（用于期初导入匹配） */
CALL add_table_column('fd_material', 'his_id', 'varchar(64)', '第三方系统产品档案ID（HIS等）', NULL);
/
/* 期初库存导入明细表：第三方/HIS 库存明细 ID 统一为 his_id（旧列 third_party_detail_id 迁移见 sql/maintenance/upgrade_stk_initial_import_entry_hisid_decimal.sql） */
CALL add_table_column('stk_initial_import_entry', 'his_id', 'varchar(128)', '第三方/HIS系统库存明细ID（对应导入列 his_id）', NULL);
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
CALL add_table_column('stk_io_bill_entry', 'bill_no', 'varchar(64)', '出入库单号（冗余主表 bill_no，便于按单号查明细）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'warehouse_id', 'bigint', '明细仓库ID（冗余，与主表或业务一致）', NULL);
/

CALL add_table_column('stk_io_profit_loss_entry', 'suppler_id', 'varchar(128)', '供应商ID，出退库单明细内的供应商id', NULL);
/
/* 产品档案 fd_material 增加 入选原因 */
CALL add_table_column('fd_material', 'selection_reason', 'varchar(512)', '入选原因', NULL);
/

/* 产品档案 fd_material 增加 是否计费 */
CALL add_table_column('fd_material', 'is_billing', 'char(4)', '是否计费：1=计费,2=不计费', '2');
/

/* 产品档案 fd_material 增加 默认所属仓库ID（用于科室盘盈可退库仓库） */
CALL add_table_column('fd_material', 'default_warehouse_id', 'bigint', '产品档案默认所属仓库ID（科室盘盈可退库仓库）', NULL);
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
/* 出库单号：与 table.sql 一致；旧库若仅有 bill_no 无本列，Mapper 列表用 bill_no+子查询回退，不依赖本列 */
CALL add_table_column('stk_dep_inventory', 'out_order_no', 'varchar(64)', '出库单号', NULL);
/
CALL add_table_column('stk_dep_inventory', 'bill_type', 'INT', '单据类型 201出库', NULL);
/
/* 科室库存表增加备注（出库单生成的可填写“本库存科室出库业务生成”） */
CALL add_table_column('stk_dep_inventory', 'remark', 'varchar(500)', '备注', NULL);
/
/* 耗材科室列表与租户关联 */
CALL add_table_column('fd_department', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 科室表备注（维护/导入） */
CALL add_table_column('fd_department', 'remark', 'varchar(500)', '备注', NULL);
/
/* 科室与第三方系统对照（导入与维护）；已废弃 his_dept_id，若库中仍存在该列则删除 */
/* SqlInitRunner 每条片段单独 execute，不可在同一段内写多条分号语句（需 allowMultiQueries） */
SET @__db := DATABASE();
/
SET @__exist_his_dept := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 'fd_department' AND COLUMN_NAME = 'his_dept_id'
);
/
SET @__drop_his_sql := IF(@__exist_his_dept > 0,
  'ALTER TABLE fd_department DROP COLUMN `his_dept_id`',
  'SELECT ''skip_fd_department_his_dept_id'' AS msg'
);
/
PREPARE __stmt_his FROM @__drop_his_sql;
/
EXECUTE __stmt_his;
/
DEALLOCATE PREPARE __stmt_his;
/
/* fd_department：third_party_dept_id 重命名为 his_id（HIS系统科室ID）；若仅有新库已含 his_id 则跳过 */
SET @__exist_tp := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 'fd_department' AND COLUMN_NAME = 'third_party_dept_id'
);
/
SET @__exist_his := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 'fd_department' AND COLUMN_NAME = 'his_id'
);
/
SET @__mig_sql := IF(@__exist_tp > 0 AND @__exist_his = 0,
  'ALTER TABLE fd_department CHANGE COLUMN `third_party_dept_id` `his_id` varchar(128) DEFAULT NULL COMMENT ''HIS系统科室ID''',
  IF(@__exist_his = 0,
    'ALTER TABLE fd_department ADD COLUMN `his_id` varchar(128) DEFAULT NULL COMMENT ''HIS系统科室ID''',
    'SELECT ''skip_fd_department_his_id'' AS msg'
  )
);
/
PREPARE __stmt_mig FROM @__mig_sql;
/
EXECUTE __stmt_mig;
/
DEALLOCATE PREPARE __stmt_mig;
/
CALL add_table_column('sys_user', 'his_id', 'varchar(128)', 'HIS系统用户ID', NULL);
/
CALL add_table_column('fd_warehouse_category', 'his_id', 'varchar(128)', 'HIS系统库房分类ID', NULL);
/
CALL add_table_column('fd_finance_category', 'his_id', 'varchar(128)', 'HIS系统财务分类ID', NULL);
/
CALL add_table_column('fd_finance_category', 'parent_id', 'bigint(20)', '上级财务分类ID', NULL);
/
CALL add_table_column('fd_department', 'parent_id', 'bigint(20)', '上级科室ID', NULL);
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
/* 科室批量消耗主表/明细：删除审计字段 */
CALL add_table_column('t_hc_ks_xh', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('t_hc_ks_xh', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('t_hc_ks_xh', 'reverse_flag', 'int', '是否反消耗单(0否1是)', '0');
/
CALL add_table_column('t_hc_ks_xh', 'reverse_of_consume_id', 'bigint', '反消耗来源主单ID', NULL);
/
CALL add_table_column('t_hc_ks_xh', 'reverse_of_bill_no', 'varchar(64)', '反消耗来源主单号', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'delete_time', 'datetime', '删除时间', NULL);
/
/* 科室批量消耗明细：科室库存冗余快照字段，保证后续追溯不依赖库存实时数据 */
CALL add_table_column('t_hc_ks_xh_entry', 'dep_inventory_id', 'bigint', '来源科室库存ID(stk_dep_inventory.id)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'kc_no', 'bigint', '来源仓库库存ID(stk_inventory.id)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'batch_id', 'bigint', '批次对象ID(stk_batch.id)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'warehouse_id', 'bigint', '库存归属仓库ID', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'supplier_id', 'varchar(128)', '供应商ID', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'factory_id', 'bigint', '生产厂家ID(fd_factory.factory_id)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_no', 'varchar(128)', '耗材批号', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_date', 'datetime', '耗材日期', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'warehouse_date', 'datetime', '入库日期', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'settlement_type', 'varchar(16)', '结算方式', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_name', 'varchar(256)', '耗材名称快照', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_speci', 'varchar(256)', '规格快照', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_model', 'varchar(256)', '型号快照', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'material_factory_id', 'bigint', '生产厂家ID快照(fd_factory.factory_id)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'src_consume_id', 'bigint', '反消耗来源主单ID(正向消耗主单)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'src_consume_bill_no', 'varchar(64)', '反消耗来源主单号(正向消耗单号)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'src_consume_entry_id', 'bigint', '反消耗来源明细ID(正向消耗明细ID)', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'src_consume_qty', 'decimal(18,2)', '正向消耗数量快照', NULL);
/
CALL add_table_column('t_hc_ks_xh_entry', 'src_can_reverse_qty', 'decimal(18,2)', '反消耗生成时可退数量快照', NULL);
/
/* 兼容历史库：t_hc_ks_xh_entry 早期字段为 batch_numer，统一迁移/补齐为 batch_number */
SET @__db := DATABASE();
/
SET @__ksxh_exist_old := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 't_hc_ks_xh_entry' AND COLUMN_NAME = 'batch_numer'
);
/
SET @__ksxh_exist_new := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @__db AND TABLE_NAME = 't_hc_ks_xh_entry' AND COLUMN_NAME = 'batch_number'
);
/
SET @__ksxh_batch_sql := IF(@__ksxh_exist_old > 0 AND @__ksxh_exist_new = 0,
  'ALTER TABLE t_hc_ks_xh_entry CHANGE COLUMN `batch_numer` `batch_number` varchar(100) DEFAULT NULL COMMENT ''批号''',
  IF(@__ksxh_exist_new = 0,
    'ALTER TABLE t_hc_ks_xh_entry ADD COLUMN `batch_number` varchar(100) DEFAULT NULL COMMENT ''批号''',
    'SELECT ''skip_t_hc_ks_xh_entry_batch_number'' AS msg'
  )
);
/
PREPARE __stmt_ksxh_batch FROM @__ksxh_batch_sql;
/
EXECUTE __stmt_ksxh_batch;
/
DEALLOCATE PREPARE __stmt_ksxh_batch;
/
/* 科室批量消耗明细 <-> 出库明细关联（主键UUID7；双方ID/明细ID/单号使用varchar） */
CREATE TABLE IF NOT EXISTS `t_hc_ks_xh_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7（36位）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `consume_id` varchar(36) DEFAULT NULL COMMENT '科室消耗主表ID（varchar冗余）',
  `consume_bill_no` varchar(64) DEFAULT NULL COMMENT '科室消耗单号',
  `consume_entry_id` varchar(36) DEFAULT NULL COMMENT '科室消耗明细ID（varchar冗余）',
  `consume_bill_date` date DEFAULT NULL COMMENT '科室消耗日期',
  `consume_status` int DEFAULT NULL COMMENT '科室消耗单状态',
  `src_bill_kind` varchar(32) DEFAULT NULL COMMENT '来源单据类型（OUT_WAREHOUSE）',
  `src_bill_id` varchar(36) DEFAULT NULL COMMENT '来源单据主表ID（出库单ID）',
  `src_bill_no` varchar(64) DEFAULT NULL COMMENT '来源单据号（出库单号）',
  `src_entry_id` varchar(36) DEFAULT NULL COMMENT '来源单据明细ID（出库单明细ID）',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID（冗余）',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID（冗余）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID（冗余）',
  `material_name` varchar(256) DEFAULT NULL COMMENT '耗材名称快照',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格快照',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号快照',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号（追溯）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号（追溯）',
  `material_no` varchar(128) DEFAULT NULL COMMENT '耗材批号',
  `supplier_id` varchar(128) DEFAULT NULL COMMENT '供应商ID',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID',
  `out_entry_qty` decimal(18,2) DEFAULT NULL COMMENT '来源出库明细数量',
  `available_qty` decimal(18,2) DEFAULT NULL COMMENT '库存剩余数量（引用时）',
  `default_consume_qty` decimal(18,2) DEFAULT NULL COMMENT '默认带出数量（min(库存剩余,出库明细数量)）',
  `consume_qty` decimal(18,2) DEFAULT NULL COMMENT '本次消耗数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `amount` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '辅条码',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_ks_xh_ref_tenant` (`tenant_id`),
  KEY `idx_ks_xh_ref_consume` (`consume_id`,`consume_entry_id`),
  KEY `idx_ks_xh_ref_src` (`src_bill_id`,`src_entry_id`),
  KEY `idx_ks_xh_ref_src_no` (`src_bill_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗明细-出库单明细关联表';
/
/* 批次表：租户隔离与历史追溯 */
CALL add_table_column('stk_batch', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/

/* ========== 批次追溯来源补充字段：用于从批次卡片直接判断来源动作 ==========
   说明：batch_source用于展示 RK/ZR 等来源lx；origin_* 为可审计的扩展信息。*/
CALL add_table_column('stk_batch', 'origin_bill_type', 'int', '来源单据类型（stk_io_bill.bill_type）', NULL);
/
CALL add_table_column('stk_batch', 'origin_flow_lx', 'varchar(16)', '来源流水lx（如RK/ZR/PY/QC等）', NULL);
/
CALL add_table_column('stk_batch', 'origin_business_type', 'varchar(64)', '来源业务类型中文（便于追溯展示）', NULL);
/
CALL add_table_column('stk_batch', 'origin_from_warehouse_id', 'bigint', '来源仓库ID（调拨等场景用）', NULL);
/
CALL add_table_column('stk_batch', 'origin_to_warehouse_id', 'bigint', '目标仓库ID/科室仓库ID（调拨等场景用）', NULL);
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
/* stk_io_stocktaking 主表：与 material/table.sql 对齐（旧库若仅有部分列时补齐） */
CALL add_table_column('stk_io_stocktaking', 'stock_no', 'varchar(64)', '盘点单号', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'suppler_id', 'bigint', '供应商ID', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'stock_date', 'date', '盘点日期', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'stock_status', 'int', '盘点状态', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'user_id', 'bigint', '操作人', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'stock_type', 'int', '盘点类型', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('stk_io_stocktaking', 'audit_date', 'datetime', '审核时间', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'remark', 'varchar(500)', '备注', NULL);
/
CALL add_table_column('stk_io_stocktaking', 'is_month_init', 'int', '是否月结', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* stk_io_profit_loss 主表：与 material/table.sql 对齐 */
CALL add_table_column('stk_io_profit_loss', 'bill_no', 'varchar(64)', '盈亏单号', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'stocktaking_id', 'bigint', '关联盘点单ID', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'stocktaking_no', 'varchar(64)', '盘点单号', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'bill_status', 'int', '单据状态 1待审核 2已审核', 1);
/
CALL add_table_column('stk_io_profit_loss', 'audit_by', 'varchar(64)', '审核人', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'audit_date', 'datetime', '审核时间', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'create_by', 'varchar(64)', '创建人', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'update_time', 'datetime', '更新时间', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'update_by', 'varchar(64)', '更新人', NULL);
/
CALL add_table_column('stk_io_profit_loss', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('wh_fixed_number', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('dept_fixed_number', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 定数表：备注与逻辑删除审计（存量库若仅有 del_flag 时补齐） */
CALL add_table_column('wh_fixed_number', 'remark', 'varchar(512)', '备注', NULL);
/
CALL add_table_column('wh_fixed_number', 'delete_by', 'varchar(64)', '删除人', NULL);
/
CALL add_table_column('wh_fixed_number', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('dept_fixed_number', 'remark', 'varchar(512)', '备注', NULL);
/
CALL add_table_column('dept_fixed_number', 'delete_by', 'varchar(64)', '删除人', NULL);
/
CALL add_table_column('dept_fixed_number', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_material_import', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* 流水与档案日志表：冗余租户ID 便于按客户查流水/日志 */
CALL add_table_column('t_hc_ck_flow', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'batch_id', 'BIGINT', '批次对象表ID（stk_batch.id）', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'origin_business_type', 'varchar(64)', '来源业务类型中文（便于追溯展示）', NULL);
/
/* 仓库/科室流水：逻辑删除审计 */
CALL add_table_column('t_hc_ck_flow', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('t_hc_ck_flow', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('t_hc_ks_flow', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_material_status_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_material_change_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* ========== 高值耗材相关表：租户关联 + 主条码/辅条码 ========== */
CALL add_table_column('gz_order', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('gz_order', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('gz_order', 'is_follow_flag', 'varchar(16)', '跟台标识', NULL);
/
CALL add_table_column('gz_order', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_order', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_order', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_order_entry', 'master_barcode', 'varchar(128)', '主条码', NULL);
/
CALL add_table_column('gz_order_entry', 'secondary_barcode', 'varchar(128)', '辅条码', NULL);
/
CALL add_table_column('gz_order_entry', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('gz_order_entry', 'bill_no', 'varchar(64)', '单号冗余', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'bill_no', 'varchar(64)', '单号冗余', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('gz_shipment_entry', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('gz_shipment_entry', 'bill_no', 'varchar(64)', '单号冗余', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'bill_no', 'varchar(64)', '单号冗余', NULL);
/
CALL add_table_column('gz_shipment_entry', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('gz_refund_stock_entry', 'department_id', 'bigint', '科室ID', NULL);
/
CALL add_table_column('gz_depot_inventory', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
/* ========== 耗材仓库结算方式：入库/出库/消耗；入库单、明细、仓库库存、科室库存写入结算方式 ========== */
CALL add_table_column('stk_io_bill', 'settlement_type', 'varchar(16)', '结算方式 1入库结算 2出库结算 3消耗结算（来自仓库）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'settlement_type', 'varchar(16)', '结算方式（与主表一致）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'material_name', 'varchar(256)', '产品名称（快照）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'material_speci', 'varchar(256)', '规格（快照）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'material_model', 'varchar(256)', '型号（快照）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'material_factory_id', 'bigint', '生产厂家ID（快照，fd_factory.factory_id）', NULL);
/
-- 出库单与科室申领 / 库房申请单追溯（引用库房申请出库、关联 wh_wh_apply_ck_entry_ref）
CALL add_table_column('stk_io_bill', 'd_apply_id', 'varchar(32)', '科室申领主表ID（bas_apply.id）', NULL);
/
CALL add_table_column('stk_io_bill', 'wh_warehouse_apply_id', 'varchar(36)', '库房申请单主键（wh_warehouse_apply.id）', NULL);
/
CALL add_table_column('stk_io_bill', 'wh_warehouse_apply_bill_no', 'varchar(64)', '库房申请单号（冗余）', NULL);
/
CALL add_table_column('stk_io_bill_entry', 'wh_apply_entry_id', 'varchar(36)', '库房申请单明细ID（引用出库时回填）', NULL);
/
CALL add_table_column('stk_inventory', 'settlement_type', 'varchar(16)', '结算方式（来自入库单）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'settlement_type', 'varchar(16)', '结算方式（来自出库单）', NULL);
/
CALL add_table_column('stk_inventory', 'material_name', 'varchar(256)', '产品名称（快照）', NULL);
/
CALL add_table_column('stk_inventory', 'material_speci', 'varchar(256)', '规格（快照）', NULL);
/
CALL add_table_column('stk_inventory', 'material_model', 'varchar(256)', '型号（快照）', NULL);
/
CALL add_table_column('stk_inventory', 'material_factory_id', 'bigint', '生产厂家ID（快照，fd_factory.factory_id）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'material_name', 'varchar(256)', '产品名称（快照）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'material_speci', 'varchar(256)', '规格（快照）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'material_model', 'varchar(256)', '型号（快照）', NULL);
/
CALL add_table_column('stk_dep_inventory', 'material_factory_id', 'bigint', '生产厂家ID（快照，fd_factory.factory_id）', NULL);
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
CALL add_table_column('bas_apply_entry', 'stock_warehouse_id', 'bigint', '科室申领明细可用库存所属仓库(fd_warehouse.id)，审核按该仓拆分避免串库', NULL);
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
CALL add_table_column('fd_supplier', 'his_id', 'varchar(128)', 'HIS供应商ID', NULL);
/

-- 供应商档案变更记录表 fd_supplier_change_log：全量建表见 material/table.sql

-- fd_factory 生产厂家
CALL add_table_column('fd_factory', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('fd_factory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_factory', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_factory', 'his_id', 'varchar(128)', 'HIS生产厂家ID', NULL);
/

-- 生产厂家档案变更记录表 fd_factory_change_log：全量建表见 material/table.sql

-- fd_unit 计量单位
CALL add_table_column('fd_unit', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('fd_unit', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_unit', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_unit', 'remark', 'varchar(500)', '备注', NULL);
/

-- fd_location 货位
CALL add_table_column('fd_location', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('fd_location', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_location', 'delete_time', 'datetime', '删除时间', NULL);
/

-- fd_warehouse_category
CALL add_table_column('fd_warehouse_category', 'remark', 'varchar(512)', '备注', NULL);
/
CALL add_table_column('fd_warehouse_category', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('fd_warehouse_category', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('fd_warehouse_category', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/

-- gz_dep_apply
CALL add_table_column('gz_dep_apply', 'del_flag', 'int', '删除标志', 0);
/
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
CALL add_table_column('gz_dep_apply_entry', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_dep_apply_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_dep_apply_entry', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
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
CALL add_table_column('gz_depot_inventory', 'del_flag', 'int', '删除标志', 0);
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
CALL add_table_column('gz_depot_inventory', 'supplier_id', 'bigint', '供应商ID', NULL);
/
CALL add_table_column('gz_depot_inventory', 'order_id', 'bigint', '备货单ID', NULL);
/
CALL add_table_column('gz_depot_inventory', 'order_no', 'varchar(64)', '备货单单号', NULL);
/
CALL add_table_column('gz_depot_inventory', 'order_entry_id', 'bigint', '备货单明细ID', NULL);
/
CALL add_table_column('gz_depot_inventory', 'inhospitalcode_list_id', 'bigint', '院内码列表ID', NULL);
/

-- gz_order_entry
CALL add_table_column('gz_order_entry', 'tenant_id', 'varchar(36)', '租户ID', NULL);
/
CALL add_table_column('gz_order_entry', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_order_entry', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
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
CALL add_table_column('gz_order_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
/

-- gz_order_entry_inhospitalcode_list
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'batch_number', 'varchar(100)', '批号', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'warehouse_id', 'bigint', '仓库ID', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'supplier_id', 'bigint', '供应商ID', NULL);
/
CALL add_table_column('gz_order_entry_inhospitalcode_list', 'del_flag', 'int', '删除标志', 0);
/
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
CALL add_table_column('gz_patient_info', 'del_flag', 'char(1)', '删除标志', '0');
/
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
CALL add_table_column('gz_refund_goods', 'del_flag', 'int', '删除标志', 0);
/
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
CALL add_table_column('gz_refund_goods_entry', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_refund_goods_entry', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
CALL add_table_column('gz_refund_goods_entry', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
/
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
CALL add_table_column('gz_refund_goods_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
/

-- gz_refund_stock
CALL add_table_column('gz_refund_stock', 'stock_no', 'varchar(64)', '退库单号(旧版字段)', NULL);
/
CALL add_table_column('gz_refund_stock', 'stock_date', 'datetime', '退库日期(旧版字段)', NULL);
/
CALL add_table_column('gz_refund_stock', 'stock_status', 'int', '状态(旧版字段)', NULL);
/
CALL add_table_column('gz_refund_stock', 'stock_type', 'int', '类型(旧版字段)', NULL);
/
CALL add_table_column('gz_refund_stock', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_refund_stock', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
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
CALL add_table_column('gz_refund_stock_entry', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
/
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
CALL add_table_column('gz_refund_stock_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
/

-- 表注释修正：gz_refund_stock_entry 为「退库」明细，勿与 gz_refund_goods_entry「退货」明细混用；CREATE IF NOT EXISTS 不会更新已存在表的注释
ALTER TABLE `gz_refund_stock_entry` COMMENT = '高值退库明细表';
/

-- gz_shipment
CALL add_table_column('gz_shipment', 'del_flag', 'int', '删除标志', 0);
/
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
CALL add_table_column('gz_shipment_entry', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_shipment_entry', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
/
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
CALL add_table_column('gz_shipment_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
/

-- gz_traceability
CALL add_table_column('gz_traceability', 'del_flag', 'int', '删除标志', 0);
/
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
CALL add_table_column('gz_traceability_entry', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_traceability_entry', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_traceability_entry', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
CALL add_table_column('gz_traceability_entry', 'in_hospital_code', 'varchar(200)', '院内码', NULL);
/
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
CALL add_table_column('gz_traceability_entry', 'supplier_id', 'bigint', '供应商ID', NULL);
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
/* stk_io_profit_loss_entry：与 material/table.sql 对齐（早期 create 脚本可能缺少下列列） */
CALL add_table_column('stk_io_profit_loss_entry', 'paren_id', 'bigint', '盈亏单ID', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'stocktaking_entry_id', 'bigint', '来源盘点明细ID', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'kc_no', 'bigint', '库存明细id', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'material_id', 'bigint', '耗材ID', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'batch_no', 'varchar(100)', '批次号', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'batch_number', 'varchar(100)', '批号', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'book_qty', 'decimal(18,2)', '账面数量', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'stock_qty', 'decimal(18,2)', '盘点数量', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'profit_qty', 'decimal(18,2)', '盈亏数量', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'unit_price', 'decimal(18,2)', '单价', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'profit_amount', 'decimal(18,2)', '盈亏金额', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'begin_time', 'date', '生产日期', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'end_time', 'date', '有效期', NULL);
/
CALL add_table_column('stk_io_profit_loss_entry', 'del_flag', 'int', '删除标志', 0);
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
/* stk_io_stocktaking_entry：数量/金额/批号等与 material/table.sql 对齐 */
CALL add_table_column('stk_io_stocktaking_entry', 'kc_no', 'bigint', '库存明细id', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'stock_qty', 'decimal(18,2)', '盘点数量', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'profit_qty', 'decimal(18,2)', '盈亏数量', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'stock_amount', 'decimal(18,2)', '盘点金额', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'profit_amount', 'decimal(18,2)', '盈亏金额', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'batch_number', 'varchar(100)', '批号', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'begin_time', 'date', '生产日期', NULL);
/
CALL add_table_column('stk_io_stocktaking_entry', 'end_time', 'date', '有效期', NULL);
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

-- 变更日志表补 tenant_id（多租户隔离）
CALL add_table_column('fd_department_change_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_supplier_change_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('fd_factory_change_log', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/

-- 高值科室库存补 tenant_id
CALL add_table_column('gz_dep_inventory', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/
CALL add_table_column('gz_dep_inventory', 'del_flag', 'int', '删除标志', 0);
/
CALL add_table_column('gz_dep_inventory', 'master_barcode', 'varchar(200)', '主条码', NULL);
/
CALL add_table_column('gz_dep_inventory', 'secondary_barcode', 'varchar(200)', '辅条码', NULL);
/
CALL add_table_column('gz_dep_inventory', 'supplier_id', 'bigint', '供应商ID', NULL);
/
CALL add_table_column('gz_dep_inventory', 'delete_by', 'varchar(64)', '删除者', NULL);
/
CALL add_table_column('gz_dep_inventory', 'delete_time', 'datetime', '删除时间', NULL);
/
CALL add_table_column('gz_dep_inventory', 'create_by', 'varchar(64)', '创建者', NULL);
/
CALL add_table_column('gz_dep_inventory', 'create_time', 'datetime', '创建时间', NULL);
/
CALL add_table_column('gz_dep_inventory', 'update_by', 'varchar(64)', '更新者', NULL);
/
CALL add_table_column('gz_dep_inventory', 'update_time', 'datetime', '更新时间', NULL);
/

-- 科室批量消耗明细补 tenant_id
CALL add_table_column('t_hc_ks_xh_entry', 'tenant_id', 'varchar(36)', '租户ID(同sb_customer.customer_id)', NULL);
/

-- 科室档案变更记录表 fd_department_change_log：全量建表见 material/table.sql

ALTER TABLE purchase_plan MODIFY COLUMN plan_status char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' NOT NULL COMMENT '计划状态（0未提交1待审核 2已审核 3已执行 4已取消）';
/

-- 盘盈新增明细/待入账表 stk_profit_loss_pending：全量建表见 material/table.sql

-- ========== sys_print_setting：存量表补 tenant_id 与索引（新库已在 table.sql 全量建表含 idx_tenant_bill）==========
CALL add_table_column('sys_print_setting', 'tenant_id', 'varchar(64)', '租户/客户ID，NULL表示全库默认模板', NULL);
/
-- SqlInitRunner 每条片段单独 execute，须拆成多段（同连接会话变量仍有效）；勿在同一段内写多条分号语句
SET @idx_ps_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE() AND table_name = 'sys_print_setting' AND index_name = 'idx_tenant_bill'
);
/
SET @sql_ps_idx := IF(@idx_ps_exists = 0,
  'CREATE INDEX idx_tenant_bill ON sys_print_setting (tenant_id, bill_type)',
  'SELECT 1');
/
PREPARE stmt_ps_idx FROM @sql_ps_idx;
/
EXECUTE stmt_ps_idx;
/
DEALLOCATE PREPARE stmt_ps_idx;
/

-- ========== 基础资料等「导入」按钮：默认对客户开放 ==========
-- 1) sys_menu：保证 default_open_to_customer=1（新租户/功能重置会按此字段下发 hc_customer_menu）
UPDATE sys_menu
SET default_open_to_customer = '1',
    update_time = NOW()
WHERE IFNULL(status, '0') = '0'
  AND (is_platform IS NULL OR is_platform != '1')
  AND perms IN (
    'foundation:depart:import',
    'foundation:supplier:import',
    'foundation:factory:import',
    'foundation:warehouseCategory:import',
    'foundation:financeCategory:import',
    'foundation:material:import',
    'system:user:import'
  );
/

-- 2) 存量租户：为已启用客户补授权（避免仅改菜单表后老客户仍无「导入」权限）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'foundation:depart:import',
    'foundation:supplier:import',
    'foundation:factory:import',
    'foundation:warehouseCategory:import',
    'foundation:financeCategory:import',
    'foundation:material:import',
    'system:user:import'
  )
  AND IFNULL(m.status, '0') = '0'
  AND (m.is_platform IS NULL OR m.is_platform != '1')
WHERE IFNULL(c.hc_status, '0') = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id
      AND h.menu_id = m.menu_id
  );
/

-- ========== 库房申请单（科室申领审核按仓拆分）==========
-- 全量建表：wh_warehouse_apply、wh_warehouse_apply_entry、wh_wh_apply_ck_entry_ref 见 material/table.sql
-- 存量库若表已存在但缺少作废/关联相关列，下列 CALL 安全补齐（已存在则跳过）
CALL add_table_column('wh_warehouse_apply', 'void_whole_flag', 'int NOT NULL DEFAULT 0', '整单作废：0否 1是', '0');
/
CALL add_table_column('wh_warehouse_apply', 'void_whole_by', 'varchar(64)', '整单作废人', NULL);
/
CALL add_table_column('wh_warehouse_apply', 'void_whole_time', 'datetime', '整单作废时间', NULL);
/
CALL add_table_column('wh_warehouse_apply', 'void_whole_reason', 'varchar(500)', '整单作废原因', NULL);
/

CALL add_table_column('wh_warehouse_apply_entry', 'line_void_status', 'int NOT NULL DEFAULT 0', '明细作废状态：0正常 1已作废', '0');
/
CALL add_table_column('wh_warehouse_apply_entry', 'line_void_qty', 'decimal(18,2) NOT NULL DEFAULT 0', '累计作废数量', '0');
/
CALL add_table_column('wh_warehouse_apply_entry', 'line_void_by', 'varchar(64)', '明细作废操作人', NULL);
/
CALL add_table_column('wh_warehouse_apply_entry', 'line_void_time', 'datetime', '明细作废时间', NULL);
/
CALL add_table_column('wh_warehouse_apply_entry', 'line_void_reason', 'varchar(500)', '明细作废原因', NULL);
/

-- wh_wh_apply_ck_entry_ref（出库关联表）全量建表见 material/table.sql；存量库若无此表请执行 table.sql 对应 CREATE TABLE 段
/

CALL add_table_column('gz_order', 'apply_department_id', 'bigint', '申请科室ID（备货验收）', NULL);
/

-- 高值单据明细引用关系（主键 UUID7 36 位；双方主键/明细 ID 使用字符串便于扩展）
CREATE TABLE IF NOT EXISTS `gz_order_entry_code_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户',
  `src_acceptance_id` varchar(36) DEFAULT NULL COMMENT '备货验收主表ID',
  `src_acceptance_no` varchar(64) DEFAULT NULL COMMENT '验收单号',
  `src_order_entry_id` varchar(36) DEFAULT NULL COMMENT '验收明细ID',
  `src_barcode_line_id` varchar(36) DEFAULT NULL COMMENT '条码明细ID(gz_order_entry_inhospitalcode_list)',
  `src_in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `tgt_bill_kind` varchar(32) DEFAULT NULL COMMENT '目标单据类型 GZ_SHIPMENT 等',
  `tgt_main_id` varchar(36) DEFAULT NULL COMMENT '目标主表ID',
  `tgt_bill_no` varchar(64) DEFAULT NULL COMMENT '目标单号',
  `tgt_entry_id` varchar(36) DEFAULT NULL COMMENT '目标明细ID',
  `ref_purpose` varchar(200) DEFAULT NULL COMMENT '引用用途（中文）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID冗余',
  `material_name` varchar(300) DEFAULT NULL COMMENT '耗材名称冗余',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库冗余',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_code_ref_barcode` (`src_barcode_line_id`),
  KEY `idx_gz_code_ref_tgt` (`tgt_bill_kind`,`tgt_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货验收条码明细引用关系';
/

CREATE TABLE IF NOT EXISTS `gz_shipment_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL,
  `shipment_entry_id` varchar(36) DEFAULT NULL COMMENT '备货出库明细ID',
  `src_bill_kind` varchar(32) DEFAULT NULL COMMENT '来源类型',
  `src_main_id` varchar(36) DEFAULT NULL,
  `src_bill_no` varchar(64) DEFAULT NULL,
  `src_detail_id` varchar(36) DEFAULT NULL,
  `src_in_hospital_code` varchar(200) DEFAULT NULL,
  `tgt_bill_kind` varchar(32) DEFAULT NULL,
  `tgt_main_id` varchar(36) DEFAULT NULL,
  `tgt_bill_no` varchar(64) DEFAULT NULL,
  `tgt_entry_id` varchar(36) DEFAULT NULL,
  `ref_purpose` varchar(200) DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  `material_name` varchar(300) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_ship_ref_entry` (`shipment_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货出库明细引用关系';
/

CREATE TABLE IF NOT EXISTS `gz_refund_goods_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL,
  `refund_goods_entry_id` varchar(36) DEFAULT NULL COMMENT '退库/退货明细ID',
  `src_bill_kind` varchar(32) DEFAULT NULL,
  `src_main_id` varchar(36) DEFAULT NULL,
  `src_bill_no` varchar(64) DEFAULT NULL,
  `src_detail_id` varchar(36) DEFAULT NULL,
  `src_in_hospital_code` varchar(200) DEFAULT NULL,
  `tgt_bill_kind` varchar(32) DEFAULT NULL,
  `tgt_main_id` varchar(36) DEFAULT NULL,
  `tgt_bill_no` varchar(64) DEFAULT NULL,
  `tgt_entry_id` varchar(36) DEFAULT NULL,
  `ref_purpose` varchar(200) DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  `material_name` varchar(300) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_rg_ref_entry` (`refund_goods_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货退库/退货明细引用关系';
/

INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '引用单据查询', COALESCE((SELECT parent_id FROM sys_menu WHERE perms = 'gzOrder:apply:list' LIMIT 1), 0), 90, '#', '', 1, 0, 'F', '0', '0', 'gz:refDoc:query', '#', 'admin', NOW(), '高值引用验收/出库低敏感查询'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'gz:refDoc:query');
/

-- ========== 高值模块最终核对清单（发布后自检 SQL）==========
-- 说明：以下 SQL 为“只读自检”，可在发布完成后执行，快速核验环境一致性。

-- 1) 高值核心表是否具备逻辑删除三件套（del_flag/delete_by/delete_time）
SELECT c.table_name,
       MAX(CASE WHEN c.column_name = 'del_flag' THEN 1 ELSE 0 END) AS has_del_flag,
       MAX(CASE WHEN c.column_name = 'delete_by' THEN 1 ELSE 0 END) AS has_delete_by,
       MAX(CASE WHEN c.column_name = 'delete_time' THEN 1 ELSE 0 END) AS has_delete_time
FROM information_schema.columns c
WHERE c.table_schema = DATABASE()
  AND c.table_name IN (
    'gz_order','gz_order_entry','gz_shipment','gz_shipment_entry',
    'gz_refund_goods','gz_refund_goods_entry','gz_traceability','gz_traceability_entry',
    'gz_depot_inventory','gz_dep_inventory','gz_dep_apply','gz_dep_apply_entry','gz_patient_info'
  )
GROUP BY c.table_name
ORDER BY c.table_name;
/

-- 2) 高值明细表条码/供应商字段完整性
SELECT c.table_name,
       MAX(CASE WHEN c.column_name = 'master_barcode' THEN 1 ELSE 0 END) AS has_master_barcode,
       MAX(CASE WHEN c.column_name = 'secondary_barcode' THEN 1 ELSE 0 END) AS has_secondary_barcode,
       MAX(CASE WHEN c.column_name = 'supplier_id' THEN 1 ELSE 0 END) AS has_supplier_id
FROM information_schema.columns c
WHERE c.table_schema = DATABASE()
  AND c.table_name IN (
    'gz_order_entry','gz_shipment_entry','gz_refund_goods_entry',
    'gz_traceability_entry','gz_dep_apply_entry','gz_depot_inventory','gz_dep_inventory'
  )
GROUP BY c.table_name
ORDER BY c.table_name;
/

-- 3) gz_order 扩展字段（department_id/is_follow_flag）检查
SELECT c.table_name,
       MAX(CASE WHEN c.column_name = 'department_id' THEN 1 ELSE 0 END) AS has_department_id,
       MAX(CASE WHEN c.column_name = 'is_follow_flag' THEN 1 ELSE 0 END) AS has_is_follow_flag
FROM information_schema.columns c
WHERE c.table_schema = DATABASE()
  AND c.table_name = 'gz_order'
GROUP BY c.table_name;
/

-- 4) 住院高值扫码/追溯权限菜单检查（应返回完整一组 perms）
SELECT m.menu_id, m.menu_name, m.parent_id, m.perms, m.menu_type, m.status
FROM sys_menu m
WHERE m.perms IN (
  'gz:traceability:list','gz:traceability:query','gz:traceability:add',
  'gz:traceability:edit','gz:traceability:remove','gz:traceability:audit',
  'gz:traceability:export','gz:traceability:printMaterial','gz:traceability:printBarcode'
)
ORDER BY m.menu_type, m.menu_id;
/

-- 5) 高值表 create_by/update_by/delete_by 是否存在“非数字用户ID”残留（按需抽样）
-- 说明：当前约定写 user_id（字符串）。若历史存在 user_name，此处可帮助发现异常数据。
SELECT 'gz_order' AS table_name, COUNT(*) AS suspect_rows
FROM gz_order
WHERE del_flag != 1
  AND (
    (create_by IS NOT NULL AND create_by <> '' AND create_by NOT REGEXP '^[0-9]+$')
    OR (update_by IS NOT NULL AND update_by <> '' AND update_by NOT REGEXP '^[0-9]+$')
    OR (delete_by IS NOT NULL AND delete_by <> '' AND delete_by NOT REGEXP '^[0-9]+$')
  );
/