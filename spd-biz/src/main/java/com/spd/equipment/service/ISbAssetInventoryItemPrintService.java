package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetInventoryItemPrint;

/**
 * 资产盘点单明细与标签打印关联表 Service
 */
public interface ISbAssetInventoryItemPrintService {

    List<SbAssetInventoryItemPrint> selectList(SbAssetInventoryItemPrint q);
    List<SbAssetInventoryItemPrint> selectByInventoryItemId(String inventoryItemId);
    SbAssetInventoryItemPrint selectById(String id);
    int insert(SbAssetInventoryItemPrint row);
    int deleteById(String id);
}
