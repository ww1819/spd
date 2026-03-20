# 耗材库存/流水字段对齐说明

## 对齐维度（业务含义）

| 维度 | 字段/来源 |
|------|-----------|
| 批号（生产批号） | `batch_number` |
| 生产日期/有效期 | `begin_time` / `end_time`（库存、流水、批次表） |
| 主条码/辅条码 | `main_barcode` / `sub_barcode`（仓库库存、科室库存、流水） |
| 归属仓库 | `warehouse_id` |
| 供应商 | `supplier_id`（科室流水 `t_hc_ks_flow.supplier_id` 仍为 varchar 兼容历史） |
| 单价 | `unit_price` |
| 生产厂家 | `factory_id` → `fd_factory` |

## 本次 DDL 变更（新库见 `material/table.sql`，存量库执行 `material/column.sql`）

- **`stk_inventory`**：`factory_id`（冗余，与批次/档案一致）
- **`stk_dep_inventory`**：`warehouse_id`、`begin_time`、`end_time`、`factory_id`
- **`t_hc_ck_flow`**：`factory_id`
- **`t_hc_ks_flow`**：`warehouse_id`、`factory_id`

## 代码修复要点

1. **`HcKsFlowMapper.xml`**：`batch_id` 列与 VALUES 曾不一致，已补全 `#{batchId}`；并支持 `warehouse_id`、`factory_id` 写入。
2. **`StkIoBillServiceImpl`**：入库写入 `stk_inventory.factory_id`（来自 `stk_batch`）；各类仓库流水补 `factory_id`、条码；科室库存写入 `warehouse_id`、效期、`factory_id`；科室流水补 `warehouse_id`、`factory_id`、条码。
3. **`resolveFactoryId(StkInventory)`**：优先库存行 `factory_id`，否则按 `batch_id` 查 `stk_batch`。

## 高值耗材（gz_*）

高值表多在 `column.sql` 增量与运行时建表，需单独核对 `gz_depot_inventory`、`gz_order_entry`、跟台/计费等表是否均含上述维度；`gz_order_entry` 当前 INSERT 未含 `factory_id` 时，可按业务需要扩展（见后续迭代）。

## 升级步骤

1. 备份数据库。
2. 执行 `spd-admin/src/main/resources/sql/mysql/material/column.sql` 中新增 `CALL add_table_column(...)` 段（或整文件，已幂等）。
3. 重启应用。

## 前端对齐（spd-ui）

- **仓库库存** `firstInventory.vue`：查询「生产批号」→ `batchNumber`；列表区分「入库批次号 `batchNo` / 生产批号 `batchNumber` / 耗材批次号 `materialNo`」，并展示主/辅条码。
- **科室库存** `depInventory/components/InventoryDetail.vue`、`SelectDepInventory.vue`：展示归属仓库、生产批号/耗材批次号、主/辅条码；生产厂家优先 `fdFactory`（后端 COALESCE 库存行与档案）；供应商走 `supplier`；效期列使用 `beginDate`/`endDate`（与后端字段一致）。
- **选库存弹窗** `SelectInventory.vue`：同上字段展示。

## 后端列表接口增强（与前端配套）

- `StkInventoryMapper.selectStkInventoryVo`：扩展关联耗材规格/单位/厂家/库房与财务分类，`COALESCE(stk.factory_id, m.factory_id)` 厂家；支持按 `batchNumber` 模糊查询。
- `StkDepInventoryMapper.selectStkDepInventoryVo`：补全 `batch_number`、归属仓库、厂家 COALESCE、供应商字符串关联；列表支持 `warehouseId`/`supplierId`/`batchNumber` 条件。
