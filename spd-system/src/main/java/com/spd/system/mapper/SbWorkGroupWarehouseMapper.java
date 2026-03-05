package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbWorkGroupWarehouse;

/**
 * 工作组仓库权限 sb_work_group_warehouse 数据层
 */
public interface SbWorkGroupWarehouseMapper {

  List<Long> selectWarehouseIdsByGroupId(String groupId);

  int deleteByGroupId(@Param("groupId") String groupId, @Param("deleteBy") String deleteBy);

  int insert(SbWorkGroupWarehouse row);

  int batchInsert(List<SbWorkGroupWarehouse> list);
}
