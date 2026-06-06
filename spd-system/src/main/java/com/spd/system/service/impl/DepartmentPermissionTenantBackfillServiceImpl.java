package com.spd.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.system.mapper.SysPostDepartmentMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.service.IDepartmentPermissionTenantBackfillService;

/**
 * sys_user_department / sys_post_department 存量数据 tenant_id 补全。
 */
@Service
public class DepartmentPermissionTenantBackfillServiceImpl implements IDepartmentPermissionTenantBackfillService
{
    @Autowired
    private SysUserDepartmentMapper sysUserDepartmentMapper;

    @Autowired
    private SysPostDepartmentMapper sysPostDepartmentMapper;

    @Override
    public int backfillUserDepartmentByUserId(Long userId)
    {
        if (userId == null)
        {
            return 0;
        }
        int n = sysUserDepartmentMapper.backfillTenantIdByUserIdFromUser(userId);
        n += sysUserDepartmentMapper.backfillTenantIdByUserIdFromDepartment(userId);
        return n;
    }

    @Override
    public int backfillPostDepartmentByPostId(Long postId)
    {
        if (postId == null)
        {
            return 0;
        }
        int n = sysPostDepartmentMapper.backfillTenantIdByPostIdFromPost(postId);
        n += sysPostDepartmentMapper.backfillTenantIdByPostIdFromDepartment(postId);
        return n;
    }

    @Override
    public int backfillAll()
    {
        int n = sysUserDepartmentMapper.backfillAllTenantIdFromUser();
        n += sysUserDepartmentMapper.backfillAllTenantIdFromDepartment();
        n += sysPostDepartmentMapper.backfillAllTenantIdFromPost();
        n += sysPostDepartmentMapper.backfillAllTenantIdFromDepartment();
        return n;
    }
}
