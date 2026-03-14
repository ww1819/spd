package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentSupplier;

public interface ISbEquipmentSupplierService {

    List<SbEquipmentSupplier> selectList(SbEquipmentSupplier q);
    SbEquipmentSupplier selectById(String id);
    int insert(SbEquipmentSupplier row);
    int update(SbEquipmentSupplier row);
    int deleteById(String id);
}
