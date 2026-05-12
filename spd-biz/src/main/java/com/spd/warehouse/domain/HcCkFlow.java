package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 仓库流水对象 t_hc_ck_flow
 *
 * @author spd
 */
public class HcCkFlow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 出库单id */
    private Long billId;
    /** 出库单明细id */
    private Long entryId;
    /** 期初单主表ID（UUID7） */
    private String refBillId;
    /** 期初单明细ID（UUID7） */
    private String refEntryId;
    /** 仓库ID */
    private Long warehouseId;
    /** 耗材ID */
    private Long materialId;
    /** 批次号 */
    private String batchNo;
    /** 批号 */
    private String batchNumber;

    /** 批次对象表ID（stk_batch.id） */
    private Long batchId;
    /** 数量 */
    private BigDecimal qty;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 金额 */
    private BigDecimal amt;
    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 供应商ID */
    private Long supplierId;
    /** 仓库ID（varchar 快照，与 bigint 冗余一致） */
    private String warehouseIdStr;
    /** 供应商ID（varchar 快照） */
    private String supplierIdStr;
    /** 科室ID（varchar 快照） */
    private String departmentIdStr;
    /** 科室ID（bigint，仓库流水按科室统计） */
    private Long departmentId;
    /** 耗材编码快照 */
    private String materialCode;
    /** 耗材名称快照 */
    private String materialName;
    /** 业务单号快照 */
    private String billNo;
    /** 主单ID varchar */
    private String billIdStr;
    /** 明细ID varchar */
    private String entryIdStr;
    /** 耗材ID varchar */
    private String materialIdStr;
    /** 库存明细 kc_no varchar */
    private String kcNoStr;
    /** 批次ID varchar */
    private String batchIdStr;
    /** 生产厂家ID varchar */
    private String factoryIdStr;
    /** 供应商名称快照 */
    private String supplierName;
    /** 生产厂家ID（fd_factory.factory_id） */
    private Long factoryId;
    /** 关联仓库库存id */
    private Long kcNo;
    /** 类型：RK入库/CK出库/TH退货/TK退库/ZC调拨转出/ZR调拨转入/KSZC科室转科仓库转出/KSZR科室转科仓库转入 */
    private String lx;
    /** 流水时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowTime;

    /** 来源业务类型中文（便于追溯展示） */
    private String originBusinessType;
    /** 删除标志 */
    private Integer delFlag;
    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;
    /** 高值耗材主条码 */
    private String mainBarcode;
    /** 高值耗材辅条码 */
    private String subBarcode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getRefBillId() { return refBillId; }
    public void setRefBillId(String refBillId) { this.refBillId = refBillId; }
    public String getRefEntryId() { return refEntryId; }
    public void setRefEntryId(String refEntryId) { this.refEntryId = refEntryId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getAmt() { return amt; }
    public void setAmt(BigDecimal amt) { this.amt = amt; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getWarehouseIdStr() { return warehouseIdStr; }
    public void setWarehouseIdStr(String warehouseIdStr) { this.warehouseIdStr = warehouseIdStr; }
    public String getSupplierIdStr() { return supplierIdStr; }
    public void setSupplierIdStr(String supplierIdStr) { this.supplierIdStr = supplierIdStr; }
    public String getDepartmentIdStr() { return departmentIdStr; }
    public void setDepartmentIdStr(String departmentIdStr) { this.departmentIdStr = departmentIdStr; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public String getBillIdStr() { return billIdStr; }
    public void setBillIdStr(String billIdStr) { this.billIdStr = billIdStr; }
    public String getEntryIdStr() { return entryIdStr; }
    public void setEntryIdStr(String entryIdStr) { this.entryIdStr = entryIdStr; }
    public String getMaterialIdStr() { return materialIdStr; }
    public void setMaterialIdStr(String materialIdStr) { this.materialIdStr = materialIdStr; }
    public String getKcNoStr() { return kcNoStr; }
    public void setKcNoStr(String kcNoStr) { this.kcNoStr = kcNoStr; }
    public String getBatchIdStr() { return batchIdStr; }
    public void setBatchIdStr(String batchIdStr) { this.batchIdStr = batchIdStr; }
    public String getFactoryIdStr() { return factoryIdStr; }
    public void setFactoryIdStr(String factoryIdStr) { this.factoryIdStr = factoryIdStr; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getFactoryId() { return factoryId; }
    public void setFactoryId(Long factoryId) { this.factoryId = factoryId; }
    public Long getKcNo() { return kcNo; }
    public void setKcNo(Long kcNo) { this.kcNo = kcNo; }
    public String getLx() { return lx; }
    public void setLx(String lx) { this.lx = lx; }
    public Date getFlowTime() { return flowTime; }
    public void setFlowTime(Date flowTime) { this.flowTime = flowTime; }
    public String getOriginBusinessType() { return originBusinessType; }
    public void setOriginBusinessType(String originBusinessType) { this.originBusinessType = originBusinessType; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
