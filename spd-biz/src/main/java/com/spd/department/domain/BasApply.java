package com.spd.department.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室申领对象 bas_apply
 * 
 * @author spd
 * @date 2024-02-26
 */
public class BasApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 申领单号 */
    @Excel(name = "申领单号")
    private String applyBillNo;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyBillDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long userId;

    /** 申请状态 */
    @Excel(name = "申请状态")
    private Integer applyBillStatus;

    /** 删除标识 */
    private Integer delFlag;

    /** 科室申领明细信息 */
    private List<BasApplyEntry> basApplyEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 操作人对象 */
    private SysUser user;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setApplyBillNo(String applyBillNo) 
    {
        this.applyBillNo = applyBillNo;
    }

    public String getApplyBillNo() 
    {
        return applyBillNo;
    }
    public void setApplyBillDate(Date applyBillDate) 
    {
        this.applyBillDate = applyBillDate;
    }

    public Date getApplyBillDate() 
    {
        return applyBillDate;
    }
    public void setWarehouseId(Long warehouseId) 
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() 
    {
        return warehouseId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setApplyBillStatus(Integer applyBillStatus) 
    {
        this.applyBillStatus = applyBillStatus;
    }

    public Integer getApplyBillStatus() 
    {
        return applyBillStatus;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    public List<BasApplyEntry> getBasApplyEntryList()
    {
        return basApplyEntryList;
    }

    public void setBasApplyEntryList(List<BasApplyEntry> basApplyEntryList)
    {
        this.basApplyEntryList = basApplyEntryList;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applyBillNo", getApplyBillNo())
            .append("applyBillDate", getApplyBillDate())
            .append("warehouseId", getWarehouseId())
            .append("userId", getUserId())
            .append("applyBillStatus", getApplyBillStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("basApplyEntryList", getBasApplyEntryList())
                .append("warehouse", getWarehouse())
                .append("user", getUser())
            .toString();
    }
}
