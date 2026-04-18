package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室批量消耗明细对象 t_hc_ks_xh_entry
 * 
 * @author spd
 * @date 2025-01-15
 */
public class DeptBatchConsumeEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parenId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 批号 */
    @Excel(name = "批号")
    private String batchNumer;

    /** 来源科室库存ID */
    private Long depInventoryId;

    /** 高值科室虚拟库存 gz_dep_inventory.id（与 dep_inventory_id 二选一） */
    private Long gzDepInventoryId;

    /** 来源仓库库存ID */
    private Long kcNo;

    /** 批次对象ID */
    private Long batchId;

    /** 库存归属仓库ID */
    private Long warehouseId;

    /** 科室ID */
    private Long departmentId;

    /** 供应商ID */
    private String supplierId;

    /** 生产厂家ID */
    private Long factoryId;

    /** 耗材批号 */
    private String materialNo;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date beginTime;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 删除标识 */
    private Integer delFlag;

    /** 高值耗材主条码 */
    @Excel(name = "高值耗材主条码")
    private String mainBarcode;
    /** 高值耗材辅条码 */
    @Excel(name = "高值耗材辅条码")
    private String subBarcode;

    /** 耗材日期 */
    private Date materialDate;

    /** 入库日期 */
    private Date warehouseDate;

    /** 结算方式 */
    private String settlementType;

    /** 耗材名称快照 */
    private String materialName;

    /** 规格快照 */
    private String materialSpeci;

    /** 型号快照 */
    private String materialModel;

    /** 生产厂家ID快照 */
    private Long materialFactoryId;

    /** 引用来源单据主表ID（出库单） */
    private String refId;

    /** 引用来源单据主表ID（出库单） */
    private String refOutBillId;

    /** 引用来源单据明细ID（出库单明细） */
    private String refOutEntryId;

    /** 引用来源单号（出库单号） */
    private String refOutBillNo;

    /** 来源出库单明细数量 */
    private BigDecimal refOutEntryQty;

    /** 来源库存剩余数量 */
    private BigDecimal refOutAvailableQty;

    /** 默认带出消耗数量 */
    private BigDecimal refDefaultConsumeQty;

    /** 删除者 */
    private String deleteBy;

    /** 删除时间 */
    private Date deleteTime;

    /** 租户ID */
    private String tenantId;

    /** 反消耗来源主单ID（正向消耗主单） */
    private Long srcConsumeId;

    /** 反消耗来源主单号（正向消耗单号） */
    private String srcConsumeBillNo;

    /** 反消耗来源明细ID（正向消耗明细ID） */
    private Long srcConsumeEntryId;

    /** 正向消耗数量快照 */
    private BigDecimal srcConsumeQty;

    /** 生成反消耗时可退数量快照 */
    private BigDecimal srcCanReverseQty;

    /** 耗材对象 */
    private FdMaterial material;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParenId(Long parenId) 
    {
        this.parenId = parenId;
    }

    public Long getParenId() 
    {
        return parenId;
    }

    public void setMaterialId(Long materialId) 
    {
        this.materialId = materialId;
    }

    public Long getMaterialId() 
    {
        return materialId;
    }

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setQty(BigDecimal qty) 
    {
        this.qty = qty;
    }

    public BigDecimal getQty() 
    {
        return qty;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
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

    public void setBatchNumer(String batchNumer) 
    {
        this.batchNumer = batchNumer;
    }

    public String getBatchNumer() 
    {
        return batchNumer;
    }

    public Long getDepInventoryId() {
        return depInventoryId;
    }

    public void setDepInventoryId(Long depInventoryId) {
        this.depInventoryId = depInventoryId;
    }

    public Long getGzDepInventoryId() {
        return gzDepInventoryId;
    }

    public void setGzDepInventoryId(Long gzDepInventoryId) {
        this.gzDepInventoryId = gzDepInventoryId;
    }

    public Long getKcNo() {
        return kcNo;
    }

    public void setKcNo(Long kcNo) {
        this.kcNo = kcNo;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public void setBeginTime(Date beginTime) 
    {
        this.beginTime = beginTime;
    }

    public Date getBeginTime() 
    {
        return beginTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
    public Date getMaterialDate() { return materialDate; }
    public void setMaterialDate(Date materialDate) { this.materialDate = materialDate; }
    public Date getWarehouseDate() { return warehouseDate; }
    public void setWarehouseDate(Date warehouseDate) { this.warehouseDate = warehouseDate; }
    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
    public String getMaterialModel() { return materialModel; }
    public void setMaterialModel(String materialModel) { this.materialModel = materialModel; }
    public Long getMaterialFactoryId() { return materialFactoryId; }
    public void setMaterialFactoryId(Long materialFactoryId) { this.materialFactoryId = materialFactoryId; }
    public String getRefId() { return refId; }
    public void setRefId(String refId) { this.refId = refId; }
    public String getRefOutBillId() { return refOutBillId; }
    public void setRefOutBillId(String refOutBillId) { this.refOutBillId = refOutBillId; }
    public String getRefOutEntryId() { return refOutEntryId; }
    public void setRefOutEntryId(String refOutEntryId) { this.refOutEntryId = refOutEntryId; }
    public String getRefOutBillNo() { return refOutBillNo; }
    public void setRefOutBillNo(String refOutBillNo) { this.refOutBillNo = refOutBillNo; }
    public BigDecimal getRefOutEntryQty() { return refOutEntryQty; }
    public void setRefOutEntryQty(BigDecimal refOutEntryQty) { this.refOutEntryQty = refOutEntryQty; }
    public BigDecimal getRefOutAvailableQty() { return refOutAvailableQty; }
    public void setRefOutAvailableQty(BigDecimal refOutAvailableQty) { this.refOutAvailableQty = refOutAvailableQty; }
    public BigDecimal getRefDefaultConsumeQty() { return refDefaultConsumeQty; }
    public void setRefDefaultConsumeQty(BigDecimal refDefaultConsumeQty) { this.refDefaultConsumeQty = refDefaultConsumeQty; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getSrcConsumeId() { return srcConsumeId; }
    public void setSrcConsumeId(Long srcConsumeId) { this.srcConsumeId = srcConsumeId; }
    public String getSrcConsumeBillNo() { return srcConsumeBillNo; }
    public void setSrcConsumeBillNo(String srcConsumeBillNo) { this.srcConsumeBillNo = srcConsumeBillNo; }
    public Long getSrcConsumeEntryId() { return srcConsumeEntryId; }
    public void setSrcConsumeEntryId(Long srcConsumeEntryId) { this.srcConsumeEntryId = srcConsumeEntryId; }
    public BigDecimal getSrcConsumeQty() { return srcConsumeQty; }
    public void setSrcConsumeQty(BigDecimal srcConsumeQty) { this.srcConsumeQty = srcConsumeQty; }
    public BigDecimal getSrcCanReverseQty() { return srcCanReverseQty; }
    public void setSrcCanReverseQty(BigDecimal srcCanReverseQty) { this.srcCanReverseQty = srcCanReverseQty; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parenId", getParenId())
            .append("materialId", getMaterialId())
            .append("unitPrice", getUnitPrice())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("batchNumer", getBatchNumer())
            .append("depInventoryId", getDepInventoryId())
            .append("gzDepInventoryId", getGzDepInventoryId())
            .append("kcNo", getKcNo())
            .append("batchId", getBatchId())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("supplierId", getSupplierId())
            .append("factoryId", getFactoryId())
            .append("materialNo", getMaterialNo())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("delFlag", getDelFlag())
            .append("mainBarcode", getMainBarcode())
            .append("subBarcode", getSubBarcode())
            .append("materialDate", getMaterialDate())
            .append("warehouseDate", getWarehouseDate())
            .append("settlementType", getSettlementType())
            .append("materialName", getMaterialName())
            .append("materialSpeci", getMaterialSpeci())
            .append("materialModel", getMaterialModel())
            .append("materialFactoryId", getMaterialFactoryId())
            .append("tenantId", getTenantId())
            .append("srcConsumeId", getSrcConsumeId())
            .append("srcConsumeBillNo", getSrcConsumeBillNo())
            .append("srcConsumeEntryId", getSrcConsumeEntryId())
            .append("srcConsumeQty", getSrcConsumeQty())
            .append("srcCanReverseQty", getSrcCanReverseQty())
            .append("deleteBy", getDeleteBy())
            .append("deleteTime", getDeleteTime())
            .append("remark", getRemark())
            .toString();
    }
}
