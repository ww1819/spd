package com.spd.foundation.service;

import java.util.List;
import java.util.Map;

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

    /**
     * 批量删除库房分类
     *
     * @param warehouseCategoryIds 需要删除的库房分类主键集合
     * @return 结果
     */
    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long warehouseCategoryIds);

    /**
     * 批量更新库房分类名称简码（根据名称生成拼音首字母）
     *
     * @param ids 库房分类主键集合
     */
    void updateReferred(List<Long> ids);

    /**
     * 库房分类 Excel 导入：仅校验
     */
    Map<String, Object> validateWarehouseCategoryImport(List<FdWarehouseCategory> list, Boolean isUpdateSupport);

    /**
     * 库房分类 Excel 导入：确认后落库
     */
    String importWarehouseCategory(List<FdWarehouseCategory> list, Boolean isUpdateSupport, String operName, boolean confirmed);

//    /**
//     * 删除库房分类信息
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);
}
