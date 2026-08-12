# 衡水市第三人民医院（HS_003）

| 字段 | 值 |
|------|-----|
| tenant_key | `HS_003` |
| customerId | `hengsui-third-001` |
| customerCode | `HSSDSRMYY` |
| branchKey | `hengsui` |
| 前端判断 | `isHsThirdTenant` / getters `*RequiresHisId` |

需求与问题请写入：[需求与问题.md](./需求与问题.md)

---

## 功能差异明细

### 1. 主数据 / 基础档案

| 菜单/功能 | 差异说明 | 关键实现 |
|-----------|----------|----------|
| 科室 | 新增/导入须填 **HIS 科室 ID**；HIS ID 字段可编辑（新增时） | `departImportRequiresHisDeptId`；`FdDepartmentController/ServiceImpl` |
| 供应商 | 新增/导入须填 **HIS 供应商 ID** | `supplierImportRequiresHisId`；`FdSupplier*` |
| 生产厂家 | 新增/导入须填 **HIS 生产厂家 ID** | `factoryImportRequiresHisId`；`FdFactory*` |
| 财务分类 | 新增须填 HIS ID | `FdFinanceCategory*` |
| 库房分类 | 新增须填 HIS ID | `FdWarehouseCategory*` |
| 耗材档案 | 编码由系统按分类前缀生成：**GZ / DZ / SJ + 5 位**，前端禁用手改；产品档案 is_gz 变更可同步 HIS 收费镜像 | `FdMaterialServiceImpl`；`material/index.vue` |

### 2. 仓储业务

| 菜单/功能 | 差异说明 | 关键实现 |
|-----------|----------|----------|
| 出库审核通过后 | **自动收货确认**（与枣强相同集合） | `StkIoBillServiceImpl.AUTO_OUTBOUND_RECEIPT_TENANT_IDS` |
| 入库/出库/退货/退库打印 | 默认 **Hs 版式**（非枣强走 Hs） | `*PrintHs.vue`；入口按 `isZqInboundPrintTenant` 反向选择 |
| 科室盘点 | 缺省退库仓时有衡水特殊处理 | `DeptStocktakingServiceImpl` |

### 3. HIS 计费 / 患者收费（衡水特色）

| 菜单/功能 | 差异说明 | 关键实现 |
|-----------|----------|----------|
| 患者收费查询 | HIS 计费镜像抓取与查询 | `department/patientCharge`；权限 `department:patientCharge:*` |
| 高值扫描核销 | HIS 计费镜像高值扫码核销 | `gz/highChargeScan` |
| HIS 计费自动处理 | 抓取后自动低值消耗 / 自动退费开关 | `HisBillingTenantConstants`；`sb_tenant_setting`；菜单「衡水计费自动消耗开关」 |
| 众阳 HIS 单据推送 | **未接入**（与枣强不同） | `MSUN_INTEGRATED_TENANT_IDS` 不含本租户 |

### 4. 菜单开通

通过 `hc_customer_menu` 开通；与计费相关的菜单/按钮（患者收费、计费自动处理等）主要为衡水场景设计，开通时注意勿误开给无需该能力的租户。

### 5. 前端专属/优先组件

- `orderPrintHs.vue`、`outOrderPrintHs.vue`、`refundGoodsOrderPrintHs.vue`、`refundDepotOrderPrintHs.vue`
- 主数据页多为共享页 + 衡水校验分支（未整页拆分）

### 6. 与枣强对比（避免串改）

| 点 | 衡水 | 枣强 |
|----|------|------|
| 主数据来源 | 可手工，但强制 HIS ID | 禁止手工，HIS 同步 |
| 耗材编码 | 分类前缀自动生成 | 同步为主 |
| 计费镜像 | 有 | 无 |
| 众阳推送 | 无 | 有 |
| 申请单弹窗审核 | 无 | 有 |
