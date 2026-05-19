package com.spd.system.domain.dto;

import java.util.List;

/**
 * 菜单管理-批量赋权请求体
 */
public class MenuBatchGrantBody {

  /** 待赋权菜单 ID（与前端勾选一致，不扩展父级或子孙） */
  private List<Long> menuIds;

  /** 目标租户 customer_id 列表 */
  private List<String> customerIds;

  /** 为租户写入 hc_customer_menu */
  private Boolean grantTenant;

  /** 为该租户下全部工作组写入 sys_post_menu */
  private Boolean grantAllPosts;

  /** 为该租户下全部用户写入 sys_user_menu */
  private Boolean grantAllUsers;

  public List<Long> getMenuIds() {
    return menuIds;
  }

  public void setMenuIds(List<Long> menuIds) {
    this.menuIds = menuIds;
  }

  public List<String> getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(List<String> customerIds) {
    this.customerIds = customerIds;
  }

  public Boolean getGrantTenant() {
    return grantTenant;
  }

  public void setGrantTenant(Boolean grantTenant) {
    this.grantTenant = grantTenant;
  }

  public Boolean getGrantAllPosts() {
    return grantAllPosts;
  }

  public void setGrantAllPosts(Boolean grantAllPosts) {
    this.grantAllPosts = grantAllPosts;
  }

  public Boolean getGrantAllUsers() {
    return grantAllUsers;
  }

  public void setGrantAllUsers(Boolean grantAllUsers) {
    this.grantAllUsers = grantAllUsers;
  }
}
