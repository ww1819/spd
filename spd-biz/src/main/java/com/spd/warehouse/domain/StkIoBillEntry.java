package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 出入库明细对象 stk_io_bill_entry
 *
 * @author spd
 * @date 2023-12-17
 */
public class StkIoBillEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parenId;

    /** 出入库单号（冗余主表，便于明细直接展示/筛选） */
    @Excel(name = "单号")
    private String billNo;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long commodityId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 产品名称（快照，与保存时耗材档案一致） */
    @Excel(name = "产品名称")
    private String materialName;

    /** 规格（快照） */
    @Excel(name = "规格")
    private String materialSpeci;

    /** 型号（快照） */
    @Excel(name = "型号")
    private String materialModel;

    /** 生产厂家ID（快照，fd_factory.factory_id） */
    private Long materialFactoryId;

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
    private String batchNumber;

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

    /** 耗材对象 */
    private FdMaterial material;

    /** 仓库ID（用于科室退库锁定可退库仓库） */
    private Long warehouseId;

    /** 查询参数：仓库名称 */
    private String warehouseName;

    /** 查询参数：结算类型 */
    private String settlementType;

    /** 科室库存明细id（反写） */
    private Long kcNo;

    /** 高值耗材主条码 */
    @Excel(name = "高值耗材主条码")
    private String mainBarcode;
    /** 高值耗材辅条码 */
    @Excel(name = "高值耗材辅条码")
    private String subBarcode;
    /** 供应商ID（出退库单明细内的供应商id） */
    @Excel(name = "供应商ID")
    private String supplerId;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;
    /** 删除者 */
    private String deleteBy;
    /** 删除时间 */
    private Date deleteTime;

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

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public void setCommodityId(Long commodityId)
    {
        this.commodityId = commodityId;
    }

    public Long getCommodityId()
    {
        return commodityId;
    }
    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSpeci() {
        return materialSpeci;
    }

    public void setMaterialSpeci(String materialSpeci) {
        this.materialSpeci = materialSpeci;
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }

    public Long getMaterialFactoryId() {
        return materialFactoryId;
    }

    public void setMaterialFactoryId(Long materialFactoryId) {
        this.materialFactoryId = materialFactoryId;
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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public Long getKcNo() {
        return kcNo;
    }

    public void setKcNo(Long kcNo) {
        this.kcNo = kcNo;
    }

    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
    public String getSupplerId() { return supplerId; }
    public void setSupplerId(String supplerId) { this.supplerId = supplerId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parenId", getParenId())
            .append("billNo", getBillNo())
            .append("commodityId", getCommodityId())
            .append("materialId", getMaterialId())
            .append("materialName", getMaterialName())
            .append("materialSpeci", getMaterialSpeci())
            .append("materialModel", getMaterialModel())
            .append("materialFactoryId", getMaterialFactoryId())
            .append("unitPrice", getUnitPrice())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("remark", getRemark())
            .append("batchNumber", getBatchNumber())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("delFlag", getDelFlag())
            .append("material", getMaterial())
            .toString();
    }
}
