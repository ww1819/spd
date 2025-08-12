package com.spd.biz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备采购申请对象 equipment_purchase_application
 * 
 * @author spd
 * @date 2024-01-15
 */
public class EquipmentPurchaseApplication extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applicationNo;

    /** 申请标题 */
    @Excel(name = "申请标题")
    private String applicationTitle;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String equipmentName;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String equipmentModel;

    /** 设备规格 */
    @Excel(name = "设备规格")
    private String equipmentSpecification;

    /** 设备品牌 */
    @Excel(name = "设备品牌")
    private String equipmentBrand;

    /** 采购数量 */
    @Excel(name = "采购数量")
    private Integer quantity;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 预算金额 */
    @Excel(name = "预算金额")
    private BigDecimal budgetAmount;

    /** 紧急程度（1紧急 2一般 3不紧急） */
    @Excel(name = "紧急程度", readConverterExp = "1=紧急,2=一般,3=不紧急")
    private String urgencyLevel;

    /** 申请理由 */
    @Excel(name = "申请理由")
    private String applicationReason;

    /** 技术要求 */
    @Excel(name = "技术要求")
    private String technicalRequirements;

    /** 供应商建议 */
    @Excel(name = "供应商建议")
    private String supplierSuggestion;

    /** 期望交货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    /** 申请部门 */
    @Excel(name = "申请部门")
    private String applicationDepartment;

    /** 申请人 */
    @Excel(name = "申请人")
    private String applicant;

    /** 申请人电话 */
    @Excel(name = "申请人电话")
    private String applicantPhone;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applicationDate;

    /** 状态（0待审核 1审核通过 2审核拒绝 3已采购 4已完成） */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=审核通过,2=审核拒绝,3=已采购,4=已完成")
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewer;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewDate;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String reviewOpinion;

    /** 采购订单号 */
    @Excel(name = "采购订单号")
    private String purchaseOrderNo;

    /** 采购日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date purchaseDate;

    /** 实际采购金额 */
    @Excel(name = "实际采购金额")
    private BigDecimal actualAmount;

    /** 实际交货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际交货日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryDate;

    /** 安装日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "安装日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date installationDate;

    /** 验收日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "验收日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date acceptanceDate;

    /** 保修期（月） */
    @Excel(name = "保修期（月）")
    private Integer warrantyPeriod;

    /** 维护联系人 */
    @Excel(name = "维护联系人")
    private String maintenanceContact;

    /** 维护电话 */
    @Excel(name = "维护电话")
    private String maintenancePhone;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setApplicationNo(String applicationNo) 
    {
        this.applicationNo = applicationNo;
    }

    public String getApplicationNo() 
    {
        return applicationNo;
    }
    public void setApplicationTitle(String applicationTitle) 
    {
        this.applicationTitle = applicationTitle;
    }

    public String getApplicationTitle() 
    {
        return applicationTitle;
    }
    public void setEquipmentName(String equipmentName) 
    {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentName() 
    {
        return equipmentName;
    }
    public void setEquipmentModel(String equipmentModel) 
    {
        this.equipmentModel = equipmentModel;
    }

    public String getEquipmentModel() 
    {
        return equipmentModel;
    }
    public void setEquipmentSpecification(String equipmentSpecification) 
    {
        this.equipmentSpecification = equipmentSpecification;
    }

    public String getEquipmentSpecification() 
    {
        return equipmentSpecification;
    }
    public void setEquipmentBrand(String equipmentBrand) 
    {
        this.equipmentBrand = equipmentBrand;
    }

    public String getEquipmentBrand() 
    {
        return equipmentBrand;
    }
    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }
    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }
    public void setBudgetAmount(BigDecimal budgetAmount) 
    {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getBudgetAmount() 
    {
        return budgetAmount;
    }
    public void setUrgencyLevel(String urgencyLevel) 
    {
        this.urgencyLevel = urgencyLevel;
    }

    public String getUrgencyLevel() 
    {
        return urgencyLevel;
    }
    public void setApplicationReason(String applicationReason) 
    {
        this.applicationReason = applicationReason;
    }

    public String getApplicationReason() 
    {
        return applicationReason;
    }
    public void setTechnicalRequirements(String technicalRequirements) 
    {
        this.technicalRequirements = technicalRequirements;
    }

    public String getTechnicalRequirements() 
    {
        return technicalRequirements;
    }
    public void setSupplierSuggestion(String supplierSuggestion) 
    {
        this.supplierSuggestion = supplierSuggestion;
    }

    public String getSupplierSuggestion() 
    {
        return supplierSuggestion;
    }
    public void setExpectedDeliveryDate(Date expectedDeliveryDate) 
    {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getExpectedDeliveryDate() 
    {
        return expectedDeliveryDate;
    }
    public void setApplicationDepartment(String applicationDepartment) 
    {
        this.applicationDepartment = applicationDepartment;
    }

    public String getApplicationDepartment() 
    {
        return applicationDepartment;
    }
    public void setApplicant(String applicant) 
    {
        this.applicant = applicant;
    }

    public String getApplicant() 
    {
        return applicant;
    }
    public void setApplicantPhone(String applicantPhone) 
    {
        this.applicantPhone = applicantPhone;
    }

    public String getApplicantPhone() 
    {
        return applicantPhone;
    }
    public void setApplicationDate(Date applicationDate) 
    {
        this.applicationDate = applicationDate;
    }

    public Date getApplicationDate() 
    {
        return applicationDate;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setReviewer(String reviewer) 
    {
        this.reviewer = reviewer;
    }

    public String getReviewer() 
    {
        return reviewer;
    }
    public void setReviewDate(Date reviewDate) 
    {
        this.reviewDate = reviewDate;
    }

    public Date getReviewDate() 
    {
        return reviewDate;
    }
    public void setReviewOpinion(String reviewOpinion) 
    {
        this.reviewOpinion = reviewOpinion;
    }

    public String getReviewOpinion() 
    {
        return reviewOpinion;
    }
    public void setPurchaseOrderNo(String purchaseOrderNo) 
    {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public String getPurchaseOrderNo() 
    {
        return purchaseOrderNo;
    }
    public void setPurchaseDate(Date purchaseDate) 
    {
        this.purchaseDate = purchaseDate;
    }

    public Date getPurchaseDate() 
    {
        return purchaseDate;
    }
    public void setActualAmount(BigDecimal actualAmount) 
    {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getActualAmount() 
    {
        return actualAmount;
    }
    public void setDeliveryDate(Date deliveryDate) 
    {
        this.deliveryDate = deliveryDate;
    }

    public Date getDeliveryDate() 
    {
        return deliveryDate;
    }
    public void setInstallationDate(Date installationDate) 
    {
        this.installationDate = installationDate;
    }

    public Date getInstallationDate() 
    {
        return installationDate;
    }
    public void setAcceptanceDate(Date acceptanceDate) 
    {
        this.acceptanceDate = acceptanceDate;
    }

    public Date getAcceptanceDate() 
    {
        return acceptanceDate;
    }
    public void setWarrantyPeriod(Integer warrantyPeriod) 
    {
        this.warrantyPeriod = warrantyPeriod;
    }

    public Integer getWarrantyPeriod() 
    {
        return warrantyPeriod;
    }
    public void setMaintenanceContact(String maintenanceContact) 
    {
        this.maintenanceContact = maintenanceContact;
    }

    public String getMaintenanceContact() 
    {
        return maintenanceContact;
    }
    public void setMaintenancePhone(String maintenancePhone) 
    {
        this.maintenancePhone = maintenancePhone;
    }

    public String getMaintenancePhone() 
    {
        return maintenancePhone;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applicationNo", getApplicationNo())
            .append("applicationTitle", getApplicationTitle())
            .append("equipmentName", getEquipmentName())
            .append("equipmentModel", getEquipmentModel())
            .append("equipmentSpecification", getEquipmentSpecification())
            .append("equipmentBrand", getEquipmentBrand())
            .append("quantity", getQuantity())
            .append("unitPrice", getUnitPrice())
            .append("totalAmount", getTotalAmount())
            .append("budgetAmount", getBudgetAmount())
            .append("urgencyLevel", getUrgencyLevel())
            .append("applicationReason", getApplicationReason())
            .append("technicalRequirements", getTechnicalRequirements())
            .append("supplierSuggestion", getSupplierSuggestion())
            .append("expectedDeliveryDate", getExpectedDeliveryDate())
            .append("applicationDepartment", getApplicationDepartment())
            .append("applicant", getApplicant())
            .append("applicantPhone", getApplicantPhone())
            .append("applicationDate", getApplicationDate())
            .append("status", getStatus())
            .append("reviewer", getReviewer())
            .append("reviewDate", getReviewDate())
            .append("reviewOpinion", getReviewOpinion())
            .append("purchaseOrderNo", getPurchaseOrderNo())
            .append("purchaseDate", getPurchaseDate())
            .append("actualAmount", getActualAmount())
            .append("deliveryDate", getDeliveryDate())
            .append("installationDate", getInstallationDate())
            .append("acceptanceDate", getAcceptanceDate())
            .append("warrantyPeriod", getWarrantyPeriod())
            .append("maintenanceContact", getMaintenanceContact())
            .append("maintenancePhone", getMaintenancePhone())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
} 