package com.spd.foundation.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 集采类型 fd_jc_type（医院自维）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FdJcType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "类型编码")
    private String code;

    @Excel(name = "类型名称")
    private String name;

    private Integer sortOrder;

    /** 1在用 2停用 */
    @Excel(name = "使用状态", readConverterExp = "1=在用,2=停用")
    private String isUse;

    private Integer delFlag;

    private String tenantId;
}
