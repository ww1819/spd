package com.spd.foundation.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.foundation.service.IMsunHisMasterSyncService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 枣强县中医院：众阳 HIS 主数据一键同步（无需单独按钮权限，能进维护页即可调用）。
 */
@RestController
@RequestMapping("/foundation/msunHis")
public class MsunHisMasterSyncController extends BaseController
{
    private final IMsunHisMasterSyncService msunHisMasterSyncService;

    public MsunHisMasterSyncController(IMsunHisMasterSyncService msunHisMasterSyncService)
    {
        this.msunHisMasterSyncService = msunHisMasterSyncService;
    }

    /**
     * syncType: depts | identities | suppliers | producers | categories | materials
     */
    @PostMapping("/sync/{syncType}")
    public AjaxResult sync(@PathVariable String syncType)
    {
        return msunHisMasterSyncService.sync(syncType);
    }
}
