package com.spd.warehouse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.service.IStkBatchService;

/**
 * 批次追溯 Controller
 */
@RestController
@RequestMapping("/warehouse/batch")
public class StkBatchController extends BaseController
{
    @Autowired
    private IStkBatchService stkBatchService;

    /**
     * 查询批次列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkBatch stkBatch)
    {
        startPage();
        List<StkBatch> list = stkBatchService.selectStkBatchList(stkBatch);
        return getDataTable(list);
    }

    /**
     * 查询批次详情
     */
    @PreAuthorize("@ss.hasPermi('warehouse:batch:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkBatchService.selectStkBatchById(id));
    }
}

