package com.spd.system.service;

/**
 * 租户管理员（super 工作组 / super_01）与客户菜单权限（hc_customer_menu）对齐，
 * 避免平台为客户开通新功能后租户管理员侧栏仍不可见。
 */
public interface ITenantAdminMenuSyncService {

  /**
   * 将租户 {@code hc_customer_menu} 中已开通且未暂停的菜单，补充写入 super 岗位（sys_post_menu）
   * 与 super_01 用户（sys_user_menu）；仅追加缺失项，不删除已有或客户已收回的授权。
   *
   * @param tenantId 租户 customer_id
   * @return 本次新增的菜单关联条数（岗位 + 用户）
   */
  int syncHcCustomerMenusToTenantAdmins(String tenantId);
}
