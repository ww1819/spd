package com.spd.foundation.service;

import com.spd.common.core.domain.AjaxResult;

/**
 * 枣强县中医院：从 scminterface 前置机拉取众阳 HIS 主数据并更新 SPD。
 */
public interface IMsunHisMasterSyncService
{
    /**
     * @param syncType depts|identities|suppliers|producers|categories|materials
     */
    AjaxResult sync(String syncType);
}
