package com.spd.gz.mapper;

import java.util.List;

import com.spd.gz.domain.GzShipmentEntryRef;
import org.apache.ibatis.annotations.Param;

public interface GzShipmentEntryRefMapper
{
    int batchInsert(@Param("list") List<GzShipmentEntryRef> list);

    int deleteByParenShipmentId(@Param("parenId") Long parenId);
}
