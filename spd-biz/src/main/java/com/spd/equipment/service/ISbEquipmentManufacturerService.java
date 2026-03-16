package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentManufacturer;

public interface ISbEquipmentManufacturerService {

    List<SbEquipmentManufacturer> selectList(SbEquipmentManufacturer q);
    SbEquipmentManufacturer selectById(String id);
    int insert(SbEquipmentManufacturer row);
    int update(SbEquipmentManufacturer row);
    int deleteById(String id);
}
