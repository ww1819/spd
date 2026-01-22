package com.spd.department.domain;

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
 * 科室批量消耗对象 t_hc_ks_xh
 * 
 * @author spd
 * @date 2025-01-15
 */
public class DeptBatchConsume extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 消耗单号 */
    @Excel(name = "消耗单号")
    private String consumeBillNo;

    /** 消耗日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "消耗日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date consumeBillDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long userId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer consumeBillStatus;

    /** 总金额 */
    private java.math.BigDecimal totalAmount;

    /** 审核人姓名 */
    @Excel(name = "审核人")
    private String auditPersonName;

    /** 制单人姓名 */
    @Excel(name = "制单人")
    private String createrName;

    /** 审核人 */
    private String auditBy;

    /** 审核日期 */
    private Date auditDate;

    /** 驳回原因 */
    private String rejectReason;

    /** 删除标识 */
    private Integer delFlag;

    /** 科室批量消耗明细信息 */
    private List<DeptBatchConsumeEntry> deptBatchConsumeEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 操作人对象 */
    private SysUser user;

    /** 科室对象 */
    private FdDepartment department;

    /** 审核人对象 */
    private SysUser auditPerson;

    /** 制单人对象 */
    private SysUser creater;

    /** 查询参数：起始日期 */
    private Date beginDate;

    /** 查询参数：截止日期 */
    private Date endDate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setConsumeBillNo(String consumeBillNo) 
    {
        this.consumeBillNo = consumeBillNo;
    }

    public String getConsumeBillNo() 
    {
        return consumeBillNo;
    }

    public void setConsumeBillDate(Date consumeBillDate) 
    {
        this.consumeBillDate = consumeBillDate;
    }

    public Date getConsumeBillDate() 
    {
        return consumeBillDate;
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

    public void setConsumeBillStatus(Integer consumeBillStatus) 
    {
        this.consumeBillStatus = consumeBillStatus;
    }

    public Integer getConsumeBillStatus() 
    {
        return consumeBillStatus;
    }

    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    public List<DeptBatchConsumeEntry> getDeptBatchConsumeEntryList()
    {
        return deptBatchConsumeEntryList;
    }

    public void setDeptBatchConsumeEntryList(List<DeptBatchConsumeEntry> deptBatchConsumeEntryList)
    {
        this.deptBatchConsumeEntryList = deptBatchConsumeEntryList;
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

    public String getAuditPersonName() {
        return auditPersonName;
    }

    public void setAuditPersonName(String auditPersonName) {
        this.auditPersonName = auditPersonName;
    }

    public SysUser getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(SysUser auditPerson) {
        this.auditPerson = auditPerson;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public SysUser getCreater() {
        return creater;
    }

    public void setCreater(SysUser creater) {
        this.creater = creater;
    }

    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("consumeBillNo", getConsumeBillNo())
            .append("consumeBillDate", getConsumeBillDate())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("userId", getUserId())
            .append("consumeBillStatus", getConsumeBillStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("totalAmount", getTotalAmount())
            .append("auditBy", getAuditBy())
            .append("auditDate", getAuditDate())
            .append("auditPersonName", getAuditPersonName())
            .append("createrName", getCreaterName())
            .append("rejectReason", getRejectReason())
            .append("deptBatchConsumeEntryList", getDeptBatchConsumeEntryList())
            .append("warehouse", getWarehouse())
            .append("user", getUser())
            .append("department", getDepartment())
            .append("auditPerson", getAuditPerson())
            .append("creater", getCreater())
            .toString();
    }
}
