package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdDepartment;

/**
 * 科室Service接口
 *
 * @author spd
 * @date 2023-11-26
 */
public interface IFdDepartmentService
{
    /**
     * 查询科室
     *
     * @param id 科室主键
     * @return 科室
     */
    public FdDepartment selectFdDepartmentById(String id);

    /**
     * 查询科室列表
     *
     * @param fdDepartment 科室
     * @return 科室集合
     */
    public List<FdDepartment> selectFdDepartmentList(FdDepartment fdDepartment);

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

//    /**
//     * 批量删除科室
//     *
//     * @param ids 需要删除的科室主键集合
//     * @return 结果
//     */
//    public int deleteFdDepartmentByIds(String[] ids);

    /**
     * 删除科室信息
     *
     * @param id 科室主键
     * @return 结果
     */
    public int deleteFdDepartmentById(String id);

    /**
     * 查询所有科室列表
     * @return
     */
    List<FdDepartment> selectdepartmenAll();

    /**
     * 根据用户ID获取科室ID列表
     *
     * @param userId 用户ID
     * @return 选中科室ID列表
     */
    public List<Long> selectDepartmenListByUserId(Long userId);

    /**
     * 根据用户ID获取科室列表
     * @param userId
     * @return
     */
    List<FdDepartment> selectUserDepartmenAll(Long userId);

    /**
     * 批量更新科室名称简码
     *
     * @param ids 科室ID列表
     */
    void updateReferred(java.util.List<Long> ids);
}
