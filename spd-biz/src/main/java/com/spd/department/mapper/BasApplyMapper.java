package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 科室申领Mapper接口
 * 
 * @author spd
 * @date 2024-02-26
 */
@Mapper
@Repository
public interface BasApplyMapper 
{
    /**
     * 查询科室申领
     * 
     * @param id 科室申领主键
     * @return 科室申领
     */
    public BasApply selectBasApplyById(Long id);

    /**
     * 查询科室申领列表
     * 
     * @param basApply 科室申领
     * @return 科室申领集合
     */
    public List<BasApply> selectBasApplyList(BasApply basApply);

    /**
     * 新增科室申领
     * 
     * @param basApply 科室申领
     * @return 结果
     */
    public int insertBasApply(BasApply basApply);

    /**
     * 修改科室申领
     * 
     * @param basApply 科室申领
     * @return 结果
     */
    public int updateBasApply(BasApply basApply);

    /**
     * 删除科室申领
     * 
     * @param id 科室申领主键
     * @return 结果
     */
    public int deleteBasApplyById(Long id);

    /**
     * 批量删除科室申领
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBasApplyByIds(Long[] ids);

    /**
     * 批量删除科室申领明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBasApplyEntryByParenIds(Long[] ids);
    
    /**
     * 批量新增科室申领明细
     * 
     * @param basApplyEntryList 科室申领明细列表
     * @return 结果
     */
    public int batchBasApplyEntry(List<BasApplyEntry> basApplyEntryList);
    

    /**
     * 通过科室申领主键删除科室申领明细信息
     * 
     * @param id 科室申领ID
     * @return 结果
     */
    public int deleteBasApplyEntryByParenId(Long id);

    String selectMaxBillNo(String date);
}
