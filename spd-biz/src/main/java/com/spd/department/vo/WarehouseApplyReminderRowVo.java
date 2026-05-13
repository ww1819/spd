package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 首页/消息提醒：待出库申领单列表行（含关联已审核出库时间）
 */
public class WarehouseApplyReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String applyBillNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String departmentName;

    private BigDecimal totalAmount;

    private String creatorName;

    /** 关联 bill_type=201 且 bill_status=2 的出库单审核时间最大值；为空表示尚无已审核出库 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOutboundAuditDate;

    private Integer applyBillStatus;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getApplyBillNo()
    {
        return applyBillNo;
    }

    public void setApplyBillNo(String applyBillNo)
    {
        this.applyBillNo = applyBillNo;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public void setCreatorName(String creatorName)
    {
        this.creatorName = creatorName;
    }

    public Date getLastOutboundAuditDate()
    {
        return lastOutboundAuditDate;
    }

    public void setLastOutboundAuditDate(Date lastOutboundAuditDate)
    {
        this.lastOutboundAuditDate = lastOutboundAuditDate;
    }

    public Integer getApplyBillStatus()
    {
        return applyBillStatus;
    }

    public void setApplyBillStatus(Integer applyBillStatus)
    {
        this.applyBillStatus = applyBillStatus;
    }
}
