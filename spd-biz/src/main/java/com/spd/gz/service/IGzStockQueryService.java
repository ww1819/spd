package com.spd.gz.service;

import java.util.List;
import com.spd.common.core.page.TotalInfo;
import com.spd.gz.domain.GzStockQueryParam;
import com.spd.gz.domain.vo.GzDepotInventoryTraceResultVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceVo;
import com.spd.gz.domain.vo.GzStockQueryEntryVo;

public interface IGzStockQueryService
{
    List<GzStockQueryEntryVo> selectOutboundRefundEntryList(GzStockQueryParam param);

    long countOutboundRefundEntryList(GzStockQueryParam param);

    TotalInfo sumOutboundRefundEntryList(GzStockQueryParam param);

    List<GzStockQueryEntryVo> selectInboundRefundEntryList(GzStockQueryParam param);

    long countInboundRefundEntryList(GzStockQueryParam param);

    TotalInfo sumInboundRefundEntryList(GzStockQueryParam param);

    List<GzStockQueryEntryVo> selectFollowEntryList(GzStockQueryParam param);

    long countFollowEntryList(GzStockQueryParam param);

    TotalInfo sumFollowEntryList(GzStockQueryParam param);

    List<GzDepotInventoryTraceVo> selectDepotInventoryTrace(String inHospitalCode);

    GzDepotInventoryTraceResultVo buildDepotInventoryTraceResult(String inHospitalCode);

    String repairDepInventoryInHospitalCode(String inHospitalCode, String shipmentNo);
}
