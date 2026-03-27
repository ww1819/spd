package com.spd.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.warehouse.domain.StkProfitLossPending;
import com.spd.warehouse.mapper.StkProfitLossPendingMapper;
import com.spd.warehouse.service.IStkProfitLossPendingService;

/**
 * 盘盈待入账明细 Service 实现
 */
@Service
public class StkProfitLossPendingServiceImpl implements IStkProfitLossPendingService
{
    @Autowired
    private StkProfitLossPendingMapper stkProfitLossPendingMapper;

    @Override
    public StkProfitLossPending selectStkProfitLossPendingById(Long id)
    {
        StkProfitLossPending pending = stkProfitLossPendingMapper.selectStkProfitLossPendingById(id);
        if (pending != null)
        {
            SecurityUtils.ensureTenantAccess(pending.getTenantId());
        }
        return pending;
    }

    @Override
    public List<StkProfitLossPending> selectStkProfitLossPendingList(StkProfitLossPending pending)
    {
        return stkProfitLossPendingMapper.selectStkProfitLossPendingList(pending);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePendingStatusById(Long id, String applyStatus, String settlementEffectStatus)
    {
        if (StringUtils.isEmpty(applyStatus) || StringUtils.isEmpty(settlementEffectStatus))
        {
            throw new ServiceException("状态参数不能为空");
        }
        StkProfitLossPending existing = stkProfitLossPendingMapper.selectStkProfitLossPendingById(id);
        if (existing == null)
        {
            throw new ServiceException("待入账明细不存在");
        }
        SecurityUtils.ensureTenantAccess(existing.getTenantId());
        return stkProfitLossPendingMapper.updatePendingStatusById(
            id,
            applyStatus,
            settlementEffectStatus,
            SecurityUtils.getUserIdStr()
        );
    }
}

