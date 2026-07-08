package com.spd.gz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzStockQueryParam;
import com.spd.gz.domain.vo.GzDepInventoryCodeRepairVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceResultVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceVo;
import com.spd.gz.domain.vo.GzStockQueryEntryVo;

public interface GzStockQueryMapper
{
    List<GzStockQueryEntryVo> selectOutboundRefundEntryList(GzStockQueryParam param);

    long countOutboundRefundEntryList(GzStockQueryParam param);

    List<GzDepotInventoryTraceVo> selectDepotInventoryTraceByInHospitalCode(String inHospitalCode);

    List<GzDepotInventoryTraceVo> selectSuspectBatchDeductionByInHospitalCode(String inHospitalCode);

    GzDepotInventoryTraceResultVo selectDepotInventorySnapshotByInHospitalCode(String inHospitalCode);

    GzDepInventoryCodeRepairVo selectDepInventoryCodeRepairCandidate(
        @Param("inHospitalCode") String inHospitalCode,
        @Param("shipmentNo") String shipmentNo);

    int updateDepFlowInHospitalCode(
        @Param("wrongCode") String wrongCode,
        @Param("correctCode") String correctCode);

    int updateWhFlowInHospitalCodeForShipment(
        @Param("wrongCode") String wrongCode,
        @Param("correctCode") String correctCode,
        @Param("shipmentNo") String shipmentNo);
}
