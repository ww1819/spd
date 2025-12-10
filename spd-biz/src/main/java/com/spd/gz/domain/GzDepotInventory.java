package com.spd.gz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值备货库存明细对象 gz_depot_inventory
 *
 * @author spd
 * @date 2024-06-11
 */
public class GzDepotInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private BigDecimal qty;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 入库批次号 */
    @Excel(name = "入库批次号")
    private String batchNo;

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

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplierId;

    /** 院内码 */
    @Excel(name = "院内码")
    private String inHospitalCode;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 耗材对象 */
    private FdMaterial material;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 查询参数：仓库名称 */
    private String warehouseName;

    /** 查询参数：耗材名称 */
    private String materialName;

    /** 查询参数：开始日期 */
    private String beginDate;

    /** 查询参数：结束日期 */
    private String endDate;

    /** 查询参数：入库单号（模糊查询） */
    private String orderNo;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getQty()
    {
        return qty;
    }
    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
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
    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setInHospitalCode(String inHospitalCode)
    {
        this.inHospitalCode = inHospitalCode;
    }

    public String getInHospitalCode()
    {
        return inHospitalCode;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("qty", getQty())
            .append("materialId", getMaterialId())
            .append("warehouseId", getWarehouseId())
            .append("unitPrice", getUnitPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("materialNo", getMaterialNo())
            .append("materialDate", getMaterialDate())
            .append("warehouseDate", getWarehouseDate())
            .append("supplierId", getSupplierId())
            .append("inHospitalCode", getInHospitalCode())
            .append("endTime", getEndTime())
            .append("warehouse", getWarehouse())
            .append("material", getMaterial())
            .append("supplier", getSupplier())
            .append("warehouseName", getWarehouseName())
            .append("materialName", getMaterialName())
            .toString();
    }
}
