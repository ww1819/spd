package com.spd.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.system.domain.SysUserUiConfig;
import com.spd.system.mapper.SysUserUiConfigMapper;
import com.spd.system.service.ISysUserUiConfigService;

@Service
public class SysUserUiConfigServiceImpl implements ISysUserUiConfigService
{
    @Autowired
    private SysUserUiConfigMapper sysUserUiConfigMapper;

    @Override
    public String getConfigValue(Long userId, String configKey)
    {
        if (userId == null || configKey == null)
        {
            return null;
        }
        SysUserUiConfig row = sysUserUiConfigMapper.selectByUserAndKey(userId, configKey);
        return row != null ? row.getConfigValue() : null;
    }

    @Override
    public void saveConfig(Long userId, String configKey, String configValue)
    {
        SysUserUiConfig row = new SysUserUiConfig();
        row.setUserId(userId);
        row.setConfigKey(configKey);
        row.setConfigValue(configValue);
        sysUserUiConfigMapper.insertOrUpdate(row);
    }
}
