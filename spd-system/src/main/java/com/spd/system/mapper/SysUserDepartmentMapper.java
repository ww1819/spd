package com.spd.system.mapper;

import com.spd.system.domain.SysUserDepartment;

import java.util.List;

public interface SysUserDepartmentMapper {

    /**
     * 通过用户ID删除用户和科室关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserDepartmentByUserId(Long userId);

    /**
     * 通过科室ID查询科室使用数量
     *
     * @param postId 科室ID
     * @return 结果
     */
    public int countUserDepartmentById(Long postId);

    /**
     * 批量删除用户和科室关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserDepartment(Long[] ids);

    /**
     * 批量新增用户科室信息
     *
     * @param userDepartmentList
     * @return 结果
     */
    public int batchUserDepartment(List<SysUserDepartment> userDepartmentList);

    /**
     * 用户已授权的科室 ID（sys_user_department）
     */
    public List<Long> selectDepartmentIdsByUserId(Long userId);

    /** 按用户 ID 从 sys_user.customer_id 补全 tenant_id */
    int backfillTenantIdByUserIdFromUser(Long userId);

    /** 按用户 ID 从 fd_department.tenant_id 补全 tenant_id */
    int backfillTenantIdByUserIdFromDepartment(Long userId);

    int backfillAllTenantIdFromUser();

    int backfillAllTenantIdFromDepartment();
}
