package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdEquipmentDict;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 设备字典Mapper接口
 *
 * @author spd
 * @date 2024-12-16
 */
@Mapper
@Repository
public interface FdEquipmentDictMapper
{
    /**
     * 查询设备字典
     *
     * @param code 设备字典编码
     * @return 设备字典
     */
    public FdEquipmentDict selectFdEquipmentDictByCode(String code);
    
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
     * 删除设备字典
     *
     * @param id 设备字典主键
     * @return 结果
     */
    public int deleteFdEquipmentDictById(Long id);

    /**
     * 批量删除设备字典
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFdEquipmentDictByIds(Long[] ids);
}

