package com.spd.warehouse.service;

import com.spd.warehouse.domain.StkScrapBill;

import java.util.List;

/**
 * 库房报废单
 */
public interface IStkScrapBillService {

    StkScrapBill selectStkScrapBillById(Long id);

    List<StkScrapBill> selectStkScrapBillList(StkScrapBill query);

    StkScrapBill insertStkScrapBill(StkScrapBill bill);

    int updateStkScrapBill(StkScrapBill bill);

    int submitStkScrapBillByIds(Long[] ids);

    int deleteStkScrapBillByIds(Long[] ids);
}
