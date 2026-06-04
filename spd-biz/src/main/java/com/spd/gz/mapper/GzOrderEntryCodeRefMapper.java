package com.spd.gz.mapper;

import java.util.List;
import java.util.Map;

import com.spd.gz.domain.GzOrderEntryCodeRef;
import org.apache.ibatis.annotations.Param;

public interface GzOrderEntryCodeRefMapper
{
    int batchInsert(@Param("list") List<GzOrderEntryCodeRef> list);

    int deleteByTgtMainIdAndKind(@Param("tgtMainId") String tgtMainId, @Param("tgtBillKind") String tgtBillKind);

    List<String> selectSrcAcceptanceIdsByTgtMainId(@Param("tgtMainId") String tgtMainId, @Param("tgtBillKind") String tgtBillKind);

    List<Map<String, Object>> selectOccupiedByBarcodeLineIds(
        @Param("barcodeLineIds") List<String> barcodeLineIds,
        @Param("tgtBillKind") String tgtBillKind,
        @Param("excludeTgtMainId") String excludeTgtMainId);
}
