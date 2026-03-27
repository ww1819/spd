package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.service.IFdUnitService;
import java.text.DecimalFormat;

/**
 * 单位明细Service业务层处理
 *
 * @author spd
 * @date 2024-04-07
 */
@Service
public class FdUnitServiceImpl implements IFdUnitService
{
    @Autowired
    private FdUnitMapper fdUnitMapper;

    @Override
    public FdUnit selectFdUnitByUnitId(Long unitId)
    {
        FdUnit u = fdUnitMapper.selectFdUnitByUnitId(unitId);
        if (u != null)
        {
            SecurityUtils.ensureTenantAccess(u.getTenantId());
        }
        return u;
    }

    @Override
    public List<FdUnit> selectFdUnitList(FdUnit fdUnit)
    {
        if (fdUnit != null && StringUtils.isEmpty(fdUnit.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdUnit.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdUnitMapper.selectFdUnitList(fdUnit);
    }

    @Override
    public int insertFdUnit(FdUnit fdUnit)
    {
        if (StringUtils.isEmpty(fdUnit.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdUnit.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(fdUnit.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdUnit.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (fdUnit.getDelFlag() == null)
        {
            fdUnit.setDelFlag(0);
        }
        if (StringUtils.isEmpty(fdUnit.getUnitCode()))
        {
            fdUnit.setUnitCode(generateUnitCode());
        }
        fdUnit.setCreateTime(DateUtils.getNowDate());
        return fdUnitMapper.insertFdUnit(fdUnit);
    }

    private String generateUnitCode()
    {
        String tid = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid))
        {
            tid = null;
        }
        String maxCode = fdUnitMapper.selectMaxUnitCode(tid);
        int nextNum = 1000024;

        if (StringUtils.isNotEmpty(maxCode) && maxCode.startsWith("D") && maxCode.length() > 1)
        {
            try
            {
                String numStr = maxCode.substring(1);
                int maxNum = Integer.parseInt(numStr);
                nextNum = maxNum + 1;
            }
            catch (NumberFormatException e)
            {
                nextNum = 1000024;
            }
        }

        DecimalFormat df = new DecimalFormat("0000000");
        return "D" + df.format(nextNum);
    }

    @Override
    public int updateFdUnit(FdUnit fdUnit)
    {
        if (fdUnit.getUnitId() == null)
        {
            throw new ServiceException("单位主键不能为空");
        }
        FdUnit before = fdUnitMapper.selectFdUnitByUnitId(fdUnit.getUnitId());
        if (before == null)
        {
            throw new ServiceException("单位不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdUnit.setTenantId(before.getTenantId());
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdUnit.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdUnit.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return fdUnitMapper.updateFdUnit(fdUnit);
    }

    @Override
    public int deleteFdUnitByUnitIds(Long unitIds)
    {
        FdUnit fdUnit = fdUnitMapper.selectFdUnitByUnitId(unitIds);
        if (fdUnit == null)
        {
            throw new ServiceException(String.format("单位明细：%s，不存在!", unitIds));
        }
        SecurityUtils.ensureTenantAccess(fdUnit.getTenantId());
        fdUnit.setDelFlag(1);
        fdUnit.setDeleteBy(SecurityUtils.getUserIdStr());
        fdUnit.setDeleteTime(DateUtils.getNowDate());
        fdUnit.setUpdateBy(SecurityUtils.getUserIdStr());
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }
}
