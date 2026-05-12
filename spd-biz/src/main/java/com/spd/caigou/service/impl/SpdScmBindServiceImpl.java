package com.spd.caigou.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        String user = SecurityUtils.getUserIdStr();
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
    public List<SpdScmSupplierBind> listSupplierBinds(String spdSupplierCode, String scmSupplierCode, String referredCode)
    {
        return spdScmSupplierBindMapper.selectListByTenantId(tenantId(),
                emptyToNull(StringUtils.trim(spdSupplierCode)),
                emptyToNull(StringUtils.trim(scmSupplierCode)),
                emptyToNull(StringUtils.trim(referredCode)));
    }

    private static String emptyToNull(String s)
    {
        return StringUtils.isEmpty(s) ? null : s;
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
        String user = SecurityUtils.getUserIdStr();
        // 同一租户下每个 SPD 供应商仅一条对照（表 uk：tenant_id + supplier_id）；允许多家 SPD 共用同一平台编码
        SpdScmSupplierBind active = spdScmSupplierBindMapper.selectByTenantAndSupplier(tid, sid);
        if (active != null)
        {
            SpdScmSupplierBind row = new SpdScmSupplierBind();
            row.setTenantId(tid);
            row.setSupplierId(sid);
            row.setScmSupplierCode(scmSupplierCode.trim());
            row.setRemark(remark);
            row.setUpdateBy(user);
            spdScmSupplierBindMapper.updateByTenantAndSupplier(row);
            return;
        }
        SpdScmSupplierBind anyDel = spdScmSupplierBindMapper.selectByTenantAndSupplierAnyDel(tid, sid);
        if (anyDel != null)
        {
            SpdScmSupplierBind row = new SpdScmSupplierBind();
            row.setTenantId(tid);
            row.setSupplierId(sid);
            row.setScmSupplierCode(scmSupplierCode.trim());
            row.setRemark(remark);
            row.setUpdateBy(user);
            spdScmSupplierBindMapper.updateReviveByTenantAndSupplier(row);
            return;
        }
        SpdScmSupplierBind row = new SpdScmSupplierBind();
        row.setId(IdUtils.fastUUID());
        row.setTenantId(tid);
        row.setSupplierId(sid);
        row.setScmSupplierCode(scmSupplierCode.trim());
        row.setRemark(remark);
        row.setCreateBy(user);
        spdScmSupplierBindMapper.insert(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeSupplierBinds(Set<Long> supplierIds)
    {
        if (supplierIds == null || supplierIds.isEmpty())
        {
            throw new ServiceException("请选择要删除的供应商绑定");
        }
        LinkedHashSet<String> sidStr = new LinkedHashSet<>();
        for (Long id : supplierIds)
        {
            if (id != null)
            {
                sidStr.add(String.valueOf(id));
            }
        }
        if (sidStr.isEmpty())
        {
            throw new ServiceException("请选择要删除的供应商绑定");
        }
        String tid = tenantId();
        String user = SecurityUtils.getUserIdStr();
        return spdScmSupplierBindMapper.logicalDeleteByTenantAndSupplierIds(tid, new ArrayList<>(sidStr), user);
    }
}
