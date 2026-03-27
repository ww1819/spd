package com.spd.system.service;

/**
 * 用户界面个性化配置
 */
public interface ISysUserUiConfigService
{
    String getConfigValue(Long userId, String configKey);

    void saveConfig(Long userId, String configKey, String configValue);
}
