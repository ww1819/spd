package com.spd.foundation.service.impl;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.SbTenantSetting;
import com.spd.foundation.mapper.SbTenantSettingMapper;
import com.spd.foundation.service.ISbTenantSettingService;

@Service
public class SbTenantSettingServiceImpl implements ISbTenantSettingService
{
    @Autowired
    private SbTenantSettingMapper sbTenantSettingMapper;

    @Override
    public String getSettingValue(String tenantId, String settingKey, String defaultValue)
    {
        if (StringUtils.isAnyBlank(tenantId, settingKey))
        {
            return defaultValue;
        }
        SbTenantSetting row = sbTenantSettingMapper.selectByTenantAndKey(tenantId, settingKey.trim());
        if (row == null || StringUtils.isBlank(row.getSettingValue()))
        {
            return defaultValue;
        }
        return row.getSettingValue().trim();
    }

    @Override
    public void saveSettingValue(String tenantId, String settingKey, String settingValue, String remark)
    {
        if (StringUtils.isAnyBlank(tenantId, settingKey))
        {
            return;
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = new Date();
        SbTenantSetting existing = sbTenantSettingMapper.selectByTenantAndKey(tenantId, settingKey.trim());
        if (existing != null)
        {
            SbTenantSetting u = new SbTenantSetting();
            u.setTenantId(tenantId);
            u.setSettingKey(settingKey.trim());
            u.setSettingValue(settingValue);
            u.setRemark(remark);
            u.setUpdateBy(uid);
            u.setUpdateTime(now);
            sbTenantSettingMapper.updateSbTenantSetting(u);
            return;
        }
        SbTenantSetting ins = new SbTenantSetting();
        ins.setId(UUID7.generateUUID7());
        ins.setTenantId(tenantId);
        ins.setSettingKey(settingKey.trim());
        ins.setSettingValue(settingValue);
        ins.setRemark(remark);
        ins.setDelFlag(0);
        ins.setCreateBy(uid);
        ins.setCreateTime(now);
        ins.setUpdateBy(uid);
        ins.setUpdateTime(now);
        sbTenantSettingMapper.insertSbTenantSetting(ins);
    }
}
