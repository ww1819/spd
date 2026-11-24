package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdUnitChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单位档案变更记录
 */
public interface FdUnitChangeLogMapper {

    int insert(FdUnitChangeLog record);

    List<FdUnitChangeLog> selectByEntityId(@Param("unitId") Long unitId);
}
