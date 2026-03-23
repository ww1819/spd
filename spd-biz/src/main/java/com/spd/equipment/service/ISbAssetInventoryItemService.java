package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetInventoryItem;

/**
 * 资产盘点单明细表 Service
 */
public interface ISbAssetInventoryItemService {

    List<SbAssetInventoryItem> selectList(SbAssetInventoryItem q);
    List<SbAssetInventoryItem> selectByInventoryId(String inventoryId);
    SbAssetInventoryItem selectById(String id);
    int insert(SbAssetInventoryItem row);
    int insertBatch(List<SbAssetInventoryItem> list);
    int update(SbAssetInventoryItem row);
    int deleteById(String id);
    /** 按盘点单ID逻辑删除该单下所有明细（用于重新生成明细前清空） */
    int deleteByInventoryId(String inventoryId);
}
