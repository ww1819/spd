package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdMaterialCategory;

/**
 * 耗材分类维护Mapper接口
 * 
 * @author spd
 * @date 2024-03-04
 */
public interface FdMaterialCategoryMapper 
{
    /**
     * 查询耗材分类维护
     * 
     * @param materialCategoryId 耗材分类维护主键
     * @return 耗材分类维护
     */
    public FdMaterialCategory selectFdMaterialCategoryByMaterialCategoryId(Long materialCategoryId);

    /**
     * 查询耗材分类维护列表
     * 
     * @param fdMaterialCategory 耗材分类维护
     * @return 耗材分类维护集合
     */
    public List<FdMaterialCategory> selectFdMaterialCategoryList(FdMaterialCategory fdMaterialCategory);

    /**
     * 新增耗材分类维护
     * 
     * @param fdMaterialCategory 耗材分类维护
     * @return 结果
     */
    public int insertFdMaterialCategory(FdMaterialCategory fdMaterialCategory);

    /**
     * 修改耗材分类维护
     * 
     * @param fdMaterialCategory 耗材分类维护
     * @return 结果
     */
    public int updateFdMaterialCategory(FdMaterialCategory fdMaterialCategory);

    /**
     * 删除耗材分类维护
     * 
     * @param materialCategoryId 耗材分类维护主键
     * @return 结果
     */
    public int deleteFdMaterialCategoryByMaterialCategoryId(Long materialCategoryId);

    /**
     * 批量删除耗材分类维护
     * 
     * @param materialCategoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFdMaterialCategoryByMaterialCategoryIds(Long[] materialCategoryIds);
}
