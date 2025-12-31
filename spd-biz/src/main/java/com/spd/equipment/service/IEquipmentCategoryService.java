package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.EquipmentCategory;

/**
 * 设备分类Service接口
 *
 * @author spd
 * @date 2024-12-17
 */
public interface IEquipmentCategoryService
{
    /**
     * 查询设备分类
     *
     * @param categoryId 设备分类主键
     * @return 设备分类
     */
    public EquipmentCategory selectEquipmentCategoryByCategoryId(Long categoryId);

    /**
     * 查询设备分类列表
     *
     * @param equipmentCategory 设备分类
     * @return 设备分类集合
     */
    public List<EquipmentCategory> selectEquipmentCategoryList(EquipmentCategory equipmentCategory);

    /**
     * 查询设备分类树形列表
     *
     * @return 设备分类集合
     */
    public List<EquipmentCategory> selectEquipmentCategoryTree();

    /**
     * 新增设备分类
     *
     * @param equipmentCategory 设备分类
     * @return 结果
     */
    public int insertEquipmentCategory(EquipmentCategory equipmentCategory);

    /**
     * 修改设备分类
     *
     * @param equipmentCategory 设备分类
     * @return 结果
     */
    public int updateEquipmentCategory(EquipmentCategory equipmentCategory);

    /**
     * 批量删除设备分类
     *
     * @param categoryIds 需要删除的设备分类主键集合
     * @return 结果
     */
    public int deleteEquipmentCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除设备分类信息
     *
     * @param categoryId 设备分类主键
     * @return 结果
     */
    public int deleteEquipmentCategoryByCategoryId(Long categoryId);
}

