package com.spd.finance.domain.vo;

import java.math.BigDecimal;

/**
 * 财务结算汇总表二：科室出退库按产品档案库房分类 id（storeroom_id 12/11/13）汇总金额（普通耗材/高值耗材/试剂列）
 */
public class FinanceDeptConsumablePickupRowVo
{
    private Long departmentId;

    private String departmentName;

    /** 普通耗材（storeroom_id = 12） */
    private BigDecimal plainConsumablesAmt;

    /** 高值耗材（storeroom_id = 11） */
    private BigDecimal highValueConsumablesAmt;

    /** 试剂（storeroom_id = 13） */
    private BigDecimal reagentAmt;

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    public BigDecimal getPlainConsumablesAmt()
    {
        return plainConsumablesAmt;
    }

    public void setPlainConsumablesAmt(BigDecimal plainConsumablesAmt)
    {
        this.plainConsumablesAmt = plainConsumablesAmt;
    }

    public BigDecimal getHighValueConsumablesAmt()
    {
        return highValueConsumablesAmt;
    }

    public void setHighValueConsumablesAmt(BigDecimal highValueConsumablesAmt)
    {
        this.highValueConsumablesAmt = highValueConsumablesAmt;
    }

    public BigDecimal getReagentAmt()
    {
        return reagentAmt;
    }

    public void setReagentAmt(BigDecimal reagentAmt)
    {
        this.reagentAmt = reagentAmt;
    }
}
