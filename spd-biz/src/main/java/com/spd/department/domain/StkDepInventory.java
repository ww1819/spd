package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
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

    /** 耗材对象 */
    private FdMaterial material;

    /** 科室对象 */
    private FdDepartment department;

    /** 供应商对象 */
    private FdSupplier supplier;

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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }
}
