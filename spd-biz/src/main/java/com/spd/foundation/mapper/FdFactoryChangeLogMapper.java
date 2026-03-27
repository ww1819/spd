package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdFactoryChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产厂家档案变更记录
 */
public interface FdFactoryChangeLogMapper {

    int insert(FdFactoryChangeLog record);

    List<FdFactoryChangeLog> selectByFactoryId(@Param("factoryId") Long factoryId);
}
