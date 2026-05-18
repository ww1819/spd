package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdDepartment;

/**
 * 科室汇总申购主表 dep_purchase_apply_agg；明细带仓库ID，审核后按明细仓库拆入 dep_purchase_apply。
 */
public class DepPurchaseApplyAgg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "汇总申购单号")
    private String purchaseBillNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseBillDate;

    private Long departmentId;

    private Long userId;

    /** 1待审核 2已审核(已拆分) 3驳回 */
    private Integer purchaseBillStatus;

    /** 0未拆分 1已拆分 */
    private Integer splitStatus;

    private BigDecimal totalAmount;

    private Integer urgencyLevel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    private String rejectReason;

    private Integer delFlag;

    private String deleteBy;

    private Date deleteTime;

    private String tenantId;

    private List<DepPurchaseApplyAggEntry> entryList;

    private FdDepartment department;

    private SysUser user;

    /** 查询：明细所属仓库（筛选含该仓明细的汇总单） */
    private Long warehouseId;

    /** 查询：制单日期起 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 查询：制单日期止 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurchaseBillNo() {
        return purchaseBillNo;
    }

    public void setPurchaseBillNo(String purchaseBillNo) {
        this.purchaseBillNo = purchaseBillNo;
    }

    public Date getPurchaseBillDate() {
        return purchaseBillDate;
    }

    public void setPurchaseBillDate(Date purchaseBillDate) {
        this.purchaseBillDate = purchaseBillDate;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPurchaseBillStatus() {
        return purchaseBillStatus;
    }

    public void setPurchaseBillStatus(Integer purchaseBillStatus) {
        this.purchaseBillStatus = purchaseBillStatus;
    }

    public Integer getSplitStatus() {
        return splitStatus;
    }

    public void setSplitStatus(Integer splitStatus) {
        this.splitStatus = splitStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(Integer urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public List<DepPurchaseApplyAggEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DepPurchaseApplyAggEntry> entryList) {
        this.entryList = entryList;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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
}
