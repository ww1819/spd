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
}
