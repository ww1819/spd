package com.spd.foundation.mapper;

import java.util.List;

import com.spd.foundation.domain.SpdFoundationDataSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SpdFoundationDataSnapshotMapper
{
    int insert(SpdFoundationDataSnapshot row);

    List<SpdFoundationDataSnapshot> selectList(@Param("tenantId") String tenantId,
        @Param("entityType") String entityType,
        @Param("entityId") String entityId);
}
