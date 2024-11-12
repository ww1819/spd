package com.spd.warehouse.vo;

import com.spd.common.annotation.Excel;

/**
 * 库存明细Vo StkInventoryVo
 *
 * @author spd
 * @date 2023-12-17
 */
public class StkInventoryVo {

    /** ID */
    private Long id;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private String materialName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    @Override
    public String toString() {
        return "StkInventoryVo{" +
                "id=" + id +
                ", materialName=" + materialName +
                '}';
    }
}
