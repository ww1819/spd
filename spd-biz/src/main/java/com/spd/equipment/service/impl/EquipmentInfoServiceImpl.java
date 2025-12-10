package com.spd.equipment.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.common.utils.uuid.UUID7;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.equipment.mapper.EquipmentInfoMapper;
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.common.utils.DateUtils;

/**
 * 设备信息管理Service业务层处理
 * 
 * @author spd
 * @date 2024-01-01
 */
@Service
public class EquipmentInfoServiceImpl implements IEquipmentInfoService 
{
    @Autowired
    private EquipmentInfoMapper equipmentInfoMapper;

    /**
     * 查询设备信息管理
     * 
     * @param id 设备信息管理主键
     * @return 设备信息管理
     */
    @Override
    public EquipmentInfo selectEquipmentInfoById(String id)
    {
        EquipmentInfo equipmentInfo = equipmentInfoMapper.selectEquipmentInfoById(id);
        if (equipmentInfo != null && equipmentInfo.getAttachedMaterials() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> attachedMaterialsList = objectMapper.readValue(equipmentInfo.getAttachedMaterials(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                equipmentInfo.setAttachedMaterialsList(attachedMaterialsList);
            } catch (Exception e) {
                // 处理异常，设置为空列表
                equipmentInfo.setAttachedMaterialsList(new ArrayList<>());
            }
        }
        return equipmentInfo;
    }

    /**
     * 查询设备信息管理列表
     * 
     * @param equipmentInfo 设备信息管理
     * @return 设备信息管理
     */
    @Override
    public List<EquipmentInfo> selectEquipmentInfoList(EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoMapper.selectEquipmentInfoList(equipmentInfo);
        // 处理每个对象的附属资料列表
        for (EquipmentInfo info : list) {
            if (info.getAttachedMaterials() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<String> attachedMaterialsList = objectMapper.readValue(info.getAttachedMaterials(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    info.setAttachedMaterialsList(attachedMaterialsList);
                } catch (Exception e) {
                    // 处理异常，设置为空列表
                    info.setAttachedMaterialsList(new ArrayList<>());
                }
            }
        }
        return list;
    }

    /**
     * 新增设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    @Override
    public int insertEquipmentInfo(EquipmentInfo equipmentInfo)
    {
                        // 生成UUID作为主键
                equipmentInfo.setId(UUID7.generateUUID7Simple());
                
                // 设置默认删除标志
                equipmentInfo.setDelFlag("0");
                
                // 处理附属资料列表转JSON
                if (equipmentInfo.getAttachedMaterialsList() != null && !equipmentInfo.getAttachedMaterialsList().isEmpty()) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        equipmentInfo.setAttachedMaterials(objectMapper.writeValueAsString(equipmentInfo.getAttachedMaterialsList()));
                    } catch (JsonProcessingException e) {
                        // 处理异常
                    }
                }
                
                equipmentInfo.setCreateTime(DateUtils.getNowDate());
        return equipmentInfoMapper.insertEquipmentInfo(equipmentInfo);
    }

    /**
     * 修改设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    @Override
    public int updateEquipmentInfo(EquipmentInfo equipmentInfo)
    {
        // 处理附属资料列表转JSON
        if (equipmentInfo.getAttachedMaterialsList() != null && !equipmentInfo.getAttachedMaterialsList().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                equipmentInfo.setAttachedMaterials(objectMapper.writeValueAsString(equipmentInfo.getAttachedMaterialsList()));
            } catch (JsonProcessingException e) {
                // 处理异常
            }
        }
        
        equipmentInfo.setUpdateTime(DateUtils.getNowDate());
        return equipmentInfoMapper.updateEquipmentInfo(equipmentInfo);
    }

    /**
     * 批量删除设备信息管理
     * 
     * @param ids 需要删除的设备信息管理主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentInfoByIds(String[] ids)
    {
        return equipmentInfoMapper.deleteEquipmentInfoByIds(ids);
    }

    /**
     * 删除设备信息管理信息
     * 
     * @param id 设备信息管理主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentInfoById(String id)
    {
        return equipmentInfoMapper.deleteEquipmentInfoById(id);
    }

    /**
     * 根据资产编号查询设备信息
     * 
     * @param assetCode 资产编号
     * @return 设备信息管理
     */
    @Override
    public EquipmentInfo selectEquipmentInfoByAssetCode(String assetCode)
    {
        return equipmentInfoMapper.selectEquipmentInfoByAssetCode(assetCode);
    }

    /**
     * 查询设备信息统计
     * 
     * @param equipmentInfo 设备信息管理
     * @return 统计结果
     */
    @Override
    public List<EquipmentInfo> selectEquipmentInfoStatistics(EquipmentInfo equipmentInfo)
    {
        return equipmentInfoMapper.selectEquipmentInfoStatistics(equipmentInfo);
    }
} 