package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.EquipmentAccessoryStock;

public interface EquipmentAccessoryStockMapper {

    List<EquipmentAccessoryStock> selectList(EquipmentAccessoryStock q);

    EquipmentAccessoryStock selectActive(@Param("accessoryId") String accessoryId,
            @Param("warehouseCode") String warehouseCode);

    int insert(EquipmentAccessoryStock row);

    int addQty(@Param("id") String id, @Param("qty") java.math.BigDecimal qty,
            @Param("updateBy") String updateBy, @Param("updateTime") java.util.Date updateTime);

    int deductQty(@Param("accessoryId") String accessoryId, @Param("warehouseCode") String warehouseCode,
            @Param("qty") java.math.BigDecimal qty, @Param("updateBy") String updateBy,
            @Param("updateTime") java.util.Date updateTime);
}
