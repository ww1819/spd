package com.spd.warehouse.service;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 单据引用关联
 */
public interface IHcDocBillRefService {

    /**
     * 保存 stk_io_bill 新增后的引用行：按 docRefList 与明细顺序对齐，写入目标单号/明细ID。
     */
    void saveRefsAfterStkBillInsert(StkIoBill requestBill, StkIoBill reloadedWithEntries);
}
