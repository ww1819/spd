package com.spd.warehouse.service;

import java.util.List;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.vo.StkProfitLossEntryVo;

/**
 * 盈亏单 Service 接口
 *
 * @author spd
 */
public interface IStkIoProfitLossService {

    /**
     * 根据主键查询盈亏单（含明细）
     */
    StkIoProfitLoss selectStkIoProfitLossById(Long id);

    /**
     * 查询盈亏单列表
     */
    List<StkIoProfitLoss> selectStkIoProfitLossList(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 根据已审核盘点单ID加载有盈亏明细，组装为盈亏单草稿（仅主表+明细数据，不落库）
     * 仅当盘点单已审核且该盘点单尚未生成盈亏单时可加载
     */
    StkIoProfitLoss loadDraftByStocktakingId(Long stocktakingId);

    /**
     * 新增盈亏单
     */
    int insertStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 修改盈亏单
     */
    int updateStkIoProfitLoss(StkIoProfitLoss stkIoProfitLoss);

    /**
     * 删除盈亏单（逻辑删除）
     */
    int deleteStkIoProfitLossById(Long id);

    /**
     * 审核盈亏单：校验当前库存=账面数量后，盘亏扣库存+插流水PK，盘盈写批次+加库存+插流水PY
     */
    int auditStkIoProfitLoss(Long id);

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
