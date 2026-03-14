package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentBrand;

public interface SbEquipmentBrandMapper {

    List<SbEquipmentBrand> selectList(SbEquipmentBrand q);

    SbEquipmentBrand selectById(String id);

    int insert(SbEquipmentBrand row);

    int update(SbEquipmentBrand row);
}
