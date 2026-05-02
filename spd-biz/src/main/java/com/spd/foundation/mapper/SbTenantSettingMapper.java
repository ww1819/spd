package com.spd.foundation.mapper;

import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.SbTenantSetting;

public interface SbTenantSettingMapper
{
    SbTenantSetting selectByTenantAndKey(@Param("tenantId") String tenantId, @Param("settingKey") String settingKey);

    int insertSbTenantSetting(SbTenantSetting row);

    int updateSbTenantSetting(SbTenantSetting row);
}
