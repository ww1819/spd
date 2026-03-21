# 租户 ID 稳妥使用说明（LoginUser / Header / 数据库）

## 问题背景

- **数据库** `sys_user.customer_id` 可能晚于登录被补全；
- **Redis** 中的 `LoginUser` 是**登录时快照**，不会自动随库更新；
- **请求头** `X-Tenant-Id` 由前端工作台传递，与 `getCustomerId()`（读缓存里的用户）不是同一条链路。

因此会出现「库里有租户、Header 也有，但 `getCustomerId()` 仍为空」的现象。

## 稳妥方案（已实现，三层）

### 1. 请求入口同步：`LoginUserTenantSyncFilter`

- 位置：JWT 认证之后、`TenantContextFilter` 之前。
- 行为：若当前 `LoginUser.getUser().customerId` **为空**，则按主键执行  
  `SELECT customer_id FROM sys_user WHERE user_id = ?`（轻量单列）。
- 若库中有值：写回 `LoginUser` 并调用 `TokenService.setLoginUser` **刷新 Redis**，后续请求即一致。
- 若库中也为空（如平台账号）：不更新，无额外副作用。

### 2. 解析顺序：`SecurityUtils.resolveEffectiveTenantId`

统一按顺序取有效租户（实体字段 → 登录用户 `customerId` → `TenantContext` → 请求头 `X-Tenant-Id`）。

业务 SQL 的 `<bind>` 应使用 **`SecurityUtils.scopedTenantIdForSql()`**，避免在 XML 中写 `resolveEffectiveTenantId(null)` 的 OGNL `null` 兼容问题。

### 3. 租户线程上下文：`TenantContextFilter`

- 用户已绑定 `customer_id`：以用户为准，并校验与 `X-Tenant-Id` 是否一致（防篡改）。
- 用户未绑定：允许用请求头写入 `TenantContext`，供 `resolveEffectiveTenantId` 使用。

## 运维建议

1. **修改用户租户后**：同步过滤器会在「缓存中 customer_id 仍为空」时自动补一次；若缓存里已是**旧租户**而库已改，请用户**重新登录**或清理其 Redis 登录态。
2. **仍异常时**：抓一条请求确认同时带 `Authorization` 与 `X-Tenant-Id`，并核对库中 `sys_user.customer_id` 与 `sb_customer.customer_id` 一致。

## 相关类

- `com.spd.framework.security.filter.LoginUserTenantSyncFilter`
- `com.spd.framework.security.filter.TenantContextFilter`
- `com.spd.common.utils.SecurityUtils#resolveEffectiveTenantId` / `scopedTenantIdForSql`
- `com.spd.system.mapper.SysUserMapper#selectCustomerIdByUserId`
