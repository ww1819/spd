package com.spd.gz.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzStockQueryParam;
import com.spd.gz.domain.vo.GzDepotInventoryTraceResultVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceVo;
import com.spd.gz.domain.vo.GzStockQueryEntryVo;
import com.spd.gz.service.IGzStockQueryService;

/**
 * 高值库存查询（备货出/退库明细、院内码追溯）
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
        startPage();
        List<GzStockQueryEntryVo> list = gzStockQueryService.selectOutboundRefundEntryList(param);
        return getDataTable(list);
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
