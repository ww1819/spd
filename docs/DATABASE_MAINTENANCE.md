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
- **全库初始化**为高危操作，执行前务必备份；保留表包括：`sys_menu`、`sys_dict_*`、`sys_config`、`sys_role`、`sys_role_menu`、`sys_role_dept`、`sys_dept`、`sys_post`、`sys_user` 系列关联表、`sb_menu`、`sys_job` 及 Quartz 表等；另 **保留** `fd_category68`、**以 `scm_` / `spd_` 开头的全部表**（供应链与 SPD 侧配置，永不清）；**不删除** `user_name='admin'` 的用户，并尝试补全 `sys_user_role(管理员,1)`。  
  - **耗材工作组**：`sys_post`、`sys_user_post` 在跳过列表中不会参与主循环整表清空；主循环会清空 `sys_post_menu` 等子表，但须在主循环**之后**单独 `DELETE` **`tenant_id` 非空** 的 `sys_user_post`、`sys_post` 行，否则租户侧工作组会残留。  
  - **设备工作组**：`sb_work_group*` 主循环一般为整表清空；若需兜底，同一实现类在主循环后按 **`customer_id` 非空** 再删一遍子表→`sb_work_group`。  
- **租户维度白名单**（`FULL_RESET_TENANT_NULL_PRESERVE_WHITELIST`，当前为空）：若某表加入该白名单，则全库初始化时对该表**不**再 `DELETE` 整表，而是只删除 `tenant_id` / `customer_id` 非空且非纯空白的行，**保留**两列均为 NULL 或仅空白的行（便于保留平台管理员相关记录）。白名单表须至少含 `tenant_id` 或 `customer_id` 之一；详见实现类内注释。

若线上库表结构与脚本不一致，请在测试库验证后再用。

## 4. 菜单与搭建新库一致

- **耗材菜单**：以 `spd-admin/src/main/resources/sql/mysql/material/menu.sql` 为准（含 `ON DUPLICATE KEY UPDATE`），搭建新库时在若依基础菜单之后执行；另有增量片段见 `spd/sql/*.sql`。
- **设备菜单**：以 `spd-admin/src/main/resources/sql/mysql/equipment/menu.sql` 为准。
- **表结构**：`material/table.sql`、`material/column.sql`、`equipment/table.sql`、`equipment/column.sql`；启动项 `SqlInitRunner` 按 `/` 分段执行。

新增「初始化数据库 / 清理租户」按钮权限脚本：`spd/sql/add_customer_database_maintenance_menus.sql`（执行一次合并进权限体系）。

- **基础资料「导入」默认对客户开放**：`menu.sql` 中科室/供应商/厂家/库房分类/财务分类等导入按钮已设 `default_open_to_customer=1`；**耗材产品档案**「新增导入」「更新导入」共用权限 **`foundation:material:import`**（`menu.sql` **8.5** 段 `menu_id=2298`「耗材产品导入」）；**财务分类**页「新增导入」「更新导入」共用 **`foundation:financeCategory:import`**。`material/column.sql` 末尾另有 **UPDATE sys_menu** + **回填 hc_customer_menu**（含 `foundation:financeCategory:import`、`foundation:material:import`、`system:user:import` 等），便于存量库升级后老客户自动拥有导入权限。
- **menu_id 冲突修复**：客户管理下的「初始化数据库 / 清理耗材 / 清理设备」按钮已使用 **3100、3101、3102**，避免与「库房分类 2280 段」「财务分类导入 2297」等基础资料菜单 ID 重复，防止 `ON DUPLICATE KEY` 覆盖后导致财务分类导入对客户不可见。若存量库曾用旧脚本覆盖，请核对 `sys_menu` 中 `foundation:financeCategory:import` 对应行是否为 **2290** 子菜单且 `default_open_to_customer=1`，并执行 `column.sql` 末尾回填 `hc_customer_menu`。
- **厂家 / 财务分类菜单去重**：`material/menu.sql` 中厂家侧菜单已统一为 **「厂家维护」**（menu_id=2250，component=`foundation/factory/index`），财务主菜单名为 **「财务分类」**（2290）；与设备端 `sb_menu` 的「厂家维护」同页同组件但分表，勿在 `sys_menu` 再手工插入同 path。若界面仍出现两条，请执行 `spd/sql/maintenance/dedupe_hc_foundation_menus.sql` 诊断重复项。

## 5. 参考 SQL 脚本目录

- `spd/sql/maintenance/repair_customer_admin_menu_ids_no_conflict.sql` — **财务分类导入**与**耗材产品导入**（`foundation:material:import`，新增/更新导入共用）对客户默认开放（`default_open_to_customer` + `hc_customer_menu` 回填）；新库以 `menu.sql` 为准，并需存在 `menu.sql` 中 **8.5) 耗材产品档案导入**（`menu_id=2298`）
- `spd/sql/maintenance/dedupe_hc_foundation_menus.sql` — 诊断 `sys_menu` 中厂家/财务分类等同 component 重复 C 菜单
- `spd/sql/maintenance/backfill_stk_io_bill_entry_bill_no.sql` — 明细单号回填
- `spd/sql/maintenance/drop_fd_category68_tenant_id_if_exists.sql` — 若曾误给系统字典表 `fd_category68` 增加 `tenant_id`，可按注释删除该列
- `spd/sql/add_customer_database_maintenance_menus.sql` — 客户管理相关权限菜单

前端需在「客户管理」列表行内调用上述 API，并弹出二次确认（全库初始化必须输入口令 `CONFIRM_PURGE_ALL_TENANT_DATA`）。

### 6. 前端入口（已实现）

- **spd-ui** `views/material/system/customer/index.vue`：工具栏「全库初始化」、行内「清理耗材数据」。
- **spd-sb** `views/system/customer/index.vue`：工具栏「全库初始化」、行内「清理设备数据」「清理耗材数据」。
- **spd-sb** `views/material/system/customer/index.vue`：与 spd-ui 耗材页一致（全库初始化 + 清理耗材数据）。

口令对话框：仅当输入 **完全一致** `CONFIRM_PURGE_ALL_TENANT_DATA` 时「确认执行」才可点。
