package com.spd.caigou.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 出入库对象 stk_io_bill
 *
 * @author spd
 * @date 2023-12-17
 */
public class CaigouJihua extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 出入库单号 */
    @Excel(name = "采购计划单号")
    private String code;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
