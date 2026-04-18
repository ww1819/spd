package com.spd.his.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.exception.ServiceException;
import com.spd.his.config.HisTenantJdbcAccess;
import com.spd.his.domain.HisExternalDb;
import com.spd.his.mapper.HisExternalDbMapper;
import com.spd.his.service.IHisExternalDbService;

@Service
public class HisExternalDbServiceImpl implements IHisExternalDbService
{
    private static final String DB_SQLSERVER = "SQLSERVER";
    private static final String DB_MYSQL = "MYSQL";

    @Autowired
    private HisExternalDbMapper hisExternalDbMapper;

    @Autowired
    private HisTenantJdbcAccess hisTenantJdbcAccess;

    @Override
    public List<HisExternalDb> selectList(HisExternalDb query)
    {
        List<HisExternalDb> list = hisExternalDbMapper.selectList(query != null ? query : new HisExternalDb());
        for (HisExternalDb row : list)
        {
            maskPassword(row);
        }
        return list;
    }

    @Override
    public HisExternalDb selectByTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return null;
        }
        HisExternalDb row = hisExternalDbMapper.selectByTenantId(tenantId.trim());
        if (row != null)
        {
            maskPassword(row);
        }
        return row;
    }

    @Override
    public int insert(HisExternalDb row)
    {
        validateAndNormalize(row, true);
        row.setTenantId(row.getTenantId().trim());
        if (hisExternalDbMapper.selectByTenantId(row.getTenantId()) != null)
        {
            throw new ServiceException("租户ID已存在：" + row.getTenantId());
        }
        int n = hisExternalDbMapper.insert(row);
        hisTenantJdbcAccess.evictTenant(row.getTenantId());
        return n;
    }

    @Override
    public int update(HisExternalDb row)
    {
        if (row == null || StringUtils.isBlank(row.getTenantId()))
        {
            throw new ServiceException("租户ID不能为空");
        }
        row.setTenantId(row.getTenantId().trim());
        HisExternalDb existing = hisExternalDbMapper.selectByTenantId(row.getTenantId());
        if (existing == null)
        {
            throw new ServiceException("记录不存在：" + row.getTenantId());
        }
        if (StringUtils.isBlank(row.getPassword()))
        {
            row.setPassword(existing.getPassword());
        }
        validateAndNormalize(row, false);
        int n = hisExternalDbMapper.updateByTenantId(row);
        hisTenantJdbcAccess.evictTenant(row.getTenantId());
        return n;
    }

    @Override
    public int deleteByTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("租户ID不能为空");
        }
        String tid = tenantId.trim();
        int n = hisExternalDbMapper.deleteByTenantId(tid);
        hisTenantJdbcAccess.evictTenant(tid);
        return n;
    }

    private static void maskPassword(HisExternalDb row)
    {
        if (row != null)
        {
            row.setPassword(StringUtils.isNotEmpty(row.getPassword()) ? "******" : "");
        }
    }

    private static void validateAndNormalize(HisExternalDb row, boolean isInsert)
    {
        if (row == null)
        {
            throw new ServiceException("参数不能为空");
        }
        if (isInsert && StringUtils.isBlank(row.getTenantId()))
        {
            throw new ServiceException("租户ID不能为空");
        }
        if (row.getTenantId() != null && row.getTenantId().length() > 36)
        {
            throw new ServiceException("租户ID长度不能超过36");
        }
        String dbType = normalizeDbType(row.getDbType());
        row.setDbType(dbType);
        if (StringUtils.isAnyBlank(row.getJdbcUrl(), row.getUsername()))
        {
            throw new ServiceException("JDBC URL 与账号不能为空");
        }
        row.setJdbcUrl(row.getJdbcUrl().trim());
        row.setUsername(row.getUsername().trim());
        if (row.getJdbcUrl().length() > 1024)
        {
            throw new ServiceException("JDBC URL 过长");
        }
        if (!DB_SQLSERVER.equals(dbType) && !DB_MYSQL.equals(dbType))
        {
            throw new ServiceException("db_type 仅支持 SQLSERVER 或 MYSQL");
        }
        if (DB_MYSQL.equals(dbType))
        {
            if (StringUtils.isBlank(row.getSqlInpatientRange()) || StringUtils.isBlank(row.getSqlOutpatientRange()))
            {
                throw new ServiceException("MYSQL 时住院/门诊区间 SQL 均不能为空（须各含两个时间占位 ?）");
            }
        }
        if (StringUtils.isBlank(row.getEnabled()))
        {
            row.setEnabled("1");
        }
        if (!"0".equals(row.getEnabled()) && !"1".equals(row.getEnabled()))
        {
            throw new ServiceException("启用状态只能为 0 或 1");
        }
        if (row.getDriverClass() != null && row.getDriverClass().trim().isEmpty())
        {
            row.setDriverClass(null);
        }
    }

    private static String normalizeDbType(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return DB_SQLSERVER;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("SQLSERVER") || "MSSQL".equals(u))
        {
            return DB_SQLSERVER;
        }
        if ("MYSQL".equals(u) || "MARIADB".equals(u))
        {
            return DB_MYSQL;
        }
        return u;
    }
}
