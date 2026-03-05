package com.spd.system.domain.vo;

/**
 * 客户菜单功能管理列表 VO（含菜单名称与状态）
 */
public class SbCustomerMenuManageVo {

  private String customerId;
  private String menuId;
  private String menuName;
  /** 0启用 1停用 */
  private String status;

  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getMenuId() { return menuId; }
  public void setMenuId(String menuId) { this.menuId = menuId; }
  public String getMenuName() { return menuName; }
  public void setMenuName(String menuName) { this.menuName = menuName; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
