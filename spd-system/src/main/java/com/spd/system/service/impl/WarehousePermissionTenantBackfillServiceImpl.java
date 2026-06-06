package com.spd.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.system.mapper.SysPostWarehouseMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.service.IWarehousePermissionTenantBackfillService;

/**
 * sys_user_warehouse / sys_post_warehouse 存量数据 tenant_id 补全。
 */
@Service
public class WarehousePermissionTenantBackfillServiceImpl implements IWarehousePermissionTenantBackfillService
{
    @Autowired
    private SysUserWarehouseMapper sysUserWarehouseMapper;

    @Autowired
    private SysPostWarehouseMapper sysPostWarehouseMapper;

    @Override
    public int backfillUserWarehouseByUserId(Long userId)
    {
        if (userId == null)
        {
            return 0;
        }
        int n = sysUserWarehouseMapper.backfillTenantIdByUserIdFromUser(userId);
        n += sysUserWarehouseMapper.backfillTenantIdByUserIdFromWarehouse(userId);
        return n;
    }

    @Override
    public int backfillPostWarehouseByPostId(Long postId)
    {
        if (postId == null)
        {
            return 0;
        }
        int n = sysPostWarehouseMapper.backfillTenantIdByPostIdFromPost(postId);
        n += sysPostWarehouseMapper.backfillTenantIdByPostIdFromWarehouse(postId);
        return n;
    }

    @Override
    public int backfillAll()
    {
        int n = sysUserWarehouseMapper.backfillAllTenantIdFromUser();
        n += sysUserWarehouseMapper.backfillAllTenantIdFromWarehouse();
        n += sysPostWarehouseMapper.backfillAllTenantIdFromPost();
        n += sysPostWarehouseMapper.backfillAllTenantIdFromWarehouse();
        return n;
    }
}
