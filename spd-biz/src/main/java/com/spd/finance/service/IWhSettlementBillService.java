package com.spd.finance.service;

import com.spd.finance.domain.WhSettlementBill;
import com.spd.finance.domain.WhSettlementBillEntry;

import java.util.List;

/**
 * 仓库结算单Service接口
 */
public interface IWhSettlementBillService {

    List<WhSettlementBill> list(WhSettlementBill query);

    WhSettlementBill getById(String id);

    /** 新增：仅主表，无明细时需先提取数据 */
    int add(WhSettlementBill row);

    int update(WhSettlementBill row);

    int remove(String id);

    /** 提取数据：按仓库+开始/结束时间+结算方式，拉取未结算的入库或出库明细 */
    List<WhSettlementBillEntry> extractData(Long warehouseId, String settlementMethod, java.util.Date beginTime, java.util.Date endTime);

    /** 保存明细（覆盖原明细）；审核后不可用 */
    int saveEntries(String billId, List<WhSettlementBillEntry> entries);

    /** 删除指定明细（逻辑删除）；审核后不可用 */
    int removeEntries(String billId, List<String> entryIds);

    /** 审核：更新状态，并按供应商分组生成供应商结算单 */
    int audit(String id);
}
