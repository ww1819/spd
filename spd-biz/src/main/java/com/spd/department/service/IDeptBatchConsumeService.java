package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.DeptBatchConsume;

/**
 * 科室批量消耗Service接口
 * 
 * @author spd
 * @date 2025-01-15
 */
public interface IDeptBatchConsumeService 
{
    /**
     * 查询科室批量消耗
     * 
     * @param id 科室批量消耗主键
     * @return 科室批量消耗
     */
    public DeptBatchConsume selectDeptBatchConsumeById(Long id);

    /**
     * 查询科室批量消耗列表
     * 
     * @param deptBatchConsume 科室批量消耗
     * @return 科室批量消耗集合
     */
    public List<DeptBatchConsume> selectDeptBatchConsumeList(DeptBatchConsume deptBatchConsume);

    /**
     * 新增科室批量消耗
     * 
     * @param deptBatchConsume 科室批量消耗
     * @return 结果
     */
    public int insertDeptBatchConsume(DeptBatchConsume deptBatchConsume);

    /**
     * 修改科室批量消耗
     * 
     * @param deptBatchConsume 科室批量消耗
     * @return 结果
     */
    public int updateDeptBatchConsume(DeptBatchConsume deptBatchConsume);

    /**
     * 批量删除科室批量消耗
     * 
     * @param ids 需要删除的科室批量消耗主键集合
     * @return 结果
     */
    public int deleteDeptBatchConsumeByIds(Long[] ids);

    /**
     * 删除科室批量消耗信息
     * 
     * @param id 科室批量消耗主键
     * @return 结果
     */
    public int deleteDeptBatchConsumeById(Long id);

    /**
     * 审核科室批量消耗
     * @param id 消耗单ID
     * @param auditBy 审核人
     * @return 结果
     */
    int auditConsume(String id, String auditBy);
}
