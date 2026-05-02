package com.spd.foundation.service;

import com.spd.foundation.domain.SbTenantSetting;

public interface ISbTenantSettingService
{
    String getSettingValue(String tenantId, String settingKey, String defaultValue);

    void saveSettingValue(String tenantId, String settingKey, String settingValue, String remark);
}
