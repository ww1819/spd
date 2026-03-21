# 耗材 sys_menu 梳理说明（基于导出 `sys_menu_202603212150 - 副本.csv`）

## 1. 统计摘要

| 项目 | 数量 |
|------|------|
| 总行数（含停用） | 652 |
| status=0 正常行 | 652 |
| **重复 perms**（非空、多行） | **48** |
| 其中「耗材类」前缀（warehouse/foundation/finance/hc/caigou/inWarehouse/department 等） | 45 |

## 2. 重复类型说明（勿盲目按 min(menu_id) 合并）

### A. 真重复：两套「基础资料」菜单（menu.sql 与历史手工/增量 ID）

同一 `component`、同一父「基础资料」下出现 **两套** C 菜单（如 1092 与 2230 均为 `foundation/depart/index`），应 **保留较小/较早 ID**，将较大 ID 整条树合并删除。

**合并顺序**：先对 **子级 F 按钮** 调用 `sp_hc_merge_sys_menu(保留, 删除)`，再合并 **C 主菜单**。

### B. 配置错误：F 按钮与父级 C 共用了 `:list`（不是合并关系）

例如：

- `1568`「申领单审核查询」与父级 `1566` 同为 `department:dApplyAudit:list`
- `1581` 与父级 `1563` 同为 `department:consumeDetail:list`

此类 **不能** 用「把 F 合并进 C」的语义处理；推荐 **删除多余 F 行**（权限仍由父级 C 的 `list` 覆盖），或改为独立 `:query` 并在前后端对齐。

### C. 目录 M 与页面 C 共用同一 perms

- `1239` 为 **M**「盈亏处理」，`1552` 为 **C**「盈亏报表」，均带 `warehouse:profitLoss:list` → **目录应清空 perms**，保留在子菜单/页面。

### D. 发票「按钮」与「页面」共用 `finance:invoice:add`

- `2203` 为 F「发票新增」，`2207` 为 C「发票录入」→ **页面 C** 应使用 `finance:invoice:list`（与 `FinInvoiceController` 列表接口一致）。

### E. 监控缓存（若依默认）

- `113`「缓存监控」与 `114`「缓存列表」常同为 `monitor:cache:list`，属若依常见重复；是否改 perms 或删其一由平台侧决定，**本脚本仅注释说明**。

### F. 备货退库 / 备货退货

- `1197`、`1198` 均为 `gzOrder:goodsApply:list`，需业务上拆权限或合并菜单，**本脚本不自动改库**。

---

## 3. 本地复现扫描

```bash
cd spd/scripts
python analyze_sys_menu_csv.py "sys_menu_202603212150 - 副本.csv"
```

---

## 4. 执行清理

1. **全库备份**。
2. 先确保已存在存储过程：`dedupe_hc_material_sys_menu.sql` 中的 `sp_hc_merge_sys_menu`。
3. 执行：`fix_sys_menu_dupes_from_export_20260321.sql`（按文件内分段与说明执行）。
