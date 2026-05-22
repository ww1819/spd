package com.spd.system.dto;

import java.io.Serializable;

/**
 * 用户仓库权限授权请求体
 */
public class UserWarehouseGrantBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long[] warehouseIds;

    public Long[] getWarehouseIds() {
        return warehouseIds;
    }

    public void setWarehouseIds(Long[] warehouseIds) {
        this.warehouseIds = warehouseIds;
    }
}
