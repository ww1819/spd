package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdWarehouseCategoryChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库房分类档案变更记录
 */
public interface FdWarehouseCategoryChangeLogMapper {

    int insert(FdWarehouseCategoryChangeLog record);

    List<FdWarehouseCategoryChangeLog> selectByEntityId(@Param("warehouseCategoryId") Long warehouseCategoryId);
}
