-- ========== 耗材模块 数据完整性 ==========
-- 建议在 table.sql、column.sql、menu.sql 之后执行；按「/」分段执行
-- 数据完整性检查，为有默认值的字段赋值
-- 文末含：hc.login.defaultCustomerId、岗位 system:post:sync（与 spd/sql/maintenance/add_hc_default_customer_config_and_post_sync_menu.sql 一致）
-- 文末含：sys_print_setting 全库默认 + 衡水三院打印模板（与 spd/sql/maintenance/add_print_setting_tenant_id_and_seed_hs_receipt.sql 一致，已合并至本文件）
-- 物料表 是否启用
update fd_material fm set fm.is_use = '2' where fm.is_use is null;
/
-- 物料表 是否归组
update fd_material fm set fm.is_gz = '2' where fm.is_gz  is null;
/
-- 物料表 是否跟踪
update fd_material fm set fm.is_follow = '2' where fm.is_follow is null;
/
-- 物料表 是否计费
update fd_material fm set fm.is_billing = '2' where fm.is_billing is null;
/

UPDATE sys_dict_data
SET dict_label='启用'
WHERE dict_type='is_use_status' and dict_value='1' and dict_label='停用';
/


UPDATE sys_dict_data
SET dict_label='停用'
WHERE dict_type='is_use_status' and dict_value='2' and dict_label='启用';
/

-- 采购计划状态 plan_status（前端 dict.type.plan_status）：0未提交 1待审核 2已审核 3已执行 4已取消
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_time)
SELECT '采购计划状态', 'plan_status', '0', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'plan_status');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time, remark)
SELECT 1, '未提交', '0', 'plan_status', '0', NOW(), '采购计划状态：未提交'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '0');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time, remark)
SELECT 2, '待审核', '1', 'plan_status', '0', NOW(), '采购计划状态：待审核'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '1');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time, remark)
SELECT 3, '已审核', '2', 'plan_status', '0', NOW(), '采购计划状态：已审核'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '2');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time, remark)
SELECT 4, '已执行', '3', 'plan_status', '0', NOW(), '采购计划状态：已执行'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '3');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time, remark)
SELECT 5, '已取消', '4', 'plan_status', '0', NOW(), '采购计划状态：已取消'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '4');
/

-- 将客户管理、客户菜单功能管理挂到「系统管理」下（修复 parent_id，派生表避免同表 UPDATE 报错）
/
UPDATE sys_menu
SET parent_id = COALESCE((SELECT t.menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' LIMIT 1) t), 1)
WHERE menu_id IN (2100, 2101);
/

-- 确保 admin 用户（user_id=1）拥有管理员角色（role_id=1）
/
INSERT INTO sys_user_role (user_id, role_id)
SELECT 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);
/
-- 为 admin 角色（role_id=1）授予客户管理、客户菜单功能管理菜单（2100/2101/2102/2103/2104/2105）
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2100 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2100);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2102 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2102);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2101 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2101);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2103 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2103);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2104 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2104);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2105 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2105);
/

-- 科室维护（2230–2237）：挂到「基础资料」M 下，若无则系统管理（与 material/menu.sql 中 COALESCE 一致）
/
UPDATE sys_menu
SET parent_id = COALESCE(
  (SELECT t.menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '基础资料' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t),
  (SELECT t.menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t),
  1
)
WHERE menu_id = 2230;
/

-- 为 admin 角色授予科室维护全套按钮（含导入、更新简码）
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2230 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2230);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2231 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2231);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2232 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2232);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2233 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2233);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2234 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2234);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2235 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2235);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2236 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2236);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2237 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2237);
/

-- 客户管理、客户菜单功能管理设为仅平台管理（不对客户显示）；需先执行 column.sql 增加 is_platform 字段
/
UPDATE sys_menu SET is_platform = '1' WHERE menu_id IN (2100, 2101, 2102, 2103, 2104, 2105);
/

