package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;

/**
 * 仓库申请单主表 wh_warehouse_apply（科室申领审核后按仓拆分）
 */
public class WhWarehouseApply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "仓库申请单号")
    private String applyBillNo;

    /** 科室申领主表 ID（字符串形式） */
    private String basApplyId;

    @Excel(name = "科室申领单号")
    private String basApplyBillNo;

    private Long warehouseId;

    private Long departmentId;

    /** 1待审核 2已生效 3关闭 5整单作废 */
    private Integer billStatus;

    /** 整单作废：0否 1是 */
    private Integer voidWholeFlag;

    private String voidWholeBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date voidWholeTime;

    private String voidWholeReason;

    /** 列表查询：是否包含已整单作废（默认 false） */
    private Boolean includeVoidWhole;

    private BigDecimal totalQty;

    private BigDecimal totalAmt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sourceAuditDate;

    private String tenantId;

    private Integer delFlag;

    private FdWarehouse warehouse;

    private FdDepartment department;

    private List<WhWarehouseApplyEntry> entryList;

    /** 出库引用候选列表用：本单剩余可下推出库数量合计（非表字段） */
    private BigDecimal pendingOutboundTotal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyBillNo() {
        return applyBillNo;
    }

    public void setApplyBillNo(String applyBillNo) {
        this.applyBillNo = applyBillNo;
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

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    public Integer getVoidWholeFlag() {
        return voidWholeFlag;
    }

    public void setVoidWholeFlag(Integer voidWholeFlag) {
        this.voidWholeFlag = voidWholeFlag;
    }

    public String getVoidWholeBy() {
        return voidWholeBy;
    }

    public void setVoidWholeBy(String voidWholeBy) {
        this.voidWholeBy = voidWholeBy;
    }

    public Date getVoidWholeTime() {
        return voidWholeTime;
    }

    public void setVoidWholeTime(Date voidWholeTime) {
        this.voidWholeTime = voidWholeTime;
    }

    public String getVoidWholeReason() {
        return voidWholeReason;
    }

    public void setVoidWholeReason(String voidWholeReason) {
        this.voidWholeReason = voidWholeReason;
    }

    public Boolean getIncludeVoidWhole() {
        return includeVoidWhole;
    }

    public void setIncludeVoidWhole(Boolean includeVoidWhole) {
        this.includeVoidWhole = includeVoidWhole;
    }

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public Date getSourceAuditDate() {
        return sourceAuditDate;
    }

    public void setSourceAuditDate(Date sourceAuditDate) {
        this.sourceAuditDate = sourceAuditDate;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public List<WhWarehouseApplyEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<WhWarehouseApplyEntry> entryList) {
        this.entryList = entryList;
    }

    public BigDecimal getPendingOutboundTotal() {
        return pendingOutboundTotal;
    }

    public void setPendingOutboundTotal(BigDecimal pendingOutboundTotal) {
        this.pendingOutboundTotal = pendingOutboundTotal;
    }
}
