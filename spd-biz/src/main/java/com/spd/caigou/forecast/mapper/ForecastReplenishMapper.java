package com.spd.caigou.forecast.mapper;

import com.spd.caigou.forecast.domain.ForecastFixedMaterialRow;
import com.spd.caigou.forecast.domain.ForecastMaterialQtyRow;
import com.spd.caigou.forecast.domain.PurchaseForecastEntry;
import com.spd.caigou.forecast.domain.PurchaseForecastTask;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 采购预测补货 Mapper
 */
public interface ForecastReplenishMapper {

    List<PurchaseForecastTask> selectTaskList(PurchaseForecastTask query);

    PurchaseForecastTask selectTaskById(@Param("id") Long id);

    List<PurchaseForecastEntry> selectEntryListByTaskId(@Param("taskId") Long taskId);

    PurchaseForecastEntry selectEntryById(@Param("id") Long id);

    int insertTask(PurchaseForecastTask task);

    int updateTask(PurchaseForecastTask task);

    int batchInsertEntry(@Param("list") List<PurchaseForecastEntry> list);

    int updateEntryConfirm(PurchaseForecastEntry entry);

    String selectMaxTaskNo(@Param("dateNum") String dateNum);

    /**
     * 仓库定数：启用+监测中，关联产品档案
     */
    List<ForecastFixedMaterialRow> selectEnabledFixedMaterials(
        @Param("warehouseId") Long warehouseId,
        @Param("isGz") String isGz);

    /**
     * 近区间净出库：201为正、401为负，按物料汇总
     */
    List<ForecastMaterialQtyRow> selectNetOutboundQty(
        @Param("warehouseId") Long warehouseId,
        @Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate,
        @Param("materialIds") List<Long> materialIds);

    /**
     * 未完成采购计划在途：plan_status in (0,1)
     */
    List<ForecastMaterialQtyRow> selectInTransitPlanQty(
        @Param("warehouseId") Long warehouseId,
        @Param("materialIds") List<Long> materialIds);
}