-- 将耗材菜单全部加到 admin 用户名下（用户权限表 sys_user_menu，耗材登录后仅读此表）
-- admin 假定为 user_id=1；插入 sys_menu 中 status='0' 的全部菜单，已存在则跳过
/
INSERT INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT 1, m.menu_id, NULL
FROM sys_menu m
WHERE m.status = '0'
  AND NOT EXISTS (SELECT 1 FROM sys_user_menu um WHERE um.user_id = 1 AND um.menu_id = m.menu_id);
/
-- 若表已通过 column.sql 增加 create_by、create_time，可执行以下更新（可选）
-- UPDATE sys_user_menu SET create_by = 'admin', create_time = NOW() WHERE user_id = 1 AND create_by IS NULL;
/

-- ========== 批次表 tenant_id 回填（便于多租户隔离与历史追溯） ==========
-- 从关联的入库单主表回填 stk_batch.tenant_id
UPDATE stk_batch b
INNER JOIN stk_io_bill bill ON bill.id = b.bill_id AND bill.tenant_id IS NOT NULL
SET b.tenant_id = bill.tenant_id
WHERE b.tenant_id IS NULL;
/

-- ========== 新增租户字段表 tenant_id 回填（从仓库/科室关联取数） ==========
-- 期初导入主表：从仓库取 tenant_id
UPDATE stk_initial_import m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 期初导入明细：从主表取 tenant_id
UPDATE stk_initial_import_entry e
INNER JOIN stk_initial_import b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 申领模板主表：从仓库取 tenant_id
UPDATE bas_apply_template m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室申领主表：从仓库取 tenant_id
UPDATE bas_apply m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室申领明细：从主表取 tenant_id
UPDATE bas_apply_entry e
INNER JOIN bas_apply b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 申领模板明细：从主表取 tenant_id
UPDATE bas_apply_template_entry e
INNER JOIN bas_apply_template b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 科室批量消耗主表：优先从仓库取，无则从科室取
UPDATE t_hc_ks_xh m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
UPDATE t_hc_ks_xh m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室批量消耗明细：从主表取 tenant_id
UPDATE t_hc_ks_xh_entry e
INNER JOIN t_hc_ks_xh b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 盘点主表：从仓库取 tenant_id
UPDATE stk_io_stocktaking m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 盘点明细：从主表取 tenant_id
UPDATE stk_io_stocktaking_entry e
INNER JOIN stk_io_stocktaking b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 盈亏主表：从仓库取 tenant_id
UPDATE stk_io_profit_loss m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 盈亏明细：从主表取 tenant_id
UPDATE stk_io_profit_loss_entry e
INNER JOIN stk_io_profit_loss b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 仓库定数：从仓库取 tenant_id
UPDATE wh_fixed_number m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室定数：从科室取 tenant_id（表结构为 warehouse_id 则用仓库；若为 department_id 则用科室）
UPDATE dept_fixed_number m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 仓库流水：从仓库取 tenant_id
UPDATE t_hc_ck_flow f
INNER JOIN fd_warehouse w ON w.id = f.warehouse_id AND w.tenant_id IS NOT NULL
SET f.tenant_id = w.tenant_id
WHERE f.tenant_id IS NULL;
/
-- 科室流水：从科室取 tenant_id（表有 department_id）
UPDATE t_hc_ks_flow f
INNER JOIN fd_department d ON d.id = f.department_id AND d.tenant_id IS NOT NULL
SET f.tenant_id = d.tenant_id
WHERE f.tenant_id IS NULL;
/
-- ========== 高值耗材表 tenant_id 回填 ==========
-- gz_order：从仓库取 tenant_id
UPDATE gz_order m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- gz_depot_inventory：从仓库取 tenant_id
UPDATE gz_depot_inventory m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- gz_dep_inventory：优先从科室取 tenant_id
UPDATE gz_dep_inventory m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值订单明细：从主表取 tenant_id
UPDATE gz_order_entry e
INNER JOIN gz_order b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 高值订单明细：从主表回填仓库ID、单号冗余（与备货/入库明细展示、追溯一致）
UPDATE gz_order_entry e
INNER JOIN gz_order b ON b.id = e.paren_id AND b.del_flag != 1
SET e.warehouse_id = COALESCE(e.warehouse_id, b.warehouse_id),
    e.bill_no = CASE WHEN (e.bill_no IS NULL OR TRIM(e.bill_no) = '') AND b.order_no IS NOT NULL THEN b.order_no ELSE e.bill_no END
