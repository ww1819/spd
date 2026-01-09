package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdDepartment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 新品申购申请对象 new_product_apply
 * 
 * @author spd
 * @date 2025-01-01
 */
public class NewProductApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 申购单号 */
    @Excel(name = "申购单号")
    private String applyNo;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyDate;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 申请状态 */
    @Excel(name = "申请状态")
    private Integer applyStatus;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date auditDate;

    /** 开始日期（用于查询） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 结束日期（用于查询） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 申请理由及效益分析 */
    private String reasonAndBenefit;

    /** 医保(是/否) */
    private String medicalInsurance;

    /** 集采(是/否) */
    private String centralizedProcurement;

    /** 采购形式(长期/临采) */
    private String procurementForm;

    /** 平台价格 */
    private BigDecimal platformPrice;

    /** 新品申购申请明细信息 */
    private List<NewProductApplyEntry> applyEntryList;

    /** 院内同类产品信息 */
    private List<NewProductApplyDetail> applyDetailList;

    /** 操作人对象 */
    private SysUser createByUser;

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

    public void setApplyNo(String applyNo) 
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo() 
    {
        return applyNo;
    }

    public void setApplyDate(Date applyDate) 
    {
        this.applyDate = applyDate;
    }

    public Date getApplyDate() 
    {
        return applyDate;
    }

    public void setDepartmentId(Long departmentId) 
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId() 
    {
        return departmentId;
    }

    public void setApplyStatus(Integer applyStatus) 
    {
        this.applyStatus = applyStatus;
    }

    public Integer getApplyStatus() 
    {
        return applyStatus;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
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

    public Date getBeginDate() 
    {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) 
    {
        this.beginDate = beginDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }

    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public String getReasonAndBenefit() 
    {
        return reasonAndBenefit;
    }

    public void setReasonAndBenefit(String reasonAndBenefit) 
    {
        this.reasonAndBenefit = reasonAndBenefit;
    }

    public String getMedicalInsurance() 
    {
        return medicalInsurance;
    }

    public void setMedicalInsurance(String medicalInsurance) 
    {
        this.medicalInsurance = medicalInsurance;
    }

    public String getCentralizedProcurement() 
    {
        return centralizedProcurement;
    }

    public void setCentralizedProcurement(String centralizedProcurement) 
    {
        this.centralizedProcurement = centralizedProcurement;
    }

    public String getProcurementForm() 
    {
        return procurementForm;
    }

    public void setProcurementForm(String procurementForm) 
    {
        this.procurementForm = procurementForm;
    }

    public BigDecimal getPlatformPrice() 
    {
        return platformPrice;
    }

    public void setPlatformPrice(BigDecimal platformPrice) 
    {
        this.platformPrice = platformPrice;
    }

    public List<NewProductApplyEntry> getApplyEntryList()
    {
        return applyEntryList;
    }

    public void setApplyEntryList(List<NewProductApplyEntry> applyEntryList)
    {
        this.applyEntryList = applyEntryList;
    }

    public List<NewProductApplyDetail> getApplyDetailList()
    {
        return applyDetailList;
    }

    public void setApplyDetailList(List<NewProductApplyDetail> applyDetailList)
    {
        this.applyDetailList = applyDetailList;
    }

    public SysUser getCreateByUser()
    {
        return createByUser;
    }

    public void setCreateByUser(SysUser createByUser)
    {
        this.createByUser = createByUser;
    }

    public FdDepartment getDepartment()
    {
        return department;
    }

    public void setDepartment(FdDepartment department)
    {
        this.department = department;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applyNo", getApplyNo())
            .append("applyDate", getApplyDate())
            .append("departmentId", getDepartmentId())
            .append("applyStatus", getApplyStatus())
            .append("totalAmount", getTotalAmount())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

