package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 首页/消息提醒：待处理科室申购单列表行（含已审核时间，用于与申领单预警一致的筛选与 24h 隐藏规则）
 */
public class WarehousePurchaseReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String purchaseBillNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String departmentName;

    private BigDecimal totalAmount;

    private String creatorName;

    /** 1 待审核 2 已审核 */
    private Integer purchaseBillStatus;

    /**
     * 已审核时取主表 update_time（审核通过时写入）；待审核为 null。
     * 与申领单侧 lastOutboundAuditDate 语义对应，供前端「已审核满 24 小时不再展示」等逻辑使用。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPurchaseAuditDate;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getPurchaseBillNo()
    {
        return purchaseBillNo;
    }

    public void setPurchaseBillNo(String purchaseBillNo)
    {
        this.purchaseBillNo = purchaseBillNo;
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

    public Integer getPurchaseBillStatus()
    {
        return purchaseBillStatus;
    }

    public void setPurchaseBillStatus(Integer purchaseBillStatus)
    {
        this.purchaseBillStatus = purchaseBillStatus;
    }

    public Date getLastPurchaseAuditDate()
    {
        return lastPurchaseAuditDate;
    }

    public void setLastPurchaseAuditDate(Date lastPurchaseAuditDate)
    {
        this.lastPurchaseAuditDate = lastPurchaseAuditDate;
    }
}
