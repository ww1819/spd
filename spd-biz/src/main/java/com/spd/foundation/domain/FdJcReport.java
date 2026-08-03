package com.spd.foundation.domain;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 集采报量 fd_jc_report（按周期+产品 或 周期+类型；模式切换不删旧数据）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FdJcReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long periodId;

    /** PRODUCT / TYPE */
    private String reportMode;

    /** PRODUCT 模式必填 */
    private Long materialId;

    /** TYPE 模式必填 */
    private Long jcTypeId;

    @Excel(name = "报量数")
    private BigDecimal reportQty;

    private Integer delFlag;

    private String tenantId;

    /** 展示用（非表字段） */
    private String periodName;
    private String periodStartYm;
    private String periodEndYm;
    private String materialCode;
    private String materialName;
    private String jcTypeCode;
    private String jcTypeName;
}
