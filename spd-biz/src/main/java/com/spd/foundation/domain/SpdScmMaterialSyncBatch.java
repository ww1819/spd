package com.spd.foundation.domain;

import java.util.Date;

/**
 * SPD 拉取供应链档案批次 spd_scm_material_sync_batch
 */
public class SpdScmMaterialSyncBatch
{
    private String id;
    private String tenantId;
    private String hospitalCode;
    private String syncBy;
    private Date syncTime;
    private Integer itemCount;
    /** 1成功 2失败 */
    private String resultStatus;
    private String resultMsg;
    private Date createTime;

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

    public String getHospitalCode()
    {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode)
    {
        this.hospitalCode = hospitalCode;
    }

    public String getSyncBy()
    {
        return syncBy;
    }

    public void setSyncBy(String syncBy)
    {
        this.syncBy = syncBy;
    }

    public Date getSyncTime()
    {
        return syncTime;
    }

    public void setSyncTime(Date syncTime)
    {
        this.syncTime = syncTime;
    }

    public Integer getItemCount()
    {
        return itemCount;
    }

    public void setItemCount(Integer itemCount)
    {
        this.itemCount = itemCount;
    }

    public String getResultStatus()
    {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus)
    {
        this.resultStatus = resultStatus;
    }

    public String getResultMsg()
    {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg)
    {
        this.resultMsg = resultMsg;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
