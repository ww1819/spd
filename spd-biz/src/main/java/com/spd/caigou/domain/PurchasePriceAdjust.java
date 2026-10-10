package com.spd.caigou.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 采购调价单主表 purchase_price_adjust
 */
public class PurchasePriceAdjust extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "单据号")
    private String billNo;

    /** archive / warehouse / department */
    private String adjustType;

    /** 列表展示用 */
    private String adjustTypeName;

    private Long supplierId;

    private String supplierName;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "制单日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billDate;

    private String contactName;

    private String contactPhone;

    /** 0未提交 1未审核（已提交） 2已审核 */
    private String billStatus;

    private String auditBy;

    private String auditByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditDate;

    private String printBy;

    private String printByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date printDate;

    private String delFlag;

    private String tenantId;

    private String createByName;

    /** 查询：bill / audit */
    private String dateQueryType;

    private String beginDate;

    private String endDate;

    private List<PurchasePriceAdjustEntry> entryList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(String adjustType) {
        this.adjustType = adjustType;
    }

    public String getAdjustTypeName() {
        return adjustTypeName;
    }

    public void setAdjustTypeName(String adjustTypeName) {
        this.adjustTypeName = adjustTypeName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public String getAuditByName() {
        return auditByName;
    }

    public void setAuditByName(String auditByName) {
        this.auditByName = auditByName;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getPrintBy() {
        return printBy;
    }

    public void setPrintBy(String printBy) {
        this.printBy = printBy;
    }

    public String getPrintByName() {
        return printByName;
    }

    public void setPrintByName(String printByName) {
        this.printByName = printByName;
    }

    public Date getPrintDate() {
        return printDate;
    }

    public void setPrintDate(Date printDate) {
        this.printDate = printDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getDateQueryType() {
        return dateQueryType;
    }

    public void setDateQueryType(String dateQueryType) {
        this.dateQueryType = dateQueryType;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<PurchasePriceAdjustEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<PurchasePriceAdjustEntry> entryList) {
        this.entryList = entryList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("billNo", getBillNo())
            .append("adjustType", getAdjustType())
            .append("supplierId", getSupplierId())
            .append("billDate", getBillDate())
            .append("billStatus", getBillStatus())
            .append("tenantId", getTenantId())
            .toString();
    }
}
