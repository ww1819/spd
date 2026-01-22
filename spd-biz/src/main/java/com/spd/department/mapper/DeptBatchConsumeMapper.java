package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 科室批量消耗Mapper接口
 * 
 * @author spd
 * @date 2025-01-15
 */
@Mapper
@Repository
public interface DeptBatchConsumeMapper 
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
     * 删除科室批量消耗
     * 
     * @param id 科室批量消耗主键
     * @return 结果
     */
    public int deleteDeptBatchConsumeById(Long id);

    /**
     * 批量删除科室批量消耗
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptBatchConsumeByIds(Long[] ids);

    /**
     * 批量删除科室批量消耗明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptBatchConsumeEntryByParenIds(Long[] ids);
    
    /**
     * 批量新增科室批量消耗明细
     * 
     * @param deptBatchConsumeEntryList 科室批量消耗明细列表
     * @return 结果
     */
    public int batchDeptBatchConsumeEntry(List<DeptBatchConsumeEntry> deptBatchConsumeEntryList);
    

    /**
     * 通过科室批量消耗主键删除科室批量消耗明细信息
     * 
     * @param id 科室批量消耗ID
     * @return 结果
     */
    public int deleteDeptBatchConsumeEntryByParenId(Long id);

    /**
     * 查询最大单号
     * 
     * @param date 日期
     * @return 最大单号
     */
    String selectMaxBillNo(String date);
}
