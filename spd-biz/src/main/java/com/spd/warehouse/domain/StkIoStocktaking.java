package com.spd.warehouse.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 盘点对象 stk_io_stocktaking
 *
 * @author spd
 * @date 2024-06-27
 */
public class StkIoStocktaking extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    private String stockNo;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplerId;

    /** 盘点日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date stockDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer stockStatus;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long userId;

    /** 盘点类型 */
    @Excel(name = "盘点类型")
    private Integer stockType;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 驳回原因 */
    @Excel(name = "驳回原因")
    private String rejectReason;

    /** 是否月结 */
    private Integer isMonthInit;

    /** 盈亏金额（用于列表显示，从明细汇总） */
    private java.math.BigDecimal profitAmount;

    /** 总金额（用于列表显示，从明细汇总） */
    private java.math.BigDecimal totalAmount;

    /** 查询参数：开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 查询参数：结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 盘点明细信息 */
    private List<StkIoStocktakingEntry> stkIoStocktakingEntryList;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 科室对象 */
    private FdDepartment department;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setStockNo(String stockNo)
    {
        this.stockNo = stockNo;
    }

    public String getStockNo()
    {
        return stockNo;
    }
    public void setSupplerId(Long supplerId)
    {
        this.supplerId = supplerId;
    }

    public Long getSupplerId()
    {
        return supplerId;
    }
    public void setStockDate(Date stockDate)
    {
        this.stockDate = stockDate;
    }

    public Date getStockDate()
    {
        return stockDate;
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
    public void setStockStatus(Integer stockStatus)
    {
        this.stockStatus = stockStatus;
    }

    public Integer getStockStatus()
    {
        return stockStatus;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setStockType(Integer stockType)
    {
        this.stockType = stockType;
    }

    public Integer getStockType()
    {
        return stockType;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }
    public void setAuditDate(Date auditDate)
    {
        this.auditDate = auditDate;
    }

    public Date getAuditDate()
    {
        return auditDate;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public Integer getIsMonthInit() {
        return isMonthInit;
    }

    public void setIsMonthInit(Integer isMonthInit) {
        this.isMonthInit = isMonthInit;
    }

    public java.math.BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(java.math.BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public List<StkIoStocktakingEntry> getStkIoStocktakingEntryList()
    {
        return stkIoStocktakingEntryList;
    }

    public void setStkIoStocktakingEntryList(List<StkIoStocktakingEntry> stkIoStocktakingEntryList)
    {
        this.stkIoStocktakingEntryList = stkIoStocktakingEntryList;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("stockNo", getStockNo())
            .append("supplerId", getSupplerId())
            .append("stockDate", getStockDate())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("stockStatus", getStockStatus())
            .append("userId", getUserId())
            .append("stockType", getStockType())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("rejectReason", getRejectReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("stkIoStocktakingEntryList", getStkIoStocktakingEntryList())
            .append("supplier", getSupplier())
            .append("department", getDepartment())
            .append("warehouse", getWarehouse())
            .append("isMonthInit", getIsMonthInit())
            .append("profitAmount", getProfitAmount())
            .append("totalAmount", getTotalAmount())
            .toString();
    }
}
