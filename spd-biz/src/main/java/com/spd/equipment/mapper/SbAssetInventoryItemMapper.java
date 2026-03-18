package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetInventoryItem;

/**
 * 资产盘点单明细表 Mapper
 */
public interface SbAssetInventoryItemMapper {

    List<SbAssetInventoryItem> selectList(SbAssetInventoryItem q);
    List<SbAssetInventoryItem> selectByInventoryId(@Param("inventoryId") String inventoryId);
    SbAssetInventoryItem selectById(String id);
    int insert(SbAssetInventoryItem row);
    int insertBatch(@Param("list") List<SbAssetInventoryItem> list);
    int update(SbAssetInventoryItem row);
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
    int deleteByInventoryId(@Param("inventoryId") String inventoryId, @Param("delBy") String delBy);
}
