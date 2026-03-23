package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentBrand;

public interface ISbEquipmentBrandService {

    List<SbEquipmentBrand> selectList(SbEquipmentBrand q);
    SbEquipmentBrand selectById(String id);
    int insert(SbEquipmentBrand row);
    int update(SbEquipmentBrand row);
    int deleteById(String id);
}
