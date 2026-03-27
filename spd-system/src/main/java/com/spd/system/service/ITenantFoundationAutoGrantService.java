package com.spd.system.service;

/**
 * 租户基础资料（科室/仓库）新增后，自动授权给租户默认管理员 super_01 与管理员组岗位（post_code=super），
 * 避免「有数据但用户/岗位无关联」导致列表与业务权限异常。
 */
public interface ITenantFoundationAutoGrantService {

  /**
   * 将新科室授权给 super_01 与 super 岗位（sys_user_department、sys_post_department）。
   */
  void grantDepartmentToTenantAdmins(String tenantId, Long departmentId);

  /**
   * 将新仓库授权给 super_01 与 super 岗位（sys_user_warehouse、sys_post_warehouse）。
   */
  void grantWarehouseToTenantAdmins(String tenantId, Long warehouseId);
}
