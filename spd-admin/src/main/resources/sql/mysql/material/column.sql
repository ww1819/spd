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
