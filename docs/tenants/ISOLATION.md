# 租户修改隔离规范（强制）

目标：同一菜单下不同租户需求不同时，**改租户 A 不得影响租户 B**。

## 0. 文档分流（先看写在哪）

| 文档 | 修改范围 |
|------|----------|
| `总览-共性需求与问题.md` | 共性 / 全体租户共享代码 |
| `{租户}/需求与问题.md` | **仅该租户** |

入口：[README.md](./README.md)

## 1. 决策树：改共享页还是拆独立页？

```
需求是否仅单个租户？
├─ 否（全租户一致）→ 改共享页面 / 共享后端逻辑
└─ 是
   ├─ 差异很小（显隐按钮、校验、字段、文案）
   │    → 共享页内用租户判断（getter / msunHis / TenantEnum）
   └─ 差异很大（布局、流程、表格列、打印版式、整页交互）
        → 为该租户单独生成前端页面（推荐）
```

### 必须拆独立页的典型场景

- 打印版式不同（已有模式：`orderPrintHs.vue` / `orderPrintZq.vue`）
- 同一菜单下表单步骤、主从表结构明显不同
- 一方依赖 HIS 推送/同步 UI，另一方完全不需要且会干扰
- 共享页内 `v-if` 分支已超过约 3～4 处且持续膨胀

### 命名与落点

| 类型 | 约定 | 示例 |
|------|------|------|
| 租户后缀组件 | `{业务}{Hs\|Zq}.vue` | `outOrderPrintHs.vue` |
| 薄包装入口 | 原 `index`/`xxxPrint.vue` 只做租户路由选择 | `orderPrint.vue` → 动态组件 |
| 整页独立 | `views/.../{feature}/indexHs.vue` 或 `tenants/hs003/...` | 菜单 `component` 指向租户页 |
| 常量 | 只写在 `msunHis.js` / `TenantEnum`，禁止魔法字符串散落 | `HS_THIRD_TENANT` |

菜单仍走 `sys_menu` + `hc_customer_menu`：可为不同租户配置**不同 component 路径**指向独立页面。

## 2. 后端隔离

- 租户判断优先：`TenantEnum.fromCustomerId(...)` / `TenantRegistry.isCurrentTenant(...)`
- 禁止只改「当前打开的共享 Service」却不加租户条件
- 数据隔离：`tenant_id` / `SecurityUtils.getCustomerId()`，不以请求体 customerId 为准
- 新增租户专属逻辑：集中常量类或 `*TenantRegistry`，与文档同步

## 3. 前端隔离

- 判断函数：`isHsThirdTenant` / `isZqTcmTenant` / `isMsunIntegratedTenant`（`msunHis.js`）
- 全局状态：`getters.isZqTcmTenant`、`departImportRequiresHisDeptId` 等
- 拆页后：入口文件只负责 `customerId` → 组件映射，业务实现放在租户文件内

```js
// 入口薄包装示例（打印已采用）
computed: {
  printComp() {
    return isZqInboundPrintTenant(this.customerId) ? 'XxxPrintZq' : 'XxxPrintHs'
  }
}
```

## 4. 实施检查清单（Agent 每次租户改动必过）

- [ ] 已读该租户 `REQUIREMENTS.md` 与 `FEATURES.md`
- [ ] 确认影响范围仅为目标 `customerId` / `TenantEnum`
- [ ] 未改动其他租户专属文件（`*Hs` ↔ `*Zq` 互不串改）
- [ ] 大差异已拆页，或在共享页用明确租户分支且有注释标明租户
- [ ] 前端常量与后端 `TenantEnum` 一致
- [ ] 更新对应租户 `FEATURES.md`（行为变更时）

## 5. 禁止事项

- 在共享页用「默认改成租户 A 行为」且不加判断
- 复制粘贴整页后两边继续改同一共享逻辑导致漂移却不拆入口
- 硬编码 `hengsui-third-001` / `zaoqiang-tcm-001` 而不走常量（存量逐步收敛）
- 为租户 A 改菜单 component 却未通过 `hc_customer_menu` 区分开通
