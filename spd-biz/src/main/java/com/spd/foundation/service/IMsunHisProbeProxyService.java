package com.spd.foundation.service;

import com.spd.common.core.domain.AjaxResult;
import java.util.Map;

/**
 * 枣强众阳 HIS 联调：SPD 后端代理 scminterface。
 */
public interface IMsunHisProbeProxyService
{
    AjaxResult currentEnv();

    AjaxResult invoke(String apiKey, Map<String, Object> params);
}
