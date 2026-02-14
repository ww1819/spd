package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 批次对象 stk_batch
 * 用于记录所有入库批次，支撑追溯；批次号唯一。
 *
 * @author spd
 */
public class StkBatch extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 批次对象表ID */
    private Long id;

    /** 批次号，存到库存表和单据明细表的批次字段 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 产品档案ID */
    private Long materialId;
    /** 产品档案编码 */
    private String materialCode;
    /** 名称 */
    private String materialName;
    /** 规格 */
    private String speci;
    /** 型号 */
    private String model;
    /** 单位ID */
    private Long unitId;
    /** 单位名称 */
    private String unitName;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 批号 */
    private String batchNumber;
    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 灭菌批号 */
    private String sterilizeBatchNo;
    /** 灭菌有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sterilizeEndTime;
    /** 使用次数/人次 */
    private Integer useTimes;
    /** 主条码 */
    private String mainBarcode;
    /** 辅条码 */
    private String subBarcode;
    /** 注册证号 */
    private String registerNo;
    /** 生产许可证号 */
    private String permitNo;
    /** 生产厂家ID */
    private Long factoryId;
    /** 生产厂家编码 */
    private String factoryCode;
    /** 生产厂家名称 */
    private String factoryName;
    /** 供应商ID */
    private Long supplierId;
    /** 供应商编码 */
    private String supplierCode;
    /** 供应商名称 */
    private String supplierName;
    /** 仓库ID */
    private Long warehouseId;
    /** 仓库编码 */
    private String warehouseCode;
    /** 仓库名称 */
    private String warehouseName;
    /** 科室ID */
    private Long departmentId;
    /** 科室编码 */
    private String departmentCode;
    /** 科室名称 */
    private String departmentName;
    /** 库房分类ID */
    private Long storeroomId;
    /** 库房分类编码 */
    private String storeroomCode;
    /** 库房分类名称 */
    private String storeroomName;
    /** 财务分类ID */
    private Long financeCategoryId;
    /** 财务分类编码 */
    private String financeCategoryCode;
    /** 财务分类名称 */
    private String financeCategoryName;
    /** 批次产生方式：采购入库/仓库盘盈/科室盘盈等 */
    private String batchSource;
    /** 单据主表ID */
    private Long billId;
    /** 单据号 */
    private String billNo;
    /** 单据明细ID */
    private Long entryId;
    /** 期初单主表ID（UUID7） */
    private String refBillId;
    /** 期初单明细ID（UUID7） */
    private String refEntryId;
    /** 院内码明细ID */
    private Long inCodeDetailId;
    /** 单据审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /** 单据审核人 */
    private String auditBy;
    /** 删除标志 */
    private Integer delFlag;
    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;
    /** 删除人 */
    private String delBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getSpeci() { return speci; }
    public void setSpeci(String speci) { this.speci = speci; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public String getSterilizeBatchNo() { return sterilizeBatchNo; }
    public void setSterilizeBatchNo(String sterilizeBatchNo) { this.sterilizeBatchNo = sterilizeBatchNo; }
    public Date getSterilizeEndTime() { return sterilizeEndTime; }
    public void setSterilizeEndTime(Date sterilizeEndTime) { this.sterilizeEndTime = sterilizeEndTime; }
    public Integer getUseTimes() { return useTimes; }
    public void setUseTimes(Integer useTimes) { this.useTimes = useTimes; }
    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }
    public String getPermitNo() { return permitNo; }
    public void setPermitNo(String permitNo) { this.permitNo = permitNo; }
    public Long getFactoryId() { return factoryId; }
    public void setFactoryId(Long factoryId) { this.factoryId = factoryId; }
    public String getFactoryCode() { return factoryCode; }
    public void setFactoryCode(String factoryCode) { this.factoryCode = factoryCode; }
    public String getFactoryName() { return factoryName; }
    public void setFactoryName(String factoryName) { this.factoryName = factoryName; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getStoreroomId() { return storeroomId; }
    public void setStoreroomId(Long storeroomId) { this.storeroomId = storeroomId; }
    public String getStoreroomCode() { return storeroomCode; }
    public void setStoreroomCode(String storeroomCode) { this.storeroomCode = storeroomCode; }
    public String getStoreroomName() { return storeroomName; }
    public void setStoreroomName(String storeroomName) { this.storeroomName = storeroomName; }
    public Long getFinanceCategoryId() { return financeCategoryId; }
    public void setFinanceCategoryId(Long financeCategoryId) { this.financeCategoryId = financeCategoryId; }
    public String getFinanceCategoryCode() { return financeCategoryCode; }
    public void setFinanceCategoryCode(String financeCategoryCode) { this.financeCategoryCode = financeCategoryCode; }
    public String getFinanceCategoryName() { return financeCategoryName; }
    public void setFinanceCategoryName(String financeCategoryName) { this.financeCategoryName = financeCategoryName; }
    public String getBatchSource() { return batchSource; }
    public void setBatchSource(String batchSource) { this.batchSource = batchSource; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getRefBillId() { return refBillId; }
    public void setRefBillId(String refBillId) { this.refBillId = refBillId; }
    public String getRefEntryId() { return refEntryId; }
    public void setRefEntryId(String refEntryId) { this.refEntryId = refEntryId; }
    public Long getInCodeDetailId() { return inCodeDetailId; }
    public void setInCodeDetailId(Long inCodeDetailId) { this.inCodeDetailId = inCodeDetailId; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }
    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }
}
