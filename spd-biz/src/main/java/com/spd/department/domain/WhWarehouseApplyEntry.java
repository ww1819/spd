package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;

/**
 * 仓库申请单明细 wh_warehouse_apply_entry
 */
public class WhWarehouseApplyEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    private String parenId;

    private String tenantId;

    private String basApplyId;

    private String basApplyBillNo;

    private String basApplyEntryId;

    private Integer lineNo;

    private Long materialId;

    private Long warehouseId;

    private Long stkInventoryId;

    private BigDecimal unitPrice;

    private BigDecimal qty;

    private BigDecimal price;

    private BigDecimal amt;

    private String batchNo;

    private String batchNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private Long supplierId;

    private Long factoryId;

    /** 明细作废状态：0正常 1已作废 */
    private Integer lineVoidStatus;

    private BigDecimal lineVoidQty;

    private String lineVoidBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lineVoidTime;

    private String lineVoidReason;

    /** 已关联出库数量（含待审核与已审核） */
    private BigDecimal linkedCkQty;

    /** 关联出库单中待审核数量 */
    private BigDecimal ckPendingAuditQty;

    /** 关联出库单中已审核数量 */
    private BigDecimal ckAuditedQty;

    /** 待出库数量 = 申请数量 - 已作废 - 已关联出库 */
    private BigDecimal pendingOutboundQty;

    /** 耗材档案（查询详情时填充，不落库） */
    private FdMaterial material;

    private Integer delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParenId() {
        return parenId;
    }

    public void setParenId(String parenId) {
        this.parenId = parenId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getBasApplyId() {
        return basApplyId;
    }

    public void setBasApplyId(String basApplyId) {
        this.basApplyId = basApplyId;
    }

    public String getBasApplyBillNo() {
        return basApplyBillNo;
    }

    public void setBasApplyBillNo(String basApplyBillNo) {
        this.basApplyBillNo = basApplyBillNo;
    }

    public String getBasApplyEntryId() {
        return basApplyEntryId;
    }

    public void setBasApplyEntryId(String basApplyEntryId) {
        this.basApplyEntryId = basApplyEntryId;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getStkInventoryId() {
        return stkInventoryId;
    }

    public void setStkInventoryId(Long stkInventoryId) {
        this.stkInventoryId = stkInventoryId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Integer getLineVoidStatus() {
        return lineVoidStatus;
    }

    public void setLineVoidStatus(Integer lineVoidStatus) {
        this.lineVoidStatus = lineVoidStatus;
    }

    public BigDecimal getLineVoidQty() {
        return lineVoidQty;
    }

    public void setLineVoidQty(BigDecimal lineVoidQty) {
        this.lineVoidQty = lineVoidQty;
    }

    public String getLineVoidBy() {
        return lineVoidBy;
    }

    public void setLineVoidBy(String lineVoidBy) {
        this.lineVoidBy = lineVoidBy;
    }

    public Date getLineVoidTime() {
        return lineVoidTime;
    }

    public void setLineVoidTime(Date lineVoidTime) {
        this.lineVoidTime = lineVoidTime;
    }

    public String getLineVoidReason() {
        return lineVoidReason;
    }

    public void setLineVoidReason(String lineVoidReason) {
        this.lineVoidReason = lineVoidReason;
    }

    public BigDecimal getLinkedCkQty() {
        return linkedCkQty;
    }

    public void setLinkedCkQty(BigDecimal linkedCkQty) {
        this.linkedCkQty = linkedCkQty;
    }

    public BigDecimal getCkPendingAuditQty() {
        return ckPendingAuditQty;
    }

    public void setCkPendingAuditQty(BigDecimal ckPendingAuditQty) {
        this.ckPendingAuditQty = ckPendingAuditQty;
    }

    public BigDecimal getCkAuditedQty() {
        return ckAuditedQty;
    }

    public void setCkAuditedQty(BigDecimal ckAuditedQty) {
        this.ckAuditedQty = ckAuditedQty;
    }

    public BigDecimal getPendingOutboundQty() {
        return pendingOutboundQty;
    }

    public void setPendingOutboundQty(BigDecimal pendingOutboundQty) {
        this.pendingOutboundQty = pendingOutboundQty;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
