package com.spd.finance.service;

import com.spd.finance.domain.FinInvoice;

import java.util.List;

/**
 * 发票管理Service接口
 *
 * @author spd
 */
public interface IFinInvoiceService {

    List<FinInvoice> list(FinInvoice query);

    FinInvoice getById(String id);

    int add(FinInvoice row);

    int update(FinInvoice row);

    int remove(String id);

    int audit(String id);
}
