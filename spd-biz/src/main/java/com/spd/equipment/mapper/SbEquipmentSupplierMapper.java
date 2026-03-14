package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentSupplier;

public interface SbEquipmentSupplierMapper {

    List<SbEquipmentSupplier> selectList(SbEquipmentSupplier q);

    SbEquipmentSupplier selectById(String id);

    int insert(SbEquipmentSupplier row);

    int update(SbEquipmentSupplier row);
}
