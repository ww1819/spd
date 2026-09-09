package com.spd.foundation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spd.foundation.domain.SpdScmMaterialSyncBatch;

@Mapper
@Repository
public interface SpdScmMaterialSyncBatchMapper
{
    int insert(SpdScmMaterialSyncBatch row);
}