WHERE e.del_flag != 1;
/

-- ========== 财务/结算表 tenant_id 回填（历史数据与多租户隔离） ==========
-- 仓库结算单主表：从仓库取 tenant_id
UPDATE wh_settlement_bill m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 仓库结算单明细：从主表取 tenant_id
UPDATE wh_settlement_bill_entry e
INNER JOIN wh_settlement_bill b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 供应商结算单主表：从来源仓库结算单取 tenant_id
UPDATE supp_settlement_bill m
INNER JOIN wh_settlement_bill w ON w.id = m.wh_settlement_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 供应商结算单明细：从主表取 tenant_id
UPDATE supp_settlement_bill_entry e
INNER JOIN supp_settlement_bill b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 供应商结算单与发票关联表：从供应商结算单主表取 tenant_id
UPDATE supp_settlement_invoice si
INNER JOIN supp_settlement_bill b ON b.id = si.supp_settlement_id AND b.tenant_id IS NOT NULL
SET si.tenant_id = b.tenant_id
WHERE si.tenant_id IS NULL;
/

-- ========== 采购/科室申请 tenant_id 回填（多租户隔离） ==========
-- 采购计划：从仓库取 tenant_id
UPDATE purchase_plan m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 采购计划明细：从主表取 tenant_id
UPDATE purchase_plan_entry e
INNER JOIN purchase_plan b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 采购订单：从仓库取 tenant_id
UPDATE purchase_order m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 采购订单明细：从主表取 tenant_id
UPDATE purchase_order_entry e
INNER JOIN purchase_order b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/

-- 三类变更日志表 tenant_id 回填（从主数据取）
UPDATE fd_department_change_log l
INNER JOIN fd_department d ON d.id = l.department_id AND d.tenant_id IS NOT NULL
SET l.tenant_id = d.tenant_id
WHERE l.tenant_id IS NULL;
/
UPDATE fd_supplier_change_log l
INNER JOIN fd_supplier s ON s.id = l.supplier_id AND s.tenant_id IS NOT NULL
SET l.tenant_id = s.tenant_id
WHERE l.tenant_id IS NULL;
/
UPDATE fd_factory_change_log l
INNER JOIN fd_factory f ON f.factory_id = l.factory_id AND f.tenant_id IS NOT NULL
SET l.tenant_id = f.tenant_id
WHERE l.tenant_id IS NULL;
/
-- 高值科室申领：从仓库取 tenant_id
UPDATE gz_dep_apply m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值科室申领明细：从主表取 tenant_id
UPDATE gz_dep_apply_entry e
INNER JOIN gz_dep_apply b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 科室采购申请：从仓库取 tenant_id
UPDATE dep_purchase_apply m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室采购申请明细：从主表取 tenant_id
UPDATE dep_purchase_apply_entry e
INNER JOIN dep_purchase_apply b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 科室库存预警：从科室取 tenant_id
UPDATE dep_inventory_warning m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 新产品申请：从科室取 tenant_id
UPDATE new_product_apply m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 新产品申请明细/条目：从主表取 tenant_id
UPDATE new_product_apply_entry e
INNER JOIN new_product_apply b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
UPDATE new_product_apply_detail e
INNER JOIN new_product_apply b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/

