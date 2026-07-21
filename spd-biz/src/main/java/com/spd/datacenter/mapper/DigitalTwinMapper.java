package com.spd.datacenter.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 数字孪生监控大屏 Mapper
 */
public interface DigitalTwinMapper
{
    /** 库存总量与金额 */
    Map<String, Object> selectInventoryTotals(@Param("warehouseId") Long warehouseId);

    /** 货位列表（含五区坐标） */
    List<Map<String, Object>> selectLocations(@Param("warehouseId") Long warehouseId);

    /**
     * 货位库存聚合：优先定数货位，其次耗材默认货位
     */
    List<Map<String, Object>> selectLocationStockAgg(@Param("warehouseId") Long warehouseId);

    /** 货位明细库存行（弹窗用） */
    List<Map<String, Object>> selectLocationStockDetail(@Param("locationId") Long locationId,
                                                       @Param("warehouseId") Long warehouseId);

    /** 今日出入库流水 */
    List<Map<String, Object>> selectIoRealtime(@Param("warehouseId") Long warehouseId,
                                               @Param("limit") Integer limit);

    /** 今日出入库笔数（可按仓） */
    Map<String, Object> selectTodayIoBillCount(@Param("warehouseId") Long warehouseId);

    /** 耗材编码 → 货位（定数优先，否则默认货位） */
    List<Map<String, Object>> selectMaterialLocationHints(@Param("warehouseId") Long warehouseId);
}
