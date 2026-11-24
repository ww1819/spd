package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdWarehouseChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库档案变更记录
 */
public interface FdWarehouseChangeLogMapper {

    int insert(FdWarehouseChangeLog record);

    List<FdWarehouseChangeLog> selectByEntityId(@Param("warehouseId") Long warehouseId);
}
