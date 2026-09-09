package com.spd.foundation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.SpdScmMaterialPushFieldCfg;
import com.spd.foundation.service.ISpdScmMaterialArchiveService;

/**
 * 院内产品档案 ↔ 供应链档案（推送 / 同步 / 应用 / 字段配置）
 */
@RestController
@RequestMapping("/foundation/scmMaterialArchive")
public class SpdScmMaterialArchiveController extends BaseController
{
    @Autowired
    private ISpdScmMaterialArchiveService spdScmMaterialArchiveService;

    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:list')")
    @GetMapping("/listPushCandidates")
    public AjaxResult listPushCandidates(@RequestParam(value = "keyword", required = false) String keyword)
    {
        return success(spdScmMaterialArchiveService.listLocalMaterialsForPush(keyword));
    }

    @Log(title = "产品档案推送供应链", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:push')")
    @PostMapping("/push")
    public AjaxResult push(@RequestBody Map<String, Object> body)
    {
        List<Long> ids = parseLongList(body != null ? body.get("materialIds") : null);
        if (ids.isEmpty())
        {
            return error("materialIds 不能为空");
        }
        return success(spdScmMaterialArchiveService.pushSelected(ids));
    }

    @Log(title = "同步供应链产品档案", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:sync')")
    @PostMapping("/sync")
    public AjaxResult sync(@RequestBody(required = false) Map<String, Object> body)
    {
        String keyword = null;
        if (body != null && body.get("keyword") != null)
        {
            keyword = String.valueOf(body.get("keyword"));
        }
        return success(spdScmMaterialArchiveService.syncFromPlatform(keyword));
    }

    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:list')")
    @GetMapping("/listMirror")
    public AjaxResult listMirror(@RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "applyStatus", required = false) String applyStatus)
    {
        return success(spdScmMaterialArchiveService.listMirrors(keyword, applyStatus));
    }

    @Log(title = "应用供应链产品档案", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:apply')")
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody Map<String, Object> body)
    {
        if (body == null || body.get("mirrorId") == null || StringUtils.isEmpty(String.valueOf(body.get("mirrorId"))))
        {
            return error("mirrorId 不能为空");
        }
        String mirrorId = String.valueOf(body.get("mirrorId")).trim();
        List<String> fields = parseStringList(body.get("fields"));
        return success(spdScmMaterialArchiveService.apply(mirrorId, fields));
    }

    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:config')")
    @GetMapping("/fieldCfg")
    public AjaxResult fieldCfg()
    {
        return success(spdScmMaterialArchiveService.listFieldCfg());
    }

    @Log(title = "产品档案推送字段配置", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('foundation:scmMaterial:config')")
    @PutMapping("/fieldCfg")
    public AjaxResult saveFieldCfg(@RequestBody List<SpdScmMaterialPushFieldCfg> list)
    {
        spdScmMaterialArchiveService.saveFieldCfg(list);
        return success();
    }

    @SuppressWarnings("unchecked")
    private static List<Long> parseLongList(Object raw)
    {
        List<Long> out = new ArrayList<>();
        if (!(raw instanceof List))
        {
            return out;
        }
        for (Object o : (List<Object>) raw)
        {
            if (o == null)
            {
                continue;
            }
            if (o instanceof Number)
            {
                out.add(((Number) o).longValue());
            }
            else
            {
                String s = String.valueOf(o).trim();
                if (!s.isEmpty())
                {
                    out.add(Long.parseLong(s));
                }
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<String> parseStringList(Object raw)
    {
        List<String> out = new ArrayList<>();
        if (!(raw instanceof List))
        {
            return out;
        }
        for (Object o : (List<Object>) raw)
        {
            if (o != null && StringUtils.isNotEmpty(String.valueOf(o)))
            {
                out.add(String.valueOf(o).trim());
            }
        }
        return out;
    }
}
