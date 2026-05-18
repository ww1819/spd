package com.spd.his.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.system.service.ISysConfigService;
import com.spd.his.domain.dto.HisProcessFetchBatchInternalBody;
import com.spd.his.service.IHisPatientChargeService;

/**
 * 供 scminterface 等前置机在镜像同步后触发自动低值消耗/退费（API Key 鉴权，不走登录态）。
 */
@RestController
@RequestMapping("/his/internal/patientCharge")
public class HisPatientChargeInternalController extends BaseController
{
    public static final String HEADER_INTERNAL_KEY = "X-Spd-Internal-Key";

    @Autowired
    private IHisPatientChargeService hisPatientChargeService;
    @Autowired
    private ISysConfigService sysConfigService;

    @PostMapping("/processFetchBatch")
    public AjaxResult processFetchBatch(
        @RequestHeader(value = HEADER_INTERNAL_KEY, required = false) String apiKey,
        @RequestBody HisProcessFetchBatchInternalBody body)
    {
        assertInternalKey(apiKey);
        if (body == null || StringUtils.isBlank(body.getTenantId())
            || StringUtils.isBlank(body.getFetchBatchId()) || StringUtils.isBlank(body.getVisitKind()))
        {
            throw new ServiceException("请提供 tenantId、fetchBatchId、visitKind");
        }
        hisPatientChargeService.processFetchBatchAuto(
            body.getTenantId().trim(),
            body.getFetchBatchId().trim(),
            body.getVisitKind().trim(),
            body.getOperatorUserId());
        return success();
    }

    private void assertInternalKey(String provided)
    {
        String expected = sysConfigService.selectConfigByKey(HisBillingTenantConstants.SETTING_INTERNAL_API_KEY);
        if (StringUtils.isBlank(expected))
        {
            throw new ServiceException("未配置内部接口密钥（sys_config：his.internal.api_key）");
        }
        if (StringUtils.isBlank(provided) || !expected.trim().equals(provided.trim()))
        {
            throw new ServiceException("内部接口密钥无效");
        }
    }
}
