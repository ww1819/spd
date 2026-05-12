package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.EquipmentAccessoryIo;

public interface EquipmentAccessoryIoMapper {

    List<EquipmentAccessoryIo> selectList(EquipmentAccessoryIo q);

    EquipmentAccessoryIo selectById(@Param("id") String id);

    int insert(EquipmentAccessoryIo row);
}
