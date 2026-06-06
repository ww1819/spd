package com.spd.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和科室关联 sys_user_department
 */
public class SysUserDepartment {

    /** 用户ID */
    private Long userId;

    /** 科室ID */
    private Long departmentId;

    /** 状态 */
    private Integer status;

    /** 租户ID(同 sys_user.customer_id) */
    private String tenantId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("departmentId", getDepartmentId())
                .append("status", getStatus())
                .append("tenantId", getTenantId())
                .toString();
    }
}
