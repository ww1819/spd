package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetInventory;
import com.spd.equipment.domain.SbAssetPrintTask;

/**
 * 资产盘点单主表 Service
 */
public interface ISbAssetInventoryService {

    List<SbAssetInventory> selectList(SbAssetInventory q);
    SbAssetInventory selectById(String id);
    SbAssetInventory selectByOrderNo(String customerId, String orderNo);
    int insert(SbAssetInventory row);
    int update(SbAssetInventory row);
    int deleteById(String id);

    /**
     * 盘点明细内打印：根据盘点单及明细生成打印任务单，并写入盘点明细与标签打印关联表（含打印任务单id、打印任务单号）
     * @param inventoryId 盘点单主表ID
     * @param inventoryItemIds 盘点单明细ID列表（为空则取该盘点单下全部未删除明细）
     * @return 生成的打印任务单
     */
    SbAssetPrintTask createPrintTaskFromInventoryItems(String inventoryId, List<String> inventoryItemIds);

    /**
     * 根据盘点单类型与表头范围，从台账生成盘点明细（仅草稿可执行；会先清空该单已有明细）
     * 按科室：该科室下所有设备；按68分类：该分类及下级分类设备；按存放地点：该地点设备
     * @return 生成的明细条数
     */
    int buildItemsFromLedger(String inventoryId);
}
