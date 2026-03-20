# 数据库维护与搭建说明

## 1. 审计字段（create_by / update_by / delete_by）

业务要求：**统一存 `sys_user.user_id` 的字符串形式**，与 `SecurityUtils.getUserIdStr()` / `BaseController.getUserIdStr()` 一致，**不要**再写入登录名 `user_name`。

已修正示例：`SysJobController`、`BasApplyServiceImpl.rejectApply`；其余若发现仍写用户名，请改为 `getUserIdStr()`。

## 2. 明细表冗余单号（示例：出入库明细）

- 表 `stk_io_bill_entry` 增加 **`bill_no`**，与主表 `stk_io_bill.bill_no` 同步写入，便于直接按单号查询明细。
- 存量数据执行：`sql/maintenance/backfill_stk_io_bill_entry_bill_no.sql`
- 其它明细表（采购、申领、结算等）可按同样模式增加「主表单号冗余列」并在保存明细时从主表赋值。

## 3. 平台 API（仅平台用户 `isPlatformUser`）

| 说明 | 方法 | 路径 | 权限 | 请求体 |
|------|------|------|------|--------|
| 全库初始化（删租户与业务数据，保留菜单/字典/参数/角色菜单等，仅保留 admin） | POST | `/material/system/customer/initFullDatabase` | `hc:system:customer:initDb` | `{"confirmToken":"CONFIRM_PURGE_ALL_TENANT_DATA"}` |
| 清理某租户耗材数据 | POST | `/material/system/customer/{customerId}/purgeConsumablesData` | `hc:system:customer:purgeHc` | `{"confirm":"PURGE_HC"}` |
| 清理某客户设备数据 | POST | `/equipment/system/customer/{customerId}/purgeEquipmentData` | `sb:system:customer:purgeEq` | `{"confirm":"PURGE_EQ"}` |
| 设备客户列表行内清理耗材（同上逻辑） | POST | `/equipment/system/customer/{customerId}/purgeConsumablesData` | `hc:system:customer:purgeHc` | `{"confirm":"PURGE_HC"}` |

实现类：`com.spd.system.service.impl.TenantDataPurgeServiceImpl`  
- 按租户清理：扫描 `information_schema` 中带 `tenant_id` / `customer_id` 的表并 `DELETE`，期间 `FOREIGN_KEY_CHECKS=0`。  
- **全库初始化**为高危操作，执行前务必备份；保留表包括：`sys_menu`、`sys_dict_*`、`sys_config`、`sys_role`、`sys_role_menu`、`sys_role_dept`、`sys_dept`、`sys_post`、`sys_user` 系列关联表、`sb_menu`、`sys_job` 及 Quartz 表等；**不删除** `user_name='admin'` 的用户，并尝试补全 `sys_user_role(管理员,1)`。

若线上库表结构与脚本不一致，请在测试库验证后再用。

## 4. 菜单与搭建新库一致

- **耗材菜单**：以 `spd-admin/src/main/resources/sql/mysql/material/menu.sql` 为准（含 `ON DUPLICATE KEY UPDATE`），搭建新库时在若依基础菜单之后执行；另有增量片段见 `spd/sql/*.sql`。
- **设备菜单**：以 `spd-admin/src/main/resources/sql/mysql/equipment/menu.sql` 为准。
- **表结构**：`material/table.sql`、`material/column.sql`、`equipment/table.sql`、`equipment/column.sql`；启动项 `SqlInitRunner` 按 `/` 分段执行。

新增「初始化数据库 / 清理租户」按钮权限脚本：`spd/sql/add_customer_database_maintenance_menus.sql`（执行一次合并进权限体系）。

## 5. 参考 SQL 脚本目录

- `spd/sql/maintenance/backfill_stk_io_bill_entry_bill_no.sql` — 明细单号回填
- `spd/sql/add_customer_database_maintenance_menus.sql` — 客户管理相关权限菜单

前端需在「客户管理」列表行内调用上述 API，并弹出二次确认（全库初始化必须输入口令 `CONFIRM_PURGE_ALL_TENANT_DATA`）。

### 6. 前端入口（已实现）

- **spd-ui** `views/material/system/customer/index.vue`：工具栏「全库初始化」、行内「清理耗材数据」。
- **spd-sb** `views/system/customer/index.vue`：工具栏「全库初始化」、行内「清理设备数据」「清理耗材数据」。
- **spd-sb** `views/material/system/customer/index.vue`：与 spd-ui 耗材页一致（全库初始化 + 清理耗材数据）。

口令对话框：仅当输入 **完全一致** `CONFIRM_PURGE_ALL_TENANT_DATA` 时「确认执行」才可点。
