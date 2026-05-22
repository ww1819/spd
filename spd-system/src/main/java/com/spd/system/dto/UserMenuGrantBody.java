package com.spd.system.dto;

import java.io.Serializable;

/**
 * 用户菜单权限授权请求体（仅更新菜单，不触碰用户主表及密码）
 */
public class UserMenuGrantBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜单 ID 列表（耗材 sys_menu 数字 ID） */
    private Long[] menuIds;

    public Long[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }
}
