package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.EquipmentInfo;

/**
 * 设备信息管理Service接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface IEquipmentInfoService 
{
    /**
     * 查询设备信息管理
     * 
     * @param id 设备信息管理主键
     * @return 设备信息管理
     */
    public EquipmentInfo selectEquipmentInfoById(String id);

    /**
     * 查询设备信息管理列表
     * 
     * @param equipmentInfo 设备信息管理
     * @return 设备信息管理集合
     */
    public List<EquipmentInfo> selectEquipmentInfoList(EquipmentInfo equipmentInfo);

    /**
     * 新增设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    public int insertEquipmentInfo(EquipmentInfo equipmentInfo);

    /**
     * 修改设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    public int updateEquipmentInfo(EquipmentInfo equipmentInfo);

    /**
     * 批量删除设备信息管理
     * 
     * @param ids 需要删除的设备信息管理主键集合
     * @return 结果
     */
    public int deleteEquipmentInfoByIds(String[] ids);

    /**
     * 删除设备信息管理信息
     * 
     * @param id 设备信息管理主键
     * @return 结果
     */
    public int deleteEquipmentInfoById(String id);

    /**
     * 根据资产编号查询设备信息
     * 
     * @param assetCode 资产编号
     * @return 设备信息管理
     */
    public EquipmentInfo selectEquipmentInfoByAssetCode(String assetCode);

    /**
     * 查询设备信息统计
     * 
     * @param equipmentInfo 设备信息管理
     * @return 统计结果
     */
    public List<EquipmentInfo> selectEquipmentInfoStatistics(EquipmentInfo equipmentInfo);
} 