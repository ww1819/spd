package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbUserPermissionWarehouse;

/**
 * 设备用户仓库权限 sb_user_permission_warehouse 数据层
 */
public interface SbUserPermissionWarehouseMapper {

  List<Long> selectWarehouseIdsByUserId(@Param("userId") Long userId, @Param("customerId") String customerId);

  int deleteByUserId(@Param("userId") Long userId, @Param("deleteBy") String deleteBy);

  int insert(SbUserPermissionWarehouse row);

  int batchInsert(List<SbUserPermissionWarehouse> list);
}
