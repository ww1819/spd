package com.spd.foundation.service;

import com.spd.common.core.domain.AjaxResult;
import java.util.Map;

/**
 * 枣强县中医院：从 scminterface 前置机拉取众阳 HIS 主数据并更新 SPD。
 */
public interface IMsunHisMasterSyncService
{
    /**
     * @param syncType depts|identities|suppliers|producers|categories|materials
     * @param probeParams 可选；非空时先按条件探针拉取（落镜像），再执行全量 sync
     */
    AjaxResult sync(String syncType, Map<String, Object> probeParams);
}
