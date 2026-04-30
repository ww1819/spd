package com.spd.foundation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.SpdFoundationDataSnapshot;
import com.spd.foundation.mapper.SpdFoundationDataSnapshotMapper;

/**
 * 主数据整单快照（与字段级 change_log 并存）
 */
@Component
public class FoundationSnapshotRecorder
{
    @Autowired
    private SpdFoundationDataSnapshotMapper spdFoundationDataSnapshotMapper;

    public void record(String tenantId, String entityType, String entityId, Object before, Object after, String operator)
    {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(entityType) || StringUtils.isEmpty(entityId))
        {
            return;
        }
        String op = StringUtils.isNotEmpty(operator) ? operator : SecurityUtils.getUserIdStr();
        SpdFoundationDataSnapshot row = new SpdFoundationDataSnapshot();
        row.setId(UUID7.generateUUID7());
        row.setTenantId(tenantId);
        row.setEntityType(entityType);
        row.setEntityId(entityId);
        row.setBeforeJson(before == null ? null : JSON.toJSONString(before));
        row.setAfterJson(after == null ? null : JSON.toJSONString(after));
        row.setCreateBy(op);
        spdFoundationDataSnapshotMapper.insert(row);
    }
}
