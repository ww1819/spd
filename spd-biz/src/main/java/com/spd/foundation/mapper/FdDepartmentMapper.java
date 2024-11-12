package com.spd.foundation.mapper;

import java.util.List;
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

    /**
     * 查询所有科室列表
     * @return
     */
    List<FdDepartment> selectdepartmenAll();

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
