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

    /** 计划状态：0-未生成，1-已生成，2-驳回 */
    private Integer planStatus;

    /** 驳回原因 */
    private String rejectReason;

    /** 科室申购明细信息 */
    private List<DepPurchaseApplyEntry> depPurchaseApplyEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 操作人对象 */
    private SysUser user;

    /** 科室对象 */
    private FdDepartment department;

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

    public void setPlanStatus(Integer planStatus) 
    {
        this.planStatus = planStatus;
    }

    public Integer getPlanStatus() 
    {
        return planStatus;
    }

    public void setRejectReason(String rejectReason) 
    {
        this.rejectReason = rejectReason;
    }

    public String getRejectReason() 
    {
        return rejectReason;
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
            .toString();
    }
}
