package com.spd.system.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量赋权：所选租户已有租户菜单权限（hc_customer_menu）
 */
public class MenuBatchGrantExistingVo {

  /** 所选租户均已拥有的菜单 ID */
  private List<Long> menuIdsAll = new ArrayList<>();

  /** 仅部分租户拥有的菜单 ID（半选展示） */
  private List<Long> menuIdsPartial = new ArrayList<>();

  public List<Long> getMenuIdsAll() {
    return menuIdsAll;
  }

  public void setMenuIdsAll(List<Long> menuIdsAll) {
    this.menuIdsAll = menuIdsAll;
  }

  public List<Long> getMenuIdsPartial() {
    return menuIdsPartial;
  }

  public void setMenuIdsPartial(List<Long> menuIdsPartial) {
    this.menuIdsPartial = menuIdsPartial;
  }
}
