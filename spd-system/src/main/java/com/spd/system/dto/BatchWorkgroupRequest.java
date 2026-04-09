package com.spd.system.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 批量设置耗材工作组（sys_user_post）
 */
public class BatchWorkgroupRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Long> userIds;

    private Long postId;

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
