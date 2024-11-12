package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdWarehouseCategory;

/**
 * 库房分类Service接口
 *
 * @author spd
 * @date 2024-04-12
 */
public interface IFdWarehouseCategoryService
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

    /**
     * 批量删除库房分类
     *
     * @param warehouseCategoryIds 需要删除的库房分类主键集合
     * @return 结果
     */
    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long warehouseCategoryIds);

//    /**
//     * 删除库房分类信息
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);
}
