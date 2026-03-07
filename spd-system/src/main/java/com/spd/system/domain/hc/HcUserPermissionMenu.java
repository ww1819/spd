package com.spd.system.domain.hc;

/**
 * 耗材用户菜单权限 hc_user_permission_menu
 */
public class HcUserPermissionMenu {

  private String id;
  private Long userId;
  private String tenantId;
  private Long menuId;
  private String createBy;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
}
