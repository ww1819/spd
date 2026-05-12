package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.EquipmentAccessory;

public interface EquipmentAccessoryMapper {

    List<EquipmentAccessory> selectList(EquipmentAccessory q);

    EquipmentAccessory selectById(@Param("id") String id);

    int countActiveByCode(@Param("code") String code, @Param("excludeId") String excludeId);

    int insert(EquipmentAccessory row);

    int update(EquipmentAccessory row);
}
