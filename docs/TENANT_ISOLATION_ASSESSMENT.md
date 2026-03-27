# 耗材 / 设备 多租户（客户 ID）评估说明

> 依据：仓库内 `material/table.sql`、`equipment/table.sql`、`material/column.sql`、`material/data_integrity.sql` 及 `spd-biz` Mapper/Service 静态扫描。**非线上库直连扫描**；上线前请在目标库执行 `information_schema` 校验。

## 1. 术语

- **客户 ID**：`sb_customer.customer_id`，耗材侧字段名多为 `tenant_id`（varchar(36)），设备侧多为 `customer_id`（char(36)），语义一致。
- **隔离**：SQL 中带当前租户条件 + 写入时回填；**回写**：`UPDATE … JOIN` 将 NULL 补成父表/仓库推导的租户。

## 2. 已合并到 `material/table.sql` 的散落建表

以下原在 `spd/sql/*.sql`，现已纳入完整建表脚本，避免脚本漂移：

| 表名 | 原脚本参考 |
|------|------------|
| `stk_io_stocktaking` / `stk_io_stocktaking_entry` | 业务与 `StkIoStocktakingMapper.xml` 对齐 |
| `stk_io_profit_loss` / `stk_io_profit_loss_entry` | 原 `create_stk_io_profit_loss.sql` + Mapper 租户列 |
| `t_hc_ks_xh` / `t_hc_ks_xh_entry` | 原 `科室批量消耗.sql`，明细批号列为 `batch_number` |

## 3. 设备系统 `equipment/table.sql`

仓库内设备 SaaS 表已全部集中在该文件；**无额外散落设备业务表**需合并。已在文件头尾补充覆盖说明。

## 4. 需要补充客户 ID（列）的表（耗材侧）

以下在**完整新库**中建议在 `table.sql` 已体现或通过 `column.sql` 的 `add_table_column` 补齐；存量库执行 `spd/sql/tenant_id_backfill_consumables.sql`（见同目录）：

| 表 | 说明 |
|----|------|
| `stk_initial_import` / `stk_initial_import_entry` | 已写入 `material/table.sql` 的 `tenant_id`；Mapper/Service 需一律带租户条件并插入回填 |
| `bas_apply_template` / `bas_apply_template_entry` | 同上 |
| `fd_department_change_log` / `fd_supplier_change_log` / `fd_factory_change_log` | 建议 `tenant_id`（或冗余）+ 按主实体 `JOIN` 回写；查询侧按租户过滤 |
| `fd_material_import` | `column.sql` 已补列；需导入流程写 `tenant_id` |
| `sys_print_setting` | 若多租户模板隔离，需 `tenant_id` + 查询条件；否则保持全局并文档约定 |

**未写入 `material/table.sql` 的基础表**（通常来自若依初始化）：`fd_material`、`fd_warehouse`、`fd_material_category` 等——租户列在 `column.sql` / `data_integrity.sql` 中维护。

## 5. 已有 `tenant_id` 但隔离/回填仍薄弱的点

| 风险 | 建议 |
|------|------|
| 全局唯一键未含租户（如 `uk_stk_batch_no(batch_no)`） | 改为 `(tenant_id, batch_no)` 等复合唯一（需清洗冲突数据） |
| `hc_user_permission_menu` 唯一键 `(user_id, menu_id)` 未含租户 | 同一用户多租户会冲突；改为 `(tenant_id, user_id, menu_id)` 或按租户拆用户 |
| `sys_user` / 部分系统 Mapper 按主键更新无 `customer_id` | SQL 默认加租户条件或统一视图 |
| 期初/模板等历史行 `tenant_id` 为空 | 用仓库/主表 `JOIN` 回写（见 `tenant_id_backfill_consumables.sql`） |

## 6. 代码侧已补的回写（本次）

- **盘盈批次**：`StkIoProfitLossServiceImpl.buildStkBatchForProfit` 增加 `setTenantId(bill.getTenantId())`。
- **入库/调拨新建批次**：`StkIoBillServiceImpl.buildStkBatchForInbound` 增加租户回填。
- **期初导入新建批次**：`StkInitialImportServiceImpl.buildStkBatchForInitial` 增加租户回填。

## 7. 后续建议（优先级）

1. 全库扫描 `tenant_id IS NULL` 的业务表，按 `data_integrity.sql` 模式批量回写。  
2. 将高风险全局唯一键改为复合唯一（含租户）。  
3. 为变更日志类表增加 `tenant_id` 并在插入时写入。  
4. Quartz / 定时任务维度租户化或任务参数带 `tenantId`。
