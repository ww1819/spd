package com.spd.foundation.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdJcType;
import com.spd.foundation.mapper.FdJcTypeMapper;
import com.spd.foundation.service.IFdJcTypeService;

@Service
public class FdJcTypeServiceImpl implements IFdJcTypeService
{
    @Autowired
    private FdJcTypeMapper fdJcTypeMapper;

    @Override
    public FdJcType selectFdJcTypeById(Long id)
    {
        FdJcType row = fdJcTypeMapper.selectFdJcTypeById(id);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    @Override
    public List<FdJcType> selectFdJcTypeList(FdJcType query)
    {
        return fdJcTypeMapper.selectFdJcTypeList(query);
    }

    @Override
    public int insertFdJcType(FdJcType row)
    {
        validate(row, null);
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
        if (row.getSortOrder() == null)
        {
            row.setSortOrder(0);
        }
        return fdJcTypeMapper.insertFdJcType(row);
    }

    @Override
    public int updateFdJcType(FdJcType row)
    {
        if (row.getId() == null)
        {
            throw new ServiceException("主键不能为空");
        }
        FdJcType existing = selectFdJcTypeById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("集采类型不存在");
        }
        validate(row, row.getId());
        row.setUpdateBy(SecurityUtils.getUserIdStr());
        row.setUpdateTime(DateUtils.getNowDate());
        return fdJcTypeMapper.updateFdJcType(row);
    }

    @Override
    public int deleteFdJcTypeById(Long id)
    {
        FdJcType existing = selectFdJcTypeById(id);
        if (existing == null)
        {
            throw new ServiceException("集采类型不存在");
        }
        if (fdJcTypeMapper.countMaterialRef(id) > 0)
        {
            throw new ServiceException("该类型已被产品档案引用，不能删除，可改为停用");
        }
        if (fdJcTypeMapper.countReportRef(id) > 0)
        {
            throw new ServiceException("该类型已有报量数据，不能删除，可改为停用");
        }
        return fdJcTypeMapper.deleteFdJcTypeById(id, SecurityUtils.getUserIdStr());
    }

    private void validate(FdJcType row, Long excludeId)
    {
        if (row == null || StringUtils.isEmpty(row.getName()) || StringUtils.isEmpty(row.getName().trim()))
        {
            throw new ServiceException("集采类型名称不能为空");
        }
        row.setName(row.getName().trim());
        if (StringUtils.isNotEmpty(row.getCode()))
        {
            row.setCode(row.getCode().trim());
            if (fdJcTypeMapper.countByTenantAndCode(null, row.getCode(), excludeId) > 0)
            {
                throw new ServiceException("集采类型编码已存在：" + row.getCode());
            }
        }
        if (fdJcTypeMapper.countByTenantAndName(null, row.getName(), excludeId) > 0)
        {
            throw new ServiceException("集采类型名称已存在：" + row.getName());
        }
    }
}
