# 示例租户 A（TENANT_A）

| 字段 | 值 |
|------|-----|
| tenant_key | `TENANT_A` |
| customerId | `tenant-a-001` |
| customerCode | `TENANT_A` |
| branchKey | `strategyA` |

用于 `TenantRegistry.isCurrentTenantBranch("strategyA")` 等分支演示，**非生产医院**。

需求与问题请写入：[需求与问题.md](./需求与问题.md)

## 功能差异明细

当前无独立生产功能清单；业务若写入 strategyA 分支，请在本目录记录，避免与衡水/枣强逻辑混淆。
