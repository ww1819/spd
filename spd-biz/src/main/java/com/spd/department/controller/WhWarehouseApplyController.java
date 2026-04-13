package com.spd.department.controller;

import java.math.BigDecimal;
import java.util.List;

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
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.service.IWhWarehouseApplyService;

/**
 * 仓库申请单（科室申领审核按仓拆分）
 */
@RestController
@RequestMapping("/department/whWarehouseApply")
public class WhWarehouseApplyController extends BaseController {

    @Autowired
    private IWhWarehouseApplyService whWarehouseApplyService;

    @PreAuthorize("@ss.hasPermi('department:whWarehouseApply:list') || @ss.hasPermi('department:dApply:list') || @ss.hasPermi('outWarehouse:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(WhWarehouseApply query) {
        startPage();
        List<WhWarehouseApply> list = whWarehouseApplyService.selectWhWarehouseApplyList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:whWarehouseApply:query') || @ss.hasPermi('department:dApply:query') || @ss.hasPermi('outWarehouse:apply:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(whWarehouseApplyService.selectWhWarehouseApplyById(id));
    }

    /**
     * 整单作废（无关联出库时）
     */
    @PreAuthorize("@ss.hasPermi('department:whWarehouseApply:voidWhole') || @ss.hasPermi('department:dApply:edit') || @ss.hasPermi('outWarehouse:apply:edit')")
    @Log(title = "库房申请单整单作废", businessType = BusinessType.UPDATE)
    @PostMapping("/voidWhole")
    public AjaxResult voidWhole(@RequestBody JSONObject body) {
        String id = body != null ? body.getString("id") : null;
        String reason = body != null ? body.getString("reason") : null;
        whWarehouseApplyService.voidWholeWhWarehouseApply(id, StringUtils.isEmpty(reason) ? "" : reason);
        return success();
    }

    /**
     * 按明细作废（增加作废数量）
     */
    @PreAuthorize("@ss.hasPermi('department:whWarehouseApply:voidEntry') || @ss.hasPermi('department:dApply:edit') || @ss.hasPermi('outWarehouse:apply:edit')")
    @Log(title = "库房申请单明细作废", businessType = BusinessType.UPDATE)
    @PostMapping("/voidEntry")
    public AjaxResult voidEntry(@RequestBody JSONObject body) {
        if (body == null) {
            return error("参数不能为空");
        }
        String whApplyId = body.getString("whApplyId");
        String entryId = body.getString("entryId");
        Object q = body.get("voidQty");
        BigDecimal voidQty = q == null ? null : new BigDecimal(String.valueOf(q));
        String reason = body.getString("reason");
        whWarehouseApplyService.voidWhWarehouseApplyEntryLine(whApplyId, entryId, voidQty,
            StringUtils.isEmpty(reason) ? "" : reason);
        return success();
    }
}
