package com.spd.equipment.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 资产盘点单明细与标签打印关联表 sb_asset_inventory_item_print
 */
public class SbAssetInventoryItemPrint extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    private String inventoryId;
    private String orderNo;
    private String inventoryItemId;
    private String assetId;
    private String printTaskId;
    private String printTaskNo;
    private String printTaskItemId;
    private String printLogId;
    private Integer delFlag;
    private String delBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(String inventoryItemId) { this.inventoryItemId = inventoryItemId; }
    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getPrintTaskId() { return printTaskId; }
    public void setPrintTaskId(String printTaskId) { this.printTaskId = printTaskId; }
    public String getPrintTaskNo() { return printTaskNo; }
    public void setPrintTaskNo(String printTaskNo) { this.printTaskNo = printTaskNo; }
    public String getPrintTaskItemId() { return printTaskItemId; }
    public void setPrintTaskItemId(String printTaskItemId) { this.printTaskItemId = printTaskItemId; }
    public String getPrintLogId() { return printLogId; }
    public void setPrintLogId(String printLogId) { this.printLogId = printLogId; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }
    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
