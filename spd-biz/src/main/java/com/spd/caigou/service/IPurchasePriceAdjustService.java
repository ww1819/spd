package com.spd.caigou.service;

import com.spd.caigou.domain.PurchasePriceAdjust;
import com.spd.caigou.domain.PurchasePriceAdjustEntry;

import java.util.List;

/**
 * 采购调价单 Service
 */
public interface IPurchasePriceAdjustService {

    PurchasePriceAdjust selectPurchasePriceAdjustById(Long id);

    List<PurchasePriceAdjust> selectPurchasePriceAdjustList(PurchasePriceAdjust query);

    /** 调价报表明细（默认仅已审核） */
    List<PurchasePriceAdjustEntry> selectReportEntryList(PurchasePriceAdjust query);

    /** 调价报表汇总（按产品，默认仅已审核） */
    List<PurchasePriceAdjustEntry> selectReportSummaryList(PurchasePriceAdjust query);

    /**
     * 新增调价单；保存时生成 TJ- 前缀唯一单号
     * @return 已落库实体（含 billNo）
     */
    PurchasePriceAdjust insertPurchasePriceAdjust(PurchasePriceAdjust bill);

    int updatePurchasePriceAdjust(PurchasePriceAdjust bill);

    /** 提交：未提交(0) → 未审核(1) */
    int submitPurchasePriceAdjustByIds(Long[] ids);

    /** 审核：未审核(1) → 已审核(2) */
    int auditPurchasePriceAdjustByIds(Long[] ids);

    int deletePurchasePriceAdjustByIds(Long[] ids);
}
