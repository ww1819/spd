package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdWarehouseCategory;

/**
 * 库房分类Mapper接口
 *
 * @author spd
 * @date 2024-04-12
 */
public interface FdWarehouseCategoryMapper
{
    /**
     * 查询库房分类
     *
     * @param warehouseCategoryId 库房分类主键
     * @return 库房分类
     */
    public FdWarehouseCategory selectFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);

    /**
     * 查询库房分类列表
     *
     * @param fdWarehouseCategory 库房分类
     * @return 库房分类集合
     */
    public List<FdWarehouseCategory> selectFdWarehouseCategoryList(FdWarehouseCategory fdWarehouseCategory);

    /**
     * 查询库房分类树形列表
     *
     * @return 库房分类集合
     */
    public List<FdWarehouseCategory> selectFdWarehouseCategoryTree();

    /**
     * 新增库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    public int insertFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory);

    /**
     * 修改库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    public int updateFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory);

//    /**
//     * 删除库房分类
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);
//
//    /**
//     * 批量删除库房分类
//     *
//     * @param warehouseCategoryIds 需要删除的数据主键集合
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long[] warehouseCategoryIds);
}
