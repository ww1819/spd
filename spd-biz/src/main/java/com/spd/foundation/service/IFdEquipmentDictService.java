package com.spd.foundation.service;

import java.util.List;

import com.spd.foundation.domain.FdEquipmentDict;

/**
 * 设备字典Service接口
 *
 * @author spd
 * @date 2024-12-16
 */
public interface IFdEquipmentDictService
{
    /**
     * 查询设备字典
     *
     * @param id 设备字典主键
     * @return 设备字典
     */
    public FdEquipmentDict selectFdEquipmentDictById(Long id);

    /**
     * 查询设备字典列表
     *
     * @param fdEquipmentDict 设备字典
     * @return 设备字典集合
     */
    public List<FdEquipmentDict> selectFdEquipmentDictList(FdEquipmentDict fdEquipmentDict);

    /**
     * 新增设备字典
     *
     * @param fdEquipmentDict 设备字典
     * @return 结果
     */
    public int insertFdEquipmentDict(FdEquipmentDict fdEquipmentDict);

    /**
     * 修改设备字典
     *
     * @param fdEquipmentDict 设备字典
     * @return 结果
     */
    public int updateFdEquipmentDict(FdEquipmentDict fdEquipmentDict);

    /**
     * 批量删除设备字典
     *
     * @param ids 需要删除的设备字典主键集合
     * @return 结果
     */
    public int deleteFdEquipmentDictByIds(Long ids);

    /**
     * 导入设备字典数据
     *
     * @param fdEquipmentDictList 设备字典数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作设备字典
     * @return 结果
     */
    public String importFdEquipmentDict(List<FdEquipmentDict> fdEquipmentDictList, Boolean isUpdateSupport, String operName);
}

