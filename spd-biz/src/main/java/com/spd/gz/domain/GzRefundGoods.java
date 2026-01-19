package com.spd.gz.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdDepartment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值退货对象 gz_refund_goods
 *
 * @author spd
 * @date 2024-06-11
 */
public class GzRefundGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 退货单号 */
    @Excel(name = "退货单号")
    private String goodsNo;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplerId;

    /** 退货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date goodsDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer goodsStatus;

    /** 退货类型 */
    @Excel(name = "退货类型")
    private Integer goodsType;

    /** 删除标识 */
    private Integer delFlag;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 审核人 */
    @Excel(name = "审核人")
    private String auditBy;

    /** 高值退货明细信息 */
    private List<GzRefundGoodsEntry> gzRefundGoodsEntryList;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 科室对象 */
    private FdDepartment department;

    /** 耗材列表 */
    private List<FdMaterial> materialList;

    /** 总金额 */
    private java.math.BigDecimal totalAmt;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setGoodsNo(String goodsNo)
    {
        this.goodsNo = goodsNo;
    }

    public String getGoodsNo()
    {
        return goodsNo;
    }
    public void setSupplerId(Long supplerId)
    {
        this.supplerId = supplerId;
    }

    public Long getSupplerId()
    {
        return supplerId;
    }
    public void setGoodsDate(Date goodsDate)
    {
        this.goodsDate = goodsDate;
    }

    public Date getGoodsDate()
    {
        return goodsDate;
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
    public void setGoodsStatus(Integer goodsStatus)
    {
        this.goodsStatus = goodsStatus;
    }

    public Integer getGoodsStatus()
    {
        return goodsStatus;
    }
    public void setGoodsType(Integer goodsType)
    {
        this.goodsType = goodsType;
    }

    public Integer getGoodsType()
    {
        return goodsType;
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
    public void setAuditBy(String auditBy)
    {
        this.auditBy = auditBy;
    }

    public String getAuditBy()
    {
        return auditBy;
    }

    public List<GzRefundGoodsEntry> getGzRefundGoodsEntryList()
    {
        return gzRefundGoodsEntryList;
    }

    public void setGzRefundGoodsEntryList(List<GzRefundGoodsEntry> gzRefundGoodsEntryList)
    {
        this.gzRefundGoodsEntryList = gzRefundGoodsEntryList;
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

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
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
            .append("goodsNo", getGoodsNo())
            .append("supplerId", getSupplerId())
            .append("goodsDate", getGoodsDate())
            .append("warehouseId", getWarehouseId())
            .append("goodsStatus", getGoodsStatus())
            .append("goodsType", getGoodsType())
            .append("delFlag", getDelFlag())
            .append("auditDate", getAuditDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("gzRefundGoodsEntryList", getGzRefundGoodsEntryList())
            .append("supplier", getSupplier())
            .append("warehouse", getSupplier())
            .toString();
    }
}
