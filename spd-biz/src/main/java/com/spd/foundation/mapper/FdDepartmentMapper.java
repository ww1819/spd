package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.foundation.domain.FdDepartment;

/**
 * 科室Mapper接口
 *
 * @author spd
 * @date 2023-11-26
 */
public interface FdDepartmentMapper
{
    /**
     * 查询科室
     *
     * @param id 科室主键
     * @return 科室
     */
    public FdDepartment selectFdDepartmentById(String id);

    /**
     * 按科室编码与租户查询（未删除）
     *
     * @param code     科室编码
     * @param tenantId 租户ID，空表示平台侧（tenant_id 为空）
     */
    FdDepartment selectFdDepartmentByCodeAndTenantId(@Param("code") String code, @Param("tenantId") String tenantId);

    /**
     * 查询科室列表
     *
     * @param fdDepartment 科室
     * @return 科室集合
     */
    public     List<FdDepartment> selectFdDepartmentList(FdDepartment fdDepartment);

    /**
     * 未删除子科室数量（用于删除前校验）
     */
    int countChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 租户下同名科室数量（未删除；可排除某 id，用于唯一校验）
     */
    int countDepartmentByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 新增科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    public int insertFdDepartment(FdDepartment fdDepartment);

    /**
     * 修改科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    public int updateFdDepartment(FdDepartment fdDepartment);

    /**
     * 查询所有科室列表
     * @return
     */
    List<FdDepartment> selectdepartmenAll();

    /**
     * 按租户ID查询科室列表（仅本客户）
     * @param tenantId 租户/客户ID
     * @return
     */
    List<FdDepartment> selectdepartmenAllByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据用户ID获取科室ID列表
     * @param userId
     * @return
     */
    List<Long> selectDepartmenListByUserId(Long userId);

    /**
     * 根据用户ID获取科室列表
     * @param userId
     * @return
     */
    List<FdDepartment> selectUserDepartmenAll(Long userId);

//    /**
//     * 删除科室
//     *
//     * @param id 科室主键
//     * @return 结果
//     */
//    public int deleteFdDepartmentById(String id);
//
//    /**
//     * 批量删除科室
//     *
//     * @param ids 需要删除的数据主键集合
//     * @return 结果
//     */
//    public int deleteFdDepartmentByIds(String[] ids);
}
