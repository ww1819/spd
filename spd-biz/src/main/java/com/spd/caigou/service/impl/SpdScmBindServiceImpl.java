package com.spd.caigou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.domain.SpdScmTenantBind;
import com.spd.caigou.mapper.SpdScmSupplierBindMapper;
import com.spd.caigou.mapper.SpdScmTenantBindMapper;
import com.spd.caigou.service.ISpdScmBindService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.IdUtils;

@Service
public class SpdScmBindServiceImpl implements ISpdScmBindService
{
    @Autowired
    private SpdScmTenantBindMapper spdScmTenantBindMapper;

    @Autowired
    private SpdScmSupplierBindMapper spdScmSupplierBindMapper;

    private String tenantId()
    {
        String tid = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid))
        {
            throw new ServiceException("未获取到当前租户，无法维护云平台编码绑定");
        }
        return tid;
    }

    @Override
    public SpdScmTenantBind getTenantBind()
    {
        return spdScmTenantBindMapper.selectByTenantId(tenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTenantBind(String scmHospitalCode, String remark)
    {
        if (StringUtils.isEmpty(StringUtils.trim(scmHospitalCode)))
        {
            throw new ServiceException("平台医院编码不能为空");
        }
        String tid = tenantId();
        String user = SecurityUtils.getUsername();
        SpdScmTenantBind existing = spdScmTenantBindMapper.selectByTenantId(tid);
        if (existing == null)
        {
            SpdScmTenantBind row = new SpdScmTenantBind();
            row.setId(IdUtils.fastUUID());
            row.setTenantId(tid);
            row.setScmHospitalCode(scmHospitalCode.trim());
            row.setRemark(remark);
            row.setCreateBy(user);
            spdScmTenantBindMapper.insert(row);
        }
        else
        {
            SpdScmTenantBind row = new SpdScmTenantBind();
            row.setTenantId(tid);
            row.setScmHospitalCode(scmHospitalCode.trim());
            row.setRemark(remark);
            row.setUpdateBy(user);
            spdScmTenantBindMapper.updateByTenantId(row);
        }
    }

    @Override
    public SpdScmSupplierBind getSupplierBind(Long supplierId)
    {
        if (supplierId == null)
        {
            return null;
        }
        return spdScmSupplierBindMapper.selectByTenantAndSupplier(tenantId(), String.valueOf(supplierId));
    }

    @Override
    public List<SpdScmSupplierBind> listSupplierBinds()
    {
        return spdScmSupplierBindMapper.selectListByTenantId(tenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSupplierBind(Long supplierId, String scmSupplierCode, String remark)
    {
        if (supplierId == null)
        {
            throw new ServiceException("供应商不能为空");
        }
        if (StringUtils.isEmpty(StringUtils.trim(scmSupplierCode)))
        {
            throw new ServiceException("平台供应商编码不能为空");
        }
        String tid = tenantId();
        String sid = String.valueOf(supplierId);
        String user = SecurityUtils.getUsername();
        SpdScmSupplierBind existing = spdScmSupplierBindMapper.selectByTenantAndSupplier(tid, sid);
        if (existing == null)
        {
            SpdScmSupplierBind row = new SpdScmSupplierBind();
            row.setId(IdUtils.fastUUID());
            row.setTenantId(tid);
            row.setSupplierId(sid);
            row.setScmSupplierCode(scmSupplierCode.trim());
            row.setRemark(remark);
            row.setCreateBy(user);
            spdScmSupplierBindMapper.insert(row);
        }
        else
        {
            SpdScmSupplierBind row = new SpdScmSupplierBind();
            row.setTenantId(tid);
            row.setSupplierId(sid);
            row.setScmSupplierCode(scmSupplierCode.trim());
            row.setRemark(remark);
            row.setUpdateBy(user);
            spdScmSupplierBindMapper.updateByTenantAndSupplier(row);
        }
    }
}
