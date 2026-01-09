package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.EquipmentReturn;

/**
 * 设备退货Mapper接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface EquipmentReturnMapper 
{
    /**
     * 查询设备退货
     * 
     * @param returnId 设备退货主键
     * @return 设备退货
     */
    public EquipmentReturn selectEquipmentReturnById(Long returnId);

    /**
     * 查询设备退货列表
     * 
     * @param equipmentReturn 设备退货
     * @return 设备退货集合
     */
    public List<EquipmentReturn> selectEquipmentReturnList(EquipmentReturn equipmentReturn);

    /**
     * 新增设备退货
     * 
     * @param equipmentReturn 设备退货
     * @return 结果
     */
    public int insertEquipmentReturn(EquipmentReturn equipmentReturn);

    /**
     * 修改设备退货
     * 
     * @param equipmentReturn 设备退货
     * @return 结果
     */
    public int updateEquipmentReturn(EquipmentReturn equipmentReturn);

    /**
     * 删除设备退货
     * 
     * @param returnId 设备退货主键
     * @return 结果
     */
    public int deleteEquipmentReturnById(Long returnId);

    /**
     * 批量删除设备退货
     * 
     * @param returnIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEquipmentReturnByIds(Long[] returnIds);

    /**
     * 审核设备退货
     * 
     * @param equipmentReturn 设备退货
     * @return 结果
     */
    public int auditEquipmentReturn(EquipmentReturn equipmentReturn);
}
