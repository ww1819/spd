package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbWorkGroup;

/**
 * 设备系统工作组表 sb_work_group 数据层
 */
public interface SbWorkGroupMapper {

  int insertSbWorkGroup(SbWorkGroup record);

  List<SbWorkGroup> selectListByCustomerId(String customerId);

  SbWorkGroup selectByGroupId(String groupId);

  int updateSbWorkGroup(SbWorkGroup record);

  int deleteByGroupId(@Param("groupId") String groupId, @Param("deleteBy") String deleteBy);
}
