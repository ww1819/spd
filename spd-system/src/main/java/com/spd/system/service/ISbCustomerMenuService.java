package com.spd.system.service;

import java.util.List;

/**
 * 设备系统客户菜单权限 服务接口
 */
public interface ISbCustomerMenuService {

  /**
   * 查询客户已分配的菜单ID列表
   */
  List<String> selectMenuIdsByCustomerId(String customerId);

  /**
   * 保存客户菜单权限（覆盖）
   */
  int saveCustomerMenus(String customerId, String[] menuIds);
}
