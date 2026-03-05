package com.spd.system.service;

import java.util.List;

/**
 * 设备用户权限（菜单/仓库/科室）业务层
 */
public interface ISbUserPermissionService {

  List<String> selectMenuIdsByUserId(Long userId, String customerId);

  int saveUserMenus(Long userId, String customerId, String[] menuIds);

  List<Long> selectWarehouseIdsByUserId(Long userId, String customerId);

  int saveUserWarehouses(Long userId, String customerId, Long[] warehouseIds);

  List<Long> selectDeptIdsByUserId(Long userId, String customerId);

  int saveUserDepts(Long userId, String customerId, Long[] deptIds);
}
