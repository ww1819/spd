package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbWorkGroupUser;

/**
 * 工作组与用户关联 sb_work_group_user 数据层
 */
public interface SbWorkGroupUserMapper {

  int insertSbWorkGroupUser(SbWorkGroupUser record);

  List<Long> selectUserIdsByGroupId(String groupId);

  List<String> selectGroupIdsByUserId(@Param("userId") Long userId, @Param("customerId") String customerId);

  int deleteByGroupIdAndUserId(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("deleteBy") String deleteBy);

  int countByGroupIdAndUserId(@Param("groupId") String groupId, @Param("userId") Long userId);
}
