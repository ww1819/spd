package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.EquipmentAdverseEvent;

/**
 * 设备不良事件 Mapper
 */
public interface EquipmentAdverseEventMapper
{
    List<EquipmentAdverseEvent> selectByEquipmentId(@Param("equipmentId") String equipmentId);

    int insertEquipmentAdverseEvent(EquipmentAdverseEvent row);

    int updateEquipmentAdverseEvent(EquipmentAdverseEvent row);

    /**
     * 将未出现在 keepIds 中的在册记录逻辑删除；keepIds 为空则删除该设备下全部在册记录。
     */
    int logicalDeleteRemoved(@Param("equipmentId") String equipmentId, @Param("keepIds") List<String> keepIds,
            @Param("deleteBy") String deleteBy);

    int logicalDeleteByEquipmentIds(@Param("equipmentIds") String[] equipmentIds, @Param("deleteBy") String deleteBy);
}
