package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.equipment.mapper.EquipmentFileMapper;
import com.spd.equipment.domain.EquipmentFile;
import com.spd.equipment.service.IEquipmentFileService;

/**
 * 设备文件Service业务层处理
 * 
 * @author spd
 * @date 2024-01-01
 */
@Service
public class EquipmentFileServiceImpl implements IEquipmentFileService 
{
    @Autowired
    private EquipmentFileMapper equipmentFileMapper;

    /**
     * 查询设备文件
     * 
     * @param fileId 设备文件主键
     * @return 设备文件
     */
    @Override
    public EquipmentFile selectEquipmentFileByFileId(String fileId)
    {
        return equipmentFileMapper.selectEquipmentFileByFileId(fileId);
    }

    /**
     * 查询设备文件列表
     * 
     * @param equipmentFile 设备文件
     * @return 设备文件
     */
    @Override
    public List<EquipmentFile> selectEquipmentFileList(EquipmentFile equipmentFile)
    {
        return equipmentFileMapper.selectEquipmentFileList(equipmentFile);
    }

    /**
     * 新增设备文件
     * 
     * @param equipmentFile 设备文件
     * @return 结果
     */
    @Override
    public int insertEquipmentFile(EquipmentFile equipmentFile)
    {
        if (equipmentFile.getFileId() == null || equipmentFile.getFileId().isEmpty()) {
            equipmentFile.setFileId(UUID7.generateUUID7Simple());
        }
        equipmentFile.setCreateTime(DateUtils.getNowDate());
        equipmentFile.setCreateBy(SecurityUtils.getUsername());
        return equipmentFileMapper.insertEquipmentFile(equipmentFile);
    }

    /**
     * 修改设备文件
     * 
     * @param equipmentFile 设备文件
     * @return 结果
     */
    @Override
    public int updateEquipmentFile(EquipmentFile equipmentFile)
    {
        equipmentFile.setUpdateTime(DateUtils.getNowDate());
        equipmentFile.setUpdateBy(SecurityUtils.getUsername());
        return equipmentFileMapper.updateEquipmentFile(equipmentFile);
    }

    /**
     * 批量删除设备文件
     * 
     * @param fileIds 需要删除的设备文件主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentFileByFileIds(String[] fileIds)
    {
        return equipmentFileMapper.deleteEquipmentFileByFileIds(fileIds);
    }

    /**
     * 删除设备文件信息
     * 
     * @param fileId 设备文件主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentFileByFileId(String fileId)
    {
        return equipmentFileMapper.deleteEquipmentFileByFileId(fileId);
    }
}
