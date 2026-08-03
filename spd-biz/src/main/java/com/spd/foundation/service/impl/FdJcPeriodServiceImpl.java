package com.spd.foundation.service.impl;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdJcPeriod;
import com.spd.foundation.mapper.FdJcPeriodMapper;
import com.spd.foundation.service.IFdJcPeriodService;

@Service
public class FdJcPeriodServiceImpl implements IFdJcPeriodService
{
    private static final Pattern YM = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    @Autowired
    private FdJcPeriodMapper fdJcPeriodMapper;

    @Override
    public FdJcPeriod selectFdJcPeriodById(Long id)
    {
        FdJcPeriod row = fdJcPeriodMapper.selectFdJcPeriodById(id);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    @Override
    public List<FdJcPeriod> selectFdJcPeriodList(FdJcPeriod query)
    {
        return fdJcPeriodMapper.selectFdJcPeriodList(query);
    }

    @Override
    public int insertFdJcPeriod(FdJcPeriod row)
    {
        validate(row);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy()))
        {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (row.getDelFlag() == null)
        {
            row.setDelFlag(0);
        }
        if (StringUtils.isEmpty(row.getIsUse()))
        {
            row.setIsUse("1");
        }
        return fdJcPeriodMapper.insertFdJcPeriod(row);
    }

    @Override
    public int updateFdJcPeriod(FdJcPeriod row)
    {
        if (row.getId() == null)
        {
            throw new ServiceException("主键不能为空");
        }
        FdJcPeriod existing = selectFdJcPeriodById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("集采周期不存在");
        }
        validate(row);
        row.setUpdateBy(SecurityUtils.getUserIdStr());
        row.setUpdateTime(DateUtils.getNowDate());
        return fdJcPeriodMapper.updateFdJcPeriod(row);
    }

    @Override
    public int deleteFdJcPeriodById(Long id)
    {
        FdJcPeriod existing = selectFdJcPeriodById(id);
        if (existing == null)
        {
            throw new ServiceException("集采周期不存在");
        }
        if (fdJcPeriodMapper.countReportRef(id) > 0)
        {
            throw new ServiceException("该周期已有报量数据，不能删除，可改为停用");
        }
        return fdJcPeriodMapper.deleteFdJcPeriodById(id, SecurityUtils.getUserIdStr());
    }

    private void validate(FdJcPeriod row)
    {
        if (row == null || StringUtils.isEmpty(row.getName()) || StringUtils.isEmpty(row.getName().trim()))
        {
            throw new ServiceException("周期名称不能为空");
        }
        row.setName(row.getName().trim());
        if (StringUtils.isEmpty(row.getStartYm()) || !YM.matcher(row.getStartYm()).matches())
        {
            throw new ServiceException("开始年月格式须为 YYYY-MM");
        }
        if (StringUtils.isEmpty(row.getEndYm()) || !YM.matcher(row.getEndYm()).matches())
        {
            throw new ServiceException("结束年月格式须为 YYYY-MM");
        }
        if (row.getEndYm().compareTo(row.getStartYm()) < 0)
        {
            throw new ServiceException("结束年月不能早于开始年月");
        }
    }
}
