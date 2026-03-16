package com.spd.finance.service;

import com.spd.finance.domain.SuppSettlementBill;
import com.spd.finance.domain.SuppSettlementInvoice;

import java.util.List;

/**
 * 供应商结算单Service接口
 */
public interface ISuppSettlementBillService {

    List<SuppSettlementBill> list(SuppSettlementBill query);

    SuppSettlementBill getById(String id);

    /** 查询该供应商结算单已关联的发票列表 */
    List<SuppSettlementInvoice> listInvoices(String suppSettlementId);

    /** 增加关联发票 */
    int addInvoice(String suppSettlementId, String invoiceId);

    /** 取消关联发票 */
    int removeInvoice(String suppSettlementId, String invoiceId);
}
