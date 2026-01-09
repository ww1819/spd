package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.EquipmentStorageDetail;

/**
 * 设备入库明细Mapper接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface EquipmentStorageDetailMapper 
{
    /**
     * 查询设备入库明细列表
     * 
     * @param storageId 入库单ID
     * @return 设备入库明细集合
     */
    public List<EquipmentStorageDetail> selectEquipmentStorageDetailList(Long storageId);

    /**
     * 新增设备入库明细
     * 
     * @param equipmentStorageDetail 设备入库明细
     * @return 结果
     */
    public int insertEquipmentStorageDetail(EquipmentStorageDetail equipmentStorageDetail);

    /**
     * 批量新增设备入库明细
     * 
     * @param detailList 设备入库明细列表
     * @return 结果
     */
    public int batchInsertEquipmentStorageDetail(List<EquipmentStorageDetail> detailList);

    /**
     * 删除设备入库明细
     * 
     * @param storageId 入库单ID
     * @return 结果
     */
    public int deleteEquipmentStorageDetailByStorageId(Long storageId);

    /**
     * 批量删除设备入库明细
     * 
     * @param storageIds 需要删除的入库单ID集合
     * @return 结果
     */
    public int deleteEquipmentStorageDetailByStorageIds(Long[] storageIds);
}
