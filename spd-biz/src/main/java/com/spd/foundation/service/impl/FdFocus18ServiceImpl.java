package com.spd.foundation.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdFocus18;
import com.spd.foundation.mapper.FdFocus18Mapper;
import com.spd.foundation.service.IFdFocus18Service;

@Service
public class FdFocus18ServiceImpl implements IFdFocus18Service
{
    @Autowired
    private FdFocus18Mapper fdFocus18Mapper;

    @Override
    public FdFocus18 selectFdFocus18ById(Long id)
    {
        FdFocus18 row = fdFocus18Mapper.selectFdFocus18ById(id);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    @Override
    public List<FdFocus18> selectFdFocus18List(FdFocus18 query)
    {
        return fdFocus18Mapper.selectFdFocus18List(query);
    }

    @Override
    public List<String> selectFdFocus18Categories()
    {
        return fdFocus18Mapper.selectFdFocus18Categories();
    }

    @Override
    public int insertFdFocus18(FdFocus18 row)
    {
        normalize(row);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy()))
        {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (row.getDelFlag() == null)
        {
            row.setDelFlag(0);
        }
        return fdFocus18Mapper.insertFdFocus18(row);
    }

    @Override
    public int updateFdFocus18(FdFocus18 row)
    {
        if (row.getId() == null)
        {
            throw new ServiceException("主键不能为空");
        }
        FdFocus18 existing = selectFdFocus18ById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("18类重点耗材不存在");
        }
        normalize(row);
        row.setUpdateBy(SecurityUtils.getUserIdStr());
        row.setUpdateTime(DateUtils.getNowDate());
        return fdFocus18Mapper.updateFdFocus18(row);
    }

    @Override
    public int deleteFdFocus18ById(Long id)
    {
        FdFocus18 existing = selectFdFocus18ById(id);
        if (existing == null)
        {
            throw new ServiceException("18类重点耗材不存在");
        }
        return fdFocus18Mapper.deleteFdFocus18ById(id, SecurityUtils.getUserIdStr());
    }

    private void normalize(FdFocus18 row)
    {
        if (row == null)
        {
            throw new ServiceException("参数不能为空");
        }
        row.setCategory(trimToNull(row.getCategory()));
        row.setClassCode(trimToNull(row.getClassCode()));
        row.setLevel1(trimToNull(row.getLevel1()));
        row.setLevel2(trimToNull(row.getLevel2()));
        row.setLevel3(trimToNull(row.getLevel3()));
        row.setGenericCode(trimToNull(row.getGenericCode()));
        row.setMedicalGenericName(trimToNull(row.getMedicalGenericName()));
        row.setMaterialCode(trimToNull(row.getMaterialCode()));
        row.setMaterial(trimToNull(row.getMaterial()));
        row.setFeatureCode(trimToNull(row.getFeatureCode()));
        row.setFeatureParam(trimToNull(row.getFeatureParam()));
        if (row.getParentId() == null || row.getParentId() < 0)
        {
            row.setParentId(0L);
        }
        if (row.getId() != null && row.getParentId() != null && row.getParentId().equals(row.getId()))
        {
            throw new ServiceException("上级菜单不能选择自己");
        }
        if (StringUtils.isEmpty(row.getCategory())
            && StringUtils.isEmpty(row.getClassCode())
            && StringUtils.isEmpty(row.getGenericCode())
            && StringUtils.isEmpty(row.getMedicalGenericName()))
        {
            throw new ServiceException("请至少填写耗材类别、分类代码、通用名代码或医保通用名之一");
        }
    }

    private String trimToNull(String val)
    {
        if (val == null)
        {
            return null;
        }
        String t = val.trim();
        return t.isEmpty() ? null : t;
    }
}
