package com.spd.dashboard.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 首页个人视角与常用菜单
 */
public interface HomeUserWorkspaceMapper
{
    String selectHomeView(@Param("userId") Long userId, @Param("tenantId") String tenantId);

    int upsertHomeView(@Param("id") String id, @Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("homeView") String homeView);

    int upsertMenuHit(@Param("id") String id, @Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("path") String path, @Param("title") String title);

    List<Map<String, Object>> selectTopMenus(@Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("limit") int limit);

    List<Map<String, Object>> selectHomeIoQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);

    List<Map<String, Object>> selectHomeApplyQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);

    List<Map<String, Object>> selectHomePurchaseQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);

    /** 采购首页：待下达采购申请单数（已审且计划未全部引用） */
    long countPendingIssuePurchaseApply();

    /** 采购首页：在途采购订单数（已审未作废，且无按订单号完全覆盖的已审入库） */
    long countInTransitPurchaseOrder();

    /** 采购首页：供应商逾期到货预警（在途且约定到货日已过） */
    long countOverduePurchaseOrder();

    /**
     * 采购首页：近 days 天约定到货的订单及时率样本。
     * 返回 Map: onTimeCount, dueCount
     */
    Map<String, Object> selectPurchaseArrivalOnTimeSample(@Param("days") int days);

    /** 库房首页：待盘点任务（仓库盘点未审主单） */
    long countPendingStocktakingTask();

    /** 库房首页：盘点差异待盈亏明细行数 */
    long countStocktakingDiffLine();

    /** 库房首页：调拨待处理单据数 */
    long countPendingTransferBill();

    /** 库房首页：退货待审数量合计 */
    BigDecimal sumPendingReturnAcceptQty();

    /** 库房首页：库区超储（定数上限）物资行数 */
    long countOverstockMaterial();
}
