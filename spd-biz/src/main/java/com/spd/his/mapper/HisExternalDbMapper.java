package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisExternalDb;

public interface HisExternalDbMapper
{
    /**
     * 查询租户已启用的 HIS 外联配置（主库）。
     */
    HisExternalDb selectEnabledByTenantId(@Param("tenantId") String tenantId);

    HisExternalDb selectByTenantId(@Param("tenantId") String tenantId);

    List<HisExternalDb> selectList(HisExternalDb query);

    int insert(HisExternalDb row);

    int updateByTenantId(HisExternalDb row);

    int deleteByTenantId(@Param("tenantId") String tenantId);
}
