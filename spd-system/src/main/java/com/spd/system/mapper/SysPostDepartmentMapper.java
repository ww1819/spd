package com.spd.system.mapper;

import com.spd.system.domain.SysPostDepartment;

import java.util.List;

/**
 * 工作组与科室关联表 数据层
 *
 * @author spd
 */
public interface SysPostDepartmentMapper
{
    /**
     * 通过工作组ID删除工作组和科室关联
     *
     * @param postId 工作组ID
     * @return 结果
     */
    public int deletePostDepartmentByPostId(Long postId);

    /**
     * 批量新增工作组科室信息
     *
     * @param postDepartmentList
     * @return 结果
     */
    public int batchPostDepartment(List<SysPostDepartment> postDepartmentList);

    /**
     * 通过工作组ID查询科室ID列表
     *
     * @param postId 工作组ID
     * @return 科室ID列表
     */
    public List<Long> selectDepartmentListByPostId(Long postId);

    /** 按工作组 ID 从 sys_post.tenant_id 补全 tenant_id */
    int backfillTenantIdByPostIdFromPost(Long postId);

    /** 按工作组 ID 从 fd_department.tenant_id 补全 tenant_id */
    int backfillTenantIdByPostIdFromDepartment(Long postId);

    int backfillAllTenantIdFromPost();

    int backfillAllTenantIdFromDepartment();
}

