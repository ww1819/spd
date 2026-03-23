package com.spd.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.spd.system.domain.SysUserUiConfig;

/**
 * 用户界面配置
 */
public interface SysUserUiConfigMapper
{
    SysUserUiConfig selectByUserAndKey(@Param("userId") Long userId, @Param("configKey") String configKey);

    int insertOrUpdate(SysUserUiConfig row);
}