-- ========== 高值退货/出库/追溯等 tenant_id 回填 ==========
-- 高值退货主表：优先从仓库取，无则从科室取
UPDATE gz_refund_goods m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
UPDATE gz_refund_goods m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值退货明细：从主表取 tenant_id
UPDATE gz_refund_goods_entry e
INNER JOIN gz_refund_goods b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 备货退库/退货明细：从主表回填科室、仓库、单号（与 gz_refund_stock_entry 字段语义对齐）
UPDATE gz_refund_goods_entry e
INNER JOIN gz_refund_goods b ON b.id = e.paren_id AND b.del_flag != 1
SET e.department_id = COALESCE(e.department_id, b.department_id),
    e.warehouse_id = COALESCE(e.warehouse_id, b.warehouse_id),
    e.bill_no = CASE WHEN (e.bill_no IS NULL OR TRIM(e.bill_no) = '') AND b.goods_no IS NOT NULL THEN b.goods_no ELSE e.bill_no END
WHERE e.del_flag != 1;
/
-- 高值出库主表：优先从仓库取，无则从科室取
UPDATE gz_shipment m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
UPDATE gz_shipment m
INNER JOIN fd_department d ON d.id = m.department_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值出库明细：从主表取 tenant_id
UPDATE gz_shipment_entry e
INNER JOIN gz_shipment b ON b.id = e.paren_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 高值追溯主表：优先从申请科室取，无则从执行科室取
UPDATE gz_traceability m
INNER JOIN fd_department d ON d.id = m.apply_dept_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
UPDATE gz_traceability m
INNER JOIN fd_department d ON d.id = m.exec_dept_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值追溯明细：从主表取 tenant_id
UPDATE gz_traceability_entry e
INNER JOIN gz_traceability b ON b.id = e.parent_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 发票表：从供应商结算单关联取 tenant_id
UPDATE fin_invoice inv
INNER JOIN supp_settlement_invoice si ON si.invoice_id = inv.id
INNER JOIN supp_settlement_bill b ON b.id = si.supp_settlement_id AND b.tenant_id IS NOT NULL
SET inv.tenant_id = b.tenant_id
WHERE inv.tenant_id IS NULL;
/
-- 患者信息：从申请科室取，无则从执行科室取
UPDATE gz_patient_info m
INNER JOIN fd_department d ON d.id = m.apply_dept_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
UPDATE gz_patient_info m
INNER JOIN fd_department d ON d.id = m.exec_dept_id AND d.tenant_id IS NOT NULL
SET m.tenant_id = d.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 耗材档案状态/变更日志：从产品档案取 tenant_id
UPDATE fd_material_status_log e
INNER JOIN fd_material b ON b.id = e.material_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
UPDATE fd_material_change_log e
INNER JOIN fd_material b ON b.id = e.material_id AND b.tenant_id IS NOT NULL
SET e.tenant_id = b.tenant_id
WHERE e.tenant_id IS NULL;
/
-- 用户-工作组关联：从用户表回填 tenant_id（需已执行 column.sql 为 sys_user_post 增加 tenant_id）
UPDATE sys_user_post up
INNER JOIN sys_user u ON u.user_id = up.user_id AND u.del_flag = '0' AND u.customer_id IS NOT NULL AND TRIM(u.customer_id) != ''
SET up.tenant_id = u.customer_id
WHERE up.tenant_id IS NULL OR TRIM(up.tenant_id) = '';
/
update fd_material fm set is_gz = '2' where fm.is_gz is null or fm.is_gz = '';
/

-- ========== 入库明细/库存/流水 供应商对齐（可选，修复历史数据） ==========
-- 已审核入库单：明细 suppler_id 为空时用主表 suppler_id
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 101 AND b.bill_status = 2 AND b.suppler_id IS NOT NULL
SET e.suppler_id = CAST(b.suppler_id AS CHAR)
WHERE (e.suppler_id IS NULL OR TRIM(COALESCE(e.suppler_id, '')) = '')
  AND (e.del_flag IS NULL OR e.del_flag != 1);
