package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.EquipmentFile;

/**
 * 设备文件Mapper接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface EquipmentFileMapper 
{
    /**
     * 查询设备文件
     * 
     * @param fileId 设备文件主键
     * @return 设备文件
     */
    public EquipmentFile selectEquipmentFileByFileId(String fileId);

    /**
     * 查询设备文件列表
     * 
     * @param equipmentFile 设备文件
     * @return 设备文件集合
     */
    public List<EquipmentFile> selectEquipmentFileList(EquipmentFile equipmentFile);

    /**
     * 新增设备文件
     * 
     * @param equipmentFile 设备文件
     * @return 结果
     */
    public int insertEquipmentFile(EquipmentFile equipmentFile);

    /**
     * 修改设备文件
     * 
     * @param equipmentFile 设备文件
     * @return 结果
     */
    public int updateEquipmentFile(EquipmentFile equipmentFile);

    /**
     * 删除设备文件
     * 
     * @param fileId 设备文件主键
     * @return 结果
     */
    public int deleteEquipmentFileByFileId(String fileId);

    /**
     * 批量删除设备文件
     * 
     * @param fileIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEquipmentFileByFileIds(String[] fileIds);
}
