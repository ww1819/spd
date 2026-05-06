package com.spd.caigou.domain;

import java.util.Date;

/**
 * SPD 供应商与云平台供应商编码绑定（按租户）
 * <p>del_flag：0 正常，1 删除（与系统通用逻辑删除一致）</p>
 */
public class SpdScmSupplierBind
{
    private String id;
    private String tenantId;
    private String supplierId;
    private String scmSupplierCode;
    private String remark;
    /** 删除标志：0 正常，1 删除 */
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(String supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getScmSupplierCode()
    {
        return scmSupplierCode;
    }

    public void setScmSupplierCode(String scmSupplierCode)
    {
        this.scmSupplierCode = scmSupplierCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
