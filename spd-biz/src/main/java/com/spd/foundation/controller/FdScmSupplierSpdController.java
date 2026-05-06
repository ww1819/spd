package com.spd.foundation.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.service.IFdScmSupplierSpdService;
import com.spd.foundation.service.IFdSupplierService;

/**
 * 平台供应商信息、下载、与院内供应商补全
 */
@RestController
@RequestMapping("/foundation/scmSupplier")
public class FdScmSupplierSpdController extends BaseController
{
    @Autowired
    private IFdScmSupplierSpdService fdScmSupplierSpdService;

    @Autowired
    private IFdSupplierService fdSupplierService;

    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:list')")
    @GetMapping("/scmList")
    public AjaxResult listScmSuppliers()
    {
        List<Map<String, Object>> list = fdScmSupplierSpdService.listScmSuppliersForTenantHospital();
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:query')")
    @GetMapping("/profile/{scmSupplierCode}")
    public AjaxResult profile(@PathVariable("scmSupplierCode") String scmSupplierCode)
    {
        return success(fdScmSupplierSpdService.loadScmSupplierProfile(scmSupplierCode));
    }

    /**
     * 院内供应商 + 平台档案（用于信息补全页）
     */
    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:query')")
    @GetMapping("/linked/{spdSupplierId}")
    public AjaxResult linked(@PathVariable("spdSupplierId") Long spdSupplierId)
    {
        FdSupplier fd = fdSupplierService.selectFdSupplierById(spdSupplierId);
        if (fd == null)
        {
            return error("院内供应商不存在");
        }
        SecurityUtils.ensureTenantAccess(fd.getTenantId());
        Map<String, Object> out = new HashMap<>();
        out.put("fdSupplier", fd);
        String code = fdScmSupplierSpdService.resolveScmSupplierCode(spdSupplierId);
        out.put("scmSupplierCode", code);
        if (StringUtils.isNotEmpty(code))
        {
            out.put("scmProfile", fdScmSupplierSpdService.loadScmSupplierProfile(code));
        }
        else
        {
            out.put("scmProfile", null);
        }
        return success(out);
    }

    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:export')")
    @GetMapping("/export/{spdSupplierId}")
    public void exportJson(@PathVariable("spdSupplierId") Long spdSupplierId, HttpServletResponse response) throws IOException
    {
        JSONObject payload = fdScmSupplierSpdService.buildExportPayload(spdSupplierId);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"scm_supplier_export_" + spdSupplierId + ".json\"");
        response.getWriter().write(payload.toJSONString());
    }

    @Log(title = "院内供应商补全（平台主数据）", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('foundation:scmSupplier:merge')")
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody Map<String, Object> body)
    {
        Object sid = body.get("spdSupplierId");
        if (sid == null)
        {
            return error("spdSupplierId 不能为空");
        }
        long id = sid instanceof Number ? ((Number) sid).longValue() : Long.parseLong(String.valueOf(sid));
        boolean overwrite = Boolean.TRUE.equals(body.get("overwriteNonEmpty"));
        fdScmSupplierSpdService.mergeScmIntoFdSupplier(id, overwrite);
        return success();
    }
}
