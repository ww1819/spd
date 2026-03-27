package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室库存对象 stk_dep_inventory
 *
 * @author spd
 * @date 2024-03-04
 */
public class StkDepInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 产品名称（表 material_name，快照） */
    private String snapMaterialName;
    /** 规格（表 material_speci） */
    private String snapMaterialSpeci;
    /** 型号（表 material_model） */
    private String snapMaterialModel;
    /** 生产厂家ID快照（表 material_factory_id） */
    private Long snapMaterialFactoryId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 批次对象表ID（stk_batch.id） */
    private Long batchId;

    /** 耗材批次号 */
    @Excel(name = "耗材批次号")
    private String materialNo;

    /** 耗材日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "耗材日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date materialDate;

    /** 入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date warehouseDate;

    /** 仓库ID */
    private Long warehouseId;

    /** 生产厂家ID（fd_factory.factory_id） */
    private Long factoryId;

    /** 单据类型 */
    private Integer billType;

    /** 单据状态 */
    private Integer billStatus;

    /** 开始日期 */
    private Date beginDate;

    /** 结束日期 */
    private Date endDate;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private String supplierId;

    /** 出库单号 */
    @Excel(name = "出库单号")
    private String outOrderNo;

    /** 单据主表id（出库单id） */
    private Long billId;
    /** 单据明细id（出库单明细id） */
    private Long billEntryId;
    /** 单据号 */
    private String billNo;

    /** 删除标识（0正常 1已删除） */
    private Integer delFlag;

    /** 关联仓库库存主键 stk_inventory.id（出库审核扣减的来源库存行，与仓库流水 HcCkFlow.kc_no 含义一致） */
    private Long kcNo;

    /** 耗材对象 */
    private FdMaterial material;

    /** 科室对象 */
    private FdDepartment department;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 归属仓库（出库来源仓库） */
    private FdWarehouse warehouse;

    /** 生产厂家（优先库存行 factory_id，否则耗材档案） */
    private FdFactory fdFactory;

    @Excel(name = "批号")
    private String batchNumber;

    /** 收货确认状态 0未确认 1已确认 */
    @Excel(name = "收货确认状态", readConverterExp = "0=未确认,1=已确认")
    private Integer receiptConfirmStatus;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    /** 结算方式（来自出库单：1入库结算 2出库结算 3消耗结算） */
    private String settlementType;

    /** 高值耗材主条码 */
    @Excel(name = "高值耗材主条码")
    private String mainBarcode;
    /** 高值耗材辅条码 */
    @Excel(name = "高值耗材辅条码")
    private String subBarcode;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public String getSnapMaterialName() {
        return snapMaterialName;
    }

    public void setSnapMaterialName(String snapMaterialName) {
        this.snapMaterialName = snapMaterialName;
    }

    public String getSnapMaterialSpeci() {
        return snapMaterialSpeci;
    }

    public void setSnapMaterialSpeci(String snapMaterialSpeci) {
        this.snapMaterialSpeci = snapMaterialSpeci;
    }

    public String getSnapMaterialModel() {
        return snapMaterialModel;
    }

    public void setSnapMaterialModel(String snapMaterialModel) {
        this.snapMaterialModel = snapMaterialModel;
    }

    public Long getSnapMaterialFactoryId() {
        return snapMaterialFactoryId;
    }

    public void setSnapMaterialFactoryId(Long snapMaterialFactoryId) {
        this.snapMaterialFactoryId = snapMaterialFactoryId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }
    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getQty()
    {
        return qty;
    }
    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }
    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }
    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }
    public void setMaterialNo(String materialNo)
    {
        this.materialNo = materialNo;
    }

    public String getMaterialNo()
    {
        return materialNo;
    }
    public void setMaterialDate(Date materialDate)
    {
        this.materialDate = materialDate;
    }

    public Date getMaterialDate()
    {
        return materialDate;
    }
    public void setWarehouseDate(Date warehouseDate)
    {
        this.warehouseDate = warehouseDate;
    }

    public Date getWarehouseDate()
    {
        return warehouseDate;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("materialId", getMaterialId())
            .append("snapMaterialName", getSnapMaterialName())
            .append("snapMaterialSpeci", getSnapMaterialSpeci())
            .append("snapMaterialModel", getSnapMaterialModel())
            .append("snapMaterialFactoryId", getSnapMaterialFactoryId())
            .append("departmentId", getDepartmentId())
            .append("qty", getQty())
            .append("unitPrice", getUnitPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("materialNo", getMaterialNo())
            .append("materialDate", getMaterialDate())
            .append("warehouseDate", getWarehouseDate())
            .append("material", getMaterial())
            .append("department", getDepartment())
            .append("tenantId", getTenantId())
            .toString();
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public FdFactory getFdFactory() {
        return fdFactory;
    }

    public void setFdFactory(FdFactory fdFactory) {
        this.fdFactory = fdFactory;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public Long getKcNo() {
        return kcNo;
    }

    public void setKcNo(Long kcNo) {
        this.kcNo = kcNo;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getReceiptConfirmStatus() {
        return receiptConfirmStatus;
    }

    public void setReceiptConfirmStatus(Integer receiptConfirmStatus) {
        this.receiptConfirmStatus = receiptConfirmStatus;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getBillEntryId() {
        return billEntryId;
    }

    public void setBillEntryId(Long billEntryId) {
        this.billEntryId = billEntryId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
}
