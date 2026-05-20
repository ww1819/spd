package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室申购对象 dep_purchase_apply
 *
 * @author spd
 * @date 2025-01-01
 */
public class DepPurchaseApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 申购单号 */
    @Excel(name = "申购单号")
    private String purchaseBillNo;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date purchaseBillDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long userId;

    /** 申购状态 */
    @Excel(name = "申购状态")
    private Integer purchaseBillStatus;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 紧急程度 */
    @Excel(name = "紧急程度")
    private Integer urgencyLevel;

    /** 期望到货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望到货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    /** 删除标识 */
    private Integer delFlag;

    /** 删除人（逻辑删除时填充） */
    private String deleteBy;
    /** 删除时间（逻辑删除时填充） */
    private Date deleteTime;

    /** 计划状态（历史字段，与收货/引用状态混用；新逻辑请用 purchasePlanRefStatus / receiptStatus） */
    private Integer planStatus;

    /** 采购计划引用：0未引用 1部分引用 2全部引用 3计划引用驳回 */
    private Integer purchasePlanRefStatus;

    /** 出库引用：0未引用 1部分引用 2全部引用 */
    private Integer outboundRefStatus;

    /** 收货确认：0未确认 1已确认 2驳回收货 */
    private Integer receiptStatus;

    /** 驳回原因 */
    private String rejectReason;

    /** 审核人 */
    private String auditBy;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    /** 审核人姓名（查询关联，非表字段） */
    private String auditPersonName;

    /** 制单人姓名（查询关联，非表字段） */
    private String createrPersonName;

    /** 科室申购明细信息 */
    private List<DepPurchaseApplyEntry> depPurchaseApplyEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 操作人对象 */
    private SysUser user;

    /** 科室对象 */
    private FdDepartment department;

    /** 租户id，客户id */
    private String tenantId;

    /** 来源科室汇总申购主表ID(UUID7)，非汇总单为空 */
    private String srcAggApplyId;

    /** 来源科室汇总申购单号 */
    private String srcAggBillNo;

    /** 查询时是否排除已被采购计划引用的申购单（仅查询用，非表字段） */
    private Boolean excludeReferenced;

    /** 排除引用时保留当前计划ID的引用（编辑计划时传入，仅查询用） */
    private Long excludeReferencedPlanId;

    /** 查询时排除的申购单号，逗号分隔（仅查询用，如当前计划明细已引用单号） */
    private String excludePurchaseBillNos;

    /** 解析后的排除单号列表（仅查询用） */
    private java.util.List<String> excludePurchaseBillNoList;

    /** 整单作废：0否 1是 */
    private Integer voidWholeFlag;

    private String voidWholeBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date voidWholeTime;

    private String voidWholeReason;

    /** 列表查询：是否包含已整单作废（默认 false） */
    private Boolean includeVoidWhole;

    /**
     * 出库引用弹窗页签：none|partial|full|lineVoid|wholeVoid
     */
    private String ckRefSheet;

    private Integer lineVoidedEntryCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLineVoidTime;

    private String lastLineVoidBy;

    private BigDecimal linkedCkTotal;

    /** 出库引用候选列表：剩余可下推出库数量合计（非表字段） */
    private BigDecimal pendingOutboundTotal;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setPurchaseBillNo(String purchaseBillNo)
    {
        this.purchaseBillNo = purchaseBillNo;
    }

    public String getPurchaseBillNo()
    {
        return purchaseBillNo;
    }

    public void setPurchaseBillDate(Date purchaseBillDate)
    {
        this.purchaseBillDate = purchaseBillDate;
    }

    public Date getPurchaseBillDate()
    {
        return purchaseBillDate;
    }

    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setPurchaseBillStatus(Integer purchaseBillStatus)
    {
        this.purchaseBillStatus = purchaseBillStatus;
    }

    public Integer getPurchaseBillStatus()
    {
        return purchaseBillStatus;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setUrgencyLevel(Integer urgencyLevel)
    {
        this.urgencyLevel = urgencyLevel;
    }

    public Integer getUrgencyLevel()
    {
        return urgencyLevel;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate)
    {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getExpectedDeliveryDate()
    {
        return expectedDeliveryDate;
    }

    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

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

    public void setPlanStatus(Integer planStatus)
    {
        this.planStatus = planStatus;
    }

    public Integer getPlanStatus()
    {
        return planStatus;
    }

    public Integer getPurchasePlanRefStatus() {
        return purchasePlanRefStatus;
    }

    public void setPurchasePlanRefStatus(Integer purchasePlanRefStatus) {
        this.purchasePlanRefStatus = purchasePlanRefStatus;
    }

    public Integer getOutboundRefStatus() {
        return outboundRefStatus;
    }

    public void setOutboundRefStatus(Integer outboundRefStatus) {
        this.outboundRefStatus = outboundRefStatus;
    }

    public Integer getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(Integer receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public String getRejectReason()
    {
        return rejectReason;
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

    public String getAuditPersonName() {
        return auditPersonName;
    }

    public void setAuditPersonName(String auditPersonName) {
        this.auditPersonName = auditPersonName;
    }

    public String getCreaterPersonName() {
        return createrPersonName;
    }

    public void setCreaterPersonName(String createrPersonName) {
        this.createrPersonName = createrPersonName;
    }

    public List<DepPurchaseApplyEntry> getDepPurchaseApplyEntryList()
    {
        return depPurchaseApplyEntryList;
    }

    public void setDepPurchaseApplyEntryList(List<DepPurchaseApplyEntry> depPurchaseApplyEntryList)
    {
        this.depPurchaseApplyEntryList = depPurchaseApplyEntryList;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("purchaseBillNo", getPurchaseBillNo())
            .append("purchaseBillDate", getPurchaseBillDate())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("userId", getUserId())
            .append("purchaseBillStatus", getPurchaseBillStatus())
            .append("totalAmount", getTotalAmount())
            .append("urgencyLevel", getUrgencyLevel())
            .append("expectedDeliveryDate", getExpectedDeliveryDate())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("depPurchaseApplyEntryList", getDepPurchaseApplyEntryList())
            .append("warehouse", getWarehouse())
            .append("user", getUser())
            .append("department", getDepartment())
                .append("tenantId", getTenantId())
            .toString();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSrcAggApplyId() {
        return srcAggApplyId;
    }

    public void setSrcAggApplyId(String srcAggApplyId) {
        this.srcAggApplyId = srcAggApplyId;
    }

    public String getSrcAggBillNo() {
        return srcAggBillNo;
    }

    public void setSrcAggBillNo(String srcAggBillNo) {
        this.srcAggBillNo = srcAggBillNo;
    }

    public Boolean getExcludeReferenced() { return excludeReferenced; }
    public void setExcludeReferenced(Boolean excludeReferenced) { this.excludeReferenced = excludeReferenced; }

    public Long getExcludeReferencedPlanId() { return excludeReferencedPlanId; }
    public void setExcludeReferencedPlanId(Long excludeReferencedPlanId) { this.excludeReferencedPlanId = excludeReferencedPlanId; }

    public String getExcludePurchaseBillNos() { return excludePurchaseBillNos; }
    public void setExcludePurchaseBillNos(String excludePurchaseBillNos) { this.excludePurchaseBillNos = excludePurchaseBillNos; }

    public java.util.List<String> getExcludePurchaseBillNoList() { return excludePurchaseBillNoList; }
    public void setExcludePurchaseBillNoList(java.util.List<String> excludePurchaseBillNoList) { this.excludePurchaseBillNoList = excludePurchaseBillNoList; }

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

    public String getCkRefSheet() {
        return ckRefSheet;
    }

    public void setCkRefSheet(String ckRefSheet) {
        this.ckRefSheet = ckRefSheet;
    }

    public Integer getLineVoidedEntryCount() {
        return lineVoidedEntryCount;
    }

    public void setLineVoidedEntryCount(Integer lineVoidedEntryCount) {
        this.lineVoidedEntryCount = lineVoidedEntryCount;
    }

    public Date getLastLineVoidTime() {
        return lastLineVoidTime;
    }

    public void setLastLineVoidTime(Date lastLineVoidTime) {
        this.lastLineVoidTime = lastLineVoidTime;
    }

    public String getLastLineVoidBy() {
        return lastLineVoidBy;
    }

    public void setLastLineVoidBy(String lastLineVoidBy) {
        this.lastLineVoidBy = lastLineVoidBy;
    }

    public BigDecimal getLinkedCkTotal() {
        return linkedCkTotal;
    }

    public void setLinkedCkTotal(BigDecimal linkedCkTotal) {
        this.linkedCkTotal = linkedCkTotal;
    }

    public BigDecimal getPendingOutboundTotal() {
        return pendingOutboundTotal;
    }

    public void setPendingOutboundTotal(BigDecimal pendingOutboundTotal) {
        this.pendingOutboundTotal = pendingOutboundTotal;
    }
}
