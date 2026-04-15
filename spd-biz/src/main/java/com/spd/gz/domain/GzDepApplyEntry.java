package com.spd.gz.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值科室申领明细对象 gz_dep_apply_entry
 * 
 * @author spd
 * @date 2024-06-22
 */
public class GzDepApplyEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父类ID */
    @Excel(name = "父类ID")
    private Long parenId;

    /** 耗材ID */
    @Excel(name = "耗材ID")
    private Long materialId;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal qty;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amt;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 批号 */
    @Excel(name = "批号")
    private String batchNumer;

    /** 供应商ID */
    private Long supplierId;

    /** 主条码 */
    private String masterBarcode;

    /** 辅条码 */
    private String secondaryBarcode;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID */
    private String tenantId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setParenId(Long parenId) 
    {
        this.parenId = parenId;
    }

    public Long getParenId() 
    {
        return parenId;
    }
    public void setMaterialId(Long materialId) 
    {
        this.materialId = materialId;
    }

    public Long getMaterialId() 
    {
        return materialId;
    }
    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
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
    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }
    public void setBatchNumer(String batchNumer) 
    {
        this.batchNumer = batchNumer;
    }

    public String getBatchNumer() 
    {
        return batchNumer;
    }
    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }
    public void setMasterBarcode(String masterBarcode)
    {
        this.masterBarcode = masterBarcode;
    }

    public String getMasterBarcode()
    {
        return masterBarcode;
    }
    public void setSecondaryBarcode(String secondaryBarcode)
    {
        this.secondaryBarcode = secondaryBarcode;
    }

    public String getSecondaryBarcode()
    {
        return secondaryBarcode;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }
    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parenId", getParenId())
            .append("materialId", getMaterialId())
            .append("unitPrice", getUnitPrice())
            .append("qty", getQty())
            .append("price", getPrice())
            .append("amt", getAmt())
            .append("batchNo", getBatchNo())
            .append("batchNumer", getBatchNumer())
            .append("supplierId", getSupplierId())
            .append("masterBarcode", getMasterBarcode())
            .append("secondaryBarcode", getSecondaryBarcode())
            .append("delFlag", getDelFlag())
            .append("tenantId", getTenantId())
            .append("remark", getRemark())
            .toString();
    }
}
