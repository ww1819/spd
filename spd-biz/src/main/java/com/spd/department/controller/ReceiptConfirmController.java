package com.spd.department.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.alibaba.fastjson2.JSONObject;

/**
 * 收货确认Controller
 * 
 * @author spd
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/department/receiptConfirm")
public class ReceiptConfirmController extends BaseController
{
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询收货确认列表（只查询已审核的出库单）
     */
    @PreAuthorize("@ss.hasPermi('department:receiptConfirm:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        // 只查询出库单（billType=201）且已审核（billStatus=2）
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(2);
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 导出收货确认列表
     */
    @PreAuthorize("@ss.hasPermi('department:receiptConfirm:export')")
    @Log(title = "收货确认", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(2);
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "收货确认数据");
    }

    /**
     * 获取收货确认详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:receiptConfirm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 批量确认收货
     */
    @PreAuthorize("@ss.hasPermi('department:receiptConfirm:confirm')")
    @Log(title = "收货确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public AjaxResult confirm(@RequestBody JSONObject json)
    {
        String ids = json.getString("ids");
        String confirmBy = json.getString("confirmBy");
        int result = stkIoBillService.confirmReceipt(ids, confirmBy);
        return toAjax(result);
    }
}
