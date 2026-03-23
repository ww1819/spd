package com.spd.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工作组和仓库关联 sys_post_warehouse
 */
public class SysPostWarehouse {

    /** 工作组ID */
    private Long postId;

    /** 仓库ID */
    private Long warehouseId;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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
                .append("warehouseId", getWarehouseId())
                .append("tenantId", getTenantId())
                .toString();
    }
}

