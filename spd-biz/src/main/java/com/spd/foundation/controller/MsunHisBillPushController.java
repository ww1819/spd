package com.spd.foundation.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.service.IMsunHisBillPushService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 众阳 HIS 出库补退等（枣强租户）。
 */
@RestController
@RequestMapping("/foundation/msunHis/bill")
public class MsunHisBillPushController extends BaseController
{
    private final IMsunHisBillPushService msunHisBillPushService;

    public MsunHisBillPushController(IMsunHisBillPushService msunHisBillPushService)
    {
        this.msunHisBillPushService = msunHisBillPushService;
    }

    /** 201 出库单补退：仅推送未成功/失败明细 */
    @PostMapping("/repush/outbound/{billId}")
    public AjaxResult repushOutbound(@PathVariable Long billId)
    {
        msunHisBillPushService.assertMsunIntegratedTenant(SecurityUtils.getCustomerId());
        msunHisBillPushService.repushOutbound(billId);
        return success("补退推送已提交");
    }
}
