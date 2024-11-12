package com.spd.gz.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值出库对象 gz_shipment
 *
 * @author spd
 * @date 2024-06-11
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

    /** 科室 */
    private FdDepartment department;

    /** 仓库 */
    private FdWarehouse warehouse;

    private List<FdMaterial> materialList;

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
    public void setShipmentType(Integer shipmentType)
    {
        this.shipmentType = shipmentType;
    }

    public Integer getShipmentType()
    {
        return shipmentType;
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

    public List<FdMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<FdMaterial> materialList) {
        this.materialList = materialList;
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
            .append("department", getDepartment())
            .append("warehouse", getWarehouse())
            .append("materialList", getMaterialList())
            .toString();
    }
}
