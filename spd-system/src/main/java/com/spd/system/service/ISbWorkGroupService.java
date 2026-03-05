package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.SbWorkGroup;

/**
 * 设备系统工作组 业务层
 */
public interface ISbWorkGroupService {

  List<SbWorkGroup> selectListByCustomerId(String customerId);

  SbWorkGroup selectByGroupId(String groupId);

  int insertSbWorkGroup(SbWorkGroup group);

  int updateSbWorkGroup(SbWorkGroup group);

  int deleteByGroupId(String groupId);

  List<Long> selectUserIdsByGroupId(String groupId);

  int addUsersToGroup(String groupId, Long[] userIds);

  int removeUserFromGroup(String groupId, Long userId);

  List<String> selectMenuIdsByGroupId(String groupId);

  int saveGroupMenus(String groupId, String customerId, String[] menuIds);

  List<Long> selectWarehouseIdsByGroupId(String groupId);

  int saveGroupWarehouses(String groupId, String customerId, Long[] warehouseIds);

  List<Long> selectDeptIdsByGroupId(String groupId);

  int saveGroupDepts(String groupId, String customerId, Long[] deptIds);

  /** 将工作组的菜单、仓库、科室权限同步到组内所有用户（写入用户权限表） */
  int syncToGroupUsers(String groupId);
}