/
-- 仓库库存：supplier_id 为空时，从已审入库单主表按批次+耗材汇总回填（多单一证时取 MAX(suppler_id)，可人工复核）
UPDATE stk_inventory s
INNER JOIN (
  SELECT e.batch_no AS bn, e.material_id AS mid, MAX(b.suppler_id) AS sid
  FROM stk_io_bill_entry e
  INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 101 AND b.bill_status = 2 AND b.suppler_id IS NOT NULL
  WHERE e.batch_no IS NOT NULL AND (e.del_flag IS NULL OR e.del_flag != 1)
  GROUP BY e.batch_no, e.material_id
) x ON x.bn = s.batch_no AND x.mid = s.material_id
SET s.supplier_id = x.sid
WHERE s.supplier_id IS NULL AND (s.del_flag IS NULL OR s.del_flag != 1);
/

-- ========== 采购计划状态字典（purchase_plan.plan_status） ==========
-- 代码逻辑：0=未提交 1=待审核 2=已审核 3=已执行 4=已取消（与字段注释 1待审核 2已审核 3已执行 4已取消 一致，仅实体类注释有笔误写为 1未提交，此处按实际逻辑补全）
-- 若不存在字典类型则先插入
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time)
SELECT '采购计划状态', 'plan_status', '0', '计划状态（0未提交 1待审核 2已审核 3已执行 4已取消）', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'plan_status');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time)
SELECT 1, '未提交', '0', 'plan_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '0');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time)
SELECT 2, '待审核', '1', 'plan_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '1');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time)
SELECT 3, '已审核', '2', 'plan_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '2');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time)
SELECT 4, '已执行', '3', 'plan_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '3');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time)
SELECT 5, '已取消', '4', 'plan_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value = '4');
/

-- ========== 耗材登录默认客户 + 岗位「同步仓库/科室/菜单」按钮（与 maintenance/add_hc_default_customer_config_and_post_sync_menu.sql 一致）==========
-- 参数 hc.login.defaultCustomerId：登录页默认组织机构（sb_customer.customer_id）；空=不默认。修改后请在参数设置刷新缓存
/
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT
  '耗材登录默认客户',
  'hc.login.defaultCustomerId',
  '',
  'N',
  'admin',
  NOW(),
  '登录页组织机构默认值，填写 sb_customer.customer_id；空表示不默认。可在参数设置中通过下拉选择（键名为 hc.login.defaultCustomerId 时）。'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'hc.login.defaultCustomerId');
/

SET @post_list_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE perms = 'system:post:list' AND menu_type = 'C'
  ORDER BY menu_id
  LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2310,
  '工作组同步权限',
  @post_list_menu_id,
  90,
  '#',
  '',
  NULL,
  1, 0, 'F', '0', '0', 'system:post:sync', '#',
  'admin', NOW(), '1', NOW(),
  '将岗位已授权的仓库/科室/菜单批量写入组内用户（耗材端与设备端岗位页按钮一致）',
  '0', '1'
FROM DUAL
WHERE @post_list_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2310 OR perms = 'system:post:sync')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  update_time = NOW();
/

-- admin 角色授予「工作组同步权限」按钮（menu_id=2310）
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2310 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2310);
/

