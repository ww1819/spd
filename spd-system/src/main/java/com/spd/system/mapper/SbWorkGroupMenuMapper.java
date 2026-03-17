package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbWorkGroupMenu;

/**
 * 工作组菜单权限 sb_work_group_menu 数据层
 */
public interface SbWorkGroupMenuMapper {

  List<String> selectMenuIdsByGroupId(String groupId);

  List<String> selectMenuIdsByGroupIdAndCustomerId(@Param("groupId") String groupId, @Param("customerId") String customerId);

  int deleteByGroupId(@Param("groupId") String groupId, @Param("deleteBy") String deleteBy);

  int insert(SbWorkGroupMenu row);

  int batchInsert(List<SbWorkGroupMenu> list);
}
