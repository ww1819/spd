package com.spd.warehouse.service;

import java.math.BigDecimal;
import java.util.Map;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 单据引用关联
 */
public interface IHcDocBillRefService {

    /**
     * 保存 stk_io_bill 新增后的引用行：按 docRefList 与明细顺序对齐，写入目标单号/明细ID。
     */
    void saveRefsAfterStkBillInsert(StkIoBill requestBill, StkIoBill reloadedWithEntries);

    /**
     * 按源单汇总各明细已被引用的数量（hc_doc_bill_ref.ref_qty 合计，del_flag=0）。
     *
     * @return key 为源明细 id 字符串
     */
    Map<String, BigDecimal> sumRefQtyBySrcBillId(String tenantId, String srcBillId);
}
