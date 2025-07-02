package com.spd.warehouse.domain;

import com.spd.common.core.domain.BaseEntity;

/**
 * 科室库存查询参数
 */
public class DepartmentInventoryQuery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 科室名称 */
    private String ksName;

    /** 耗材名称 */
    private String hcName;

    /** 耗材编码 */
    private String hcCode;

    /** 期初日期 */
    private String qcDate;

    /** 结束日期 */
    private String endDate;

    public String getKsName() {
        return ksName;
    }

    public void setKsName(String ksName) {
        this.ksName = ksName;
    }

    public String getHcName() {
        return hcName;
    }

    public void setHcName(String hcName) {
        this.hcName = hcName;
    }

    public String getHcCode() {
        return hcCode;
    }

    public void setHcCode(String hcCode) {
        this.hcCode = hcCode;
    }

    public String getQcDate() {
        return qcDate;
    }

    public void setQcDate(String qcDate) {
        this.qcDate = qcDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
} 