package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DepPurchaseApplyEntry;

/**
 * 科室申购Mapper接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface DepPurchaseApplyMapper 
{
    /**
     * 查询科室申购
     * 
     * @param id 科室申购主键
     * @return 科室申购
     */
    public DepPurchaseApply selectDepPurchaseApplyById(Long id);

    /**
     * 查询科室申购列表
     * 
     * @param depPurchaseApply 科室申购
     * @return 科室申购集合
     */
    public List<DepPurchaseApply> selectDepPurchaseApplyList(DepPurchaseApply depPurchaseApply);

    /**
     * 新增科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    public int insertDepPurchaseApply(DepPurchaseApply depPurchaseApply);

    /**
     * 修改科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    public int updateDepPurchaseApply(DepPurchaseApply depPurchaseApply);

    /**
     * 删除科室申购
     * 
     * @param id 科室申购主键
     * @return 结果
     */
    public int deleteDepPurchaseApplyById(Long id);

    /**
     * 批量删除科室申购
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDepPurchaseApplyByIds(Long[] ids);

    /**
     * 批量删除科室申购明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDepPurchaseApplyEntryByParentIds(Long[] ids);
    
    /**
     * 批量新增科室申购明细
     * 
     * @param depPurchaseApplyEntryList 科室申购明细列表
     * @return 结果
     */
    public int batchDepPurchaseApplyEntry(List<DepPurchaseApplyEntry> depPurchaseApplyEntryList);
    

    /**
     * 通过科室申购主键删除科室申购明细信息
     * 
     * @param id 科室申购ID
     * @return 结果
     */
    public int deleteDepPurchaseApplyEntryByParentId(Long id);
}
