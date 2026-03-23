package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetInventoryItemPrint;

/**
 * 资产盘点单明细与标签打印关联表 Mapper
 */
public interface SbAssetInventoryItemPrintMapper {

    List<SbAssetInventoryItemPrint> selectList(SbAssetInventoryItemPrint q);
    List<SbAssetInventoryItemPrint> selectByInventoryItemId(@Param("inventoryItemId") String inventoryItemId);
    SbAssetInventoryItemPrint selectById(String id);
    int insert(SbAssetInventoryItemPrint row);
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
}
