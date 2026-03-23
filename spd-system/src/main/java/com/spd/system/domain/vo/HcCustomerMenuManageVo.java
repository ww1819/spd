package com.spd.system.domain.vo;

/**
 * 耗材客户菜单功能管理列表 VO
 */
public class HcCustomerMenuManageVo {

  private String tenantId;
  private Long menuId;
  private String menuName;
  private String status;

  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public String getMenuName() { return menuName; }
  public void setMenuName(String menuName) { this.menuName = menuName; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
