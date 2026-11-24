package com.spd.foundation.service.impl;




import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.mapper.FdUnitChangeLogMapper;
import com.spd.foundation.domain.FdUnitChangeLog;
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

    @Autowired
    private FdUnitChangeLogMapper fdUnitChangeLogMapper;

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
        if (fdUnit.getUnitName() != null)
        {
            fdUnit.setUnitName(fdUnit.getUnitName().trim());
        }
        if (StringUtils.isEmpty(fdUnit.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdUnit.setCreateBy(SecurityUtils.getUserIdStr());
            fdUnit.setCreateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        if (fdUnit.getDelFlag() == null)
        {
            fdUnit.setDelFlag(0);
        }
        if (StringUtils.isEmpty(fdUnit.getUnitCode()))
        {
            fdUnit.setUnitCode(generateUnitCode());
        }
        if (StringUtils.isEmpty(fdUnit.getUnitName()))
        {
            throw new ServiceException("单位名称不能为空");
        }
        if (fdUnitMapper.countUnitByTenantAndUnitName(fdUnit.getTenantId(), fdUnit.getUnitName(), null) > 0)
        {
            throw new ServiceException("单位名称「" + fdUnit.getUnitName() + "」已存在，不能重复");
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
        if (fdUnit.getUnitName() != null)
        {
            fdUnit.setUnitName(fdUnit.getUnitName().trim());
        }
        FdUnit before = fdUnitMapper.selectFdUnitByUnitId(fdUnit.getUnitId());
        if (before == null)
        {
            throw new ServiceException("单位不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        if (StringUtils.isNotEmpty(fdUnit.getUnitName()))
        {
            if (fdUnitMapper.countUnitByTenantAndUnitName(before.getTenantId(), fdUnit.getUnitName(), fdUnit.getUnitId()) > 0)
            {
                throw new ServiceException("单位名称「" + fdUnit.getUnitName() + "」已存在，不能重复");
            }
        }
        fdUnit.setTenantId(before.getTenantId());
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdUnit.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdUnit.setUpdateBy(SecurityUtils.getUserIdStr());
            fdUnit.setUpdateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        int n = fdUnitMapper.updateFdUnit(fdUnit);
        FdUnit after = fdUnitMapper.selectFdUnitByUnitId(fdUnit.getUnitId());
        recordUnitFieldChanges(before, after, SecurityUtils.getUserIdStr());
        return n;
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
            fdUnit.setDeleteByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        fdUnit.setDeleteTime(DateUtils.getNowDate());
        fdUnit.setUpdateBy(SecurityUtils.getUserIdStr());
            fdUnit.setUpdateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }


    public java.util.List<FdUnitChangeLog> selectUnitChangeLog(Long unitId)
    {
        if (unitId == null) { return java.util.Collections.emptyList(); }
        return fdUnitChangeLogMapper.selectByEntityId(unitId);
    }

    private void recordUnitFieldChanges(FdUnit before, FdUnit after, String operator)
    {
        if (before == null || after == null || after.getUnitId() == null) { return; }
        java.util.Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        pushChange(after.getUnitId(), op, now, "unit_code", "单位编码", nzObj(before.getUnitCode()), nzObj(after.getUnitCode()));
        pushChange(after.getUnitId(), op, now, "unit_name", "单位名称", nzObj(before.getUnitName()), nzObj(after.getUnitName()));
    }

    private void pushChange(Long entityId, String operator, java.util.Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue)
    {
        if (java.util.Objects.equals(oldValue, newValue)) { return; }
        FdUnitChangeLog rec = new FdUnitChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setUnitId(entityId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setOperatorName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdUnitChangeLogMapper.insert(rec);
    }

    private static String nzObj(Object v)
    {
        if (v == null) { return ""; }
        return String.valueOf(v);
    }

}
