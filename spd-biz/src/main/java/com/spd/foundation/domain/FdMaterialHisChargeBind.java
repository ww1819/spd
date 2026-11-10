package com.spd.foundation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 耗材档案与 HIS 收费项对照履历 fd_material_his_charge_bind
 */
public class FdMaterialHisChargeBind
{
    private String id;
    private String tenantId;
    private Long materialId;
    private String chargeItemId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveFrom;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveTo;
    private String bindBy;
    private String unbindBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 列表展示：耗材编码 */
    private String materialCode;
    /** 列表展示：耗材名称 */
    private String materialName;
    /** 列表展示：收费编码 */
    private String chargeItemCode;
    /** 列表展示：收费名称 */
    private String chargeItemName;
    /** 是否当前生效：Y/N */
    private String currentFlag;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getChargeItemId() { return chargeItemId; }
    public void setChargeItemId(String chargeItemId) { this.chargeItemId = chargeItemId; }
    public Date getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(Date effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public Date getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(Date effectiveTo) { this.effectiveTo = effectiveTo; }
    public String getBindBy() { return bindBy; }
    public void setBindBy(String bindBy) { this.bindBy = bindBy; }
    public String getUnbindBy() { return unbindBy; }
    public void setUnbindBy(String unbindBy) { this.unbindBy = unbindBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getChargeItemCode() { return chargeItemCode; }
    public void setChargeItemCode(String chargeItemCode) { this.chargeItemCode = chargeItemCode; }
    public String getChargeItemName() { return chargeItemName; }
    public void setChargeItemName(String chargeItemName) { this.chargeItemName = chargeItemName; }
    public String getCurrentFlag() { return currentFlag; }
    public void setCurrentFlag(String currentFlag) { this.currentFlag = currentFlag; }
}
