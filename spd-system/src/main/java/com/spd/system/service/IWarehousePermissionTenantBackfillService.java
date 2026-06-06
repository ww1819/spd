package com.spd.system.service;

/**
 * 用户/工作组仓库权限表 tenant_id 补全。
 */
public interface IWarehousePermissionTenantBackfillService
{
    /**
     * 按用户 ID 补全 sys_user_warehouse.tenant_id（先用户 customer_id，再仓库 tenant_id）。
     */
    int backfillUserWarehouseByUserId(Long userId);

    /**
     * 按工作组 ID 补全 sys_post_warehouse.tenant_id（先岗位 tenant_id，再仓库 tenant_id）。
     */
    int backfillPostWarehouseByPostId(Long postId);

    /**
     * 全库补全上述两张权限表的 tenant_id。
     */
    int backfillAll();
}
