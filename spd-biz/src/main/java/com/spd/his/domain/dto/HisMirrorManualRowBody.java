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
}
