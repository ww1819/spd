package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室申领明细对象 bas_apply_entry
 * 
 * @author spd
 * @date 2024-02-26
 */
public class BasApplyEntry extends BaseEntity
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

    /**
     * 制单时所选「可用库存」所属仓库（fd_warehouse.id），审核生成仓库申请单时仅在该仓内分配，避免串库
     */
    private Long stockWarehouseId;

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

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date beginTime;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;
    /** 删除人（逻辑删除时填充） */
    private String deleteBy;
    /** 删除时间（逻辑删除时填充） */
    private Date deleteTime;

    /** 耗材对象 */
    private FdMaterial material;

    /** 可用库存所属仓库（查询展示） */
    private FdWarehouse stockWarehouse;

    /** 待出库数量（按库房申请明细汇总；未生成库房申请单时等于申请数量） */
    private BigDecimal pendingOutboundQty;
    /** 出库待审核数量 */
    private BigDecimal ckPendingAuditQty;
    /** 已下推出库合计（关联出库数量，含待审核与已审核） */
    private BigDecimal linkedCkQty;
    /** 已审核出库数量 */
    private BigDecimal ckAuditedQty;
    /** 已作废数量（库房申请明细行累计作废） */
    private BigDecimal whLineVoidQty;

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

    public Long getStockWarehouseId() {
        return stockWarehouseId;
    }

    public void setStockWarehouseId(Long stockWarehouseId) {
        this.stockWarehouseId = stockWarehouseId;
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

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

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
            .append("remark", getRemark())
            .toString();
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public FdWarehouse getStockWarehouse() {
        return stockWarehouse;
    }

    public void setStockWarehouse(FdWarehouse stockWarehouse) {
        this.stockWarehouse = stockWarehouse;
    }

    public BigDecimal getPendingOutboundQty() {
        return pendingOutboundQty;
    }

    public void setPendingOutboundQty(BigDecimal pendingOutboundQty) {
        this.pendingOutboundQty = pendingOutboundQty;
    }

    public BigDecimal getCkPendingAuditQty() {
        return ckPendingAuditQty;
    }

    public void setCkPendingAuditQty(BigDecimal ckPendingAuditQty) {
        this.ckPendingAuditQty = ckPendingAuditQty;
    }

    public BigDecimal getLinkedCkQty() {
        return linkedCkQty;
    }

    public void setLinkedCkQty(BigDecimal linkedCkQty) {
        this.linkedCkQty = linkedCkQty;
    }

    public BigDecimal getCkAuditedQty() {
        return ckAuditedQty;
    }

    public void setCkAuditedQty(BigDecimal ckAuditedQty) {
        this.ckAuditedQty = ckAuditedQty;
    }

    public BigDecimal getWhLineVoidQty() {
        return whLineVoidQty;
    }

    public void setWhLineVoidQty(BigDecimal whLineVoidQty) {
        this.whLineVoidQty = whLineVoidQty;
    }
}
