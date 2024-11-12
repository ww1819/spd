package com.spd.gz.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值入库对象 gz_order
 *
 * @author spd
 * @date 2024-06-11
 */
public class GzOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 入库单号 */
    @Excel(name = "入库单号")
    private String orderNo;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplerId;

    /** 入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer orderStatus;

    /** 入库类型 */
    @Excel(name = "入库类型")
    private Integer orderType;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 高值退货明细信息 */
    private List<GzOrderEntry> gzOrderEntryList;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 仓库对象 */
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
    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }
    public void setSupplerId(Long supplerId)
    {
        this.supplerId = supplerId;
    }

    public Long getSupplerId()
    {
        return supplerId;
    }
    public void setOrderDate(Date orderDate)
    {
        this.orderDate = orderDate;
    }

    public Date getOrderDate()
    {
        return orderDate;
    }
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
    }
    public void setOrderStatus(Integer orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderStatus()
    {
        return orderStatus;
    }
    public void setOrderType(Integer orderType)
    {
        this.orderType = orderType;
    }

    public Integer getOrderType()
    {
        return orderType;
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

    public List<GzOrderEntry> getGzOrderEntryList()
    {
        return gzOrderEntryList;
    }

    public void setGzRefundGoodsEntryList(List<GzOrderEntry> gzOrderEntryList)
    {
        this.gzOrderEntryList = gzOrderEntryList;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
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
            .append("orderNo", getOrderNo())
            .append("supplerId", getSupplerId())
            .append("orderDate", getOrderDate())
            .append("warehouseId", getWarehouseId())
            .append("orderStatus", getOrderStatus())
            .append("orderType", getOrderType())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("gzOrderEntryList", getGzOrderEntryList())
            .append("supplier", getSupplier())
            .append("warehouse", getWarehouse())
            .append("materialList", getMaterialList())
            .toString();
    }
}
