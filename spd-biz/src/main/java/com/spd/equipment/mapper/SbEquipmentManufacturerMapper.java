package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentManufacturer;

public interface SbEquipmentManufacturerMapper {

    List<SbEquipmentManufacturer> selectList(SbEquipmentManufacturer q);

    SbEquipmentManufacturer selectById(String id);

    int insert(SbEquipmentManufacturer row);

    int update(SbEquipmentManufacturer row);
}
