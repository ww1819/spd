package com.spd.caigou.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购计划明细对象 purchase_plan_entry
 *
 * @author spd
 * @date 2024-01-15
 */
public class PurchasePlanEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 主表ID */
    private Long parentId;

    /** 耗材ID */
    private Long materialId;

    /** 计划数量 */
    @Excel(name = "计划数量")
    private BigDecimal qty;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 申购数量（引用科室申购单时的汇总数量，仅展示） */
    private BigDecimal applyQty;
    /** 删除人（逻辑删除时填充） */
    private String deleteBy;
    /** 删除时间（逻辑删除时填充） */
    private Date deleteTime;
    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    /** 供应商ID（取自产品档案，用户可选择并保存，审核后按此拆分订单） */
    private Long supplierId;
    /** 申请科室ID（按申购单明细拆分时写入） */
    private Long applyDepartmentId;
    /** 申请科室（查询时填充，不持久化） */
    private FdDepartment applyDepartment;

    /** 耗材信息 */
    private FdMaterial material;
    /** 当前仓库库存数量（查询时填充，不持久化） */
    private BigDecimal stockQty;
    /** 该计划明细关联的申购单号（从关联表聚合，逗号分隔，仅展示） */
    private String applyBillNos;
    /** 关联的科室申购单明细ID列表（bas_apply_entry，保存时写入关联表，不持久化） */
    private List<Long> basApplyEntryIds;
    /** 关联的科室申购单明细ID列表（dep_purchase_apply_entry，保存时写入 purchase_plan_entry_dep_apply） */
    private List<Long> depApplyEntryIds;

    public List<Long> getBasApplyEntryIds() { return basApplyEntryIds; }
    public void setBasApplyEntryIds(List<Long> basApplyEntryIds) { this.basApplyEntryIds = basApplyEntryIds; }
    public List<Long> getDepApplyEntryIds() { return depApplyEntryIds; }
    public void setDepApplyEntryIds(List<Long> depApplyEntryIds) { this.depApplyEntryIds = depApplyEntryIds; }
    public BigDecimal getApplyQty() { return applyQty; }
    public void setApplyQty(BigDecimal applyQty) { this.applyQty = applyQty; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public BigDecimal getStockQty() { return stockQty; }
    public void setStockQty(BigDecimal stockQty) { this.stockQty = stockQty; }
    public String getApplyBillNos() { return applyBillNos; }
    public void setApplyBillNos(String applyBillNos) { this.applyBillNos = applyBillNos; }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setMaterialId(Long materialId) 
    {
        this.materialId = materialId;
    }

    public Long getMaterialId() 
    {
        return materialId;
    }

    public void setQty(BigDecimal qty) 
    {
        this.qty = qty;
    }

    public BigDecimal getQty() 
    {
        return qty;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setAmt(BigDecimal amt) 
    {
        this.amt = amt;
    }

    public BigDecimal getAmt() 
    {
        return amt;
    }

    public void setSpeci(String speci) 
    {
        this.speci = speci;
    }

    public String getSpeci() 
    {
        return speci;
    }

    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getApplyDepartmentId() { return applyDepartmentId; }
    public void setApplyDepartmentId(Long applyDepartmentId) { this.applyDepartmentId = applyDepartmentId; }
    public FdDepartment getApplyDepartment() { return applyDepartment; }
    public void setApplyDepartment(FdDepartment applyDepartment) { this.applyDepartment = applyDepartment; }

    public FdMaterial getMaterial() 
    {
        return material;
    }

    public void setMaterial(FdMaterial material) 
    {
        this.material = material;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("materialId", getMaterialId())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("speci", getSpeci())
            .append("model", getModel())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
