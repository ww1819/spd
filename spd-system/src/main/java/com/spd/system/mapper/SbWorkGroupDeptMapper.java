package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbWorkGroupDept;

/**
 * 工作组科室权限 sb_work_group_dept 数据层
 */
public interface SbWorkGroupDeptMapper {

  List<Long> selectDeptIdsByGroupId(String groupId);

  int deleteByGroupId(@Param("groupId") String groupId, @Param("deleteBy") String deleteBy);

  int insert(SbWorkGroupDept row);

  int batchInsert(List<SbWorkGroupDept> list);
}
