package com.spd.caigou.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购订单明细对象 purchase_order_entry
 *
 * @author spd
 * @date 2024-01-15
 */
public class PurchaseOrderEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 主表ID */
    private Long parentId;

    /** 耗材ID */
    private Long materialId;

    /** 耗材编码 */
    @Excel(name = "耗材编码")
    private String materialCode;

    /** 耗材名称 */
    @Excel(name = "耗材名称")
    private String materialName;

    /** 耗材规格 */
    @Excel(name = "耗材规格")
    private String materialSpec;

    /** 耗材单位 */
    @Excel(name = "耗材单位")
    private String materialUnit;

    /** 订单数量 */
    @Excel(name = "订单数量")
    private BigDecimal orderQty;

    /** 已收货数量 */
    @Excel(name = "已收货数量")
    private BigDecimal receivedQty;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 预期交货日期 */
    @Excel(name = "预期交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    /** 实际交货日期 */
    @Excel(name = "实际交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualDeliveryDate;

    /** 质量状态（0待检验 1合格 2不合格） */
    @Excel(name = "质量状态", readConverterExp = "0=待检验,1=合格,2=不合格")
    private String qualityStatus;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 耗材信息 */
    private FdMaterial material;

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

    public void setMaterialCode(String materialCode) 
    {
        this.materialCode = materialCode;
    }

    public String getMaterialCode() 
    {
        return materialCode;
    }

    public void setMaterialName(String materialName) 
    {
        this.materialName = materialName;
    }

    public String getMaterialName() 
    {
        return materialName;
    }

    public void setMaterialSpec(String materialSpec) 
    {
        this.materialSpec = materialSpec;
    }

    public String getMaterialSpec() 
    {
        return materialSpec;
    }

    public void setMaterialUnit(String materialUnit) 
    {
        this.materialUnit = materialUnit;
    }

    public String getMaterialUnit() 
    {
        return materialUnit;
    }

    public void setOrderQty(BigDecimal orderQty) 
    {
        this.orderQty = orderQty;
    }

    public BigDecimal getOrderQty() 
    {
        return orderQty;
    }

    public void setReceivedQty(BigDecimal receivedQty) 
    {
        this.receivedQty = receivedQty;
    }

    public BigDecimal getReceivedQty() 
    {
        return receivedQty;
    }

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) 
    {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getExpectedDeliveryDate() 
    {
        return expectedDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) 
    {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Date getActualDeliveryDate() 
    {
        return actualDeliveryDate;
    }

    public void setQualityStatus(String qualityStatus) 
    {
        this.qualityStatus = qualityStatus;
    }

    public String getQualityStatus() 
    {
        return qualityStatus;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

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
            .append("materialCode", getMaterialCode())
            .append("materialName", getMaterialName())
            .append("materialSpec", getMaterialSpec())
            .append("materialUnit", getMaterialUnit())
            .append("orderQty", getOrderQty())
            .append("receivedQty", getReceivedQty())
            .append("unitPrice", getUnitPrice())
            .append("totalAmount", getTotalAmount())
            .append("expectedDeliveryDate", getExpectedDeliveryDate())
            .append("actualDeliveryDate", getActualDeliveryDate())
            .append("qualityStatus", getQualityStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
