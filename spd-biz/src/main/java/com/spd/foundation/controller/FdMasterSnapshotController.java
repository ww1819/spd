package com.spd.foundation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.SpdFoundationDataSnapshot;
import com.spd.foundation.mapper.SpdFoundationDataSnapshotMapper;

/**
 * 院内主数据变更整单快照查询
 */
@RestController
@RequestMapping("/foundation/masterSnapshot")
public class FdMasterSnapshotController extends BaseController
{
    @Autowired
    private SpdFoundationDataSnapshotMapper spdFoundationDataSnapshotMapper;

    @PreAuthorize("@ss.hasPermi('foundation:masterSnapshot:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "entityType", required = false) String entityType,
        @RequestParam(value = "entityId", required = false) String entityId)
    {
        String tenantId = StringUtils.isNotEmpty(SecurityUtils.getCustomerId())
            ? SecurityUtils.getCustomerId() : SecurityUtils.requiredScopedTenantIdForSql();
        List<SpdFoundationDataSnapshot> list = spdFoundationDataSnapshotMapper.selectList(tenantId, entityType, entityId);
        return success(list);
    }
}
