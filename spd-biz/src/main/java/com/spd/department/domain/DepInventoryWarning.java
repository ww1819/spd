package com.spd.department.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室库存预警设置对象 dep_inventory_warning
 *
 * @author spd
 * @date 2026-01-03
 */
public class DepInventoryWarning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 少于数量预警 */
    @Excel(name = "少于数量预警")
    private BigDecimal minQtyWarning;

    /** 多余数量预警 */
    @Excel(name = "多余数量预警")
    private BigDecimal maxQtyWarning;

    /** 删除标识 */
    private Integer delFlag;

    /** 科室对象 */
    private FdDepartment department;

    /** 耗材对象 */
    private FdMaterial material;

    /** 供应商ID（用于查询） */
    private Long supplierId;

    /** 产品名称（用于查询） */
    private String materialName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setMinQtyWarning(BigDecimal minQtyWarning)
    {
        this.minQtyWarning = minQtyWarning;
    }

    public BigDecimal getMinQtyWarning()
    {
        return minQtyWarning;
    }

    public void setMaxQtyWarning(BigDecimal maxQtyWarning)
    {
        this.maxQtyWarning = maxQtyWarning;
    }

    public BigDecimal getMaxQtyWarning()
    {
        return maxQtyWarning;
    }

    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

    public FdDepartment getDepartment()
    {
        return department;
    }

    public void setDepartment(FdDepartment department)
    {
        this.department = department;
    }

    public FdMaterial getMaterial()
    {
        return material;
    }

    public void setMaterial(FdMaterial material)
    {
        this.material = material;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("departmentId", getDepartmentId())
            .append("materialId", getMaterialId())
            .append("minQtyWarning", getMinQtyWarning())
            .append("maxQtyWarning", getMaxQtyWarning())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

