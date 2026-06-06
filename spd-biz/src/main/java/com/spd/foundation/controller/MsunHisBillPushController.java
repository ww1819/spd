package com.spd.foundation.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.service.IMsunHisBillPushService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /** 201 出库单手动推送：登录即可，不校验菜单权限；仅已审核单可推（见 pushOutbound 服务层） */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/push/outbound/{billId}")
    public AjaxResult pushOutbound(@PathVariable Long billId)
    {
        msunHisBillPushService.assertMsunIntegratedTenant(SecurityUtils.getCustomerId());
        msunHisBillPushService.pushOutbound(billId);
        return success("HIS推送已提交");
    }

    /** @deprecated 使用 {@link #pushOutbound(Long)} */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/repush/outbound/{billId}")
    public AjaxResult repushOutbound(@PathVariable Long billId)
    {
        msunHisBillPushService.assertMsunIntegratedTenant(SecurityUtils.getCustomerId());
        msunHisBillPushService.repushOutbound(billId);
        return success("HIS推送已提交");
    }
}
