package com.spd.gz.mapper;

import java.util.List;

import com.spd.gz.domain.GzRefundGoodsEntryRef;
import org.apache.ibatis.annotations.Param;

public interface GzRefundGoodsEntryRefMapper
{
    int batchInsert(@Param("list") List<GzRefundGoodsEntryRef> list);

    int deleteByParenRefundGoodsId(@Param("parenId") Long parenId);

    int deleteByTgtMainIdAndKind(@Param("tgtMainId") String tgtMainId, @Param("tgtBillKind") String tgtBillKind);
}
