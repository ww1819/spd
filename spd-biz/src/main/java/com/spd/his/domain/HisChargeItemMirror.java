package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * HIS 收费项目本地镜像（来源 v_charge_item）。
 */
public class HisChargeItemMirror
{
    private String tenantId;
    private String chargeItemId;
    private String itemCode;
    private String itemName;
    private String itemType;
    private String consumableType;
    private String specModel;
    private String unit;
    private BigDecimal price;
    private String manufacturer;
    private String registerNo;
    private String isActive;
    private String hisCreateTime;
    private String hisUpdateTime;
    /** 本地删除标记：0正常，1已删除（HIS未返回） */
    private Integer deletedFlag;
    private Date createTime;
    private Date updateTime;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getChargeItemId() { return chargeItemId; }
    public void setChargeItemId(String chargeItemId) { this.chargeItemId = chargeItemId; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getConsumableType() { return consumableType; }
    public void setConsumableType(String consumableType) { this.consumableType = consumableType; }
    public String getSpecModel() { return specModel; }
    public void setSpecModel(String specModel) { this.specModel = specModel; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public String getHisCreateTime() { return hisCreateTime; }
    public void setHisCreateTime(String hisCreateTime) { this.hisCreateTime = hisCreateTime; }
    public String getHisUpdateTime() { return hisUpdateTime; }
    public void setHisUpdateTime(String hisUpdateTime) { this.hisUpdateTime = hisUpdateTime; }
    public Integer getDeletedFlag() { return deletedFlag; }
    public void setDeletedFlag(Integer deletedFlag) { this.deletedFlag = deletedFlag; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
