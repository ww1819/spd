package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbEquipmentSupplier;

public interface ISbEquipmentSupplierService {

    List<SbEquipmentSupplier> selectList(SbEquipmentSupplier q);
    SbEquipmentSupplier selectById(String id);
    /** 按名称查找或创建供应商（当前客户下），用于导入等场景 */
    SbEquipmentSupplier getOrCreateByName(String name);
    int insert(SbEquipmentSupplier row);
    int update(SbEquipmentSupplier row);
    int deleteById(String id);
}
