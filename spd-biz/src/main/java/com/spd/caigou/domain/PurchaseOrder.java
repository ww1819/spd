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
 * 采购订单对象 purchase_order
 *
 * @author spd
 * @date 2024-01-15
 */
public class PurchaseOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 订单单号 */
    @Excel(name = "订单单号")
    private String orderNo;

    /** 订单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderDate;

    /** 供应商ID */
    private Long supplierId;

    /** 仓库ID */
    private Long warehouseId;

    /** 部门ID */
    private Long departmentId;

    /** 订单状态（0待审核 2已审核 3已执行 4已取消） */
    @Excel(name = "订单状态", readConverterExp = "0=待审核,2=已审核,3=已执行,4=已取消")
    private String orderStatus;

    /** 订单类型（1采购订单 2退货订单） */
    @Excel(name = "订单类型", readConverterExp = "1=采购订单,2=退货订单")
    private String orderType;

    /** 紧急程度（1低 2中 3高） */
    @Excel(name = "紧急程度", readConverterExp = "1=低,2=中,3=高")
    private String urgencyLevel;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 已付金额 */
    @Excel(name = "已付金额")
    private BigDecimal paidAmount;

    /** 未付金额 */
    @Excel(name = "未付金额")
    private BigDecimal unpaidAmount;

    /** 预期交货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预期交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    /** 实际交货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualDeliveryDate;

    /** 付款条件 */
    @Excel(name = "付款条件")
    private String paymentTerms;

    /** 交货地址 */
    @Excel(name = "交货地址")
    private String deliveryAddress;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

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

    /** 供应商信息 */
    private FdSupplier supplier;

    /** 仓库信息 */
    private FdWarehouse warehouse;

    /** 部门信息 */
    private FdDepartment department;

    /** 采购订单明细列表 */
    private List<PurchaseOrderEntry> purchaseOrderEntryList;

    /** 开始日期（查询用） */
    private String beginDate;

    /** 结束日期（查询用） */
    private String endDate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }

    public void setOrderDate(Date orderDate) 
    {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() 
    {
        return orderDate;
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

    public void setOrderStatus(String orderStatus) 
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() 
    {
        return orderStatus;
    }

    public void setOrderType(String orderType) 
    {
        this.orderType = orderType;
    }

    public String getOrderType() 
    {
        return orderType;
    }

    public void setUrgencyLevel(String urgencyLevel) 
    {
        this.urgencyLevel = urgencyLevel;
    }

    public String getUrgencyLevel() 
    {
        return urgencyLevel;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) 
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidAmount() 
    {
        return paidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount) 
    {
        this.unpaidAmount = unpaidAmount;
    }

    public BigDecimal getUnpaidAmount() 
    {
        return unpaidAmount;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) 
    {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getExpectedDeliveryDate() 
    {
        return expectedDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) 
    {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Date getActualDeliveryDate() 
    {
        return actualDeliveryDate;
    }

    public void setPaymentTerms(String paymentTerms) 
    {
        this.paymentTerms = paymentTerms;
    }

    public String getPaymentTerms() 
    {
        return paymentTerms;
    }

    public void setDeliveryAddress(String deliveryAddress) 
    {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() 
    {
        return deliveryAddress;
    }

    public void setContactPerson(String contactPerson) 
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() 
    {
        return contactPerson;
    }

    public void setContactPhone(String contactPhone) 
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() 
    {
        return contactPhone;
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

    public List<PurchaseOrderEntry> getPurchaseOrderEntryList() 
    {
        return purchaseOrderEntryList;
    }

    public void setPurchaseOrderEntryList(List<PurchaseOrderEntry> purchaseOrderEntryList) 
    {
        this.purchaseOrderEntryList = purchaseOrderEntryList;
    }

    public String getBeginDate() 
    {
        return beginDate;
    }

    public void setBeginDate(String beginDate) 
    {
        this.beginDate = beginDate;
    }

    public String getEndDate() 
    {
        return endDate;
    }

    public void setEndDate(String endDate) 
    {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderNo", getOrderNo())
            .append("orderDate", getOrderDate())
            .append("supplierId", getSupplierId())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("orderStatus", getOrderStatus())
            .append("orderType", getOrderType())
            .append("urgencyLevel", getUrgencyLevel())
            .append("totalAmount", getTotalAmount())
            .append("paidAmount", getPaidAmount())
            .append("unpaidAmount", getUnpaidAmount())
            .append("expectedDeliveryDate", getExpectedDeliveryDate())
            .append("actualDeliveryDate", getActualDeliveryDate())
            .append("paymentTerms", getPaymentTerms())
            .append("deliveryAddress", getDeliveryAddress())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("auditBy", getAuditBy())
            .append("auditDate", getAuditDate())
            .append("auditOpinion", getAuditOpinion())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
