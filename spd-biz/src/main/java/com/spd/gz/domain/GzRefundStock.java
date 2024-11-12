package com.spd.gz.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值退库对象 gz_refund_stock
 *
 * @author spd
 * @date 2024-06-11
 */
public class GzRefundStock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 退库单号 */
    @Excel(name = "退库单号")
    private String stockNo;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 退库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date stockDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer stockStatus;

    /** 退库类型 */
    @Excel(name = "退库类型")
    private Integer stockType;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 高值退货明细信息 */
    private List<GzRefundStockEntry> gzRefundStockEntryList;

    /** 科室 */
    private FdDepartment department;

    /** 仓库 */
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
    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
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
    public void setStockStatus(Integer stockStatus)
    {
        this.stockStatus = stockStatus;
    }

    public Integer getStockStatus()
    {
        return stockStatus;
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

    public List<GzRefundStockEntry> getGzRefundStockEntryList()
    {
        return gzRefundStockEntryList;
    }

    public void setGzRefundStockEntryList(List<GzRefundStockEntry> gzRefundStockEntryList)
    {
        this.gzRefundStockEntryList = gzRefundStockEntryList;
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
            .append("departmentId", getDepartmentId())
            .append("stockDate", getStockDate())
            .append("warehouseId", getWarehouseId())
            .append("stockStatus", getStockStatus())
            .append("stockType", getStockType())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("gzRefundStockEntryList", getGzRefundStockEntryList())
            .append("department", getDepartment())
            .append("warehouse", getWarehouse())
            .toString();
    }
}
