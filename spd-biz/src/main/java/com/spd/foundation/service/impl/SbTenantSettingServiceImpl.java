package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
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
    /** 空配置占位：避免 his.internal.operator_user_id 等缺省键在单笔链路内反复查库 */
    private static final String ABSENT = "\u0000";

    private final ConcurrentHashMap<String, String> valueCache = new ConcurrentHashMap<>();

    @Autowired
    private SbTenantSettingMapper sbTenantSettingMapper;

    @Override
    public String getSettingValue(String tenantId, String settingKey, String defaultValue)
    {
        if (StringUtils.isAnyBlank(tenantId, settingKey))
        {
            return defaultValue;
        }
        String key = cacheKey(tenantId, settingKey);
        String cached = valueCache.get(key);
        if (cached != null)
        {
            return ABSENT.equals(cached) ? defaultValue : cached;
        }
        SbTenantSetting row = sbTenantSettingMapper.selectByTenantAndKey(tenantId, settingKey.trim());
        if (row == null || StringUtils.isBlank(row.getSettingValue()))
        {
            valueCache.put(key, ABSENT);
            return defaultValue;
        }
        String value = row.getSettingValue().trim();
        valueCache.put(key, value);
        return value;
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
            valueCache.remove(cacheKey(tenantId, settingKey));
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
        valueCache.remove(cacheKey(tenantId, settingKey));
    }

    private static String cacheKey(String tenantId, String settingKey)
    {
        return tenantId + "|" + settingKey.trim();
    }
}
