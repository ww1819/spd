package com.spd.department.mapper;

import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
    public int deleteDeptBatchConsumeById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 批量删除科室批量消耗
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptBatchConsumeByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 批量删除科室批量消耗明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptBatchConsumeEntryByParenIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
    
    /**
     * 批量新增科室批量消耗明细
     * 
     * @param deptBatchConsumeEntryList 科室批量消耗明细列表
     * @return 结果
     */
    public int batchDeptBatchConsumeEntry(List<DeptBatchConsumeEntry> deptBatchConsumeEntryList);

    /**
     * 新增科室批量消耗明细
     */
    int insertDeptBatchConsumeEntry(DeptBatchConsumeEntry deptBatchConsumeEntry);

    /**
     * 新增科室批量消耗明细与出库明细关联
     */
    int insertDeptBatchConsumeEntryRef(DeptBatchConsumeEntry deptBatchConsumeEntry);

    /**
     * 删除科室消耗与出库明细关联（逻辑删明细时同步清理关联）
     */
    int deleteDeptBatchConsumeEntryRefByConsumeIds(@Param("ids") Long[] ids, @Param("tenantId") String tenantId);
    

    /**
     * 通过科室批量消耗主键删除科室批量消耗明细信息
     * 
     * @param id 科室批量消耗ID
     * @return 结果
     */
    public int deleteDeptBatchConsumeEntryByParenId(@Param("parenId") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 查询最大单号
     * 
     * @param date 日期
     * @return 最大单号
     */
    String selectMaxBillNo(String date);

    /**
     * 引用出库单：查询可引用的科室库存行（低敏感接口）
     */
    List<Map<String, Object>> selectOutRefEntryList(DeptBatchConsume deptBatchConsume);

    /**
     * 查询已审核的科室批量消耗明细列表（用于消耗追溯报表）
     * 
     * @param deptBatchConsume 查询条件
     * @return 明细列表
     */
    List<Map<String, Object>> selectAuditedConsumeDetailList(DeptBatchConsume deptBatchConsume);

    /**
     * 已审核消耗追溯：当前筛选条件下全部明细行的数量、金额合计
     *
     * @param deptBatchConsume 查询条件
     * @return 合计
     */
    TotalInfo selectAuditedConsumeReportTotal(DeptBatchConsume deptBatchConsume);

    /**
     * 查询已审核的科室批量消耗汇总列表（按耗材汇总，用于消耗追溯报表）
     * 
     * @param deptBatchConsume 查询条件
     * @return 汇总列表
     */
    List<Map<String, Object>> selectAuditedConsumeSummaryList(DeptBatchConsume deptBatchConsume);
}
