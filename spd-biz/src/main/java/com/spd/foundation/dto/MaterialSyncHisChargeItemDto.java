package com.spd.foundation.dto;

import com.spd.foundation.domain.FdMaterial;
import java.util.List;

/**
 * 将产品档案 is_gz 同步到已对照 HIS 收费项目 value_level
 */
public class MaterialSyncHisChargeItemDto
{
    /** 仅同步勾选的 materialIds */
    private List<Long> materialIds;

    /** true：按 queryCriteria 同步当前查询结果全部产品档案 */
    private Boolean syncAll;

    /** syncAll=true 时使用的列表查询条件（与 /foundation/material/list 一致） */
    private FdMaterial queryCriteria;

    public List<Long> getMaterialIds()
    {
        return materialIds;
    }

    public void setMaterialIds(List<Long> materialIds)
    {
        this.materialIds = materialIds;
    }

    public Boolean getSyncAll()
    {
        return syncAll;
    }

    public void setSyncAll(Boolean syncAll)
    {
        this.syncAll = syncAll;
    }

    public FdMaterial getQueryCriteria()
    {
        return queryCriteria;
    }

    public void setQueryCriteria(FdMaterial queryCriteria)
    {
        this.queryCriteria = queryCriteria;
    }
}
