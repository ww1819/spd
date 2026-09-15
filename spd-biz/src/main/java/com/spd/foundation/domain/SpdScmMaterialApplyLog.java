package com.spd.foundation.domain;

import java.util.Date;

/**
 * SPD 应用供应链档案留痕 spd_scm_material_apply_log
 */
public class SpdScmMaterialApplyLog
{
    private String id;
    private String tenantId;
    private String syncBatchId;
    private String mirrorId;
    private String scmArchiveId;
    private String spdMaterialId;
    private String appliedFieldsJson;
    private String beforeJson;
    private String afterJson;
    private String applyBy;
    private Date applyTime;
    private String remark;

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

    public String getSyncBatchId()
    {
        return syncBatchId;
    }

    public void setSyncBatchId(String syncBatchId)
    {
        this.syncBatchId = syncBatchId;
    }

    public String getMirrorId()
    {
        return mirrorId;
    }

    public void setMirrorId(String mirrorId)
    {
        this.mirrorId = mirrorId;
    }

    public String getScmArchiveId()
    {
        return scmArchiveId;
    }

    public void setScmArchiveId(String scmArchiveId)
    {
        this.scmArchiveId = scmArchiveId;
    }

    public String getSpdMaterialId()
    {
        return spdMaterialId;
    }

    public void setSpdMaterialId(String spdMaterialId)
    {
        this.spdMaterialId = spdMaterialId;
    }

    public String getAppliedFieldsJson()
    {
        return appliedFieldsJson;
    }

    public void setAppliedFieldsJson(String appliedFieldsJson)
    {
        this.appliedFieldsJson = appliedFieldsJson;
    }

    public String getBeforeJson()
    {
        return beforeJson;
    }

    public void setBeforeJson(String beforeJson)
    {
        this.beforeJson = beforeJson;
    }

    public String getAfterJson()
    {
        return afterJson;
    }

    public void setAfterJson(String afterJson)
    {
        this.afterJson = afterJson;
    }

    public String getApplyBy()
    {
        return applyBy;
    }

    public void setApplyBy(String applyBy)
    {
        this.applyBy = applyBy;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
