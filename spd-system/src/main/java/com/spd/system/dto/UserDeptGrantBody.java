package com.spd.system.dto;

import java.io.Serializable;

/**
 * 用户科室权限授权请求体
 */
public class UserDeptGrantBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long[] departmentIds;

    public Long[] getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(Long[] departmentIds) {
        this.departmentIds = departmentIds;
    }
}
