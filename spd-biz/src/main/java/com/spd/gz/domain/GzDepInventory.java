package com.spd.gz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值科室库存对象 gz_dep_inventory
 *
 * @author spd
 * @date 2024-06-22
 */
public class GzDepInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 供应商ID（用于查询条件） */
    private Long supplierId;

    /** 耗材名称（用于查询条件） */
    private String materialName;

    /** 是否显示零库存（用于查询条件） */
    private Boolean showZeroStock;

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

    /** 院内码 */
    @Excel(name = "院内码")
    private String inHospitalCode;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 耗材对象 */
    private FdMaterial material;

    /** 科室对象 */
    private FdDepartment department;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 出库单号 */
    @Excel(name = "出库单号")
    private String shipmentNo;

    /** 出库审核人 */
    @Excel(name = "出库审核人")
    private String auditBy;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

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
    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }
    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }
    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getMaterialName()
    {
        return materialName;
    }
    public void setShowZeroStock(Boolean showZeroStock)
    {
        this.showZeroStock = showZeroStock;
    }

    public Boolean getShowZeroStock()
    {
        return showZeroStock;
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

    public String getInHospitalCode() {
        return inHospitalCode;
    }

    public void setInHospitalCode(String inHospitalCode) {
        this.inHospitalCode = inHospitalCode;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("materialId", getMaterialId())
            .append("departmentId", getDepartmentId())
            .append("qty", getQty())
            .append("unitPrice", getUnitPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("materialNo", getMaterialNo())
            .append("materialDate", getMaterialDate())
            .append("warehouseDate", getWarehouseDate())
            .append("inHospitalCode", getInHospitalCode())
            .append("endTime", getEndTime())
            .append("material", getMaterial())
            .append("department", getDepartment())
            .append("warehouse", getWarehouse())
            .append("shipmentNo", getShipmentNo())
            .append("auditBy", getAuditBy())
            .append("auditDate", getAuditDate())
            .toString();
    }
}
