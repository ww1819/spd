package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisMirrorManualRowBody
{
    /** INPATIENT / OUTPATIENT */
    private String visitKind;
    /** 镜像表主键 id */
    private String mirrorRowId;
    /** 处理方：手动处理 / 自动处理；空则默认手动处理 */
    private String processParty;
    /**
     * 核销科室（SPD fd_department.id）。
     * 为空时按计费执行科室对照；自选科室核销时必填。
     */
    private Long consumeDepartmentId;
    /**
     * 为 true 时必须带 consumeDepartmentId，禁止回落到执行科室对照。
     * 前端「自选科室核销」固定传 true，避免字段未绑定却静默按执行科室扣库。
     */
    private Boolean requireConsumeDepartment;
}
