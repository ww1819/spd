package com.spd.finance.domain.vo;

import java.math.BigDecimal;

/**
 * 财务结算汇总表二：科室出退库按产品档案财务分类汇总金额
 * （「高值/高值耗材」→高值耗材，「普通」→普通耗材，其余已维护财务分类→试剂）
 */
public class FinanceDeptConsumablePickupRowVo
{
    private Long departmentId;

    private String departmentName;

    /** 院区（来自科室档案 fd_department.campus） */
    private String campus;

    /** 普通耗材（财务分类名称=「普通」） */
    private BigDecimal plainConsumablesAmt;

    /** 高值耗材（财务分类名称=「高值」或「高值耗材」） */
    private BigDecimal highValueConsumablesAmt;

    /** 试剂（已维护财务分类且非高值、非普通，如生化/免疫/分子等） */
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

    public String getCampus()
    {
        return campus;
    }

    public void setCampus(String campus)
    {
        this.campus = campus;
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
