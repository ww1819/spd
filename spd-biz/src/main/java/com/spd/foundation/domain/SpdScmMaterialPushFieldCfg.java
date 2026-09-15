package com.spd.foundation.domain;

import java.util.Date;

/**
 * 产品档案推送/应用字段配置 spd_scm_material_push_field_cfg
 */
public class SpdScmMaterialPushFieldCfg
{
    private String id;
    private String tenantId;
    private String fieldCode;
    private String fieldLabel;
    /** 1推送 0不推送 */
    private String pushEnabled;
    /** 1允许应用 0锁定 */
    private String applyEnabled;
    private Integer sortNo;
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

    public String getFieldCode()
    {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode)
    {
        this.fieldCode = fieldCode;
    }

    public String getFieldLabel()
    {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel)
    {
        this.fieldLabel = fieldLabel;
    }

    public String getPushEnabled()
    {
        return pushEnabled;
    }

    public void setPushEnabled(String pushEnabled)
    {
        this.pushEnabled = pushEnabled;
    }

    public String getApplyEnabled()
    {
        return applyEnabled;
    }

    public void setApplyEnabled(String applyEnabled)
    {
        this.applyEnabled = applyEnabled;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
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
