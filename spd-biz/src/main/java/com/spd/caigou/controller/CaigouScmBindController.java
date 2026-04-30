package com.spd.caigou.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.service.ISpdScmBindService;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;

/**
 * 采购侧：当前租户与云平台医院编码、供应商编码绑定维护
 */
@RestController
@RequestMapping("/caigou/scmBind")
public class CaigouScmBindController extends BaseController
{
    @Autowired
    private ISpdScmBindService spdScmBindService;

    @PreAuthorize("@ss.hasPermi('caigou:dingdan:list')")
    @GetMapping("/tenant")
    public AjaxResult getTenantBind()
    {
        return success(spdScmBindService.getTenantBind());
    }

    @Log(title = "云平台医院编码绑定", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('caigou:dingdan:edit')")
    @PutMapping("/tenant")
    public AjaxResult saveTenantBind(@RequestBody Map<String, Object> body)
    {
        String code = body.get("scmHospitalCode") != null ? String.valueOf(body.get("scmHospitalCode")) : null;
        String remark = body.get("remark") != null ? String.valueOf(body.get("remark")) : null;
        spdScmBindService.saveTenantBind(code, remark);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('caigou:dingdan:list')")
    @GetMapping("/supplier/list")
    public AjaxResult listSupplierBinds()
    {
        List<SpdScmSupplierBind> list = spdScmBindService.listSupplierBinds();
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('caigou:dingdan:list')")
    @GetMapping("/supplier/{supplierId}")
    public AjaxResult getSupplierBind(@PathVariable("supplierId") Long supplierId)
    {
        return success(spdScmBindService.getSupplierBind(supplierId));
    }

    @Log(title = "云平台供应商编码绑定", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('caigou:dingdan:edit')")
    @PutMapping("/supplier")
    public AjaxResult saveSupplierBind(@RequestBody Map<String, Object> body)
    {
        Object sid = body.get("supplierId");
        if (sid == null)
        {
            return error("supplierId 不能为空");
        }
        Long supplierId = sid instanceof Number ? ((Number) sid).longValue() : Long.parseLong(String.valueOf(sid));
        String code = body.get("scmSupplierCode") != null ? String.valueOf(body.get("scmSupplierCode")) : null;
        String remark = body.get("remark") != null ? String.valueOf(body.get("remark")) : null;
        if (StringUtils.isEmpty(StringUtils.trim(code)))
        {
            return error("scmSupplierCode 不能为空");
        }
        spdScmBindService.saveSupplierBind(supplierId, code, remark);
        return success();
    }
}
