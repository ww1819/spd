package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdLocationMapper;
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.service.IFdLocationService;

/**
 * 货位Service业务层处理
 *
 * @author spd
 * @date 2024-12-13
 */
@Service
public class FdLocationServiceImpl implements IFdLocationService
{
    @Autowired
    private FdLocationMapper fdLocationMapper;

    @Override
    public FdLocation selectFdLocationByLocationId(Long locationId)
    {
        FdLocation loc = fdLocationMapper.selectFdLocationByLocationId(locationId);
        if (loc != null)
        {
            SecurityUtils.ensureTenantAccess(loc.getTenantId());
        }
        return loc;
    }

    @Override
    public List<FdLocation> selectFdLocationList(FdLocation fdLocation)
    {
        if (fdLocation != null && StringUtils.isEmpty(fdLocation.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdLocation.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdLocationMapper.selectFdLocationList(fdLocation);
    }

    @Override
    public List<FdLocation> selectFdLocationTree()
    {
        FdLocation q = new FdLocation();
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdLocationMapper.selectFdLocationTree(q);
    }

    @Override
    public int insertFdLocation(FdLocation fdLocation)
    {
        if (StringUtils.isEmpty(fdLocation.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdLocation.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(fdLocation.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdLocation.setCreateBy(SecurityUtils.getUserIdStr());
        }
        fdLocation.setCreateTime(DateUtils.getNowDate());
        if (fdLocation.getParentId() == null)
        {
            fdLocation.setParentId(0L);
        }
        if (fdLocation.getDelFlag() == null)
        {
            fdLocation.setDelFlag(0);
        }
        return fdLocationMapper.insertFdLocation(fdLocation);
    }

    @Override
    public int updateFdLocation(FdLocation fdLocation)
    {
        if (fdLocation.getLocationId() == null)
        {
            throw new ServiceException("货位主键不能为空");
        }
        FdLocation before = fdLocationMapper.selectFdLocationByLocationId(fdLocation.getLocationId());
        if (before == null)
        {
            throw new ServiceException("货位不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdLocation.setTenantId(before.getTenantId());
        fdLocation.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdLocation.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdLocation.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return fdLocationMapper.updateFdLocation(fdLocation);
    }

    @Override
    public int deleteFdLocationByLocationIds(Long locationIds)
    {
        FdLocation fdLocation = fdLocationMapper.selectFdLocationByLocationId(locationIds);
        if (fdLocation == null)
        {
            throw new ServiceException(String.format("货位：%s，不存在!", locationIds));
        }
        SecurityUtils.ensureTenantAccess(fdLocation.getTenantId());
        fdLocation.setDelFlag(1);
        fdLocation.setDeleteBy(SecurityUtils.getUserIdStr());
        fdLocation.setDeleteTime(DateUtils.getNowDate());
        fdLocation.setUpdateTime(DateUtils.getNowDate());
        fdLocation.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdLocationMapper.updateFdLocation(fdLocation);
    }
}
