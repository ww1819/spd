package com.spd.gz.mapper;

import java.util.List;

import com.spd.gz.domain.GzOrderEntryCodeRef;
import org.apache.ibatis.annotations.Param;

public interface GzOrderEntryCodeRefMapper
{
    int batchInsert(@Param("list") List<GzOrderEntryCodeRef> list);

    int deleteByTgtMainIdAndKind(@Param("tgtMainId") String tgtMainId, @Param("tgtBillKind") String tgtBillKind);
}
