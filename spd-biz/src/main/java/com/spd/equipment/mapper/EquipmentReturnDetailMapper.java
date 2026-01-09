package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.EquipmentReturnDetail;

/**
 * 设备退货明细Mapper接口
 * 
 * @author spd
 * @date 2024-01-01
 */
public interface EquipmentReturnDetailMapper 
{
    /**
     * 查询设备退货明细列表
     * 
     * @param returnId 退货单ID
     * @return 设备退货明细集合
     */
    public List<EquipmentReturnDetail> selectEquipmentReturnDetailList(Long returnId);

    /**
     * 新增设备退货明细
     * 
     * @param equipmentReturnDetail 设备退货明细
     * @return 结果
     */
    public int insertEquipmentReturnDetail(EquipmentReturnDetail equipmentReturnDetail);

    /**
     * 批量新增设备退货明细
     * 
     * @param detailList 设备退货明细列表
     * @return 结果
     */
    public int batchInsertEquipmentReturnDetail(List<EquipmentReturnDetail> detailList);

    /**
     * 删除设备退货明细
     * 
     * @param returnId 退货单ID
     * @return 结果
     */
    public int deleteEquipmentReturnDetailByReturnId(Long returnId);

    /**
     * 批量删除设备退货明细
     * 
     * @param returnIds 需要删除的退货单ID集合
     * @return 结果
     */
    public int deleteEquipmentReturnDetailByReturnIds(Long[] returnIds);
}
