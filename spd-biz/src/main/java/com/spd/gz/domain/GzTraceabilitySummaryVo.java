package com.spd.gz.domain;

import java.math.BigDecimal;
import com.spd.foundation.domain.FdMaterial;

/**
 * 使用追溯汇总行（按科室+耗材汇总）
 */
public class GzTraceabilitySummaryVo
{
    /** 执行科室ID */
    private Long execDeptId;

    /** 执行科室名称 */
    private String execDeptName;

    /** 申请科室ID */
    private Long applyDeptId;

    /** 申请科室名称 */
    private String applyDeptName;

    /** 供应商ID（汇总维度为供应商时使用） */
    private Long supplierId;

    /** 供应商名称 */
    private String supplierName;

    /** 生产厂家名称 */
    private String factoryName;

    /** 汇总数量 */
    private BigDecimal quantity;

    /** 汇总金额 */
    private BigDecimal amount;

    /** 耗材档案 */
    private FdMaterial material;

    public Long getExecDeptId()
    {
        return execDeptId;
    }

    public void setExecDeptId(Long execDeptId)
    {
        this.execDeptId = execDeptId;
    }

    public String getExecDeptName()
    {
        return execDeptName;
    }

    public void setExecDeptName(String execDeptName)
    {
        this.execDeptName = execDeptName;
    }

    public Long getApplyDeptId()
    {
        return applyDeptId;
    }

    public void setApplyDeptId(Long applyDeptId)
    {
        this.applyDeptId = applyDeptId;
    }

    public String getApplyDeptName()
    {
        return applyDeptName;
    }

    public void setApplyDeptName(String applyDeptName)
    {
        this.applyDeptName = applyDeptName;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public String getFactoryName()
    {
        return factoryName;
    }

    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public FdMaterial getMaterial()
    {
        return material;
    }

    public void setMaterial(FdMaterial material)
    {
        this.material = material;
    }
}
