package com.spd.caigou.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购计划对象 purchase_plan
 *
 * @author spd
 * @date 2024-01-15
 */
public class PurchasePlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 计划单号 */
    @Excel(name = "计划单号")
    private String planNo;

    /** 供应商ID */
    private Long supplierId;

    /** 仓库ID */
    private Long warehouseId;

    /** 部门ID */
    private Long departmentId;

    /** 计划日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planDate;

    /** 计划状态（0未提交 1未提交 2已审核 3已执行 4已取消） */
    @Excel(name = "计划状态", readConverterExp = "0=未提交,1=未提交,2=已审核,3=已执行,4=已取消")
    private String planStatus;

    /** 采购员 */
    @Excel(name = "采购员")
    private String proPerson;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String telephone;

    /** 审核人 */
    @Excel(name = "审核人")
    private String auditBy;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String auditOpinion;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 开始日期（查询用） */
    private String beginDate;

    /** 结束日期（查询用） */
    private String endDate;

    /** 供应商信息 */
    private FdSupplier supplier;

    /** 仓库信息 */
    private FdWarehouse warehouse;

    /** 部门信息 */
    private FdDepartment department;

    /** 采购计划明细列表 */
    private List<PurchasePlanEntry> purchasePlanEntryList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPlanNo(String planNo) 
    {
        this.planNo = planNo;
    }

    public String getPlanNo() 
    {
        return planNo;
    }

    public void setSupplierId(Long supplierId) 
    {
        this.supplierId = supplierId;
    }

    public Long getSupplierId() 
    {
        return supplierId;
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

    public void setPlanDate(Date planDate) 
    {
        this.planDate = planDate;
    }

    public Date getPlanDate() 
    {
        return planDate;
    }

    public void setPlanStatus(String planStatus) 
    {
        this.planStatus = planStatus;
    }

    public String getPlanStatus() 
    {
        return planStatus;
    }

    public void setProPerson(String proPerson) 
    {
        this.proPerson = proPerson;
    }

    public String getProPerson() 
    {
        return proPerson;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setTelephone(String telephone) 
    {
        this.telephone = telephone;
    }

    public String getTelephone() 
    {
        return telephone;
    }

    public void setAuditBy(String auditBy) 
    {
        this.auditBy = auditBy;
    }

    public String getAuditBy() 
    {
        return auditBy;
    }

    public void setAuditDate(Date auditDate) 
    {
        this.auditDate = auditDate;
    }

    public Date getAuditDate() 
    {
        return auditDate;
    }

    public void setAuditOpinion(String auditOpinion) 
    {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditOpinion() 
    {
        return auditOpinion;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setBeginDate(String beginDate) 
    {
        this.beginDate = beginDate;
    }

    public String getBeginDate() 
    {
        return beginDate;
    }

    public void setEndDate(String endDate) 
    {
        this.endDate = endDate;
    }

    public String getEndDate() 
    {
        return endDate;
    }

    public FdSupplier getSupplier() 
    {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) 
    {
        this.supplier = supplier;
    }

    public FdWarehouse getWarehouse() 
    {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) 
    {
        this.warehouse = warehouse;
    }

    public FdDepartment getDepartment() 
    {
        return department;
    }

    public void setDepartment(FdDepartment department) 
    {
        this.department = department;
    }

    public List<PurchasePlanEntry> getPurchasePlanEntryList()
    {
        return purchasePlanEntryList;
    }

    public void setPurchasePlanEntryList(List<PurchasePlanEntry> purchasePlanEntryList)
    {
        this.purchasePlanEntryList = purchasePlanEntryList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("planNo", getPlanNo())
            .append("supplierId", getSupplierId())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("planDate", getPlanDate())
            .append("planStatus", getPlanStatus())
            .append("proPerson", getProPerson())
            .append("totalAmount", getTotalAmount())
            .append("telephone", getTelephone())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("auditBy", getAuditBy())
            .append("auditDate", getAuditDate())
            .toString();
    }
}
