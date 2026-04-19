package com.spd.warehouse.service;

import java.math.BigDecimal;
import java.util.Map;

import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.vo.InboundEntryRefChannelQtyVo;

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

    /**
     * 入库单明细：出库引用通道（RK_TO_CK）已审核/待审核占用量，key 为源明细 id 字符串。
     */
    Map<String, InboundEntryRefChannelQtyVo> sumInboundOutboundChannelBySrcBillId(String tenantId, String srcBillId);

    /**
     * 入库单明细：退货引用通道（RK_TO_TH）已审核/待审核占用量。
     */
    Map<String, InboundEntryRefChannelQtyVo> sumInboundReturnChannelBySrcBillId(String tenantId, String srcBillId);

    /**
     * 科室退库单明细：退货引用通道（TK_TO_TH）已审核/待审核占用量。
     */
    Map<String, InboundEntryRefChannelQtyVo> sumTkReturnChannelBySrcBillId(String tenantId, String srcBillId);

    /**
     * 按 ref_type 汇总各源明细已被引用数量（不区分审核态）。
     */
    Map<String, BigDecimal> sumRefQtyBySrcBillIdAndRefType(String tenantId, String srcBillId, String refType);
}
