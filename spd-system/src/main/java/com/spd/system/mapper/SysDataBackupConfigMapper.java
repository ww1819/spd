package com.spd.system.mapper;

import com.spd.system.domain.SysDataBackupConfig;

/**
 * 数据备份配置 Mapper
 */
public interface SysDataBackupConfigMapper
{
    SysDataBackupConfig selectById(Long id);

    SysDataBackupConfig selectByTenantId(String tenantId);

    int insert(SysDataBackupConfig row);

    int update(SysDataBackupConfig row);

    int updateLastResult(SysDataBackupConfig row);

    int updateJobId(SysDataBackupConfig row);
}
