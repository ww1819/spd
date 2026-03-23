package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DepPurchaseApplyEntry;
import org.apache.ibatis.annotations.Param;

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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteDepPurchaseApplyById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteDepPurchaseApplyByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除科室申购明细 */
    public int deleteDepPurchaseApplyEntryByParentIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
    
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
    public int deleteDepPurchaseApplyEntryByParentId(@Param("id") Long id, @Param("deleteBy") String deleteBy);
}
