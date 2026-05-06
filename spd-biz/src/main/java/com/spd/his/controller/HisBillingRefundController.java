package com.spd.his.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.his.domain.dto.HisBillingRefundHighBody;
import com.spd.his.domain.dto.HisBillingRefundLowBody;
import com.spd.his.service.IHisBillingRefundService;

/**
 * 科室计费退费（衡水三院租户）：低值按消耗关联返还库存，高值按条码/关联行返还。
 */
@RestController
@RequestMapping("/his/billingRefund")
public class HisBillingRefundController extends BaseController
{
    @Autowired
    private IHisBillingRefundService hisBillingRefundService;

    @PreAuthorize("@ss.hasPermi('department:patientCharge:billingRefundLow')")
    @Log(title = "计费低值退费返还", businessType = BusinessType.UPDATE)
    @PostMapping("/low")
    public AjaxResult refundLow(@RequestBody HisBillingRefundLowBody body)
    {
        return success(hisBillingRefundService.processLowValueRefund(body));
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:billingRefundHigh')")
    @Log(title = "计费高值退费返还", businessType = BusinessType.UPDATE)
    @PostMapping("/high")
    public AjaxResult refundHigh(@RequestBody HisBillingRefundHighBody body)
    {
        return success(hisBillingRefundService.processHighValueRefund(body));
    }
}
