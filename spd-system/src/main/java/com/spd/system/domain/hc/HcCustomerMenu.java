package com.spd.system.domain.hc;

/**
 * 耗材客户菜单权限 hc_customer_menu
 */
public class HcCustomerMenu {

  private String tenantId;
  private Long menuId;
  private String status;
  private String isEnabled;
  private String createBy;
  private java.util.Date createTime;

  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getIsEnabled() { return isEnabled; }
  public void setIsEnabled(String isEnabled) { this.isEnabled = isEnabled; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
  public java.util.Date getCreateTime() { return createTime; }
  public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }
}
