package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.EquipmentAccessory;
import com.spd.equipment.domain.EquipmentAccessoryIo;
import com.spd.equipment.domain.EquipmentAccessoryIoEntry;
import com.spd.equipment.domain.EquipmentAccessoryStock;
import com.spd.equipment.domain.dto.EquipmentAccessoryIoSubmitBody;

public interface IEquipmentAccessoryService {

    List<EquipmentAccessory> selectAccessoryList(EquipmentAccessory q);

    EquipmentAccessory selectAccessoryById(String id);

    int insertAccessory(EquipmentAccessory row);

    int updateAccessory(EquipmentAccessory row);

    int deleteAccessoryById(String id);

    List<EquipmentAccessoryStock> selectStockList(EquipmentAccessoryStock q);

    List<EquipmentAccessoryIo> selectIoList(EquipmentAccessoryIo q);

    EquipmentAccessoryIo selectIoById(String id);

    List<EquipmentAccessoryIoEntry> selectIoEntries(String ioId);

    int submitIo(EquipmentAccessoryIoSubmitBody body);
}
