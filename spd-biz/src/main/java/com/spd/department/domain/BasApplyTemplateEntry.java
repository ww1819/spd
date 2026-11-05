package com.spd.department.domain;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 科室申领制单模板明细对象 bas_apply_template_entry
 *
 * @author spd
 */
public class BasApplyTemplateEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模板主表ID */
    @Excel(name = "模板主表ID")
    private Long parenId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 仓库ID（申购模板 SG 用；申领可空） */
    private Long warehouseId;

    /** 仓库名称（非表字段，详情联查展示） */
    private String warehouseName;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 排序 */
    private Integer sortOrder;

    /** 耗材对象 */
    private FdMaterial material;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setParenId(Long parenId) {
        this.parenId = parenId;
    }

    public Long getParenId() {
        return parenId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public FdMaterial getMaterial() {
        return material;
    }

    public void setMaterial(FdMaterial material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parenId", getParenId())
            .append("materialId", getMaterialId())
            .append("warehouseId", getWarehouseId())
            .append("qty", getQty())
            .append("sortOrder", getSortOrder())
            .toString();
    }
}
