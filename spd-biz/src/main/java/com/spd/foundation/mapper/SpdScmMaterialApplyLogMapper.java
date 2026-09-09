package com.spd.foundation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spd.foundation.domain.SpdScmMaterialApplyLog;

@Mapper
@Repository
public interface SpdScmMaterialApplyLogMapper
{
    int insert(SpdScmMaterialApplyLog row);
}
