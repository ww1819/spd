package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdLocationChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 货位档案变更记录
 */
public interface FdLocationChangeLogMapper {

    int insert(FdLocationChangeLog record);

    List<FdLocationChangeLog> selectByEntityId(@Param("locationId") Long locationId);
}
