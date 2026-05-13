package com.spd.system.mapper;

import com.spd.system.domain.SysLicense;

/**
 * 系统离线授权 Mapper
 */
public interface SysLicenseMapper
{
    SysLicense selectSysLicenseById(Long id);

    int insertSysLicense(SysLicense row);

    int updateSysLicenseActivation(SysLicense row);
}
