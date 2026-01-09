package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.EquipmentStorage;

/**
 * 设备入库Service接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface IEquipmentStorageService 
{
    /**
     * 查询设备入库
     * 
     * @param storageId 设备入库主键
     * @return 设备入库
     */
    public EquipmentStorage selectEquipmentStorageById(Long storageId);

    /**
     * 查询设备入库列表
     * 
     * @param equipmentStorage 设备入库
     * @return 设备入库集合
     */
    public List<EquipmentStorage> selectEquipmentStorageList(EquipmentStorage equipmentStorage);

    /**
     * 新增设备入库
     * 
     * @param equipmentStorage 设备入库
     * @return 结果
     */
    public int insertEquipmentStorage(EquipmentStorage equipmentStorage);

    /**
     * 修改设备入库
     * 
     * @param equipmentStorage 设备入库
     * @return 结果
     */
    public int updateEquipmentStorage(EquipmentStorage equipmentStorage);

    /**
     * 批量删除设备入库
     * 
     * @param storageIds 需要删除的设备入库主键集合
     * @return 结果
     */
    public int deleteEquipmentStorageByIds(Long[] storageIds);

    /**
     * 删除设备入库信息
     * 
     * @param storageId 设备入库主键
     * @return 结果
     */
    public int deleteEquipmentStorageById(Long storageId);

    /**
     * 审核设备入库
     * 
     * @param storageId 设备入库主键
     * @return 结果
     */
    public int auditEquipmentStorage(Long storageId);
}
