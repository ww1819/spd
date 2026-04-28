package com.spd.gz.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.gz.mapper.GzBillEntryChangeLogMapper;
import com.spd.warehouse.mapper.StkBillEntryChangeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gz/changeLog")
public class GzBillEntryChangeLogController extends BaseController {

    @Autowired
    private GzBillEntryChangeLogMapper gzBillEntryChangeLogMapper;

    @Autowired
    private StkBillEntryChangeLogMapper stkBillEntryChangeLogMapper;

    @PreAuthorize(
        "@ss.hasPermi('gzOrder:apply:query')"
            + " or @ss.hasPermi('inWarehouse:apply:query')"
            + " or @ss.hasPermi('outWarehouse:apply:query')"
            + " or @ss.hasPermi('inWarehouse:refundGoodsApply:query')"
            + " or @ss.hasPermi('outWarehouse:refundDepotApply:query')"
    )
    @GetMapping("/list")
    public AjaxResult list(@RequestParam("billType") String billType, @RequestParam("billId") Long billId) {
        if (billType != null && billType.startsWith("STK_IO_BILL")) {
            return success(stkBillEntryChangeLogMapper.selectByBill(billType, billId));
        }
        return success(gzBillEntryChangeLogMapper.selectByBill(billType, billId));
    }
}
