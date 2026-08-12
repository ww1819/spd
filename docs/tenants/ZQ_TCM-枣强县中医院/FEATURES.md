# 枣强县中医院（ZQ_TCM）

| 字段 | 值 |
|------|-----|
| tenant_key | `ZQ_TCM` |
| customerId | `zaoqiang-tcm-001` |
| customerCode | `ZQXZYY` |
| branchKey | `zaoqiang` |
| 前端判断 | `isZqTcmTenant` / `isMsunIntegratedTenant` / `isZqInboundPrintTenant` |

需求与问题请写入：[需求与问题.md](./需求与问题.md)

---

## 功能差异明细

### 1. 主数据 / 基础档案（强约束）

| 菜单/功能 | 差异说明 | 关键实现 |
|-----------|----------|----------|
| 科室 / 供应商 / 厂家等 | **禁止手工新增**，提示从 HIS 同步；隐藏新增/导入按钮 | `ZqTcmMasterDataGuard`；各 `foundation/*/index.vue`；`ZQ_TCM_MANUAL_ADD_DENIED_MSG` |
| 耗材档案 | 禁止手工新增；**编辑已有档案**时仅允许改：财务分类、厂家、供应商、单价 | `FdMaterialServiceImpl`；`material/index.vue`（`isZqTcmTenant`） |
| 库房 | 展示 **HIS药库科室ID** | `warehouse/index.vue` |
| 用户管理 | 隐藏部分新增 / 新增导入 | `system/user/index.vue` |
| 众阳主数据同步 | 同步按钮、联调探针页 | `MsunHisSyncButton`；`foundation/msunProbe`；`MsunHisTenantRegistry` |

### 2. 仓储业务

| 菜单/功能 | 差异说明 | 关键实现 |
|-----------|----------|----------|
| 入库申请（到货验收） | 弹窗内可 **直接审核**（其他租户走入库审核菜单） | `inWarehouse/apply/index.vue` → `isZqTenant` |
| 出库申请 | 弹窗内可直接审核 | `outWarehouse/apply/index.vue` |
| 出库审核通过后 | **自动收货确认** | `StkIoBillServiceImpl.AUTO_OUTBOUND_RECEIPT_TENANT_IDS` |
| 出入库/退货/退库打印 | **Zq 专属版式** | `*PrintZq.vue`；`isZqInboundPrintTenant` |
| 出库/退库审核 | 众阳推送状态列、失败可重推 | `msunHis.js`；`pushMsunOutbound` / `pushMsunReturn` |

### 3. 众阳 HIS 集成（枣强特色）

| 能力 | 说明 | 相关 |
|------|------|------|
| 接入登记 | 唯一写入 `MSUN_INTEGRATED_TENANT_IDS` / `MsunHisTenantRegistry` | 前后端须同步维护 |
| 单据推送 | 出库、退库等推送与状态 | `outWarehouse/audit`、`refundDepotAudit` |
| SSO 示例 | 文档多用本租户 ID 举例 | `spd/docs/单点登录(SSO)对接说明.md` |

### 4. 不做的能力（相对衡水）

- 不走衡水式「HIS 计费镜像自动消耗」主路径（`HisBillingTenantConstants` 面向衡水）
- 主数据不开放手工建档（与衡水「可建档但填 HIS ID」相反）

### 5. 前端专属组件

- `orderPrintZq.vue`、`outOrderPrintZq.vue`、`refundGoodsOrderPrintZq.vue`、`refundDepotOrderPrintZq.vue`
- 共享业务页内大量 `isZqTcmTenant` 分支；若某菜单差异继续扩大，应拆 `indexZq.vue` 并由菜单或入口映射

### 6. 与衡水对比（避免串改）

| 点 | 枣强 | 衡水 |
|----|------|------|
| 主数据 | HIS 同步 only | 手工 + HIS ID |
| 打印 | `*Zq` | `*Hs` |
| 众阳推送 | 有 | 无 |
| 申请弹窗审核 | 有 | 无 |
| 计费镜像自动消耗 | 无 | 有 |
