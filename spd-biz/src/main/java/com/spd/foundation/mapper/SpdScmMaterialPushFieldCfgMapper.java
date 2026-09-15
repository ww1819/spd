package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.foundation.domain.SpdScmMaterialPushFieldCfg;

@Mapper
@Repository
public interface SpdScmMaterialPushFieldCfgMapper
{
    List<SpdScmMaterialPushFieldCfg> selectByTenantId(@Param("tenantId") String tenantId);

    SpdScmMaterialPushFieldCfg selectByTenantAndField(@Param("tenantId") String tenantId,
        @Param("fieldCode") String fieldCode);

    int insert(SpdScmMaterialPushFieldCfg row);

    int updateByTenantAndField(SpdScmMaterialPushFieldCfg row);
}
