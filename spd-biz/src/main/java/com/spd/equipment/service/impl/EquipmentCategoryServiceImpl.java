package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.equipment.mapper.EquipmentCategoryMapper;
import com.spd.equipment.domain.EquipmentCategory;
import com.spd.equipment.service.IEquipmentCategoryService;

/**
 * 设备分类Service业务层处理
 *
 * @author spd
 * @date 2024-12-17
 */
@Service
public class EquipmentCategoryServiceImpl implements IEquipmentCategoryService
{
    @Autowired
    private EquipmentCategoryMapper equipmentCategoryMapper;

    /**
     * 查询设备分类
     *
     * @param categoryId 设备分类主键
     * @return 设备分类
     */
    @Override
    public EquipmentCategory selectEquipmentCategoryByCategoryId(Long categoryId)
    {
        return equipmentCategoryMapper.selectEquipmentCategoryByCategoryId(categoryId);
    }

    /**
     * 查询设备分类列表
     *
     * @param equipmentCategory 设备分类
     * @return 设备分类
     */
    @Override
    public List<EquipmentCategory> selectEquipmentCategoryList(EquipmentCategory equipmentCategory)
    {
        return equipmentCategoryMapper.selectEquipmentCategoryList(equipmentCategory);
    }

    /**
     * 查询设备分类树形列表
     *
     * @return 设备分类集合
     */
    @Override
    public List<EquipmentCategory> selectEquipmentCategoryTree()
    {
        return equipmentCategoryMapper.selectEquipmentCategoryTree();
    }

    /**
     * 新增设备分类
     *
     * @param equipmentCategory 设备分类
     * @return 结果
     */
    @Override
    public int insertEquipmentCategory(EquipmentCategory equipmentCategory)
    {
        equipmentCategory.setCreateTime(DateUtils.getNowDate());
        return equipmentCategoryMapper.insertEquipmentCategory(equipmentCategory);
    }

    /**
     * 修改设备分类
     *
     * @param equipmentCategory 设备分类
     * @return 结果
     */
    @Override
    public int updateEquipmentCategory(EquipmentCategory equipmentCategory)
    {
        equipmentCategory.setUpdateTime(DateUtils.getNowDate());
        return equipmentCategoryMapper.updateEquipmentCategory(equipmentCategory);
    }

    /**
     * 批量删除设备分类
     *
     * @param categoryIds 需要删除的设备分类主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentCategoryByCategoryIds(Long[] categoryIds)
    {
        return equipmentCategoryMapper.deleteEquipmentCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除设备分类信息
     *
     * @param categoryId 设备分类主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentCategoryByCategoryId(Long categoryId)
    {
        return equipmentCategoryMapper.deleteEquipmentCategoryByCategoryId(categoryId);
    }
}

