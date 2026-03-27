package com.spd.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.utils.SecurityUtils;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.service.IStkBatchService;

/**
 * 批次追溯 Service 实现
 */
@Service
public class StkBatchServiceImpl implements IStkBatchService
{
    @Autowired
    private StkBatchMapper stkBatchMapper;

    @Override
    public StkBatch selectStkBatchById(Long id)
    {
        StkBatch batch = stkBatchMapper.selectStkBatchById(id);
        if (batch != null)
        {
            SecurityUtils.ensureTenantAccess(batch.getTenantId());
        }
        return batch;
    }

    @Override
    public List<StkBatch> selectStkBatchList(StkBatch stkBatch)
    {
        return stkBatchMapper.selectStkBatchList(stkBatch);
    }
}

