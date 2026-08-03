package com.spd.foundation.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 集采周期 fd_jc_period（带年月）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FdJcPeriod extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "周期名称")
    private String name;

    /** 开始年月 YYYY-MM */
    @Excel(name = "开始年月")
    private String startYm;

    /** 结束年月 YYYY-MM */
    @Excel(name = "结束年月")
    private String endYm;

    /** 1在用 2停用 */
    @Excel(name = "使用状态", readConverterExp = "1=在用,2=停用")
    private String isUse;

    private Integer delFlag;

    private String tenantId;
}