-- ========== 打印模板 sys_print_setting：全库默认 + 衡水市第三人民医院（hengsui-third-001）==========
-- tenant_id 为空=全库默认；未单独配置客户打印时回落全库默认（与后端 selectEffectiveDefault 一致）
/
INSERT INTO sys_print_setting (
  template_name, tenant_id, bill_type, page_width, page_height, orientation,
  margin_top, margin_bottom, margin_left, margin_right,
  font_size, table_font_size, column_spacing,
  show_purchaser, show_creator, show_auditor, show_receiver,
  is_default, status, create_time, remark
)
SELECT
  '耗材入库单-全库默认', NULL, 101, 210.00, 297.00, 'portrait',
  0, 0, 0, 0,
  22, 12, 0,
  0, 1, 1, 0,
  1, '0', NOW(), '全库默认；与 orderPrint.vue 默认样式一致'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_print_setting s
  WHERE s.bill_type = 101 AND s.is_default = 1 AND s.status = '0'
    AND (s.tenant_id IS NULL OR s.tenant_id = '')
);
/
INSERT INTO sys_print_setting (
  template_name, tenant_id, bill_type, page_width, page_height, orientation,
  margin_top, margin_bottom, margin_left, margin_right,
  font_size, table_font_size, column_spacing,
  show_purchaser, show_creator, show_auditor, show_receiver,
  is_default, status, create_time, remark
)
SELECT
  '耗材出库单-全库默认', NULL, 201, 210.00, 297.00, 'portrait',
  0, 0, 0, 0,
  16, 11, 0,
  0, 1, 1, 0,
  1, '0', NOW(), '全库默认耗材出库：宋体紧凑；标题约15px/表体11px；与 outOrderPrint.vue 一致'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_print_setting s
  WHERE s.bill_type = 201 AND s.is_default = 1 AND s.status = '0'
    AND (s.tenant_id IS NULL OR s.tenant_id = '')
);
/
INSERT INTO sys_print_setting (
  template_name, tenant_id, bill_type, page_width, page_height, orientation,
  margin_top, margin_bottom, margin_left, margin_right,
  font_size, table_font_size, column_spacing,
  show_purchaser, show_creator, show_auditor, show_receiver,
  is_default, status, create_time, remark
)
SELECT
  '耗材入库单-衡水市第三人民医院', 'hengsui-third-001', 101, 210.00, 297.00, 'portrait',
  0, 0, 0, 0,
  22, 12, 0,
  0, 1, 1, 0,
  1, '0', NOW(), '衡水三院专属；参数与全库默认一致，便于后续单独调版'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_print_setting s
  WHERE s.bill_type = 101 AND s.is_default = 1 AND s.status = '0'
    AND s.tenant_id = 'hengsui-third-001'
);
/
INSERT INTO sys_print_setting (
  template_name, tenant_id, bill_type, page_width, page_height, orientation,
  margin_top, margin_bottom, margin_left, margin_right,
  font_size, table_font_size, column_spacing,
  show_purchaser, show_creator, show_auditor, show_receiver,
  is_default, status, create_time, remark
)
SELECT
  '耗材出库单-衡水市第三人民医院', 'hengsui-third-001', 201, 210.00, 297.00, 'portrait',
  0, 0, 0, 0,
  16, 11, 0,
  0, 1, 1, 0,
  1, '0', NOW(), '衡水三院耗材出库：宋体紧凑；标题约15px/表体11px；与 outOrderPrint.vue 一致'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_print_setting s
  WHERE s.bill_type = 201 AND s.is_default = 1 AND s.status = '0'
    AND s.tenant_id = 'hengsui-third-001'
);
/
-- 已存在库：刷新耗材出库单模板字号（与上表 INSERT 一致）
/
UPDATE sys_print_setting
SET font_size = 16,
    table_font_size = 11,
    remark = '耗材出库 receipt：宋体紧凑；正文16/表11；与 outOrderPrint.vue 一致'
WHERE bill_type = 201
  AND is_default = 1
  AND status = '0'
  AND (tenant_id IS NULL OR tenant_id = '');
/
UPDATE sys_print_setting
SET font_size = 16,
    table_font_size = 11,
    remark = '衡水三院耗材出库：宋体紧凑；正文16/表11；与 outOrderPrint.vue 一致'
WHERE bill_type = 201
  AND is_default = 1
  AND status = '0'
  AND tenant_id = 'hengsui-third-001';
/
