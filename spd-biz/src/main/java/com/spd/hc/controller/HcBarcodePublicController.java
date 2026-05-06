package com.spd.hc.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.hc.domain.HcBarcodeFlow;
import com.spd.hc.domain.HcBarcodeMaster;
import com.spd.hc.mapper.HcBarcodeTraceMapper;

/**
 * 高低值条码低敏查询（与菜单 hc:barcode:public:* 对齐）
 */
@RestController
@RequestMapping("/hc/barcode/public")
public class HcBarcodePublicController extends BaseController {

    @Autowired
    private HcBarcodeTraceMapper hcBarcodeTraceMapper;

    @PreAuthorize("@ss.hasPermi('hc:barcode:public:ownership:list')")
    @GetMapping("/ownership/list")
    public TableDataInfo ownershipList(HcBarcodeMaster query) {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        startPage();
        List<HcBarcodeMaster> list = hcBarcodeTraceMapper.selectHcBarcodeMasterList(tenantId,
            query != null ? query.getBarcodeValue() : null,
            query != null ? query.getValueLevel() : null,
            query != null ? query.getBusinessTypeCode() : null,
            query != null ? query.getBillNo() : null,
            query != null ? query.getMaterialName() : null);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hc:barcode:public:ownership:query')")
    @GetMapping("/ownership/{id}")
    public AjaxResult ownershipDetail(@PathVariable("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return error("主键不能为空");
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        HcBarcodeMaster row = hcBarcodeTraceMapper.selectHcBarcodeMasterById(tenantId, id);
        if (row == null) {
            return error("记录不存在或无权访问");
        }
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('hc:barcode:public:ownership:export')")
    @Log(title = "条码归属", businessType = BusinessType.EXPORT)
    @PostMapping("/ownership/export")
    public void exportOwnership(HttpServletResponse response, HcBarcodeMaster query) {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        clearPage();
        List<HcBarcodeMaster> list = hcBarcodeTraceMapper.selectHcBarcodeMasterList(tenantId,
            query != null ? query.getBarcodeValue() : null,
            query != null ? query.getValueLevel() : null,
            query != null ? query.getBusinessTypeCode() : null,
            query != null ? query.getBillNo() : null,
            query != null ? query.getMaterialName() : null);
        ExcelUtil<HcBarcodeMaster> util = new ExcelUtil<HcBarcodeMaster>(HcBarcodeMaster.class);
        util.exportExcel(response, list, "条码归属");
    }

    @PreAuthorize("@ss.hasPermi('hc:barcode:public:circulation:list')")
    @GetMapping("/circulation/list")
    public TableDataInfo circulationList(HcBarcodeFlow query) {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        startPage();
        List<HcBarcodeFlow> list = hcBarcodeTraceMapper.selectHcBarcodeFlowList(tenantId,
            query != null ? query.getBarcodeValue() : null,
            query != null ? query.getHcBarcodeMasterId() : null);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hc:barcode:public:circulation:export')")
    @Log(title = "条码流通", businessType = BusinessType.EXPORT)
    @PostMapping("/circulation/export")
    public void exportCirculation(HttpServletResponse response, HcBarcodeFlow query) {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        clearPage();
        List<HcBarcodeFlow> list = hcBarcodeTraceMapper.selectHcBarcodeFlowList(tenantId,
            query != null ? query.getBarcodeValue() : null,
            query != null ? query.getHcBarcodeMasterId() : null);
        ExcelUtil<HcBarcodeFlow> util = new ExcelUtil<HcBarcodeFlow>(HcBarcodeFlow.class);
        util.exportExcel(response, list, "条码流通");
    }
}
