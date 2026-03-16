package com.spd.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工作组和科室关联 sys_post_department
 */
public class SysPostDepartment {

    /** 工作组ID */
    private Long postId;

    /** 科室ID */
    private Long departmentId;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
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
                .append("postId", getPostId())
                .append("departmentId", getDepartmentId())
                .append("tenantId", getTenantId())
                .toString();
    }
}

