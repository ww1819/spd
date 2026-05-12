package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.EquipmentAccessoryIoEntry;

public interface EquipmentAccessoryIoEntryMapper {

    List<EquipmentAccessoryIoEntry> selectByIoId(@Param("ioId") String ioId);

    int insert(EquipmentAccessoryIoEntry row);
}
