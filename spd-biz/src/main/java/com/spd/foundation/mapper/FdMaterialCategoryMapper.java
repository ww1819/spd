package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdMaterialCategory;
import org.apache.ibatis.annotations.Param;

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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteFdMaterialCategoryByMaterialCategoryId(@Param("materialCategoryId") Long materialCategoryId, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteFdMaterialCategoryByMaterialCategoryIds(@Param("materialCategoryIds") Long[] materialCategoryIds, @Param("deleteBy") String deleteBy);
}
