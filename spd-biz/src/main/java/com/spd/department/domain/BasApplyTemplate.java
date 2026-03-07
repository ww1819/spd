package com.spd.department.domain;

import java.util.List;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 科室申领制单模板对象 bas_apply_template
 *
 * @author spd
 */
public class BasApplyTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 租户ID(同sb_customer.customer_id) */
    @Excel(name = "租户ID")
    private String tenantId;

    /** 删除标志（0正常 1删除） */
    private Integer delFlag;
    /** 删除者 */
    private String deleteBy;
    /** 删除时间 */
    private java.util.Date deleteTime;

    /** 模板明细列表 */
    private List<BasApplyTemplateEntry> entryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public java.util.Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(java.util.Date deleteTime) { this.deleteTime = deleteTime; }

    public List<BasApplyTemplateEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<BasApplyTemplateEntry> entryList) {
        this.entryList = entryList;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("templateName", getTemplateName())
            .append("warehouseId", getWarehouseId())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
