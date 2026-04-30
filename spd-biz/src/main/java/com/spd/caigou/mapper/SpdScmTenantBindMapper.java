package com.spd.caigou.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.caigou.domain.SpdScmTenantBind;

@Mapper
@Repository
public interface SpdScmTenantBindMapper
{
    SpdScmTenantBind selectByTenantId(@Param("tenantId") String tenantId);

    String selectHospitalCodeByTenantId(@Param("tenantId") String tenantId);

    int insert(SpdScmTenantBind row);

    int updateByTenantId(SpdScmTenantBind row);
}
