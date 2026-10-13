package com.spd.warehouse.service;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkScrapBill;
import com.spd.warehouse.domain.StkScrapQueryVo;

import java.util.List;

/**
 * 库房报废单
 */
public interface IStkScrapBillService {

    StkScrapBill selectStkScrapBillById(Long id);

    List<StkScrapBill> selectStkScrapBillList(StkScrapBill query);

    List<StkScrapQueryVo> selectScrapDetailList(StkScrapQueryVo query);

    TotalInfo selectScrapDetailTotal(StkScrapQueryVo query);

    List<StkScrapQueryVo> selectScrapSummaryList(StkScrapQueryVo query);

    TotalInfo selectScrapSummaryTotal(StkScrapQueryVo query);

    StkScrapBill insertStkScrapBill(StkScrapBill bill);

    int updateStkScrapBill(StkScrapBill bill);

    int submitStkScrapBillByIds(Long[] ids);

    int auditStkScrapBillByIds(Long[] ids);

    int deleteStkScrapBillByIds(Long[] ids);
}
