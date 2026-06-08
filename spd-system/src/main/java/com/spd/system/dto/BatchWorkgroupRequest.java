package com.spd.system.dto;

import java.io.Serializable;
import java.util.List;
import com.spd.common.core.domain.entity.SysUser;

/**
 * 批量设置耗材工作组（sys_user_post）
 */
public class BatchWorkgroupRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** true：按 queryCriteria 更新当前查询结果全部；false/null：仅更新 userIds */
    private Boolean updateAll;

    /** updateAll=true 时使用的列表查询条件（与 /system/user/list 一致） */
    private SysUser queryCriteria;

    private List<Long> userIds;

    private Long postId;

    public Boolean getUpdateAll()
    {
        return updateAll;
    }

    public void setUpdateAll(Boolean updateAll)
    {
        this.updateAll = updateAll;
    }

    public SysUser getQueryCriteria()
    {
        return queryCriteria;
    }

    public void setQueryCriteria(SysUser queryCriteria)
    {
        this.queryCriteria = queryCriteria;
    }

    public List<Long> getUserIds()
    {
        return userIds;
    }

    public void setUserIds(List<Long> userIds)
    {
        this.userIds = userIds;
    }

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }
}
