package com.spd.warehouse.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.domain.StkIoProfitLossEntry;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.vo.StkProfitLossEntryVo;

/**
 * 盈亏单 Mapper 接口
 *
 * @author spd
 */
@Mapper
@Repository
public interface StkIoProfitLossMapper {

    /**
     * 根据主键查询盈亏单
     */
    StkIoProfitLoss selectStkIoProfitLossById(Long id);

    /**
     * 查询盈亏单列表
     */
    List<StkIoProfitLoss> selectStkIoProfitLossList(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 新增盈亏单主表
     */
    int insertStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 更新盈亏单主表
     */
    int updateStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 逻辑删除盈亏单
     */
    int deleteStkIoProfitLossById(Long id);

    /**
     * 根据盈亏单ID删除明细（逻辑删除）
     */
    int deleteStkIoProfitLossEntryByParenId(Long parenId);

    /**
     * 批量新增盈亏单明细
     */
    int batchStkIoProfitLossEntry(List<StkIoProfitLossEntry> list);

    /**
     * 根据盘点单ID查询“有盈亏”的盘点明细（profit_qty != 0 或 profit_amount != 0），供加载盈亏单草稿使用
     */
    List<StkIoStocktakingEntry> selectStocktakingEntriesWithProfitLoss(@Param("stocktakingId") Long stocktakingId);

    /**
     * 查询盈亏单当天最大单号
     */
    String selectMaxBillNo(@Param("datePrefix") String datePrefix);

    /**
     * 根据盘点单ID查询是否已存在盈亏单（一对一约束）
     */
    Integer countByStocktakingId(@Param("stocktakingId") Long stocktakingId);

    /**
     * 查询盈亏明细列表（用于报表）
     */
    List<StkProfitLossEntryVo> selectProfitLossEntryList(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 查询盈亏明细汇总列表（用于报表）
     */
    List<StkProfitLossEntryVo> selectProfitLossEntrySummaryList(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 查询盈亏明细列表合计（用于报表）
     */
    TotalInfo selectProfitLossEntryListTotal(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 查询盈亏明细汇总列表合计（用于报表）
     */
    TotalInfo selectProfitLossEntrySummaryListTotal(StkIoProfitLoss stkIoProfitLoss);
}
