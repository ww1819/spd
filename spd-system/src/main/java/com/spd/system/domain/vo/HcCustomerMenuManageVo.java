package com.spd.system.domain.vo;

/**
 * 耗材客户菜单功能管理列表 VO
 */
public class HcCustomerMenuManageVo {

  private String tenantId;
  private Long menuId;
  private Long parentId;
  private String menuName;
  /** M 目录 / C 菜单 / F 按钮 */
  private String menuType;
  private Integer orderNum;
  private String status;
  /** 是否在 hc_customer_menu 中已分配；仅为拼树补上的祖先目录为 false */
  private Boolean assigned;

  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public Long getParentId() { return parentId; }
  public void setParentId(Long parentId) { this.parentId = parentId; }
  public String getMenuName() { return menuName; }
  public void setMenuName(String menuName) { this.menuName = menuName; }
  public String getMenuType() { return menuType; }
  public void setMenuType(String menuType) { this.menuType = menuType; }
  public Integer getOrderNum() { return orderNum; }
  public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getAssigned() { return assigned; }
  public void setAssigned(Boolean assigned) { this.assigned = assigned; }
}
