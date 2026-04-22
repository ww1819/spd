package com.spd.system.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SysPrintDocRows;
import com.spd.system.mapper.SysPrintDocRowsMapper;
import com.spd.system.service.ISysPrintDocRowsService;

/**
 * 单据打印每页行数 服务实现
 *
 * @author spd
 */
@Service
public class SysPrintDocRowsServiceImpl implements ISysPrintDocRowsService
{
    @Autowired
    private SysPrintDocRowsMapper sysPrintDocRowsMapper;

    @Override
    public SysPrintDocRows selectByDocKind(String docKind)
    {
        if (StringUtils.isBlank(docKind))
        {
            return null;
        }
        return sysPrintDocRowsMapper.selectByDocKind(docKind.trim().toUpperCase());
    }

    @Override
    public int upsert(SysPrintDocRows row)
    {
        String kind = row.getDocKind() != null ? row.getDocKind().trim().toUpperCase() : "";
        row.setDocKind(kind);
        SysPrintDocRows existing = sysPrintDocRowsMapper.selectByDocKind(kind);
        String user = SecurityUtils.getUsername();
        if (existing == null)
        {
            row.setCreateBy(user);
            return sysPrintDocRowsMapper.insertSysPrintDocRows(row);
        }
        row.setUpdateBy(user);
        return sysPrintDocRowsMapper.updateRowsByDocKind(row);
    }
}
