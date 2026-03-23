package com.spd.equipment.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 资产盘点单主表 sb_asset_inventory
 */
public class SbAssetInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    @Excel(name = "单号")
    private String orderNo;
    @Excel(name = "制单人")
    private String createBy;
    @Excel(name = "制单时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @Excel(name = "状态")
    private String status;
    @Excel(name = "审核人")
    private String auditBy;
    @Excel(name = "审核时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    @Excel(name = "盘点类型")
    private String inventoryType;
    private String inventoryDeptId;
    @Excel(name = "盘点科室")
    private String inventoryDeptName;
    /** 盘点68分类ID（按68分类盘点时） */
    private String inventoryCategory68Id;
    /** 盘点68分类编码 */
    private String inventoryCategory68Code;
    /** 盘点存放地点（按存放地点盘点时） */
    private String storagePlace;
    @Excel(name = "计划盘点日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    private Integer totalCount;
    private Integer checkedCount;
    private Integer delFlag;
    private String delBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getInventoryType() { return inventoryType; }
    public void setInventoryType(String inventoryType) { this.inventoryType = inventoryType; }
    public String getInventoryDeptId() { return inventoryDeptId; }
    public void setInventoryDeptId(String inventoryDeptId) { this.inventoryDeptId = inventoryDeptId; }
    public String getInventoryDeptName() { return inventoryDeptName; }
    public void setInventoryDeptName(String inventoryDeptName) { this.inventoryDeptName = inventoryDeptName; }
    public String getInventoryCategory68Id() { return inventoryCategory68Id; }
    public void setInventoryCategory68Id(String inventoryCategory68Id) { this.inventoryCategory68Id = inventoryCategory68Id; }
    public String getInventoryCategory68Code() { return inventoryCategory68Code; }
    public void setInventoryCategory68Code(String inventoryCategory68Code) { this.inventoryCategory68Code = inventoryCategory68Code; }
    public String getStoragePlace() { return storagePlace; }
    public void setStoragePlace(String storagePlace) { this.storagePlace = storagePlace; }
    public Date getPlanDate() { return planDate; }
    public void setPlanDate(Date planDate) { this.planDate = planDate; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getCheckedCount() { return checkedCount; }
    public void setCheckedCount(Integer checkedCount) { this.checkedCount = checkedCount; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }
    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
