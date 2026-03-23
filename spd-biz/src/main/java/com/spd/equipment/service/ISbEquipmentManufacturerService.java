package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentManufacturer;

public interface ISbEquipmentManufacturerService {

    List<SbEquipmentManufacturer> selectList(SbEquipmentManufacturer q);
    SbEquipmentManufacturer selectById(String id);
    /** 按名称查找或创建生产厂家（当前客户下），用于导入等场景 */
    SbEquipmentManufacturer getOrCreateByName(String name);
    int insert(SbEquipmentManufacturer row);
    int update(SbEquipmentManufacturer row);
    int deleteById(String id);
}
