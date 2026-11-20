package com.spd.gz.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzStockQueryParam;
import com.spd.gz.domain.vo.GzDepotInventoryTraceResultVo;
import com.spd.gz.domain.vo.GzStockQueryEntryVo;
import com.spd.gz.service.IGzStockQueryService;

/**
 * 高值库存查询（备货入/退货、出/退库、跟台明细、院内码追溯）
 * 列表+合计实现与出/退库查询 CTKList 一致：startPage → list → PageInfo.total → sum → getDataTable。
 */
@RestController
@RequestMapping("/gz/stockQuery")
public class GzStockQueryController extends BaseController
{
    @Autowired
    private IGzStockQueryService gzStockQueryService;

    @PreAuthorize("@ss.hasPermi('gz:stockQuery:list')")
    @GetMapping("/outboundRefund/entry/list")
    public TableDataInfo listOutboundRefundEntries(GzStockQueryParam param)
    {
        return queryEntryPage(param,
            gzStockQueryService::selectOutboundRefundEntryList,
            gzStockQueryService::sumOutboundRefundEntryList);
    }

    @PreAuthorize("@ss.hasPermi('gz:stockQuery:list')")
    @GetMapping("/inboundRefund/entry/list")
    public TableDataInfo listInboundRefundEntries(GzStockQueryParam param)
    {
        return queryEntryPage(param,
            gzStockQueryService::selectInboundRefundEntryList,
            gzStockQueryService::sumInboundRefundEntryList);
    }

    @PreAuthorize("@ss.hasPermi('gz:stockQuery:list')")
    @GetMapping("/follow/entry/list")
    public TableDataInfo listFollowEntries(GzStockQueryParam param)
    {
        return queryEntryPage(param,
            gzStockQueryService::selectFollowEntryList,
            gzStockQueryService::sumFollowEntryList);
    }

    /**
     * 与 {@code StkIoRThBillController#CTKList} 相同节奏：
     * startPage → 查明细 → PageInfo 取 total → 查合计 → 普通 ArrayList 回传。
     */
    private TableDataInfo queryEntryPage(GzStockQueryParam param,
        Function<GzStockQueryParam, List<GzStockQueryEntryVo>> query,
        Function<GzStockQueryParam, TotalInfo> summer)
    {
        if (param == null) {
            param = new GzStockQueryParam();
        }
        // 与出/退库一致：不做分支限流，交给 PageHelper 分页+count
        param.setBranchFetchLimit(null);
        startPage();
        List<GzStockQueryEntryVo> pageRows = query.apply(param);
        long total = new PageInfo<>(pageRows).getTotal();
        List<GzStockQueryEntryVo> rows = toPlainList(pageRows);

        TotalInfo totalInfo = summer.apply(param);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        return getDataTable(rows, totalInfo, total);
    }

    private static List<GzStockQueryEntryVo> toPlainList(List<GzStockQueryEntryVo> list)
    {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:stockQuery:list')")
    @GetMapping("/depotInventory/trace")
    public AjaxResult traceDepotInventory(@RequestParam("inHospitalCode") String inHospitalCode)
    {
        if (StringUtils.isEmpty(inHospitalCode)) {
            return error("院内码不能为空");
        }
        GzDepotInventoryTraceResultVo result = gzStockQueryService.buildDepotInventoryTraceResult(inHospitalCode.trim());
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('gz:stockQuery:list')")
    @PostMapping("/depotInventory/repairInHospitalCode")
    public AjaxResult repairDepInventoryInHospitalCode(
        @RequestParam("inHospitalCode") String inHospitalCode,
        @RequestParam(value = "shipmentNo", required = false) String shipmentNo)
    {
        if (StringUtils.isEmpty(inHospitalCode)) {
            return error("院内码不能为空");
        }
        return success(gzStockQueryService.repairDepInventoryInHospitalCode(inHospitalCode.trim(), shipmentNo));
    }
}
