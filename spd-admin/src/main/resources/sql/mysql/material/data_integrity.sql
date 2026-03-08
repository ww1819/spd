-- ========== 耗材模块 数据完整性 ==========
-- 建议在 table.sql、column.sql、menu.sql 之后执行；按「/」分段执行
-- 数据完整性检查，为有默认值的字段赋值
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

-- 客户管理、客户菜单功能管理设为仅平台管理（不对客户显示）；需先执行 column.sql 增加 is_platform 字段
/
UPDATE sys_menu SET is_platform = '1' WHERE menu_id IN (2100, 2101, 2102, 2103, 2104, 2105);
/

-- 将耗材菜单全部加到 admin 用户名下（用户权限表 sys_user_menu，耗材登录后仅读此表）
-- admin 假定为 user_id=1；插入 sys_menu 中 status='0' 的全部菜单，已存在则跳过
/
INSERT INTO sys_user_menu (user_id, menu_id)
SELECT 1, m.menu_id
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
-- 申领模板主表：从仓库取 tenant_id
UPDATE bas_apply_template m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
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
-- 盘点主表：从仓库取 tenant_id
UPDATE stk_io_stocktaking m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 盈亏主表：从仓库取 tenant_id
UPDATE stk_io_profit_loss m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
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
-- 采购订单：从仓库取 tenant_id
UPDATE purchase_order m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 高值科室申领：从仓库取 tenant_id
UPDATE gz_dep_apply m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
/
-- 科室采购申请：从仓库取 tenant_id
UPDATE dep_purchase_apply m
INNER JOIN fd_warehouse w ON w.id = m.warehouse_id AND w.tenant_id IS NOT NULL
SET m.tenant_id = w.tenant_id
WHERE m.tenant_id IS NULL;
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
