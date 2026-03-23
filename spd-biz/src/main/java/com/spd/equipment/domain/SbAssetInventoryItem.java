package com.spd.equipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 资产盘点单明细表 sb_asset_inventory_item
 */
public class SbAssetInventoryItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    private String inventoryId;
    private String orderNo;
    private String assetId;
    private String name;
    private String spec;
    private String model;
    private BigDecimal originalValue;
    private String deptId;
    private String deptName;
    private String storagePlace;
    private String equipmentSerialNo;
    private String category68Id;
    private String category68Name;
    private String manufacturerName;
    private String needReprintLabel;
    private Integer printCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPrintTime;
    private String printStatus;
    private String checkStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;
    private String checkBy;
    private String realStoragePlace;
    private String differenceRemark;
    private Integer sortOrder;
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
    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getOriginalValue() { return originalValue; }
    public void setOriginalValue(BigDecimal originalValue) { this.originalValue = originalValue; }
    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getStoragePlace() { return storagePlace; }
    public void setStoragePlace(String storagePlace) { this.storagePlace = storagePlace; }
    public String getEquipmentSerialNo() { return equipmentSerialNo; }
    public void setEquipmentSerialNo(String equipmentSerialNo) { this.equipmentSerialNo = equipmentSerialNo; }
    public String getCategory68Id() { return category68Id; }
    public void setCategory68Id(String category68Id) { this.category68Id = category68Id; }
    public String getCategory68Name() { return category68Name; }
    public void setCategory68Name(String category68Name) { this.category68Name = category68Name; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public String getNeedReprintLabel() { return needReprintLabel; }
    public void setNeedReprintLabel(String needReprintLabel) { this.needReprintLabel = needReprintLabel; }
    public Integer getPrintCount() { return printCount; }
    public void setPrintCount(Integer printCount) { this.printCount = printCount; }
    public Date getLastPrintTime() { return lastPrintTime; }
    public void setLastPrintTime(Date lastPrintTime) { this.lastPrintTime = lastPrintTime; }
    public String getPrintStatus() { return printStatus; }
    public void setPrintStatus(String printStatus) { this.printStatus = printStatus; }
    public String getCheckStatus() { return checkStatus; }
    public void setCheckStatus(String checkStatus) { this.checkStatus = checkStatus; }
    public Date getCheckTime() { return checkTime; }
    public void setCheckTime(Date checkTime) { this.checkTime = checkTime; }
    public String getCheckBy() { return checkBy; }
    public void setCheckBy(String checkBy) { this.checkBy = checkBy; }
    public String getRealStoragePlace() { return realStoragePlace; }
    public void setRealStoragePlace(String realStoragePlace) { this.realStoragePlace = realStoragePlace; }
    public String getDifferenceRemark() { return differenceRemark; }
    public void setDifferenceRemark(String differenceRemark) { this.differenceRemark = differenceRemark; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
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
