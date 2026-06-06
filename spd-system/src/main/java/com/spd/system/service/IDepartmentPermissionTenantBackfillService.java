package com.spd.system.service;

/**
 * 用户/工作组科室权限表 tenant_id 补全。
 */
public interface IDepartmentPermissionTenantBackfillService
{
    /**
     * 按用户 ID 补全 sys_user_department.tenant_id（先用户 customer_id，再科室 tenant_id）。
     */
    int backfillUserDepartmentByUserId(Long userId);

    /**
     * 按工作组 ID 补全 sys_post_department.tenant_id（先岗位 tenant_id，再科室 tenant_id）。
     */
    int backfillPostDepartmentByPostId(Long postId);

    /**
     * 全库补全上述两张权限表的 tenant_id。
     */
    int backfillAll();
}
