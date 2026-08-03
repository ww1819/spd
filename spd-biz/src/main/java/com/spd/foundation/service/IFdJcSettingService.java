package com.spd.foundation.service;

import java.util.Map;

/**
 * 集采租户配置（报量模式等）
 */
public interface IFdJcSettingService
{
    /** 当前报量模式 PRODUCT / TYPE */
    String getReportMode();

    /**
     * 切换报量模式。旧模式报量数据保留不删；切回后仍可使用。
     */
    void saveReportMode(String reportMode);

    Map<String, Object> getSettingSummary();
}
