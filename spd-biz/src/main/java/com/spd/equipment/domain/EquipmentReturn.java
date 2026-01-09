package com.spd.equipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备退货对象 equipment_return
 * 
 * @author spd
 * @date 2024-01-01
 */
public class EquipmentReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long returnId;

    /** 退货单号 */
    @Excel(name = "退货单号")
    private String returnNo;

    /** 仓库ID */
    private Long warehouseId;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String warehouseName;

    /** 退货状态（0未审核 2已审核） */
    @Excel(name = "退货状态", readConverterExp = "0=未审核,2=已审核")
    private String returnStatus;

    /** 制单人ID */
    private Long createrId;

    /** 制单人名称 */
    @Excel(name = "制单人")
    private String createrName;

    /** 制单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "制单日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date billDate;

    /** 审核人ID */
    private Long auditorId;

    /** 审核人名称 */
    @Excel(name = "审核人")
    private String auditorName;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    /** 退货总价 */
    @Excel(name = "退货总价")
    private BigDecimal returnAmount;

    /** 供应商 */
    @Excel(name = "供应商")
    private String supplier;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 明细列表 */
    private List<EquipmentReturnDetail> detailList;

    /** 设备名称（查询条件，不持久化） */
    private String equipmentName;

    public void setReturnId(Long returnId) 
    {
        this.returnId = returnId;
    }

    public Long getReturnId() 
    {
        return returnId;
    }

    public void setReturnNo(String returnNo) 
    {
        this.returnNo = returnNo;
    }

    public String getReturnNo() 
    {
        return returnNo;
    }

    public void setWarehouseId(Long warehouseId) 
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() 
    {
        return warehouseId;
    }

    public void setWarehouseName(String warehouseName) 
    {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseName() 
    {
        return warehouseName;
    }

    public void setReturnStatus(String returnStatus) 
    {
        this.returnStatus = returnStatus;
    }

    public String getReturnStatus() 
    {
        return returnStatus;
    }

    public void setCreaterId(Long createrId) 
    {
        this.createrId = createrId;
    }

    public Long getCreaterId() 
    {
        return createrId;
    }

    public void setCreaterName(String createrName) 
    {
        this.createrName = createrName;
    }

    public String getCreaterName() 
    {
        return createrName;
    }

    public void setBillDate(Date billDate) 
    {
        this.billDate = billDate;
    }

    public Date getBillDate() 
    {
        return billDate;
    }

    public void setAuditorId(Long auditorId) 
    {
        this.auditorId = auditorId;
    }

    public Long getAuditorId() 
    {
        return auditorId;
    }

    public void setAuditorName(String auditorName) 
    {
        this.auditorName = auditorName;
    }

    public String getAuditorName() 
    {
        return auditorName;
    }

    public void setAuditDate(Date auditDate) 
    {
        this.auditDate = auditDate;
    }

    public Date getAuditDate() 
    {
        return auditDate;
    }

    public void setReturnAmount(BigDecimal returnAmount) 
    {
        this.returnAmount = returnAmount;
    }

    public BigDecimal getReturnAmount() 
    {
        return returnAmount;
    }

    public void setSupplier(String supplier) 
    {
        this.supplier = supplier;
    }

    public String getSupplier() 
    {
        return supplier;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    public void setDetailList(List<EquipmentReturnDetail> detailList) 
    {
        this.detailList = detailList;
    }

    public List<EquipmentReturnDetail> getDetailList() 
    {
        return detailList;
    }

    public void setEquipmentName(String equipmentName) 
    {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentName() 
    {
        return equipmentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("returnId", getReturnId())
            .append("returnNo", getReturnNo())
            .append("warehouseId", getWarehouseId())
            .append("warehouseName", getWarehouseName())
            .append("returnStatus", getReturnStatus())
            .append("createrId", getCreaterId())
            .append("createrName", getCreaterName())
            .append("billDate", getBillDate())
            .append("auditorId", getAuditorId())
            .append("auditorName", getAuditorName())
            .append("auditDate", getAuditDate())
            .append("returnAmount", getReturnAmount())
            .append("supplier", getSupplier())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
