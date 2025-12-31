package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.EquipmentCategory;

/**
 * 设备分类Mapper接口
 *
 * @author spd
 * @date 2024-12-17
 */
public interface EquipmentCategoryMapper
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
     * 删除设备分类
     *
     * @param categoryId 设备分类主键
     * @return 结果
     */
    public int deleteEquipmentCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除设备分类
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEquipmentCategoryByCategoryIds(Long[] categoryIds);
}

