package com.spd.gz.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdDepartment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值出库对象 gz_shipment
 *
 * @author spd
 * @date 2024-12-08
 */
public class GzShipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 出库单号 */
    @Excel(name = "出库单号")
    private String shipmentNo;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 出库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shipmentDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer shipmentStatus;

    /** 出库类型 */
    @Excel(name = "出库类型")
    private Integer shipmentType;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 高值出库明细信息 */
    private List<GzShipmentEntry> gzShipmentEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 科室对象 */
    private FdDepartment department;

    private List<FdMaterial> materialList;

    /** 总金额（用于列表显示） */
    private java.math.BigDecimal totalAmt;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setShipmentNo(String shipmentNo)
    {
        this.shipmentNo = shipmentNo;
    }

    public String getShipmentNo()
    {
        return shipmentNo;
    }
    
    /**
     * 为了前端兼容，提供 orderNo 的 getter，返回 shipmentNo 的值
     */
    public String getOrderNo()
    {
        return shipmentNo;
    }
    
    /**
     * 为了前端兼容，提供 orderNo 的 setter，设置 shipmentNo 的值
     */
    public void setOrderNo(String orderNo)
    {
        this.shipmentNo = orderNo;
    }
    
    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }
    public void setShipmentDate(Date shipmentDate)
    {
        this.shipmentDate = shipmentDate;
    }

    public Date getShipmentDate()
    {
        return shipmentDate;
    }
    
    /**
     * 为了前端兼容，提供 orderDate 的 getter，返回 shipmentDate 的值
     */
    public Date getOrderDate()
    {
        return shipmentDate;
    }
    
    /**
     * 为了前端兼容，提供 orderDate 的 setter，设置 shipmentDate 的值
     */
    public void setOrderDate(Date orderDate)
    {
        this.shipmentDate = orderDate;
    }
    
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
    }
    public void setShipmentStatus(Integer shipmentStatus)
    {
        this.shipmentStatus = shipmentStatus;
    }

    public Integer getShipmentStatus()
    {
        return shipmentStatus;
    }
    
    /**
     * 为了前端兼容，提供 orderStatus 的 getter，返回 shipmentStatus 的值
     */
    public Integer getOrderStatus()
    {
        return shipmentStatus;
    }
    
    /**
     * 为了前端兼容，提供 orderStatus 的 setter，设置 shipmentStatus 的值
     */
    public void setOrderStatus(Integer orderStatus)
    {
        this.shipmentStatus = orderStatus;
    }
    
    public void setShipmentType(Integer shipmentType)
    {
        this.shipmentType = shipmentType;
    }

    public Integer getShipmentType()
    {
        return shipmentType;
    }
    
    /**
     * 为了前端兼容，提供 orderType 的 getter，返回 shipmentType 的值
     */
    public Integer getOrderType()
    {
        return shipmentType;
    }
    
    /**
     * 为了前端兼容，提供 orderType 的 setter，设置 shipmentType 的值
     */
    public void setOrderType(Integer orderType)
    {
        this.shipmentType = orderType;
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

    public List<GzShipmentEntry> getGzShipmentEntryList()
    {
        return gzShipmentEntryList;
    }

    public void setGzShipmentEntryList(List<GzShipmentEntry> gzShipmentEntryList)
    {
        this.gzShipmentEntryList = gzShipmentEntryList;
    }
    
    /**
     * 为了前端兼容，提供 gzOrderEntryList 的 getter，返回 gzShipmentEntryList 的值
     */
    public List<GzShipmentEntry> getGzOrderEntryList()
    {
        return gzShipmentEntryList;
    }
    
    /**
     * 为了前端兼容，提供 gzOrderEntryList 的 setter，设置 gzShipmentEntryList 的值
     */
    public void setGzOrderEntryList(List<GzShipmentEntry> gzOrderEntryList)
    {
        this.gzShipmentEntryList = gzOrderEntryList;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public List<FdMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<FdMaterial> materialList) {
        this.materialList = materialList;
    }

    public java.math.BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(java.math.BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("shipmentNo", getShipmentNo())
            .append("departmentId", getDepartmentId())
            .append("shipmentDate", getShipmentDate())
            .append("warehouseId", getWarehouseId())
            .append("shipmentStatus", getShipmentStatus())
            .append("shipmentType", getShipmentType())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("gzShipmentEntryList", getGzShipmentEntryList())
            .append("warehouse", getWarehouse())
            .append("department", getDepartment())
            .append("materialList", getMaterialList())
            .toString();
    }
}

