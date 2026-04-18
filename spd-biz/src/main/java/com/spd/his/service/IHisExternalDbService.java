package com.spd.his.service;

import java.util.List;
import com.spd.his.domain.HisExternalDb;

/**
 * 主库 {@code sys_his_external_db} 维护（平台）。
 */
public interface IHisExternalDbService
{
    List<HisExternalDb> selectList(HisExternalDb query);

    HisExternalDb selectByTenantId(String tenantId);

    int insert(HisExternalDb row);

    int update(HisExternalDb row);

    int deleteByTenantId(String tenantId);
}
